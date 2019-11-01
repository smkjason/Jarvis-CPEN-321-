const UserFunctions = require('./app/user')
const EventFunctions = require('./data/event')

function routes(app){
    //all our routes can go here
    app.get("/", function(_, res) {
        res.send("Hello World! This is the backend for the calendar app")
    })

    app.get("/env", function(_, res){
        res.send(process.env.ENV)
    })

    //get the user, with a valid api token
    //returns 401 if token expired
    app.get("/user/:name", function(req, res){
        resolvePromise(UserFunctions.getUser(req.params.name, req.query.google_key), res)
    })

    app.post("/user", async function(req, res){
        try {
            var user = await UserFunctions.authCreateUser(req.body)
            res.send(user)
        } catch(err) {
            res.status(500)
            res.send({err: err})
        }
    })

    app.post("/demo_calculate_times", function(req, res){
        res.send("lol wtf")
    })
}

module.exports = {
    routes: routes
}