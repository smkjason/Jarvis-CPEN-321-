const UserFunctions = require('./app/user')
const EventFunctions = require('./data/event')

function routes(app){
    //all our routes can go here
    app.get('/', function(req, res) {
        log(req)
        res.send('Hello World! This is the backend for the calendar app')
    })

    app.get('/env', function(req, res){
        log(req)
        res.send(process.env.ENV)
    })

    //get the user, with a valid api token
    //returns 401 if token expired
    app.get('/user/:name', async function(req, res){
        log(req)
        var user = await UserFunctions.getUser(req.params.name)
        res.send(user)
    })

    app.get('/user/:name/events', async function(req, res){
        log(req)
        var events = await EventFunctions.getEvents(req.params.name)
        res.send(events)
    })

    app.post('/user', async function(req, res){
        log(req)
        try {
            var user = await UserFunctions.authCreateUser(req.body)
            res.send(user)
        } catch(err) {
            res.status(500)
            res.send({err: err})
        }
    })

    app.get('/demo_calculate_times', function(req, res){
        log(req)
        res.send('UPDATE!!')
    })
}

function log(req){
    console.log({
        method: req.method,
        body: req.body,
        params: req.params,
        url: req.url
    })
}

module.exports = {
    routes: routes
}