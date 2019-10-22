const mongoose = require('mongoose')
const Schema = mongoose.Schema
const Mixed = Schema.Types.Mixed
const ObjectId = Schema.Types.ObjectId
let validator = require('validator')

let eventSchema = new Schema({
    start_time: Mixed,
    end_time: Mixed,
    repeat_days: Array,
    repeat_until: Number,
    name: String,
    single_event: Boolean,
    user: String
})

let userModel = new Schema({
    name: String,
    google_key: String,
    id: ObjectId
})

module.exports = {
    EventModel: mongoose.model("EventModel", eventSchema),
    UserModel: mongoose.model("UserModel", userModel)
}