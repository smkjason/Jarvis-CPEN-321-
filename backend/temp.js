
var io = require('socket.io-client')

var socket = io('http://localhost:3000')

socket.on('login_response', function(data){
    console.log('response recieved:')
    console.log(data)
})
socket.on('invite', function(data){
    console.log(data)
})

socket.emit('authenticate', {message: 'something'})
