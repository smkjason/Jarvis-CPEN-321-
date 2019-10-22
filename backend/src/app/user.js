let models = require('../data/mongoose')
const User = models.UserModel

//will have a return json
function createTestUser(json){
    let user = new User(json)
    return user.save()
}

module.exports = {
    createTestUser
}
