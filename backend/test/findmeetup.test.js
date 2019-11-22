const find_timeslot = require('../src/find_timeslot');

//list of 3 arrays 
var listofevents = [[0,0,0,0,0,0,0,0,0,0,
    0,0,0,0,0,0,0,0,0,0,
    0,0,0,0], 
    [0,0,0,0,0,0,0,0,0,0,
    0,0,0,0,0,0,0,0,0,0,
        0,0,0,0],
    [0,0,0,0,0,0,0,0,0,0,
    0,0,0,0,0,0,0,0,0,0,
    0,0,0,0]];

var expected = [0,0,0,0,0,0,0,0,0,0,
    0,0,0,0,0,0,0,0,0,0,
    0,0,0,0];

var test = true;

describe('meet up schedule algorithm test', () => {
    test('find_timeslot returns correct times', () => {
    var sum = find_timeslot.calculateBestTimeslot(listofevents);

    if(sum.length != expected.length){
        test = false;
    }

    for(var i = 0; i = sum.length; i++){
        if(sum[i] != expected[i]){
            test = false;
            break;
        }
    }

    expect(test).toBe(true);
    })
})