const EMAIL = 'charlesbai321@gmail.com'
global.console = {log: jest.fn(), warn: jest.fn()}

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
        expect(urls['/user']).toBe('POST')
        expect(UserFunctions.getUsers).toHaveBeenCalledTimes(1)

        expect(urls).toHaveProperty('/user/:email')
        expect(urls['/user/:email']).toBe('GET')
        expect(UserFunctions.getUser).toHaveBeenCalledTimes(1)

        expect(urls).toHaveProperty('/user/:email/admin')
        expect(urls['/user/:email/admin']).toBe('GET')
        expect(UserFunctions.getAdminEvents).toHaveBeenCalledTimes(1)

        expect(urls).toHaveProperty('/user/:email/events')
        expect(urls['/user/:email/events']).toBe('POST')
        expect(EventFunctions.getEvents).toHaveBeenCalledTimes(1)

        expect(urls).toHaveProperty('/user/:email/invites')
        expect(urls['/user/:email/invites']).toBe('GET')
        expect(UserFunctions.invitedEvents).toHaveBeenCalledTimes(1)

        expect(urls).toHaveProperty('/user/:email/events/:id')
        expect(urls['/user/:email/events/:id']).toBe('PUT')
        expect(EventFunctions.respondEvent).toHaveBeenCalledTimes(1)

        expect(urls).toHaveProperty('/user/:email/location')
        expect(urls['/user/:email/location']).toBe('PUT')
        expect(UserFunctions.updateLocation).toHaveBeenCalledTimes(1)

        expect(urls).toHaveProperty('/events/:id')
        expect(urls['/events/:id']).toBe('GET')
        expect(EventFunctions.getEvents).toHaveBeenCalledTimes(1)

        expect(urls).toHaveProperty('/events/:id/locations')
        expect(urls['/events/:id/locations']).toBe('GET')
        expect(UserFunctions.updateLocation).toHaveBeenCalledTimes(1)

        expect(urls).toHaveProperty('/events/:id/activate')
        expect(urls['/events/:id/activate']).toBe('POST')
        expect(EventFunctions.activateEvent).toHaveBeenCalledTimes(1)

        expect(urls).toHaveProperty('/events/:id/messages')
        expect(urls['/events/:id/messages']).toBe('GET')
        expect(ChatFunctions.getMessages).toHaveBeenCalledTimes(1)
    })
})

async function delay(x){
    await (() => (new Promise(resolve => {
        setTimeout(resolve, x)
    })))()
}