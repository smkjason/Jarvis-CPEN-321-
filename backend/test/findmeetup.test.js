const find_timeslot = require('../src/find_meetup/find_timeslot');
const freetime = require('../src/find_meetup/freetime')
const TEventModel = require('../src/data/schema').TentativeEventModel
const moment = require("moment");

// var Models
// jest.mock('../src/data/schema')
// Models = require('../src/data/schema')

describe('meet up schedule algorithm test', () => {
    test('only 1 sequence', () => {
        // var user_input = new TEventModel();
        var user_input = ({
            responses: [{
                timeslots:[{
					startTime: "2019-11-27 6:00", endTime: "2019-11-27 12:00"
				},
				{
					startTime: "2019-11-27 8:00", endTime: "2019-11-27 12:00"
				},
				{
					startTime: "2019-11-27 10:00", endTime: "2019-11-27 12:00"
				},
				{
					startTime: "2019-11-27 11:00", endTime: "2019-11-27 12:00"
				}
			],
        }],
            length: "1:00",
            invitees: ["personA", "B", "C"],
            deadline: "2019-11-28",
            creatorEmail: "jarvis@gmail.com"
		});

        // user_input.responses[0].timeslots[0].push({startTime:"2019-11-26 6:00", endTime: "2019-11-26 12:00"}) 
        // user_input.responses[0].timeslots[1].push({startTime:"2019-11-26 7:00", endTime: "2019-11-26 12:00"}) 
        // user_input.responses[0].timeslots[2].push({startTime:"2019-11-26 8:00", endTime: "2019-11-26 12:00"}) 
        // user_input.responses[0].timeslots[3].push({startTime:"2019-11-26 9:00", endTime: "2019-11-26 12:00"}) 
        // user_input.creatorEmail.push("jarvis@gmail.com")
        // user_input.deadline.push("2019-11-28")

        var res = find_timeslot.calculateBestTimeslot(user_input);

        var expected_start = moment("2019-11-27 8:00").unix()
        var expected_start_2 = moment("2019-11-27 10:00").unix()
        var expected_start_3 = moment("2019-11-27 11:00").unix()

        var expected_end = moment("2019-11-27 10:00").unix()
        var expected_end_2 = moment("2019-11-27 11:00").unix()
        var expected_end_3 = moment("2019-11-27 12:00").unix()


        expect(res[0].startTime).toBe(expected_start)
        expect(res[1].startTime).toBe(expected_start_2)
        expect(res[2].startTime).toBe(expected_start_3)

        expect(res[0].endTime).toBe(expected_end)
        expect(res[1].endTime).toBe(expected_end_2)
        expect(res[2].endTime).toBe(expected_end_3)

        expect(res[0].weight).toBe(1/2 * 0.75)
        expect(res[1].weight).toBe(0.5625)
        expect(res[2].weight).toBe(0.75)
                        
    })
})


// user_input = {
//     responses: [{
//         timeslots:[{
//             startTime: "2019-11-26 6:00", endTime: "2019-11-26 12:00"
//         },{
//             startTime: "2019-11-26 7:00", endTime: "2019-11-26 12:00"
//         },{
//             startTime:"2019-11-26 8:00", endTime: "2019-11-26 12:00"
//         },{
//             startTime:"2019-11-26 9:00", endTime: "2019-11-26 12:00"
//         }]
//     }],
//     deadline: "2019-11-28",
