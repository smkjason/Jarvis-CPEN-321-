const mongoose = require('mongoose')

function init(){
    var dbString = process.env.ENV == "production" ? 
        "mongodb+srv://jarvis:123123123@jarvis-kanro.mongodb.net/prod" : 
        "mongodb+srv://jarvis:123123123@jarvis-kanro.mongodb.net/test";

    console.log(dbString)

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

function deinit(){
    mongoose.disconnect()
}

module.exports = {
    init: init,
    deinit: deinit
}

