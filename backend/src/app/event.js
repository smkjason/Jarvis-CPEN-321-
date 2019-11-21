const EventModel = require('../data/schema').EventModel
const TEventModel = require('../data/schema').TentativeEventModel
const UserModel = require('../data/schema').UserModel
const Google = require('../util/google')
const clone = require('lodash/cloneDeep')
const uuid = require('uuid/v1')
const moment = require('moment')

/*
    gets a single event

    TODO: add checking if the user can view the event
*/
async function getEvent(eventId, email){
    event = await TEventModel.findOne({id: eventId}) || await EventModel.findOne({id: eventId})
    if(!event) return

    return event
}

/*
    create a tentative event

    create and save db object
*/
async function createEvent(email, data = {}){
    data.creatorEmail = email
    data.id = uuid().replace(/-/g, '')
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
    var eventList = [];
    events = await calendar.events.list({
        calendarId: 'primary',
    })
    eventList = eventList.concat(events)
    await saveGoogleEvents(user.email, events)

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
    var tevents = await relatedTEvents(email)
    var events = await relatedEvents(email)

    return tevents.concat(events)
}

async function relatedEvents(email){
    var user = await UserModel.findOne({email: email}).exec()
    if(!user) return []

    var events = await EventModel.find({
        $or: [
            {creatorEmail: user.email},
            {attendees: {$in: [user.email]}}
        ]
    }).exec()
    return events
}

async function relatedTEvents(email){
    var user = await UserModel.findOne({email: email}).exec()
    if(!user) return []

    var events = await TEventModel.find({
        $or: [
            {creatorEmail: user.email},
            {invitees: {$in: [user.email]}}
        ]
    }).exec()
    return events
}

async function respondEvent(id, email, decline, response){
    var event = await TEventModel.findOne({id: id}).exec()
    if(!event) return {error: 'no event with id' + id}

    if(!event.invitees.includes(email)) return {error: `${email} not invited`}
    
    response.email = email
    response.declined = decline
    event.responses.push(response)
    await event.save()
    return {status: "success"}
}

async function activateEvent(id, email, timeSlot){
    var event = await TEventModel.findOne({id: id}).exec()

    if(!event) return {error: 'no event with id' + id}
    if(event.creatorEmail != email) return {error: `${email} not admin`}
    
    //save the event to the mongoDB db
    var googleEvent = finalizeEvent(event, timeSlot)
    var eventId = googleEvent.id;
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
        json.creatorEmail = json.creator.email
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
    eventJson.status = "confirmed"
    eventJson.created = (new Date()).toISOString()
    eventJson.creatorEmail = tevent.creatorEmail
    eventJson.start = {
        timeZone: "America/Vancouver",
        dateTime: moment(time.startTime).toISOString().replace(/\.000Z/, '-08:00')
    }
    eventJson.end = {
        timeZone: "America/Vancouver",
        dateTime: moment(time.endTime).toISOString().replace(/\.000Z/, '-08:00')
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
        if(!curr.declined) prev.push(curr.email)

        return prev
    }, [])
}

function parseEvents(json){
    res = []
    names = {}
    var i = 0;
    for(var j = 0; j < json.length; j++){
        if(json[j].name in names){
            res[names[json[j].name]].push(new EventModel(json[j]))
        } else {
            //new one
            res[i] = new Array
            res[i].push(new EventModel(json))
            names[json[j].name] = i++;
        }
    }
    return res
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
    getEvent
}
