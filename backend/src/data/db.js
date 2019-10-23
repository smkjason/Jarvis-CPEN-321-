const mongoose = require('mongoose')

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

module.exports = {
    init: init,
    deinit: deinit
}

