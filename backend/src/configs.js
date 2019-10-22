const fs = require('fs')

function loadConfigs(){
    env = process.env.ENV || "development"
    configs = {}
    configs['key'] = fs.readFileSync(`${env}.key`)
    configs['cert'] = fs.readFileSync(`${env}.cert`)
    //configs['mongodbConnString'] = "mongodb+srv://jarvis:123123123@jarvis-kanro.mongodb.net/test"
    return configs
}

module.exports = {
    loadConfigs: loadConfigs
}