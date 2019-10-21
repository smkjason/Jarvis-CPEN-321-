const MongoClient = require('mongodb').MongoClient;
const uri = "mongodb+srv://jarvis:123123123@jarvis-kanro.mongodb.net/test?retryWrites=true&w=majority";
const client = new MongoClient(uri, { useNewUrlParser: true });

client.connect(err => {
  if(err) {
    console.log(err);
  } else {
    console.log("hello yes i've connected");
  }
  client.close();
});