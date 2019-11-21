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
            await sendEventNotifications(socket)
        })
        socket.email = null
    })

    base.on("test", function(data){
        socket.emit("test_echo", data)
    })
}

/*private functions ----------------- */
async function setupEvents(socket){
    if(!socket.email) return socket.emit('error', {msg: 'user does not have a record in the db'})
    
    //register all event handlers
    var events = await EventFunctions.relatedEvents(socket.email)
    for(const event of events){
        console.log('registering event handler with id: ' + event.id)

        socket.on(event.id + ".send", async function(data){
            data.timestamp = (new Date()).getTime()
            data.event = event.id
            data.sender = socket.email

            //save this chat
            var chat = new Chat(data)
            await chat.save()
            
            socket.broadcast.emit(event.id + ".message", data)
        })
    }
}

async function sendEventNotifications(socket){
    if(!socket.email) return socket.emit('error', {msg: 'user does not have a record in the db'})

    var tevents = await EventFunctions.relatedTEvents(socket.email)
    for(const event of tevents){
        var responseEmails = event.responses.map(function(res){return res.email})
        if(!responseEmails.includes(socket.email)){
            console.log(`user ${socket.email} was invited to event ${event.name}`)
            //we need to send a notification for the user select a time
            socket.emit("invite", event)
        }
    }
    
}

module.exports = {
    socketSetup,
    getMessages
}