const EMAIL = 'charlesbai321@gmail.com'

jest.mock('../src/app/event')
jest.mock('../src/app/user')
jest.mock('../src/app/chat')

const UserFunctions = require('../src/app/user')
const EventFunctions = require('../src/app/event')
const ChatFunctions = require('../src/app/chat')
const routes = require('../src/routes').routes
var urls = {}
var req = {
    headers: {},
    params: {email: EMAIL},
    body: {},
    query: {}
}
var mockApp = {
    get: jest.fn((s, f) => {
        f(req, {send: jest.fn()})
        urls[s] = 'GET'
    }),
    post: jest.fn((s, f) => {
        f(req, {send: jest.fn()})
        urls[s] = 'POST'
    }),
    put: jest.fn((s, f) => {
        f(req, {send: jest.fn()})
        urls[s] = 'PUT'
    })
};

UserFunctions.getUsers = jest.fn()
UserFunctions.authCreateUser = jest.fn()
UserFunctions.getUser = jest.fn()
UserFunctions.getAdminEvents = jest.fn()
EventFunctions.getEvents = jest.fn()
EventFunctions.createEvent = jest.fn()
UserFunctions.invitedEvents = jest.fn()
EventFunctions.respondEvent = jest.fn()
UserFunctions.updateLocation = jest.fn()
EventFunctions.getEvents = jest.fn()
EventFunctions.userLocations = jest.fn()
EventFunctions.activateEvent = jest.fn()
ChatFunctions.getMessages = jest.fn()

describe('routes', () => {
    test('root', async () => {
        routes(mockApp)
        await delay(1000)

        expect(urls).toHaveProperty('/')
        expect(urls['/']).toBe('GET')

        expect(urls).toHaveProperty('/env')
        expect(urls['/env']).toBe('GET')

        expect(urls).toHaveProperty('/user')
        expect(urls['/user']).toBe('GET')
        expect(UserFunctions.getUsers).toHaveBeenCalledTimes(1)

        expect(urls).toHaveProperty('/user/:email')
        expect(urls['/user/:email']).toBe('GET')
        expect(UserFunctions.getUser).toHaveBeenCalledTimes(1)

        expect(urls).toHaveProperty('/user/:email/admin')
        expect(urls['/user/:email/admin']).toBe('GET')
        expect(UserFunctions.getAdminEvents).toHaveBeenCalledTimes(1)

        expect(urls).toHaveProperty('/user/:email/events')
        expect(urls['/user/:email/events']).toBe('GET')
        expect(EventFunctions.getEvents).toHaveBeenCalledTimes(1)
    })
})

async function delay(x){
    await (() => (new Promise(resolve => {
        setTimeout(resolve, x)
    })))()
}