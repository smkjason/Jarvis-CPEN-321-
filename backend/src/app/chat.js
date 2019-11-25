var io = require('socket.io')
var middleware = require('socketio-wildcard')();
var base
const EventFunctions = require('../app/event')
const Google = require('../util/google')
const Models = require('../data/schema')
const Event = Models.EventModel
const Chat = Models.ChatModel

async function getMessages(eventid, email, tbefore){
    //check to see if user is part of this event first
    var event = await Event.findOne({id: eventid}).exec()
    if(!event || (event.creatorEmail != email && !event.attendees.includes(email))) return []

    var messages
    if(tbefore){
        messages = await Chat.find({event: eventid, timestamp: {$lt: tbefore}}).exec()
    } else {
        messages = await Chat.find({event: eventid}).exec()
    }
    return messages
}

function newEvent(event){
    console.log('attaching event handlers for new event')
    var people = event.attendees.concat([event.creatorEmail])
    var sockets = base.sockets.sockets
    console.log(people)
    for(const socketid in sockets){
        var socket = sockets[socketid]
        console.log('CHECKING CONNECTED SOCKET ' + socket.email)

        //this guy is needed
        if(people.includes(socket.email)){
            attachEvent(socket, event)
            console.log(`ATTACHED ${socket.email} TO NEW EVENT`)
            socket.emit('event', {msg: event.id})
        }
    }
}

/*
    verify idtoken
    retrieve user
    set up events handlers
    disconnect -> do nothing?
*/
function socketSetup(server){
    base = io.listen(server)
    base.use(middleware)

    //and should have the email field
    base.on('connection', async function(socket){
        socket.on('*', function(packet){
            console.log(`${socket.email} RECEIVED PACKET: `, packet)
        })

        socket.on('authenticate', async function(data){
            console.log('authenticate event')
            console.log(data)
            //make it look like an http request
            var request = {
                headers: {},
                body: data
            }
            var email = await Google.auth(request)
            if(email) socket.email = email

            //we will now set up all the other event messages
            await setupEvents(socket)
            //await sendEventNotifications(socket)
        })
        
        socket.on('test', function(data){
            socket.emit("test_echo", data)
            socket.broadcast.emit('test_echo', data)
        })
    })

    base.on("test", function(data){
        socket.emit("test_echo", data)
        socket.broadcast.emit('test_echo', data)
    })
}

/*private functions ----------------- */
async function setupEvents(socket){
    if(!socket.email) return socket.emit('error', {msg: 'user does not have a record in the db'})
    
    //   all event handlers
    var events = await EventFunctions.relatedEvents(socket.email)
    for(const event of events){
        attachEvent(socket, event)
    }
}

function attachEvent(socket, event){
    socket.on(event.id + '.send', async function(data){
        console.log('received message', data)
        data.timestamp = Date.now() / 1000
        data.event = event.id
        data.sender = socket.email

        //save this chat
        var chat = new Chat(data)
        await chat.save()
        
        socket.broadcast.emit(event.id + ".message", data)
        socket.emit(event.id + ".message", data)
    })
    console.log('registered event handler with ' + event.id + '.send for socket ' + socket.email)
}

module.exports = {
    socketSetup,
    getMessages,
    newEvent
}