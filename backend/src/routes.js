const UserFunctions = require('./app/user')
const EventFunctions = require('./app/event')
const ChatFunctions = require('./app/chat')
const auth = require('./util/google').auth

function routes(app){
    //all our routes can go here
    app.get('/', function(req, res) {
        log(req)
        res.send('Hello World! This is the backend for the calendar app')
    })

    /*
        checks the environment
    */
    app.get('/env', function(req, res){
        log(req)
        res.send(process.env.ENV)
    })

    /*
        gets all users
    */
    app.get('/user', async function(req, res){
        log(req)
        var users = await UserFunctions.getUsers(req.query.q)
        res.send(users)
    })

    /*
        creates a new user if does not exist
    */
    app.post('/user', async function(req, res){
        log(req)
        var user = await UserFunctions.authCreateUser(await auth(req), req.body.code)
        res.send(user)
    })

    /*
        returns a user's events
    */
    app.get('/user/:email', async function(req, res){
        log(req)
        var user = await UserFunctions.getUser(await auth(req, req.params.email))
        res.send(user)
    })

    /*
        see friends
    */
   app.get('/user/:email/friends', async function(req, req){
       log(req)
       var friends = await UserFunctions.getFriends(auth(req, req.params.email))
       res.send(friends)
   })

    /*
        returns a user's events
    */
    app.get('/user/:email/events', async function(req, res){
        log(req)
        var events = await EventFunctions.getEvents(await auth(req, req.params.email))
        res.send(events)
    })

    /*
        creates a new tentative event
    */
    app.post('/user/:email/events', async function(req, res){
        log(req)
        var event = await EventFunctions.createEvent(await auth(req, req.params.email), req.body)
        res.send(event)
    })

    /*
        allows a user to respond to an event
    */
    app.put('/user/:email/events/:id', async function(req, res){
        log(req)
        var response = await EventFunctions.respondEvent(req.params.id, await auth(req, req.params.email), req.query.declined, req.body)
        res.send(response)
    })

    /*
        creates a real event out of a tentative one
    */
   app.post('/events/:id/activate', async function(req, res){
       log(req)
       var response = await EventFunctions.activateEvent(req.params.id, await auth(req, req.params.email), req.body)
       res.send(response)
   })

    /*
        returns all the chat messages for a given event before a certain time
    */
    app.get('/events/:id/messages', async function(req, res){
        log(req)
        var msgs = await ChatFunctions.getMessages(req.params.id, await auth(req), req.query.before)
        res.send(msgs)
    })
}

function log(req){
    console.log({
        headers: req.headers,
        method: req.method,
        body: req.body,
        params: req.params,
        url: req.url
    })
}

module.exports = {
    routes: routes
}