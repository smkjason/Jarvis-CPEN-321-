
const axios = require('axios')
let schema = require('../data/schema')
const configs = require('../../server')
const {OAuth2Client} = require('google-auth-library')
const client = new OAuth2Client(configs.FRONT_END_CLIENT_ID)

const User = schema.UserModel

function getUser(name, key){
    //TODO: verify google key
    console.log("success! getting user!")
    return User.findOne({name: name})
}

//will have a return json
async function authCreateUser(json){
    return await verifyAndRetrieveToken(json.idToken, json.code)
}

/*
    verifies token, creates user if it doesn't exist, and returns user
*/
async function verifyAndRetrieveToken(token, code){
    var ret = await await client.verifyIdToken({
        idToken: token,
        audience: configs.FRONT_END_CLIENT_ID
    })

    payload = ret.getPayload()

    //check if this user exists already
    user = await User.findOne({email: payload.email}).exec()
    if(user.email){
        return user;
    } else {
        var response = await axios.post('https://www.googleapis.com/oauth2/v4/token', {
            code: code,
            client_id: configs.client_id,
            client_secret: configs.client_secret,
            grant_type: 'authorization_code refresh_token'
        })
        if(response.status != "200") throw {err: "error authenticating token"}

        var user = new User({
            email: payload.email,
            refresh_token: response.data.refresh_token,
            google_token: response.data.access_token,
            expires: response.data.expires_in + Date.now()
        })
        await user.save()
        return user
    }
}


module.exports = {
    authCreateUser,
    getUser,
}
