const config = require("./src/configs")
const routes = require("./src/routes")

const express = require("express")
const https = require("https")
const app = express()

routes.routes(app)

configs = config.loadConfigs()

https.createServer(configs, app).listen(443, () => {
    console.log("listening...")
})

app.listen(3000)