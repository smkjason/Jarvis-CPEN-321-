const CONFIGS = require('../../server')
const OAuth2Client = require('google-auth-library').OAuth2Client
const client = new OAuth2Client(CONFIGS.FRONT_END_CLIENT_ID)

const axios = require('axios')
let schema = require('../data/schema')
var pbkdf2 = require('pbkdf2')


const User = schema.UserModel

function getUser(name, key){
    //TODO: verify google key
    console.log("success! getting user!")
    return User.findOne({name: name})
}

//will have a return json
function createUser(json){
    return new Promise(function(resolve, reject){
        verifyAndRetrieveToken(json.idToken, json.code)
            .then(function(userInfo){
                user = new User(userInfo)
                user.save().then(resolve).catch(reject)
            })
            .catch(reject)
    })
}

function verifyAndRetrieveToken(token, code){
    return new Promise(function(resolve, reject){
            client.verifyIdToken({
            idToken: token, 
            audience: FRONT_END_CLIENT_ID
        }).then(function(ticket){
            //ticket should be verified, lets retrieve the code now
            axios.post('https://www.googleapis.com/oauth2/v4/token', {
                code: code,
                cient_id: CONFIGS.CLIENT_ID,
                client_secret: CONFIGS.CLIENT_SECRET,
                grant_type: 'authorization_code'
            }).then(function(response){
                console.log(payload)
                //we did it! return all of the information in a json for our create user function to parse
                var payload = ticket.getPayload()
                resolve({
                    email: payload.sub,
                    refresh_token: response.data.refresh_token,
                    google_token: response.data.access_token,
                    expires: response.data.expires_in + Date.now()
                })
            }).catch(function(err){
                reject(err)
            })
        }).catch(function(err){reject(err)})
    })
}


module.exports = {
    createUser,
    getUser,
}
