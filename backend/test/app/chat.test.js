const EMAIL = 'charlesbai321@gmail.com'
global.console = {log: jest.fn(), warn: jest.fn()}

jest.mock('../../src/data/schema')
jest.mock('../../src/util/google')
jest.mock('../../src/app/event')
jest.mock('socket.io')

const ChatFunctions = require('../../src/app/chat')
const Models = require('../../src/data/schema')
const Google = require('../../src/util/google')
const EventFunctions = require('../../src/app/event')
const io = require('socket.io')

Models.ChatModel.mockImplementation((chat) => {
    expect(chat.message).not.toBe(null)
    expect(chat.sender).toBe(EMAIL)
    return {save: () => Promise.resolve({})}
})
Models.EventModel.findOne = jest.fn(() => ({
    exec: () => Promise.resolve({creatorEmail: EMAIL, attendees: []})
}))
Models.ChatModel.find = jest.fn(() => ({
    exec: () => Promise.resolve([{message: 'hello'}, {message: 'hello1'}])
}))

NewEvent = jest.fn()
Google.auth = jest.fn(() => EMAIL)

EventFunctions.relatedEvents = jest.fn(() => Promise.resolve([
    {id: 'id1'}
]))

var mockSocket = {
    on: async (s, f) => {
        console.log('called on with ' + s)
        if(s == 'authenticate') await f({idToken: 'TOKEN'})
        if(s == 'id1.send') await f({message: 'hello'})
        if(s == 'test') f({data: 'data'})
    },
    email: EMAIL,
    broadcast: {emit: jest.fn()},
    emit: () => ({})
}

io.listen = jest.fn(() => ({
    on: (s, f) => {
        if(s == 'connection') f(mockSocket)
    },
    use: () => {},
    sockets: {sockets: {s1: {
        email: EMAIL, 
        on: NewEvent,
        emit: () => ({})
    }}}
}))

beforeEach(() => {
    jest.clearAllMocks()
})

describe('getMessages', () => {
    test('does not get messages that are not part of the event', async () =>{
        var messages = await ChatFunctions.getMessages('abc', 'antheremail', null)

        expect(messages.length).toBe(0)
    })

    test('gets all messages before a time', async () => {
        var messages = await ChatFunctions.getMessages('abc', EMAIL, 34)

        expect(messages.length).toBe(2)
    })
})

describe('socket setup', () => {
    test('if everything works', async () => {
        Date.now = jest.fn(() => {
            return 1234567  
        })
        ChatFunctions.socketSetup({})

        await (() => ( new Promise(resolve => {
            setTimeout(resolve, 1000)
        })))()

        expect(io.listen).toHaveBeenCalledTimes(1)
        expect(Google.auth).toHaveBeenCalledTimes(1)
        expect(mockSocket.broadcast.emit).toHaveBeenCalledTimes(2)
        expect(Models.ChatModel).toHaveBeenCalledTimes(1)
    })
})

describe('attach new event', () => {
    test('attaches new event handlers', () =>{
        ChatFunctions.newEvent({attendees: [EMAIL], creatorEmail: 'c'+EMAIL, id: 'id3'})
        expect(NewEvent).toHaveBeenCalledTimes(1)
        expect(NewEvent.mock.calls[0][0]).toBe('id3.send')
    })
})


