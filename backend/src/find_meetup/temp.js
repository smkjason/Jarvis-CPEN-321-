<<<<<<< HEAD
var start_time = [[12,19],[13,20],[0,13]]
var end_time = [[15,23],[16,23],[12,23]]
var start_to_end = []

var start = []
var end = []
var total = 2

var sum = [0,0,0,0,0,0,0,0,0,0,
    0,0,0,0,0,0,0,0,0,0,
    0,0,0,0]

loop1:
for(var x = 0; x < 3; x++){
	console.log(x)
	start = start_time[x];
	end = end_time[x];
    console.log(start)
    console.log(end)

	start_to_end = end.map(function(item, index){
		return item - start[index];
    })
    
    console.log("start to end is" + start_to_end)
	loop2:
	//increment the intervals of meet up time
	for(var y = 0; y < start_to_end.length; y++){
			while(start_to_end[y] >=0){
				sum[start[y]] += 1;
				console.log(sum)
				start_to_end[y]--;
				start[y] += 1;
				console.log("(INSIDE) start to end is" + start_to_end)
				// if(start_to_end[y] < 0){
				// 	break loop2;
				// }
			}
		// if(y = start_to_end.length){
		// 	break loop1;
		// }
	}
 }
console.log(start_to_end)
console.log(sum)
=======
const mongoose = require('mongoose')

function init(){
    var dbString = process.env.ENV == "production" ? 
        "mongodb+srv://jarvis:123123123@jarvis-kanro.mongodb.net/prod" : 
        "mongodb+srv://jarvis:123123123@jarvis-kanro.mongodb.net/test";

    mongoose.connect(dbString, {
        useNewUrlParser: true
    })
        .then(
            console.log("successfully connected")
        )
        .catch(err => {
            console.log("err occurred!")
        })
}

init();



>>>>>>> 537a80856f00e98d6f472a4cb0837c685b0d4dfc
