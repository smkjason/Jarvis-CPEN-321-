const EventModel = require('../data/schema').EventModel
const TEventModel = require('../data/schema').TentativeEventModel
const UserModel = require('../data/schema').UserModel
const Google = require('../util/google')
const clone = require('lodash/cloneDeep')
const uuid = require('uuid/v1')
const moment = require('moment')
const freetime = require('../find_meetup/freetime')
const calculateBestTimeslot = require('../find_meetup/find_timeslot')

/*
    - finds preferred time slots 
    **Current output is one slot where every attendee can attend
    ^^need to tweak to allow for multiple timeslots

    - only admin can see the output

    ??get user input times from respondEvent??

    if frontend puts response correctly, the preferred times (responses) will be stored in the database
    now HOW TO access these responses? (using the event id?)

*/
async function getPreferredTime(eventId, email){
    var event = await TEventModel.findOne({id:eventId}).exec();
    if(!event) return {error: 'no event with eventId' + eventId};

    if(email != event.creatorEmail) return{error: `${email} is not the admin`};
    if(!event.invitees.includes(email)) return{error: `${email} not invited`};

    var period = event.length;

    var user_freetime = {responses: freetime.freeCalendarSlots(event),
                         deadline: event.deadline};

    var result_user_freetime = calculateBestTimeslot.calculateBestTimeslot(user_freetime);

    var result_input_timeslots = calculateBestTimeslot.calculateBestTimeslot(event);

    return freetime.mergeTimes(result_user_freetime, result_input_timeslots, period);
}

/*
    gets a single event

    TODO: add checking if the user can view the event
*/
async function getEvent(eventId, email){
    event = await TEventModel.findOne({id: eventId}).exec() || await EventModel.findOne({id: eventId}).exec()
    if(!event) return

    return event
}

/*
    create a tentative event

    create and save db object
*/
async function createEvent(email, data){
    data.creatorEmail = email
    data.id = uuid().replace(/-/g, '')
    for(const attendee of data.invitees){
        user = await UserModel.findOne({email: attendee}).exec()
        Google.sendNotification(user, 'Event Invite', 'You got a new invite to ' + data.name)
    }
    var tevent = new TEventModel(data)
    return await tevent.save()
}

/*
    update a created event -> only creator can do that

    what are we updating?
*/
async function updateEvent(data){

}

/*
    delete a created event

    delete db object
    save deletion to user calendar
    remove the socket
*/
async function deleteEvent(data){

}

/*
    for downloading all of google's calendar events into the database
*/
async function syncEvents(user){
    //called when we first log in
    var calendar = Google.getUserCalendar(user)
    events = await calendar.events.list({
        calendarId: 'primary',
    })
    var eventList = await saveGoogleEvents(user.email, events)

    while(events.pageToken){
        events = await calendar.events.list({
            calendarId: 'primary',
            pageToken: events.pageToken
        })
        var mongoEvents = await saveGoogleEvents(events)
        eventList = eventList.concat(...mongoEvents)
    }
    return eventList
}

/*
    get the events that the user is attending or created
*/
async function getEvents(email){
    //var tevents = await relatedTEvents(email)
    var events = await relatedEvents(email)

    return {events: events}
}

async function relatedEvents(email){
    var events = await EventModel.find({
        $or: [
            {creatorEmail: email},
            {attendees: {$in: [email]}}
        ]
    }).exec()
    return events
}

async function relatedTEvents(email){
    var events = await TEventModel.find({
        $or: [
            {creatorEmail: email},
            {invitees: {$in: [email]}}
        ]
    }).exec()
    return events
}

async function respondEvent(id, email, decline, response){
    var event = await TEventModel.findOne({id: id}).exec()
    if(!event) return {error: 'no event with id' + id}

    if(!event.invitees.includes(email) && event.creatorEmail != email) return {error: `${email} not invited`}
    
    response.email = email
    response.declined = decline
    event.responses.push(response)
    
    await event.save()
    return {status: 'success'}
}

