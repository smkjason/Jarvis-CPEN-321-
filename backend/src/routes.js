const UserFunctions = require('./app/user')
const EventFunctions = require('./app/event')
const ChatFunctions = require('./app/chat')
const auth = require('./util/google').auth

const test = require('./find_meetup/freetime').userFreeTime

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
        var user = await UserFunctions.authCreateUser(await auth(req), req.body)
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
        returns a user's events
    */
    app.get('/user/:email/events', async function(req, res){
        log(req)
        var events = await EventFunctions.getEvents(await auth(req, req.params.email))
        res.send(events)
    })

    /*
        returns events where user is admin
    */
    app.get('/user/:email/admin', async function(req, res){
        log(req)
        var events = await UserFunctions.getAdminEvents(await auth(req, req.params.email))
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
        gets all tentative invites for a user
    */
    app.get('/user/:email/invites', async function(req, res){
        log(req)
        var events = await UserFunctions.invitedEvents(await auth(req, req.params.email))
        res.send(events)
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
        updates a user's location
    */
    app.put('/user/:email/location', async function(req, res){
        log(req)
        var response = await UserFunctions.updateLocation(await auth(req, req.params.email), req.body)
        res.send(response)
    })

    /*
        view the status of an event
    */
    app.get('/events/:id', async function(req, res){
        log(req)
        var response = await EventFunctions.getEvent(req.params.id, await auth(req))
        //ChatFunctions.newEvent(response)
        res.send(response)
    })

    /*
        gets the locations of an event's attendees
    */
    app.get('/events/:id/locations', async function(req, res){
        log(req)
        var response = await EventFunctions.userLocations(req.params.id, await auth(req))
        res.send(response)
    })

    /*
        creates a real event out of a tentative one
    */
    app.post('/events/:id/activate', async function(req, res){
        log(req)
        var response = await EventFunctions.activateEvent(req.params.id, await auth(req, req.params.email), req.body)
        ChatFunctions.newEvent(response)
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

    /*
        returns available time slot for ALL invitees to attend 
    */
    app.get('/events/:id/preferred', async function(req,res){
        log(req)
        //var response = await EventFunctions.getPreferredTime(req.params.id, await auth(req, req.params.email))
        res.send({
            timeslots:[
                {
                    startTime: "2019-11-23 00:00",
                    endTime: "2019-11-23 01:00"
                },
                {
                    startTime: "2019-11-23 02:00",
                    endTime: "2019-11-23 03:00"
                },
                {
                    startTime: "2019-11-23 03:00",
                    endTime: "2019-11-23 04:00"
                },
                {
                    startTime: "2019-11-23 04:00",
                    endTime: "2019-11-23 05:00"
                },
                {
                    startTime: "2019-11-25 14:00",
                    endTime: "2019-12-31 23:59"
                }
            ]
        })
    })

    app.get('/test/:email', async function(req, res){
        res.send(await test(req.params.email, '2019-11-26'))
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