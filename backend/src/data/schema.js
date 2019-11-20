const mongoose = require('mongoose')
const Schema = mongoose.Schema
const Mixed = Schema.Types.Mixed

let eventModel = new Schema({
    status: String,
    created: String,
    updated: String,
    location: String,
    colorId: String,
    creatorEmail: {type: String, index: true},
    start: Mixed,
    end:  Mixed,
    attendees: [String],
    recurrence: Array,
    id: {type: String, index: true},
    summary: String,
    description: String,
    googleEvent: Boolean
})

let tentativeEventModel = new Schema({
    length: String,
    invitees: [String],
    deadline: String,
    name: String,
    responses: [Mixed]
})

let userModel = new Schema({
    name: String,
    google_token: String,
    refresh_token: String,
    email: {type: String, index: true},
    friends: [String]
})

let chatModel = new Schema({
    message: String,
    timestamp: Number,
    sender: String,
    event: {type: String, index: true}
})

module.exports = {
    EventModel: mongoose.model('EventModel', eventModel),
    UserModel: mongoose.model('UserModel', userModel),
    ChatModel: mongoose.model('ChatModel', chatModel),
    TentativeEventModel: mongoose.model('TentativeEventModel', tentativeEventModel),
}