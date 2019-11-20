const Google = require('../../src/util/google')
const {OAuth2Client} = require('google-auth-library')
jest.mock('google-auth-library')

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
        //expect(OAuth2Client).toHaveBeenCalledWith(verifyIdToken)
        // expect(await Google.auth({headers: {authorization: 'Bearer TOKEN'}})).toBe(

        // )
    })
})
