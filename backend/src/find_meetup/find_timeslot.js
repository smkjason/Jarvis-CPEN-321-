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

// var set = [[],[],[],[],[],[],[]];

//24 hours 
var sum = [0,0,0,0,0,0,0,0,0,0,
		   0,0,0,0,0,0,0,0,0,0,
		   0,0,0,0,0,0,0,0,0,0,
		   0,0,0,0,0,0,0,0,0,0,
		   0,0,0,0,0,0,0,0];

var sum_total = [];

var initial_date = ""
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

	console.log(eventId)


	// var meetup_duration = parseInt(moment(eventId.length).format('HHmm')) / 50;
	var member_count = eventId.invitees.length + 1;
	console.log(member_count)
	console.log(deadlinedays)

	var weight;

	var test_array = [];
	var resulting_arr = [];

	var today = new Date();
	var cur_date = today.getFullYear()+'-'+(today.getMonth()+1)+'-'+today.getDate();

	var temp_store = [];
	var old_day = parseInt(moment(cur_date).format('YYYYMMDD'))
	var nextday;
	initial_date = moment(eventId.responses[0].timeslots[0].startTime).format('YYYY-MM-DD')
	
	//this converts responses from YYYY-MM-DD hh:mm into just hhmm for free slots calc
	for(b = 0; b < eventId.responses[0].timeslots.length; b++){

		
		//start and end rn in the form of YYYY-MM-DD hh:mm
		//need to convert to hhmm
		//then need to approx to hh
		temp_start = eventId.responses[0].timeslots[b].startTime;
		temp_end = eventId.responses[0].timeslots[b].endTime;

		temp_day = moment(temp_start).format('YYYY-MM-DD');
		nextday = parseInt(moment(temp_day).format('YYYYMMDD'));

		countdays = get_days_between(cur_date, temp_day);
	
		// temp_start = moment(temp_start).format('HHmm');
		// temp_end = moment(temp_end).format('HHmm');
		
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
		
		console.log(nextday)
		console.log(old_day)
		//when moved onto another day, we add current date start/end time to calculation array
		if(nextday - old_day > 0){
			//adds slots from a date in respect to the earleist date
			temp[countdays] = temp_store;
			old_day = nextday;
		}

		temp_store.push([parseInt(temp_start),parseInt(temp_end)])

		if(countdays > deadlinedays){
			throw "COUNT DAYS EXCEEED DEADLINEDAYS, SOMETHING IS WRONG!"; 
		}

		//temp will look like this 
		//temp[[[start1,end1],[start2,end2]],
		//		[start1,end1],[start2,end2], 
				// [slots], 
				// [SLOTS]...]
	}


	//this fills in the empty days with all 0 
	//to avoid bugs
	for(var a = 0; a < temp.length; a++){
		if(temp[a] == null){
			temp[a] = [0,0];
		}
	}
	console.log("gets here")
	//create sum total for all days before the deadline
	for(var a = 0; a < deadlinedays; a++){
		sum_total[a] = (sum);
	}

	console.log("gets here2")
	console.log("CHECKING TEMP ARRAY")
	console.log(temp)

	// incrementing through each user to get each user's start/end time
for(var day_count = 0; day_count < deadlinedays; day_count++){
	console.log(eventId)

	starttoend = get_start_to_end(starttime, endtime, day_count, temp);

	console.log("LINE 184	 start to end is " + starttoend)
	console.log("day COUNT IS " + day_count)
	sum_total = get_sum_total_for_a_day(starttoend,starttime,sum_total,day_count);

	console.log(sum_total)

	//console.log("SUM TOTAL IS " + sum_total)
	console.log("gets here 191")

	// build_result_array(sum_total, day_count, test_array, member_count, meetup_duration);
	resulting_arr = build_result_array(sum_total, day_count, test_array,  eventId);


console.log(test_array);
console.log(test_array.length)

console.log(resulting_arr)
console.log("gets here217")

buildprefertime(convert_back_start, convert_back_end, resulting_arr, prefertime, day_count, eventId, weight)

}//daycount loop ends here

console.log(prefertime)
console.log(eventId)
// return set;
return prefertime
}








///////////////////////////*HELPER FUNCTIONS*/////////////////////////


