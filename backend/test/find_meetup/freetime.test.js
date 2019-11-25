const FreeTime = require('../../src/find_meetup/freetime')

describe('test mergeTimes', () => {
    test('simple merge', () => {
        var pref = [
            {startTime: 1, endTime: 3, weight: 0.5},
            {startTime: 5, endTime: 9, weight: 0.5}
        ]
        var cal = [
            {startTime: 0, endTime: 10, weight: 0.25}
        ]
        var res = FreeTime.mergeTimes(pref, cal, 2)
        //bigger slots take precedence
        expect(res[0]).toMatchObject({ weight: 0.75, startTime: 5, endTime: 9 })
        expect(res[1]).toMatchObject({ weight: 0.75, startTime: 1, endTime: 3 })
    })

    test('2 overlaps', () => {
        var pref = [
            {startTime: 1, endTime: 3, weight: 0.5},
            {startTime: 5, endTime: 9, weight: 0.3}
        ]
        var cal = [
            {startTime: 2, endTime: 6, weight: 0.25},
            {startTime: 8, endTime: 10, weight: 0.6}
        ]
        var res = FreeTime.mergeTimes(pref, cal, 1)
        //assumes sort implementation 
        expect(res[0]).toMatchObject({ weight: 0.75, startTime: 2, endTime: 3 })
        expect(res[1]).toMatchObject({ startTime: 8, endTime: 9 })
    })

    test('gives at most 5', () => {
        var pref = [
            {startTime: 1, endTime: 2, weight: 0.5},
            {startTime: 2, endTime: 3, weight: 0.5},
            {startTime: 3, endTime: 4, weight: 0.5},
            {startTime: 4, endTime: 5, weight: 0.5},
            {startTime: 5, endTime: 6, weight: 0.5},
            {startTime: 6, endTime: 9, weight: 0.3}
        ]
        var cal = [
            {startTime: 0, endTime: 10, weight: 0.25}
        ]
        var res = FreeTime.mergeTimes(pref, cal, 1)
        expect(res.length).toBe(5)
    })

    test('no matching weights', () => {
        var pref = [
            {startTime: 1, endTime: 4, weight: 0.5},
            {startTime: 6, endTime: 10, weight: 0.8}
        ]
        var cal = []
        var res = FreeTime.mergeTimes(pref, cal, 1)
        expect(res[0]).toMatchObject({ startTime: 6, endTime: 10})
        expect(res.length).toBe(1)
    })

    test('no matching periods', () => {
        var pref = [
            {startTime: 1, endTime: 4, weight: 0.5},
            {startTime: 6, endTime: 10, weight: 0.8}
        ]
        var cal = []
        var res = FreeTime.mergeTimes(pref, cal, 5)
        expect(res.length).toBe(0)
    })
})

var Models
jest.mock('../../src/data/schema')
Models = require('../../src/data/schema')

describe('user free time', () => {
    test('basic test', async () => {
        Date.now = jest.fn(() => (new Date('2019-11-25 12:00')).getTime())

        Models.EventModel.find = jest.fn(() => ({
            exec: () => Promise.resolve([
                {start: {dateTime: '2019-11-25 11:30'}, end: {dateTime: '2019-11-25 12:30'}}
            ])
        }))

        var interval = await FreeTime.userFreeTime('any', '2019-11-25')
        expect(interval.timeslots.length).toBe(1)
        expect(interval.email).toBe('any')
        expect(interval.timeslots[0].startTime).toBe('2019-11-25 12:30')
        expect(interval.timeslots[0].endTime).toBe('2019-11-26 00:00')
    })

    test('overlapping events', async () => {
        Date.now = jest.fn(() => (new Date('2019-11-24 12:00')).getTime())

        Models.EventModel.find = jest.fn(() => ({
            exec: () => Promise.resolve([
                {start: {dateTime: '2019-11-25 11:30'}, end: {dateTime: '2019-11-25 12:30'}},
                {start: {dateTime: '2019-11-25 10:30'}, end: {dateTime: '2019-11-25 12:00'}}
            ])
        }))

        var interval = await FreeTime.userFreeTime('any', '2019-11-25')
        expect(interval.timeslots.length).toBe(2)
        expect(interval.email).toBe('any')
        expect(interval.timeslots[0].startTime).toBe('2019-11-24 12:00')
        expect(interval.timeslots[0].endTime).toBe('2019-11-25 10:30')
        expect(interval.timeslots[1].startTime).toBe('2019-11-25 12:30')
        expect(interval.timeslots[1].endTime).toBe('2019-11-26 00:00')
    })

    test('calendar events span time', async () => {
        Date.now = jest.fn(() => (new Date('2019-11-24 12:00')).getTime())

        Models.EventModel.find = jest.fn(() => ({
            exec: () => Promise.resolve([
                {start: {dateTime: '2019-11-24 00:00'}, end: {dateTime: '2019-11-24 01:00'}},
                {start: {dateTime: '2019-11-25 11:30'}, end: {dateTime: '2019-11-25 12:30'}},
                {start: {dateTime: '2019-11-25 10:30'}, end: {dateTime: '2019-11-25 12:00'}},
                {start: {dateTime: '2019-11-26 10:00'}, end: {dateTime: '2019-11-26 11:00'}}
            ])
        }))

        var interval = await FreeTime.userFreeTime('any', '2019-11-25')
        expect(interval.timeslots.length).toBe(2)
        expect(interval.email).toBe('any')
        expect(interval.timeslots[0].startTime).toBe('2019-11-24 12:00')
        expect(interval.timeslots[0].endTime).toBe('2019-11-25 10:30')
        expect(interval.timeslots[1].startTime).toBe('2019-11-25 12:30')
        expect(interval.timeslots[1].endTime).toBe('2019-11-26 00:00')
    })
})

describe('free calendar slots for an event', () => {
    beforeEach(() => {
        FreeTime.userFreeTime = jest.fn()
    })

    test('for multiple people', async () => {
        await FreeTime.freeCalendarSlots({deadline: '2019-11-25', responses:[{email: 'email1'}, {email: 'email2'}]})
        expect(FreeTime.userFreeTime).toHaveBeenCalledTimes(2)
        expect(FreeTime.userFreeTime.mock.calls[0][0]).toBe('email1')
        expect(FreeTime.userFreeTime.mock.calls[1][0]).toBe('email2')
    })

    test('does not incl declined people', async () => {
        await FreeTime.freeCalendarSlots({deadline: '2019-11-25', responses:[{email: 'email1', declined: true}, {email: 'email2'}]})
        expect(FreeTime.userFreeTime).toHaveBeenCalledTimes(1)
        expect(FreeTime.userFreeTime.mock.calls[0][0]).toBe('email2')
    })
})