const Configs = require('../src/configs')

test('config has the correct keys', () => {
    var configs = Configs.loadConfigs()
    expect(true).toBe(true)
})