
const axios = require('axios')
let schema = require('../data/schema')
const configs = require('../configs')

const EventFunctions = require('./event')
const User = schema.UserModel

async function getUser(name){
    var user = await User.findOne({email: name}).exec()
    //TODO: move this to its own path
    var events = await EventFunctions.syncEvents(user)
    return events
}

//will have a return json
async function authCreateUser(email, code){
    return await verifyAndRetrieveToken(email, code)
}

/*
    verifies token, creates user if it doesn't exist, and returns user
*/
async function verifyAndRetrieveToken(email, code){
    user = await User.findOne({email: email}).exec()
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
            email: email,
            name: email,
            refresh_token: response.data.refresh_token,
            google_token: response.data.access_token
        })
        console.log({msg: 'created user', user: user})
        try{
            await user.save()
            await EventFunctions.syncEvents(user)
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
