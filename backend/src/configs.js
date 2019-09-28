const fs = require('fs')

function loadConfigs(){
    env = process.env.ENV || "development"
    configs = {}
    configs['key'] = fs.readFileSync(`${env}.key`)
    configs['cert'] = fs.readFileSync(`${env}.cert`)
    return configs
}

module.exports = {
    loadConfigs: loadConfigs
}