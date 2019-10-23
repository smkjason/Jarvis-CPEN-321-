//const UserFunctions = require('./app/event')
const mongoose = require('mongoose')
const schema = require('../data/schema')
const moment = require('moment')

const user_events = schema.EventModel

var start_time
var end_time 
var start_to_end = []

var s
var e 
var se 

var sum = [0,0,0,0,0,0,0,0,0,0,
			0,0,0,0,0,0,0,0,0,0,
			0,0,0,0]


//list of list of events
//only need to grab start/end_time for each user

function calculateBestTimeslot(listEvents){

	//incrementing through each user to get each user's start/end time
	loop1:
	for(var x = 0; x < listEvents.size; x++){
		start_time = listEvents[x].start_time; 
		end_time = listEvents[x].end_time;

		start_to_end = end_time.map(function(item, index){
			return item - start_time[index];
		})

		loop2:
		//increment the intervals of meet up time
		for(var y = 0; y < start_to_end.length; y++){
				while(start_to_end[y] > 0){
					sum[start_time[y]] += 1;
					start_to_end[y]--;
					start_time[y]++;
					if(start_to_end[y] < 0){
						break loop2;
					}
				}
			if(y = start_to_end.length){
				break loop1;
			}
		}
	 }

console.log(sum)

var start = 0;
var set = {}
for(var i = 1; i < sum.length; i++){
	if(sum[i] == 0){
		//starting a new sequence
		if(sum[i-1] != 0){
			start = i;
		}
	} else {
		//ending a sequence
		if(sum[i-1] == 0){
			//take all the 0s from start - i-1
			set[start] = i - 1
		}
	}
}
if(sum[sum.length-1] == 0){
	set[start] = sum.length - 1
}

console.log(sum)
console.log(set)

return set;
}