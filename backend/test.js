// const MongoClient = require('mongodb').MongoClient;
// const uri = "mongodb+srv://jarvis:123123123@jarvis-kanro.mongodb.net/test?retryWrites=true&w=majority";
// const client = new MongoClient(uri, { useNewUrlParser: true });

// client.connect(err => {
//   if(err) {
//     console.log(err);
//   } else {
//     console.log("hello yes i've connected");
//   }
//   client.close();
// });

const EventModel = require('./src/data/schema').EventModel

var event1 = new EventModel({start_time: "13:00"})

console.log(event1.start_time)

console.log(event1.end_time)

event1.end_time = "lol"
console.log(event1.end_time)