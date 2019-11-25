//const UserFunctions = require("./app/event");
const moment = require("moment");


var starttime = [];
var endtime = [];
var starttoend = [];

var temp = [[],[],[],[],[],[],[]];
var temp_day = ""
var day = ""
var temp_start = ""
var temp_end = ""
var temp_min

// var start = 0;

//need to convert set back to the form in "hh"
//TO DO:
//change set into an array of size 7 to store result for each day

var set = [[],[],[],[],[],[],[]];

//24 hours 
var sum = [0,0,0,0,0,0,0,0,0,0,
		   0,0,0,0,0,0,0,0,0,0,
		   0,0,0,0,0,0,0,0,0,0,
		   0,0,0,0,0,0,0,0,0,0,
		   0,0,0,0,0,0,0,0];

var sum_total = [];

var initial_sunday_date = ""
var convert_back_start = ""
var convert_back_end = ""
			
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

	var prefertime = [];
	var deadlinedays = get_deadline_days(eventId);
	var meetup_duration = parseInt(moment(eventId.length).format('HHmm'))/50;
	var member_count = eventId.length;

	var weight;
	
	var cur_seq_count = 0
	var new_seq_start = 0
	var old_seq_start = 0

	//this converts responses from YYYY-MM-DD hh:mm into just hhmm for free slots calc
for(var a = 0; a < eventId.responses.length; a++){
	for(b = 0; b < eventId.responses[a].timeslots.length; b++){

		
		//start and end rn in the form of YYYY-MM-DD hh:mm
		//need to convert to hhmm
		//then need to approx to hh
		temp_start = eventId.responses[a].timeslots[b].startTime;
		temp_end = eventId.responses[a].timeslots[b].endTime;

		temp_day = moment(temp_start).format('LL');
	
		temp_start = moment(temp_start).format('HHmm');
		temp_end = moment(temp_end).format('HHmm');
		
		temp_min = parseInt(moment(temp_start).format('mm'))
			if(temp_min < 15){
				//round down to 2:00
				temp_start = moment(temp_start).format('HH') + '00'
			}else if(temp_min < 30){
				//no round keep it 2:30
				temp_start = moment(temp_start).format('HH') + '50'
			}else if(temp_min < 45){
				temp_start = moment(temp_start).format('HH') + '50'
			}else{
				temp_start = moment(temp_start).add(1,'hours').format('HH') + '00'
				//round up to 3:00
			}

		temp_min = parseInt(moment(temp_end).format('mm'))
		if(temp_min < 15){
			//round down to 2:00
			temp_end = moment(temp_end).format('HH') + '00'
		}else if(temp_min < 30){
			//no round keep it 2:30
			temp_end = moment(temp_end).format('HH') + '50'
		}else if(temp_min < 45){
			temp_end = moment(temp_end).format('HH') + '50'
		}else{
			temp_end = moment(temp_end).add(1,'hours').format('HH') + '00'
			//round up to 3:00
		}

		day = new Date(temp_day)
		if(day.getDay() == 0){
			initial_sunday_date = moment(temp_day).format('YYYY-MM-DD');
		}
		//adds slots from a day in week to that specific day in week
		//get.Day() returns day of week, so corresponding start and end time gets stored correctly
		//temp[0] is Sunday, temp[1] is Monday, etc
		temp[day.getDay()].push([parseInt(temp_start),parseInt(temp_end)]);

		//temp will look like this 
		//temp[[[SUNDAY_start1,SUNDAY_end1],[SUNDAY_start2,SUNDAY_end2]],
		//		[MONDAY_start1,MONDAY_end1],[MONDAY_start2,MONDAY_end2], [TUESDAY slots], [WEDENSDAY SLOTS]...]
	}
}

	//create sum total for all days in the week
	for(var a = 0; a < deadlinedays; a++){
		sum_total[a].push(sum);
	}

	// incrementing through each user to get each user's start/end time
