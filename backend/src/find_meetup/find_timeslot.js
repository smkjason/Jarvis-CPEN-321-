//const UserFunctions = require("./app/event");
const mongoose = require("mongoose");
const schema = require("../data/schema");
const moment = require("moment");

const user_events = schema.EventModel;

var starttime = {};
var endtime = {};
var starttoend = [];

var start = 0;
var set = {};

var sum = [0,0,0,0,0,0,0,0,0,0,
			0,0,0,0,0,0,0,0,0,0,
			0,0,0,0];


//list of list of events
//only need to grab start/end time for each user

function calculateBestTimeslot(listEvents){

	var total = listEvents.size;
	// incrementing through each user to get each user's start/end time
	loop1:
	for(var x = 0; x < listEvents.size; x++){
		starttime = listEvents[x].start_time; 
		endtime = listEvents[x].end_time;

		starttoend = endtime.map(function(item, index){
			return item - starttime[index];
		})

		loop2:
		//increment the intervals of meet up time
		for(var y = 0; y < starttoend.length; y++){
				while(starttoend[y] > 0){
					sum[starttime[y]] += 1;
					starttoend[y]--;
					starttime[y]++;
					if(starttoend[y] < 0){
						break loop2;
					}
				}
			if(y === starttoend.length){
				break loop1;
			}
		}
	 }

console.log(sum)

for(var i = 1; i < sum.length; i++){
	if(sum[i] === total){
		//starting a new sequence
		if(sum[i-1] !== total){
			start = i;
		}
	} else {
		//ending a sequence
		if(sum[i-1] === total){
			//take all the 0s from start - i-1
			set[start] = i - 1;
		}
	}
}
if(sum[sum.length-1] === total){
	set[start] = sum.length - 1;
}

console.log(sum);
console.log(set);

return set;
}

module.exports = {
	calculateBestTimeslot
}