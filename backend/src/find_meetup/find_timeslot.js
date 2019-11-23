//const UserFunctions = require("./app/event");
const mongoose = require("mongoose");
const schema = require("../data/schema");
const moment = require("moment");


var starttime = [];
var endtime = [];
var starttoend = [];


var temp = [[],[],[],[],[],[],[]];
var temp_day = ""
var day = ""
var temp_start = ""
var temp_end = ""

var start = 0;

//need to convert set back to the form in "hh"
//TO DO:
//change set into an array of size 7 to store result for each day

var set = {};

//24 hours 
var sum = [0,0,0,0,0,0,0,0,0,0,
		   0,0,0,0,0,0,0,0,0,0,
		   0,0,0,0];

var sum_total = [[],[],[],[],[],[],[]];
			
//logic should work something like this
// Student A: 11100000111111100000
// Student B: 00000011111000010001
// Student C: 00000000111111110001
// _______________________________+
// 		  	  11100022333222220002


//list of list of events
//list of start time and end time
//starttime : YYYY-MM-DD hh-mm
//want to round up/down the minutes into hours 
//for the demo maybe just use exact hours


function calculateBestTimeslot(eventId){

	//this converts responses from YYYY-MM-DD hh:mm into just hhmm for free slots calc
	for(var a = 0; a < Object.keys(eventId.responses.timeslots).length; a++){
		//start and end rn in the form of YYYY-MM-DD hh:mm
		//need to convert to hhmm
		//then need to approx to hh
		temp_start = eventId.responses[1].timeslots[a].starttitme
		temp_end = eventId.responses[1].timeslots[a].endtime

		temp_day = moment(temp_start).format('LL');

		temp_start = moment(temp_start).format('hhmm');
		temp_end = moment(temp_end).format('hhmm');
		
		//TODO:
		//right now, it is hhmm, but need hh
		//want to round up/down the minutes into hours 

		day = new Date(temp_day)

		//adds slots from a day in week to that specific day in week
		//get.Day() returns day of week, so corresponding start and end time gets stored correctly
		//temp[0] is Sunday, temp[1] is Monday, etc
		temp[day.getDay()].push([temp_start,temp_end])

		//temp will look like this 
		//temp[[[SUNDAY_start1,SUNDAY_end1],[SUNDAY_start2,SUNDAY_end2]],
		//		[MONDAY_start1,MONDAY_end1],[MONDAY_start2,MONDAY_end2], [TUESDAY slots], [WEDENSDAY SLOTS]...]
	}

	//create sum total for all days in the week
	for(var a = 0; a < 7; a++){
		sum_total[a].push(sum);
	}

	// incrementing through each user to get each user's start/end time
for(var day_count = 0; daycount < 7; daycount++){
	for(var x = 0; x < temp[day_count].length; x++){

		//index 0 is start time, 1 is end time
        starttime[x] =  ((temp[day_count])[x])[0]; 
        endtime[x] = ((temp[day_count])[x])[1];

		//need this to calc intervals
        starttoend[x] = endtime[x] - starttime[x];
    }

	//increment the intervals of meet up time
	for(var y = 0; y < starttoend.length; y++){
			while(starttoend[y] >= 0){
				(sum_total[day_count])[starttime[y]] += 1;
				starttoend[y]--;
				starttime[y]++;
			}
	}


console.log(sum)

for(var i = 1; i < sum_total[day_count].length; i++){
	if(sum_total[day_count][i] === total){
		//starting a new sequence
		if(sum_total[day_count][i-1] !== total){
			start = i;
		}
	} else {
		//ending a sequence
		if(sum_total[day_count][i-1] === total){
			//take all the 0s from start - i-1
			set[start] = i - 1;
		}
	}
}
if(sum_total[day_count][sum_total[day_count].length-1] === total){
	set[start] = sum_total[day_count].length - 1;
}
}//daycount loop ends here

// console.log(sum);
// console.log(set);

return set;
}

module.exports = {
	calculateBestTimeslot
}