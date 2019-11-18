var io = require('socket.io')
const auth = require('../util/google').auth
const User = require('../data/schema').UserModel
const Event = require('../data/schema').EventModel
const Chat = require('../data/schema').ChatModel
const EventFunctions = require('../app/event')

async function getMessages(eventid, email, tbefore){
    //check to see if user is part of this event first
    var event = await Event.findOne({id: eventid}).exec()
    if(!event || (event.creatorEmail != email && !event.attendees.includes(email))) return []

    console.log(eventid)
    var messages
    if(tbefore){
        messages = await Chat.find({event: eventid, timestamp: {$lt: tbefore}}).exec()
    } else {
        messages = await Chat.find({event: eventid}).exec()
    }
    return messages
}

/*
    verify idtoken
    retrieve user
    set up events handlers
    disconnect -> do nothing?
*/
function socketSetup(server){
    var base = io.listen(server)

    //and should have the email field
    base.on('connection', async function(socket){
        socket.on("authenticate", async function(data){
            console.log('authenticate event')
            console.log(data)
            //make it look like an http request
            var request = {
                headers: {},
                body: data
            }
            var email = await auth(request)
            if(email) socket.email = email

            //we will now set up all the other event messages
            await setupEvents(socket)
        })
        socket.email = null
    })

    base.on("test", function(data){
        socket.emit("test_echo", data)
    })
}

/*private functions ----------------- */
async function setupEvents(socket){
    var user = await User.findOne({email: socket.email}).exec()

    if(user && user.email){
        //register all event handlers
        var events = await EventFunctions.relatedEvents(user.email)
        for(const event of events){
            console.log('registering event handler with id: ' + event.id)

            socket.on(event.id + ".send", async function(data){
                console.log("MESSAGE RECEIVED")
                console.log(data)
                console.log(data.message)
                data.timestamp = (new Date()).getTime()
                data.event = event.id
                data.sender = socket.email

                //save this chat
                var chat = new Chat(data)
                await chat.save()
                console.log("saved chat, time to emit message")

                socket.emit(event.id + ".message", data)
            })
        }
    } else {
        socket.emit('error', {msg: 'user does not have a record in the db'})
    }
}

module.exports = {
    socketSetup,
    getMessages
}