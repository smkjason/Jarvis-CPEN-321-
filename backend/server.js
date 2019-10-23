//dependencies
const config = require("./src/configs")
const routes = require("./src/routes")
const data = require("./src/data/db")

const express = require("express")
const https = require("https")
const bodyParser = require('body-parser')

const app = express()

const CONFIGS = {
    FRONT_END_CLIENT_ID: '282394539630-tiq0ifoor03gv9maddnpbll3gae1iqum.apps.googleusercontent.com',
    CLIENT_ID: '282394539630-06ith7hfdo2cqeok6g487s0slqh7ah8c.apps.googleusercontent.com',
    CLIENT_SECRET: 'JveiHU5laxj_y1oDVKtcPWgP'
}
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

module.exports = {
    CONFIGS: CONFIGS
}