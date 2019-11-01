const EventModel = require('./schema').EventModel

//retrieves calendar events from google, and uploads it to our db
function uploadEvents(user){
    //check and authorize api token in user

    //get events from google

    //upoad db
}

function demoCalculateTime(json){
    retval = parseEvents(json)
    //retval = calculateBestTimeslot(events)
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
    demoCalculateTime: demoCalculateTime
}
