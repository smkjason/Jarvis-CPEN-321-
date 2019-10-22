const UserFunctions = require('./app/user')

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

    //update the user google api token, need password for that
    app.put("/refresh_user/:name", function(req, res){
        resolvePromise(UserFunctions.refreshUser(req.params.name, req.body.pw), res)
    })

    app.post("/user", function(req, res){
        resolvePromise(UserFunctions.createUser(req.body), res)
    })
}

function resolvePromise(promise, res){
    promise
        .then(function(user){
            res.status(200)
            res.send(user)
        })
        .catch(function(err){
            console.log('error returned')
            res.status(500)
            res.send({err: err})
        })
}

module.exports = {
    routes: routes
}