function get_deadline_days(eventId){
	var deadline_date =  eventId.deadline;

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

function get_days_between(day1, day2){
	var day1_time = parseDate(day1);
	var day2_time = parseDate(day2);

	var diffintime = day2_time.getTime() - day1_time.getTime();

	var days_between = (diffintime) / (1000 * 3600 * 24);

	return days_between;
}

function parseDate(input) {
	var parts = input.match(/(\d+)/g);
	// new Date(year, month [, date [, hours[, minutes[, seconds[, ms]]]]])
	return new Date(parts[0], parts[1]-1, parts[2]); // months are 0-based
  }

  function get_sum_total_for_a_day(starttoend,starttime,sum_total,day_count){
	for(var y = 0; y <= starttoend.length; y++){
		console.log("starttime is " + starttime)
		edit_sum_total(starttoend, sum_total, starttime, day_count,y);
	}
	return sum_total;
  }

  function edit_sum_total(starttoend, sum_total, starttime, day_count,y){
	while(starttoend[y] > 0){
		(sum_total[day_count])[starttime[y]] += 1;
		starttoend[y]--;
		starttime[y]++;
	}
  }

  function get_start_to_end(starttime, endtime, day_count, temp){
  for(var x = 0; x < temp[day_count].length; x++){
	  //index 0 is start time, 1 is end time
	  starttime[x] =  ((temp[day_count])[x])[0];  //number like 250 == 2:30, 650 == 6:30
	  starttime[x] = starttime[x] / 50;
	  endtime[x] = ((temp[day_count])[x])[1];
	  endtime[x] = endtime[x] / 50;

	  //need this to calc intervals
	  starttoend[x] = endtime[x] - starttime[x]	;
	  console.log("starttoend array is " + starttoend)
	  console.log("daycount is " + day_count)
  }
  return starttoend;
  }

//   CHECKING TEMP ARRAY
//   console.log src/find_meetup/find_timeslot.js:164
// 	[ [],
// 	  [ [ 600, 1200 ], [ 800, 1200 ], [ 1000, 1200 ], [ 1100, 1200 ] ],


  function build_result_array(sum_total, day_count, test_array, eventId){

	var member_count = eventId.invitees.length + 1;
	var meetup_duration = parseInt(eventId.length) * 2;

	//  console.log(meetup_duration)
	
	var cur_seq_count = 0;
	var new_seq_start = 0;
	var old_seq_start = 0;

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
		  });
		  cur_seq_count = 0
	  }
  }
  var result_arr = []
  //checking for valid intervals
  for(var i = 0; i < test_array.length; i++){	
	if(test_array[i].num >= member_count/2 && test_array[i].size >= meetup_duration){
		result_arr.push({
			size: test_array[i].size,
			num: test_array[i].num,
			start: test_array[i].start})
		}
	}	

	return result_arr
}


function buildprefertime(convert_back_start, convert_back_end, resulting_arr, prefertime, day_count, eventId, weight){
var member_count = eventId.invitees.length + 1;
for(var i = 0; i < resulting_arr.length; i++){
//converting integer back to strings for return
if((parseInt(resulting_arr[i].start) % 2) != 0){
	convert_back_start = moment(initial_date).add(day_count - 1,'days').format('YYYY-MM-DD') + ' ' + resulting_arr[i].start/2 - 0.5 + ':30'
}else{
	convert_back_start = moment(initial_date).add(day_count - 1,'days').format('YYYY-MM-DD') + ' ' + resulting_arr[i].start/2 + ':00'
}
console.log("PARSING STUFF FOR START IS " + parseInt(resulting_arr[i].start))
console.log("PARSING STUFF FOR SIZE IS " + parseInt(resulting_arr[i].size))
// if(((resulting_arr[i].start) + (resulting_arr[i].size) - 1) % 2 != 0){
// 	convert_back_end = moment(initial_date).add(day_count - 1,'days').format('YYYY-MM-DD') + ' ' + ((resulting_arr[i].start) + (resulting_arr[i].size) - 1)/2 - 0.5 + ':30'
// }else{
// 	convert_back_end = moment(initial_date).add(day_count - 1,'days').format('YYYY-MM-DD') + ' ' + ((resulting_arr[i].start) + (resulting_arr[i].size) - 1) /2  + ':00'
// }
console.log(convert_back_start)
console.log(resulting_arr[i].size)
convert_back_end = moment(convert_back_start).add(resulting_arr[i].size/2, 'hours').format('YYYY-MM-DD HH:mm')

console.log(convert_back_end)
console.log("gets here234")
// input time has field creatoremail, but user freetime slots does not have it
// differentiate weight by checking for this field

if(eventId.hasOwnProperty('creatorEmail') ){
	weight = resulting_arr[i].num / member_count  * 0.75;
}else{
	weight = resulting_arr[i].num /member_count * 0.25;
}
console.log(prefertime)
prefertime.push({startTime: moment(convert_back_start).unix(),
				endTime: moment(convert_back_end).unix(),
				weight: weight
				});
			}
}



module.exports = {
	calculateBestTimeslot
}

