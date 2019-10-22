const MongoClient = require('mongodb').MongoClient
const mongoose = require('mongoose')

var mongoClient
var mongodbConnString

function init(config){
    // mongodbConnString = config.mongodbConnString
    // mongoClient = new MongoClient(mongodbConnString, {
    //     useNewUrlParser: true
    // })
    // mongoClient
    // mongoClient.connect()
    //     .then(
    //         console.log("successfully connected")
    //     )
    //     .catch(err => {
    //         console.log("err occurred!")
    //     })
    
    mongoose.connect("mongodb+srv://jarvis:123123123@jarvis-kanro.mongodb.net/test")
        .then(
            console.log("successfully connected")
        )
        .catch(err => {
            console.log("err occurred!")
        })
}

function deinit(){
    mongoClient.close()
}

module.exports = {
    init: init,
    deinit: deinit
}

