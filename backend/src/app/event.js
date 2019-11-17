const EventModel = require('../data/schema').EventModel
const UserModel = require('../data/schema').UserModel
const Google = require('../util/google')
const clone = require('lodash/cloneDeep')

/*
    create an event

    create and save db object
    save to user google calendar
*/
async function createEvent(name, data = {}){
    data.creatorEmail = name
    return data
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
    return await relatedEvents(email)
}

function demoCalculateTime(json){
    retval = parseEvents(json)
    return retval
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

/* private functions  ----------- */
async function saveGoogleEvents(email, events){
    console.log(events)
    console.log('there are ' + events.data.items.length + ' items to sync')
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
    demoCalculateTime,
    syncEvents,
    getEvents,
    createEvent,
    updateEvent,
    deleteEvent,
    relatedEvents
}
