const UserFunctions = require('./app/user')

function routes(app){
    //all our routes can go here
    app.get("/", function(req, res) {
        res.send("Hello World test111asdjflkajsdfjadsjfsajlkfjsa")
    })

    app.get("/env", function(req, res){
        res.send(process.env.ENV)
    })

    //do oauth
    app.get("/oauth/auth", function(req, res) {
        //result = 
        res.send({})
    })
    app.get("/oauth/start", function(req, res) {
        res.send({})
    })

    app.get("/user", function(reg, res){
        res.send("HELLO USER!!!!")
    })

    app.post("/create_test_user", function(req, res){
        UserFunctions.createTestUser(req.body)
            .then(function(user){
                console.log("user created??")
                res.status(200)
                res.send()
            })
            .catch(function(err){
                console.log('error returned')
                res.status(500)
                res.send()
            })
    })
}

module.exports = {
    routes: routes
}