const express = require("express")
const app = express()

//all our routes can go here
app.get("/", function(req, res) {
    res.send("Hello World test111asdjflkajsdfjadsjfsajlkfjsa")
})

app.get("/user", function(reg, res){
    res.send("HELLO USER!!!!");
})

app.listen(3000)