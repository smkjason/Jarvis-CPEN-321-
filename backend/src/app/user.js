let schema = require('../data/schema')
var pbkdf2 = require('pbkdf2')
let uuid = require('uuid/v1')

const User = schema.UserModel

function getUser(name, key){
    //TODO: verify google key
    console.log("success! getting user!")
    return User.findOne({name: name})
}

function refreshUser(username, pw){
    return verifyPw(username, pw)
    // return new Promise(function(resolve, reject){
    //     console.log('about to verify pw!')
    //     if(user = verifyPw(username, pw)){
    //         console.log("pw verified! time to refresh password!")
    //         //TODO: refresh password
    //         resolve(user)
    //     } else {
    //         reject({err: "error occurred"})
    //     }
    // })
}

//will have a return json
function createUser(json){
    //error check if name already exists
    //verify user googleAPI token
    if(!json.google_token && !json.refresh_token) 
        return new Promise(function(_, reject){reject({err: "no tokens!"})})
    let salt = uuid().substring(0, 10)
    pw_hash = pbkdf2.pbkdf2Sync(json.pw, salt, 2, 32)
    json.pw_hash = pw_hash
    json.salt = salt
    let user = new User(json)
    return user.save()
}

//returns user on success, else returns null
function verifyPw(name, pw){
    return new Promise(function(resolve, reject){
        User.findOne({name: name})
            .then(function(user){
                if(user.pw_hash == pbkdf2.pbkdf2Sync(pw, user.salt, 2, 32)) 
                    return resolve(user)
                console.log("o no they don't match")
                reject(null)
            })
            .fail(function(err){
                reject(null)
            })
    })
}

module.exports = {
    createUser,
    getUser,
    refreshUser
}
