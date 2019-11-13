
var io = require('socket.io-client')

var socket = io('http://ec2-3-14-144-180.us-east-2.compute.amazonaws.com/')

socket.on('login_response', function(data){
    console.log('response recieved:')
    console.log(data)
})

socket.emit('login', {message: 'hello world'})