async function activateEvent(id, email, timeSlot){
    var event = await TEventModel.findOne({id: id}).exec()

    if(!event) return {error: 'no event with id' + id}
    if(event.creatorEmail != email) return {error: `${email} not admin`}
    
    //save the event to the mongoDB db
    var googleEvent = finalizeEvent(event, timeSlot)
    var eventId = googleEvent.id;
    console.log(googleEvent)
    await googleEvent.save()

    //save to the goole calendar event
    var admin = await UserModel.findOne({email: email}).exec()
    await Google.addToCalendar(admin, googleEvent)

    var attendees = getAttendees(event)
    //save for the user
    for(const attendee of attendees){
        //create a calendar object for them, and save to their calendar
        var user = await UserModel.findOne({email: attendee}).exec()
        if(!user) continue

        //save a new event to the google calendar, add the existing mongoDB event 
        //onto the list of events to notify on
        googleEvent.id = uuid().replace(/-/g, '')
        Google.addToCalendar(user, googleEvent)
        user.new_events = (user.new_events || []).concat([eventId])
    }
    await TEventModel.deleteOne({id: event.id}).exec()
    googleEvent.id = eventId
    return googleEvent
}

async function userLocations(id, email){
    var event = await EventModel.findOne({id: id}).exec()
    if(!event) return {error: "event not found", status: "error"}

    if((Date.now() / 1000) < (moment(event.start.dateTime).unix() - 3600) || 
        (Date.now() / 1000) > moment(event.end.dateTime).unix()) {
        return {error: "event time not close"}
    }
    if(event.creatorEmail != email && !event.attendees.includes(email)) return {error: `${email} is not part of event`, status: "error"}

    var people = event.attendees.concat(event.creatorEmail)
    var users = await UserModel.find({email: {$in: people}}).exec()
    var locations = []
    for(const user of users){
        if(user.email == email) continue
        locations.push({
            user: user.email,
            lat: user.lat,
            lon: user.lon
        })
    }
    return {locations: locations}
}

/* private functions  ----------- */
async function saveGoogleEvents(email, events){
    var currentEvents = await EventModel.find({creatorEmail: email}).exec()
    eventIds = currentEvents ? currentEvents.map(function(doc){return doc.id}) : []

    var eventlist = []
    //event is Schema$Event in google calendar api
    for(const event of events.data.items){
        //skip over events we already have
        if(eventIds.includes(event.id)) continue
        
        var json = clone(event)
        json.creatorEmail = (json.creator || {}).email
        json.googleEvent = true
        json.attendees = (json.attendees || []).map(function(attendee){return attendee.email})
        var mongoEvent = new EventModel(json)
        eventlist.push(mongoEvent)
        try{
            await mongoEvent.save()
        } catch (err){
            console.log(err)
        }
    }
    return eventlist
}

function finalizeEvent(tevent, time){
    var eventJson = {}
    var startTime = moment(time.startTime)
    var endTime = moment(time.endTime)
    var temp = tevent.length.split(':')
    var secs = temp[0] * 3600 + temp[1] * 60

    //max time period
    if( (endTime.unix() - startTime.unix()) > secs){
        endTime = startTime.add(moment.duration(secs, 'seconds'))
        //we need to redo the startTime as well because moment.add changes the object
        startTime = moment(startTime).subtract(moment.duration(secs, 'seconds'))
    }

    eventJson.status = "confirmed"
    eventJson.created = (new Date()).toISOString()
    eventJson.creatorEmail = tevent.creatorEmail
    eventJson.start = {
        timeZone: "America/Vancouver",
        dateTime: startTime.toISOString().replace(/\.[0-9][0-9][0-9]Z/, '-08:00')
    }
    eventJson.end = {
        //2019-11-25T14:00:00-08:00
        timeZone: "America/Vancouver",
        dateTime: endTime.toISOString().replace(/\.[0-9][0-9][0-9]Z/, '-08:00')
    }
    eventJson.attendees = getAttendees(tevent)
    eventJson.recurrence = []
    eventJson.id = uuid().replace(/-/g, '')
    eventJson.summary = tevent.name
    eventJson.description = ""
    eventJson.googleEvent = false
    return new EventModel(eventJson)
}

function getAttendees(event){
    return event.responses.reduce(function(prev, curr){
        if(!curr.declined && curr.email != event.creatorEmail) {
            prev.push(curr.email)
        }

        return prev
    }, [])
}

module.exports = {
    syncEvents,
    getEvents,
    createEvent,
    updateEvent,
    deleteEvent,
    relatedEvents,
    relatedTEvents,
    respondEvent,
    activateEvent,
    getEvent,
    userLocations,
    getPreferredTime
}
