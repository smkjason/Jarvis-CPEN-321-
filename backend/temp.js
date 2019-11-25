
var io = require('socket.io-client')

var socket = io('http://localhost:3000')

socket.emit('authenticate', {debugName: 'jarviscpen321.1@gmail.com'})

socket.on('test_echo', function(data){
    console.log(data)
})

socket.on('event', function(data){
    console.log(data)
    setTimeout(() => socket.emit(data.msg + '.send', {message: 'hello'}), 5000)
    // socket.emit(data.msg + '.send', {message: 'hello'})
    console.log(`emited ${data.msg}`)
})

socket.emit('test', {data: 'hello'})

// var admin = require('firebase-admin')
// var serviceAccount = require('./firebasecred.json')

// admin.initializeApp({
//   credential: admin.credential.cert(serviceAccount),
//   databaseURL: "https://jarvis-cpen321.firebaseio.com"
// })

// async function sendNotification(email){
//     //send notification


//     var lol1 = {
//         notification: {
//             title: 'HELLO?',
//             body: 'WORLD'
//         },
//         token: 'cBX_dI-zk58:APA91bFturoYMDKRlYBOrdhO8S0fr4y9BaJbvuJKx6IWQIsJfmhbjPdVZgWC47bQpCGF9fFmgx4QtyYJuVUZZ8yG2nroc0MK-RUOoE2cnXNrc8A9OynA4TLQcYY1FG5RWqCjuOIEIr8J'
//     }

//     var response = await admin.messaging().send(lol1)
//     return response
// }

// sendNotification('hello')
// sendNotification('hello')
