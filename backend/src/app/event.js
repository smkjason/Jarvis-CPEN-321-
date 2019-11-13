const EventModel = require('../data/schema').EventModel
const Google = require('../util/google')
const clone = require('lodash/cloneDeep')

/*
    create an event

    create and save db object
    save to user google calendar
*/
async function createEvent(name, data){
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
    await saveGoogleEvents(events)

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
    get the events that the user has
*/
async function getEvents(email){
    events = await EventModel.find({creatorEmail: email}).exec()
    return events
}

function demoCalculateTime(json){
    retval = parseEvents(json)
    return retval
}

/* private functions  ----------- */

async function saveGoogleEvents(events){
    console.log(events)
    console.log('there are ' + events.data.items.length + 'items')
    var eventlist = []
    //event is Schema$Event in google calendar api
    for(const event of events.data.items){
        var json = clone(event)
        json.creatorEmail = json.creator.email
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
    deleteEvent
}
