var io = require('socket.io')
const auth = require('../util/google').auth
const User = require('../data/schema').UserModel
const Event = require('../data/schema').EventModel

/*
    verify idtoken
    retrieve user
    set up events handlers
    disconnect -> do nothing?
*/
function socketSetup(server){
    var base = io.listen(server)
    base.use(function(socket, next){
        console.log("connection data: " + JSON.stringify(socket.handshake.query))
        
        //email will corresp    nd to whoever the auth token is
        var email
        try{
            email = await auth(socket.handshake.query)
        } catch(err) {
            return next(new Error("idToken invalid"))
        }
        socket.email = email

        next()
    })

    //this means that this socket is authenticated
    //and should have the email field
    base.on('connection', function(socket){
        var user = await User.findOne({email: email}).exec()
        if(user && user.email){
            //register all event handlers
            var events = await Event.find({creatorEmail: email}).exec()
            for(const event of events){
                socket.on(event.id, function(data){
                    
                })
            }
        } else {
            socket.emit('error', {msg: 'user does not have a record in the db'})
        }
    })
}