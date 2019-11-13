const EventModel = require('./schema').EventModel
const Google = require('../util/google')
const clone = require('lodash/cloneDeep')

async function uploadEvents(user){
    //called when we first log in
    var calendar = Google.getUserCalendar(user)
    events = await calendar.events.list({
        calendarId: 'primary',
        auth: client,
    })
    await saveEvents(events)
    while(events.pageToken){
        events = await calendar.events.list({
            calendarId: 'primary',
            auth: client,
            pageToken: events.pageToken
        })
        await saveEvents(events)
    }
    return events.items
}

async function getEvents(email){
    events = await EventModel.find({creatorEmail: email}).exec()
    return events
}

async function saveEvents(events){
    console.log(events)
    //event is Schema$Event in google calendar api
    for(const event of events.data.items){
        var json = clone(event)
        json.creatorEmail = json.creator.email
        var mongoEvent = new EventModel(json)
        await mongoEvent.save()
    }
}

function demoCalculateTime(json){
    retval = parseEvents(json)
    return retval
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
    demoCalculateTime: demoCalculateTime,
    uploadEvents: uploadEvents,
    getEvents: getEvents
}
