const EMAIL = 'charlesbai321@gmail.com'

/* creating them */
const EventFunctions = require('../../src/app/event')
jest.mock('../../src/data/schema')
jest.mock('../../src/util/google')
const Models = require('../../src/data/schema')
const Google = require('../../src/util/google')

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
    return {exec: () => Promise.resolve([{email: EMAIL, lat: '12.23', lon: '23.23'}])}
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

var UserFindOne = jest.fn(() => {
    return {exec: () => Promise.resolve({email: EMAIL})}
})

/* stubbing them in */
Models.EventModel.mockImplementation((data) => {
    expect(data.id).not.toBe(null)
    return {save: () => Promise.resolve(data)}
})
Models.TentativeEventModel.mockImplementation((data) => {
    expect(data.id).not.toBe(null)
    return {save: () => Promise.resolve(data)}
})

Models.TentativeEventModel.findOne = TEventFindOne
Models.TentativeEventModel.find = TEventFind
Models.TentativeEventModel.deleteOne = TEventDeleteOne
Models.EventModel.findOne = EventFindOne
Models.EventModel.find = EventFind
Models.UserModel.findOne = UserFindOne
Models.UserModel.find = UserFind

Google.getUserCalendar = GoogleGetCalendar
Google.addToCalendar = GoogleAdd

beforeEach(() => {
    jest.clearAllMocks()
})

describe('getEvent', () => {
    test('gets tevent event', async () => {
        var event = await EventFunctions.getEvent('abc', EMAIL)
        expect(event).not.toBe(null)

        expect(TEventFindOne).toHaveBeenCalledTimes(1)
        expect(TEventFindOne.mock.calls[0][0].id).toBe('abc')
        expect(event.name).toBe('tevent')
    })

    test('gets event', async () => {
        var event = await EventFunctions.getEvent('def', EMAIL)
        expect(event).not.toBe(null)
        
        expect(TEventFindOne).toHaveBeenCalledTimes(1)
        expect(EventFindOne).toHaveBeenCalledTimes(1)
        expect(event.name).toBe('event')
    })
})

describe('createEvent', () => {
    test('creates event with uuid', async () => {
        var event = await EventFunctions.createEvent(EMAIL, {name: 'test event', invitees: []})
        expect(Models.TentativeEventModel).toHaveBeenCalledTimes(1)

        expect(event.creatorEmail).toBe(EMAIL)
    })
})

describe('syncEvents', () => {
    test('syncs event', async () => {
        await EventFunctions.syncEvents({email: 'dummy data'})

        expect(GoogleGetCalendar).toHaveBeenCalledTimes(1)
        expect(Models.EventModel).toHaveBeenCalledTimes(2)
        expect(Models.EventModel.mock.calls[0][0].summary).toBe('dummy event')
        expect(Models.EventModel.mock.calls[1][0].summary).toBe('dummy event2')
    })
})

describe('getEvents', () => {
    test('gets related', async () => {
        var events = await EventFunctions.relatedEvents(EMAIL)
        
        expect(events[0].summary).toBe('event1')
        expect(EventFind).toHaveBeenCalledTimes(1)
    })
})

describe('relatedTEvents', () => {
    test('gets related', async () => {
        var events = await EventFunctions.relatedTEvents(EMAIL)

        expect(events[0].summary).toBe('event2')
        expect(TEventFind).toHaveBeenCalledTimes(1)
    })
})

describe('respondEvent', () => {
    test('responds to event', async () => {
        var response = await EventFunctions.respondEvent('abc', EMAIL, true, {})
       
        expect(response.status).toBe('success')
        expect(TEventFindOne).toHaveBeenCalledTimes(1)
    })

    test('checks if invitee', async () => {
        var response = await EventFunctions.respondEvent('abc', 'invalid', true, {})

        expect(response.error).not.toBe(null)
    })
})

describe('activateEvent', () => {
    test('activates event', async () => {
        await EventFunctions.activateEvent('abc', EMAIL, {startTime: '2019-11-25 00:00', endTime: '2019-11-25 23:59'})

        expect(GoogleAdd).toHaveBeenCalledTimes(1)
        expect(Models.EventModel).toHaveBeenCalledTimes(1)
    })

    test('checks if admin', async () => {
        var response = await EventFunctions.activateEvent('abc', 'invalid', {startTime: '2019-11-25 00:00', endTime: '2019-11-25 23:59'})

        expect(response.error).not.toBe(null)
    })
})

describe('userLocations', () => {
    test('uploads locations event', async () => {
        Date.now = jest.fn(() => {
            return 1574701200*1000  
        })
        await EventFunctions.userLocations('def', EMAIL)

        expect(UserFind).toHaveBeenCalledTimes(1)
    })

    test('does not show time if not close to event', async () => {
        Date.now = jest.fn(() => {
            return (new Date('2018-11-25 10:00')).getMilliseconds()
        })
        expect(UserFind).toHaveBeenCalledTimes(0)
    })
})