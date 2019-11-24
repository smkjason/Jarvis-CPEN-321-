const EMAIL = 'charlesbai321@gmail.com'

/* creating them */
const UserFunctions = require('../../src/app/user')
jest.mock('../../src/data/schema')
jest.mock('../../src/util/google')
jest.mock('../../src/app/event')
jest.mock ('axios')
const Models = require('../../src/data/schema')
const Google = require('../../src/util/google')
const EventFunctions = require('../../src/app/event')
const axios = require('axios')

/* mock functions*/
var TEventFindOne = jest.fn((s) => {
    return {
        exec: () => { 
            return s.id == "abc" ? Promise.resolve({name: 'tevent', invitees: [EMAIL], save: () => Promise.resolve({}), responses: [], creatorEmail: EMAIL}) : Promise.resolve(null)
        }
    }
})
var EventFindOne = jest.fn((s) => {
    return {
        exec: () => { 
            return s.id == "def" ? 
                Promise.resolve({
                    name: 'event', 
                    start: {dateTime: '2019-11-25 10:00'},
                    end: {dateTime: '2019-11-25 11:00'},
                    creatorEmail: EMAIL,
                    attendees: []
                }) : 
                Promise.resolve(null);
        }
    }
})
var EventFind = jest.fn((s) => {
    return s['$or'] ? {exec: () => Promise.resolve([{summary: 'event1'}])} : {exec: () => Promise.resolve([])}
})
var TEventFind = jest.fn((s) => {
    return s['$or'] ? {exec: () => Promise.resolve([{summary: 'event2'}])} : {exec: () => Promise.resolve([])}
})
var TEventDeleteOne = jest.fn((s) => {
    return {exec: () => Promise.resolve({})}
})
var UserFind = jest.fn(() => {
    return {
        exec: () => Promise.resolve([{email: EMAIL, lat: '12.23', lon: '23.23'}]),
        select: () => ({exec: () => Promise.resolve([{email: EMAIL}])})
    }
})
var UserFindOne = jest.fn((s) => {
    return {exec: () => s.email == EMAIL ? Promise.resolve({email: EMAIL, save: () => Promise.resolve({})}) : Promise.resolve(null)}
})

var GoogleGetCalendar = jest.fn(() => ({
    events: {
        list: () => Promise.resolve({
            data: {
                items: [
                    {summary: 'dummy event', id: '123'},
                    {summary: 'dummy event2', id: '456'}
                ]
            }
        })
    }
}))
var GoogleAdd = jest.fn()

/* stubbing them in */
Models.EventModel.mockImplementation((data) => {
    expect(data.id).not.toBe(null)
    return {save: () => Promise.resolve(data)}
})
Models.TentativeEventModel.mockImplementation((data) => {
    expect(data.id).not.toBe(null)
    return {save: () => Promise.resolve(data)}
})
Models.UserModel.mockImplementation((data) => {
    expect(data.email).not.toBe(null)
    return {save: () => Promise.resolve(data)}
})

Models.TentativeEventModel.findOne = TEventFindOne
Models.TentativeEventModel.find = TEventFind
Models.TentativeEventModel.deleteOne = TEventDeleteOne
Models.EventModel.findOne = EventFindOne
Models.EventModel.find = EventFind
Models.UserModel.findOne = UserFindOne
Models.UserModel.find = UserFind

EventFunctions.syncEvents = jest.fn(() => Promise.resolve({}))
EventFunctions.relatedTEvents = jest.fn(() => [
    {name: 'event1', responses: [{email: EMAIL}]},
    {name: 'event2', responses: []}
])

Google.getUserCalendar = GoogleGetCalendar
Google.addToCalendar = GoogleAdd

axios.post = jest.fn(() => Promise.resolve({data: {}}))

beforeEach(() => {
    jest.clearAllMocks()
})

describe('getUsers', () => {
    test('gets users without sensitive info', async () => {
        var users = await UserFunctions.getUsers('query')

        expect(UserFind).toHaveBeenCalledTimes(1)
        expect(JSON.stringify(UserFind.mock.calls[0][0]))
            .toBe(JSON.stringify({email: {$regex: 'query', $options: 'i'}}))
        expect(users[0].email).toBe(EMAIL)
    })
})

describe('getUser', () => {
    test('gets user and syncs events', async () => {
        await UserFunctions.getUser(EMAIL)

        expect(UserFindOne.mock.calls[0][0].email).toBe(EMAIL)
        expect(EventFunctions.syncEvents).toHaveBeenCalledTimes(1)
    })
})

describe('createUser', () => {
    test('creates user if doesnt exist', async () => {
        await UserFunctions.authCreateUser('anotheremail@gmail.com', {code: 'CODE', idToken: 'TOKEN', FCMToken: 'fcmtoken'})

        expect(Models.UserModel).toHaveBeenCalledTimes(1)
        expect(Models.UserModel.mock.calls[0][0].email).toBe('anotheremail@gmail.com')
        expect(axios.post).toHaveBeenCalledTimes(1)
        expect(EventFunctions.syncEvents).toHaveBeenCalledTimes(1)
    })

    test('does not create user', async () => {
        await UserFunctions.authCreateUser(EMAIL, {code: 'CODE', idToken: 'TOKEN', FCMToken: 'fcmtoken'})

        expect(Models.UserModel).toHaveBeenCalledTimes(0)
        expect(axios.post).toHaveBeenCalledTimes(1)
        expect(EventFunctions.syncEvents).toHaveBeenCalledTimes(1)
    })

    test('does not update code if not in body', async () => {
        await UserFunctions.authCreateUser(EMAIL, {idToken: 'TOKEN', FCMToken: 'fcmtoken'})

        expect(Models.UserModel).toHaveBeenCalledTimes(0)
        expect(axios.post).toHaveBeenCalledTimes(0)
        expect(EventFunctions.syncEvents).toHaveBeenCalledTimes(1)
    })
})

describe('invitedEvents', () => {
    test('retrieves successfully', async () => {
        var response = await UserFunctions.invitedEvents(EMAIL)

        expect(response.events.length).toBe(1)
        expect(response.events[0].name).toBe('event2')
    })

    test('email does not exist', async () => {
        var response = await UserFunctions.invitedEvents('anotheremail@gmail.com')

        expect(response.error).toBe('anotheremail@gmail.com does not exist')
    })
})

describe('get admin events', () => {
    test('getadminevents', async () => {
        var response = await UserFunctions.getAdminEvents(EMAIL)

        expect(response.events.length).toBe(0)
    })
})

describe('updateLocation', () => {
    test('updates', async () => {
        var response = await UserFunctions.updateLocation(EMAIL, {lat: 'num', lon: 'num'})

        expect(response.status).toBe('success')
    })

    test('email does not exist', async () => {
        var response = await UserFunctions.updateLocation('anotheremail@gmail.com', {lat: 'num', lon: 'num'})

        expect(response.error).toBe('user not found')
    })
})