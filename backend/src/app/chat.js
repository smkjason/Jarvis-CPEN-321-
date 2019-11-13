var io = require('socket.io')
const auth = require('../util/google').auth

function socketSetup(server){
    var base = io.listen(server)
    base.use(function(socket, next){
        console.log("connection data: " + JSON.stringify(socket.handshake.query))
        
    })
}