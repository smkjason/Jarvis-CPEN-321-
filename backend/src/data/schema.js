const mongoose = require('mongoose')
const Schema = mongoose.Schema
const Mixed = Schema.Types.Mixed

let eventModel = new Schema({
    // start_time: Mixed,
    // end_time: Mixed,
    // repeat_days: Array,
    // repeat_until: Number,
    // name: String,
    // single_event: Boolean,
    // owner: String,
    // google: Boolean
    status: String,
    created: String,
    updated: String,
    location: String,
    colorId: String,
    creatorEmail: String,
    start: Mixed,
    end:  Mixed,
    attendees: Mixed
})

let userModel = new Schema({
    name: String,
    google_token: String,
    refresh_token: String,
    email: String
})

module.exports = {
    EventModel: mongoose.model("EventModel", eventModel),
    UserModel: mongoose.model("UserModel", userModel)
}