const Google = require('../../src/util/google')

jest.mock('google-auth-library')
jest.mock('googleapis')

const {OAuth2Client} = require('google-auth-library')
const google = require('googleapis')

var verifyIdTokenMock = jest.fn(() => {
    return Promise.resolve({
        getPayload: () => ({email: 'charlesbai321@gmail.com'})
    })
})
var setCredMock = jest.fn()
var insertMock = jest.fn()

beforeEach(() => {
    OAuth2Client.mockImplementation(() => {
        return {
            verifyIdToken: verifyIdTokenMock,
            setCredentials: setCredMock
        }
    })
    google.calendar_v3.Calendar.mockImplementation(() => {
        return {
            events: {insert: insertMock}
        }
    })
})

const client_id = require('../../src/configs').CLIENT_ID

describe('google client test', () =>{
    const OLD_ENV = process.env

    beforeEach(() => {
        jest.resetModules()
        process.env = {...OLD_ENV}
        delete process.env.NODE_ENV
    })
    afterEach(() => {
        process.env = OLD_ENV
    })

    test('auth returns email in development', async () => {
        process.env.ENV = 'development'

        expect(await Google.auth({})).toBe('jarviscpen321.1@gmail.com')
    })

    test('auth uses google client in production', async () => {
        process.env.ENV = 'production'

        try{
            await Google.auth({headers: {authorization: 'Bearer TOKEN'}})
        } catch(err){}

        expect(OAuth2Client).toHaveBeenCalledTimes(1)
        expect(OAuth2Client.mock.calls[0][0]).toBe(client_id)
    })

    test('auth sends idtoken', async () => {
        process.env.ENV = 'production'
        
        var email
        try{
            email = await Google.auth({headers: {authorization: 'Bearer TOKEN'}})
        } catch(err){}

        var arg = verifyIdTokenMock.mock.calls[0][0]
        expect(arg.idToken).toBe('TOKEN')
        expect(arg).toHaveProperty('audience')
        expect(email).toBe('charlesbai321@gmail.com')
    })

    test('throws error for missing token', async () => {
        process.env.ENV = 'production'

        try{
            await Google.auth({headers: {}})
        } catch(err){
            return
        }

        throw new Error('should have thrown an error')
    })
})

describe('getUserCalendar', () => {
    test('get user calendar', async () => {
        await Google.getUserCalendar({refresh_token: 'token1', google_token: 'token2'})

        expect(setCredMock).toHaveBeenCalledTimes(1)
        var args = setCredMock.mock.calls[0][0]
        console.log(args)
        expect(args.refresh_token).toBe('token1')
        expect(args.access_token).toBe('token2')
        expect(google.calendar_v3.Calendar).toHaveBeenCalledTimes(1)
    })
})

describe('addToCalendar', () => {
    test('removes fields', async () => {
        var event = {toJSON: () => ({_id: 'test', __v: 'lol', name: 'hello'}) }
        var user = {email: 'hello'}

        await Google.addToCalendar(user, event)

        var args = insertMock.mock.calls[0][0]
        expect(args.calendarId).toBe('primary')
        expect(JSON.stringify(args.requestBody)).toBe(JSON.stringify({name: 'hello'}))
    })
})
