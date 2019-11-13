const UserFunctions = require('./app/user')
const EventFunctions = require('./app/event')
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
    app.get('/user/:name', async function(req, res){
        log(req)
        var user = await UserFunctions.getUser(await auth(req, req.params.name))
        res.send(user)
    })

    /*
        returns a user's events
    */
    app.get('/user/:name/events', async function(req, res){
        log(req)
        var events = await EventFunctions.getEvents(await auth(req, req.params.name))
        res.send(events)
    })

    /*
        creates a new user event, and saves to calendar
    */
    app.post('/user/:name/events', async function(req, res){
        log(req)
        var event = await EventFunctions.createEvent(await auth(req, req.params.name), res.body)
        res.send(event)
    })

    app.get('/demo_calculate_times', function(req, res){
        log(req)
        res.send('huh what is going on')
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