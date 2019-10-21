const MongoClient = require('mongodb').MongoClient

var mongo_client
var mongodb_conn_string

function init(config){
    mongodb_conn_string = config.mongodb_conn_string
    mongo_client = new MongoClient(mongodb_conn_string, {
        useNewUrlParser: true
    })
    mongo_client
    mongo_client.connect()
        .then(
            console.log("successfully connected")
        )
        .catch(err => {
            console.log("err occurred!")
        })
}

function deinit(){
    mongo_client.close()
}

module.exports = {
    init: init,
    deinit: deinit
}

