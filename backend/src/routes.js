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
        res.send("HELLO USER!!!!");
    })
}

module.exports = {
    routes: routes
}