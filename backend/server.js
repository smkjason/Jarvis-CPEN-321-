//dependencies
const config = require('./src/configs')
const routes = require('./src/routes')
const data = require('./src/data/db')
const setupChat = require('./src/app/chat').socketSetup

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
setupChat(server)

server.listen(3000)

