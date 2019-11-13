const {OAuth2Client} = require('google-auth-library')
const google = require('googleapis')
const configs = require('../configs')

function getUserCalendar(user){
    var client = new OAuth2Client({clientId: configs.CLIENT_ID, clientSecret: configs.CLIENT_SECRET})
    client.setCredentials({
        refresh_token: user.refresh_token, 
        access_token: user.google_token,
    })
    return new google.calendar_v3.Calendar({version: 'v3', auth: client})
}

async function auth(req, name = null){
    var client = new OAuth2Client(configs.CLIENT_ID)
    var token = req.headers.authorization
    token = token || req.body.idToken
    token = token.replace(/(B|b)earer /, '')

    var ret = await client.verifyIdToken({
        idToken: token,
        audience: configs.CLIENT_ID
    })
    console.log('authenticated request')
    var payload = ret.getPayload()
    if(!payload.email) throw {err: 'payload does not have an id token!'}
    if(name && name != payload.email) throw {err: 'payload email does not match request'}

    return payload.email
}

module.exports = {
    getUserCalendar,
    auth
}