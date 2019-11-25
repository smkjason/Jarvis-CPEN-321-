const EventModel = require('./data/schema').EventModel
const moment = require('moment');

async function freeCalendarSlots(tevent){
    var attendees = event.responses.reduce(function(prev, curr){
        if(!curr.declined && curr.email != event.creatorEmail) {
            prev.push(curr.email)
        }

        return prev
    }, [])
    var responses = []
    for(const email of attendees){
        responses.push(userFreeTime(email, tevent.deadline))
    }
    return responses
}

async function userFreeTime(email, deadline){
    //get all events
    var events = await EventModel.find({
        $or: [
            {creatorEmail: email},
            {attendees: {$in: [email]}}
        ]
    }).exec()
    var intervals = []
    for(const event of events){
        intervals.push([
            moment(event.start.dateTime).unix(),
            moment(event.end.dateTime).unix()
        ])
    }
    //sort by start time
    intervals.sort(function(a, b){return a[0] > b[0]})

    var d_unix = moment(deadline, 'YYYY-MM-DD').add(1, 'day').unix()
    //this would be all of the free times
    var currInterval = [moment().unix(), d_unix]
    var negativeIntervals = []
    for(const interval of intervals){
        if(currInterval[0] > d_unix) {
            break
        }
        if(interval[1] < currInterval[0]) {
            continue
        }

        if(interval[0] > currInterval[0]){
            //we can create a free interval
            negativeIntervals.push([currInterval[0], Math.min(interval[0], d_unix)])
            currInterval = [interval[1], d_unix]
        } else {
            currInterval[0] = Math.max(interval[1], currInterval[0])
        }
    }
    if(currInterval[0] < d_unix) negativeIntervals.push(currInterval)

    //turn them into unixTime with start and end
    var timeslots = []
    for(const nInterval of negativeIntervals){
        timeslots.push({
            startTime: moment.unix(nInterval[0]).format('YYYY-MM-DD HH:mm'),
            endTime: moment.unix(nInterval[1]).format('YYYY-MM-DD HH:mm')
        })
    }

    return {
        timeslots: timeslots,
        email: email
    }
}

module.exports = {
    freeCalendarSlots
}