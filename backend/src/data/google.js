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

function verifyRequest(req){
    const client = new OAuth2Client(configs.CLIENT_ID)
}

module.exports = {
    getUserCalendar
}