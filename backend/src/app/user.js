
const axios = require('axios')
let schema = require('../data/schema')
const configs = require('../configs')
const {OAuth2Client} = require('google-auth-library')
const client = new OAuth2Client(configs.CLIENT_ID)
const EventFunctions = require('../data/event')

const User = schema.UserModel

async function getUser(name){
    var user = await User.findOne({email: name}).exec()
    //TODO: move this to its own path
    var events = await EventFunctions.uploadEvents(user)
    return events
}

//will have a return json
async function authCreateUser(json){
    return await verifyAndRetrieveToken(json.idToken, json.code)
}

/*
    verifies token, creates user if it doesn't exist, and returns user
*/
async function verifyAndRetrieveToken(token, code){
    var ret = await client.verifyIdToken({
        idToken: token,
        audience: configs.CLIENT_ID
    })
    payload = ret.getPayload()

    if(!payload.email) {
        console.log('email not gotten')
        throw {err: 'error authenticating id token'}
    }
    //check if this user exists already
    user = await User.findOne({email: payload.email}).exec()
    if(user && user.email){
        return user;
    } else {
        var response = await axios.post('https://www.googleapis.com/oauth2/v4/token', {
            code: code,
            client_id: configs.CLIENT_ID,
            client_secret: configs.CLIENT_SECRET,
            grant_type: 'authorization_code'
        })

        var user = new User({
            email: payload.email,
            name: payload.email,
            refresh_token: response.data.refresh_token,
            google_token: response.data.access_token
        })
        console.log({msg: 'created user', user: user})
        try{
            await user.save()
        } catch(err){
            console.log(err)
        }
        return user
    }
}


module.exports = {
    authCreateUser,
    getUser,
}
