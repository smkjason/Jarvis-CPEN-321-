const mongoose = require('mongoose')
const Schema = mongoose.Schema
const Mixed = Schema.Types.Mixed
let validator = require('validator')

let eventSchema = new Schema({
    start_time: Mixed,
    end_time: Mixed,
    repeat_days: Array,
    repeat_until: Number,
    name: String,
    single_event: Boolean,
    owner: String,
    google: Boolean
})

let userModel = new Schema({
    name: String,
    google_token: String,
    refresh_token: String,
})

module.exports = {
    EventModel: mongoose.model("EventModel", eventSchema),
    UserModel: mongoose.model("UserModel", userModel)
}