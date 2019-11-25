const EventModel = require('../data/schema').EventModel
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

/*
    slots1 & slots2 look like:
    [
        {
            startTime: "unix number",
            endTime: "unix number",
            weight: "float"
        },
        {}
    ]
*/
function mergeTimes(slotPref, slotCal, period){
    var times = []
    var slotCalidx = 0
    //we need to keep track of the previous, because the next
    //slotPref can match with all slotCals after prev
    var prevIdx = 0
    //O(N) runtime
    //we can walk along slotPref -> 
    //  if its weight > 0.75, then we can add those
    //  if it isn't, we check if the slotCal pushes it over 0.75
    for(const slot of slotPref){
        if(slot.weight >= 0.74 && slotPeriod(slot) >= period){
            //we can automatically add these
            times.push(slot)
        } else {
            var calSlot
            slotCalidx = prevIdx
            while(slotCal[slotCalidx] && !(calSlot = intersect(slot, slotCal[slotCalidx]))){
                slotCalidx++
            }
            prevIdx = slotCalidx
            while(slotCal[slotCalidx] && (calSlot = intersect(slot, slotCal[slotCalidx]))){
                if(calSlot.weight > 0.74 && slotPeriod(calSlot) >= period){
                    times.push(calSlot)
                }
                slotCalidx++
            }
        }
    }

    //now we sort them by longest period, and iterate thru, selecting the top 5
    times.sort(function(a, b){return slotPeriod(a) < slotPeriod(b)})

    var result = []
    for(const a of times){
        if(result.length == 5) break
        result.push(a)
    }
    return result
}

//gets intersection of two slots
function intersect(slot1, slot2){
    if(!slot1 || !slot2) return null

    var start = Math.max(slot1.startTime, slot2.startTime)
    var end = Math.min(slot1.endTime, slot2.endTime)
    if(start > end) return null

    return {
        weight: slot1.weight + slot2.weight,
        startTime: start,
        endTime: end
    }
}

function slotPeriod(slot){
    return slot.endTime - slot.startTime;
}

module.exports = {
    userFreeTime,
    freeCalendarSlots,
    mergeTimes
}