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