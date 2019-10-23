//const UserFunctions = require('./app/event')
const mongoose = require('mongoose')
const schema = require('../data/schema')
const moment = require('moment')

var mongoClient

const user_events = schema.EventModel


var start_time 
var end_time
var start_to_end = []
var count
var flag

var interval = []

var sum = [0,0,1,0,0,0,0,0,0,0,
			0,0,0,0,0,0,0,0,0,0,
			0,0,0,1]


//list of list of events
//only need to grab start/end_time for each user
function calculateBestTimeslot(listEvents){

	//incrementing through each user to get start/end time
	for(var x = 0; x < listEvents.size; x++){
		start_time = listEvents[x].end_time;
		end_time = listEvents[x].start_time;
		start_to_end.push(start_time - end_time);
		//increment the intervals of meet up time
		while(start_to_end > 0){
			sum[start_time] += 1;
			start_to_end--;
			start_time++;
		} 
	 }

//after finish adding the start/end time to sum
//need to check for intervals to meet up


flag = 0;
outer:
for (var i = 0; i < sum.length; i++){
	if(sum[i] == 0 && flag == 0){

		inner:
		for(var k = i + 1; k < sum.length - i; k++){
			if(sum[k] != 0 && k > i + 1){
				interval.push(i);
				interval.push(k-1);
				flag = 1;
				break inner;
			} 	
		}
	}
	if(i >= k){
		flag = 0;
	}
}

//interval now contains the available meet up times
//conver this back to string and return

console.log(sum);
console.log(sum.length);
console.log(interval);

// }



//var end_time 

// function find_meetup(config){
//     mongoose.connect("mongodb+srv://jarvis:123123123@jarvis-kanro.mongodb.net/testdb", {
//         useNewUrlParser: true
//     })
//         .then(
//             console.log("successfully connected")
//         )
//         .catch(err => {
//             console.log("err occurred!")
// 		})
// 	console.log("runs find_meetup()");
// 	// user_events = mongoose.model('eventSchema', schema);
// 	// var a = new user_events;

// 	user_events.testdb().collection('user1').find({start_time}, function(err,data){
// 		console.log(">>>>" + data);
// 	});

	
// }

// function deinit(){
//     mongoClient.close();
// }

// module.exports = {
// 	find_meetup: find_meetup,
// 	deinit: deinit
// }


//mongoose.connect("mongodb+srv://jarvis:123123123@jarvis-kanro.mongodb.net/testdb");
// var connection = mongoose.connection;
// //user_events = mongoose.model('user_events', eventSchema);

// connection.on('error', console.error.bind(console, 'connection error:'));
// connection.once('open', function () {

//     connection.db.collection("user1", function(err, collection){
// 		collection.find({})
		
		
		
		
// 		toArray(function(err, data){
// 			console.log(data); // it will print your collection data
// 			console.log("TESTING");
// 			console.log(data[0].start_time + data[1].start_time);
// 			console.log(data[1].start_time);
// 			start_time = data[0].start_time;
			
//         })
//     });

// });
// console.log(start_time);

// connection.close();