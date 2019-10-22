//dependencies
const config = require("./src/configs")
const routes = require("./src/routes")
const data = require("./src/data/db")

const express = require("express")
const https = require("https")
const bodyParser = require('body-parser')

const app = express()

//parse json
app.use(bodyParser.json())
//init rest api routes
routes.routes(app)

//load the configs, and then configure everything
configs = config.loadConfigs()

data.init(configs)

https.createServer(configs, app).listen(443, () => {
    console.log("listening...")
})

app.listen(3000)