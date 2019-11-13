//dependencies
const config = require('./src/configs')
const routes = require('./src/routes')
const data = require('./src/data/db')

const express = require('express')
const https = require('https')
const http = require('http')
const bodyParser = require('body-parser')

const app = express()


//parse json
app.use(bodyParser.json())
//init rest api routes
routes.routes(app)

data.init()

// https.createServer(configs, app).listen(443, () => {
//     console.log("listening...")
// })

var server = http.createServer(app);
var io = require('socket.io').listen(server)

io.on('connection', function(socket){
    console.log("new connection!!!")

    socket.on('login', function(data){
        console.log(data)
        socket.emit('login_response', data)
    })

    socket.on('join', function(data){
        console.log(data)
    })

    socket.on('send_msg', function(data){
        console.log(data)
        socket.emit('receive_msg', {message: (data.name + ": " + data.message)})
    })
})


server.listen(3000)

