const mongoose = require('mongoose')
const Schema = mongoose.Schema
const Mixed = Schema.Types.Mixed
const Id = Schema.Types.ObjectId

let eventModel = new Schema({
    status: String,
    created: String,
    updated: String,
    location: String,
    colorId: String,
    creatorEmail: {type: String, index: true},
    start: Mixed,
    end:  Mixed,
    attendees: Mixed,
    recurrence: Array,
    id: String,
    summary: String,
    description: String
})

let userModel = new Schema({
    name: String,
    google_token: String,
    refresh_token: String,
    email: {type: String, index: true}
})

let chatModel = new Schema({
    messsage: String,
    timestamp: Number,
    sender: String,
    chat: {type: Id, index: true}
})

module.exports = {
    EventModel: mongoose.model('EventModel', eventModel),
    UserModel: mongoose.model('UserModel', userModel),
    ChatModel: mongoose.model('ChatModel', chatModel)
}