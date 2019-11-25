const TEventModel = require('../data/schema').TentativeEventModel
const moment = require("moment");



function getFreeTime(event){

    var deadline_date =  event.deadline;

    var today = new Date();
    var cur_date = today.getFullYear()+'-'+(today.getMonth()+1)+'-'+today.getDate();

    deadline_date = parseDate(deadline_date);
    cur_date = parseDate(cur_date);

    //this gets how much time between today and deadline
    var diffintime = deadline_date.getTime() - cur_date.getTime()

    //this converts the time ^^^ to days
    var deadline_days = (diffintime / (1000 * 3600 * 24));
    var deadline_count = 0;

    //array of arrays, with length of number of invitees plus admin
    var FreeTimeModel = new TEventModel();
    var freetimeslots = [];
    //for each attendee, calc their free time
    //need to use deadline
    for(var i = 0; i < event.responses.length; i++){
        deadline_count = deadline_days;
        FreeTimeModel.responses[i].email.push(event.responses[i].email);

        while(deadline_count < deadline_days){
            FreeTimeModel.responses[i].timeslots.push()

            deadline_count++;
        }
    }





    return []
}


function parseDate(input) {
	var parts = input.match(/(\d+)/g);
	// new Date(year, month [, date [, hours[, minutes[, seconds[, ms]]]]])
	return new Date(parts[0], parts[1]-1, parts[2]); // months are 0-based
  }

function sort(tobesorted){
    
}