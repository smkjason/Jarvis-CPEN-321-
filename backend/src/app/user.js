
const axios = require('axios')
let schema = require('../data/schema')
const configs = require('../configs')

const EventFunctions = require('./event')
const User = schema.UserModel

async function getUsers(query){
    var users = await User.find({email: {$regex: query, $options: 'i'}})
        .select(['-refresh_token', '-google_token']).exec()
    return users
}

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
            grant_type: 'authorization_code',
            access_type: 'offline'
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

/*
    get all invited events
*/
async function invitedEvents(email){
    var user = await User.findOne({email: email}).exec()
    if(!user) return {error: `${email} does not exist`, status: "error"}

    var toRespond = []
    var tevents = await EventFunctions.relatedTEvents(email)
    for(const event of tevents){
        if(event.creatorEmail == email) continue

        var responseEmails = event.responses.map(function(res){return res.email})
        if(!responseEmails.includes(email)) toRespond.push(event)
    }
    return {events: toRespond}
}

/*
    update location
*/
async function updateLocation(email, location){
    var user = await User.findOne({email: email}).exec()
    if(!user) return {status: "error", error: "user not found"}

    user.lat = location.lat
    user.lon = location.lon
    await user.save()
    return {status: "success"}
}

async function getFriends(email){
    var user = await User.findOne({email: email}).exec()
    return user.friends
}

async function removeFriend(email, friend){

}

async function addFriend(email, friend){

}

module.exports = {
    authCreateUser,
    getUser,
    getFriends, 
    removeFriend, 
    addFriend,
    getUsers,
    updateLocation,
    invitedEvents
}
