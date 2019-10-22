const mongoose = require('mongoose')

var mongoClient

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
    mongoClient.close()
}

module.exports = {
    init: init,
    deinit: deinit
}

