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
    loadConfigs: loadConfigs,
    FRONT_END_CLIENT_ID: '282394539630-tiq0ifoor03gv9maddnpbll3gae1iqum.apps.googleusercontent.com',
    CLIENT_ID: '282394539630-06ith7hfdo2cqeok6g487s0slqh7ah8c.apps.googleusercontent.com',
    CLIENT_SECRET: 'JveiHU5laxj_y1oDVKtcPWgP'
}