for(var day_count = 0; day_count < deadlinedays; day_count++){
	for(var x = 0; x < temp[day_count].length; x++){

		//index 0 is start time, 1 is end time
		starttime[x] =  ((temp[day_count])[x])[0];  //number like 250 == 2:30, 650 == 6:30
		starttime[x] = starttime[x] / 50;
		endtime[x] = ((temp[day_count])[x])[1];
		endtime[x] = endtime[x] / 50;

		//need this to calc intervals
        starttoend[x] = endtime[x] - starttime[x];
    }

	//increment the intervals of meet up time
	for(var y = 0; y < starttoend.length; y++){
			while(starttoend[y] >= 0){
				(sum_total[day_count])[starttime[y]] += 1;
				starttoend[y]-= 0.5;
				starttime[y]++;
			}
	}


// console.log(sum)
//weight calc => (available ppl / total) * 0.75

for(var i = 0; i < sum_total[day_count].length; i++){

	var temp_num = (sum_total[day_count])[i]

	if(temp_num == (sum_total[day_count])[i+1]){
		//start new sequence
		temp_num = (sum_total[day_count])[i+1]
		cur_seq_count++; 
		old_seq_start = i 

	}else{
		new_seq_start = old_seq_start - cur_seq_count + 1
		temp_num = (sum_total[day_count])[i+1]
		//end sequence
		test_array.push({
			size: cur_seq_count + 1,
			num: (sum_total[day_count])[i-1],
			start: new_seq_start
		})
		cur_seq_count = 0
	}
}

//console.log(test_array);

for(var i = 0; i < test_array.length; i++){	
	if(test_array[i].num > member_count/2 && test_array[i].size > meetup_duration){
		result_array.push({
			size: test_array[i].size,
			num: test_array[i].num,
			start: test_array[i].start})
	}
}


for(var i = 0; i < result_array.length; i++){
//converting integer back to strings for return
if((parseInt(result_array[i].start) % 2) != 0){
	convert_back_start = moment(initial_sunday_date).add(day_count,'days').format('YYYY-MM-DD') + ' ' + result_array[0].start/2 - 0.5 + ':30'
}else{
	convert_back_start = moment(initial_sunday_date).add(day_count,'days').format('YYYY-MM-DD') + ' ' + result_array[0].start/2 + ':00'
}

if((parseInt(result_array[0].start) + parseInt(result_array[0].size) - 1)%2 != 0){
	convert_back_end = moment(initial_sunday_date).add(day_count,'days').format('YYYY-MM-DD') + ' ' + (parseInt(result_array[0].start) + parseInt(result_array[0].size) - 1)/2 - 0.5 + ':30'
}else{
	convert_back_end = moment(initial_sunday_date).add(day_count,'days').format('YYYY-MM-DD') + ' ' + (parseInt(result_array[0].start) + parseInt(result_array[0].size) - 1) /2  + ':00'
}

weight = parseInt(result_array[i].num) / member_count * 0.75;
// //store the result into JSON object
// prefertime.responses[1].timeslots[day_count].push(convert_back_start);
// prefertime.responses[1].timeslots[day_count].push(convert_back_end);

prefertime.push({start_time: moment(convert_back_start).unix(),
				end_time: moment(convert_back_end).unix(),
				weight: weight
				});
			}//result_array loop ends here
}//daycount loop ends here


// return set;
return prefertime
}



function get_deadline_days(eventId){
	var deadline_date =  event.deadline;

    var today = new Date();
    var cur_date = today.getFullYear()+'-'+(today.getMonth()+1)+'-'+today.getDate();

    deadline_date = parseDate(deadline_date);
    cur_date = parseDate(cur_date);

    //this gets how much time between today and deadline
    var diffintime = deadline_date.getTime() - cur_date.getTime();

    //this converts the time ^^^ to days
    var deadline_days = (diffintime / (1000 * 3600 * 24));

	return deadline_days;
}


module.exports = {
	calculateBestTimeslot
}

