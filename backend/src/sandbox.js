const mongoose = require('mongoose')
const EventModel = require('./data/schema').EventModel

function init(config){
    mongoose.connect("mongodb+srv://jarvis:123123123@jarvis-kanro.mongodb.net/test", {
        useNewUrlParser: true
    })
        .then(
            console.log("successfully connected")
        )
        .catch(err => {
            console.log("err occurred!")
        })
}

function deinit(){
    mongoose.disconnect()
}

function populateDummyData(){
    for(var i = 0; i < 5; i++){
        var event = new EventModel({
            start_time: `${Math.floor(Math.random()%6)}:00`,
            end_time: `${Math.floor(Math.random()%6+6)}:00`,
            name: "Steven"
        })
        event.save().catch(function(err){console.log(err + "third")})
    }

    for(var i = 0; i < 5; i++){
        var event = new EventModel({
            start_time: `${Math.floor(Math.random()%6)}:00`,
            end_time: `${Math.floor(Math.random()%6+6)}:00`,
            name: "Brad"
        })
        event.save().catch(function(err){console.log(err + "third")})
    }

    for(var i = 0; i < 5; i++){
        var event = new EventModel({
            start_time: `${Math.floor(Math.random()%6)}:00`,
            end_time: `${Math.floor(Math.random()%6+6)}:00`,
            name: "Rafael"
        })
        event.save().catch(function(err){console.log(err + "third")})
    }
}

function getData(){
    EventModel.find({})
        .then(function(items){
            console.log(items)
            deinit()
        })
        .catch(function(err){
            console.log(err)
            deinit()
        })
}




//actual code to be ran

init()
//populateDummyData()

getData()
//deinit()
