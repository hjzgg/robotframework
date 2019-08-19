*** Settings ***
Library  SeleniumLibrary

*** Test Cases ***

watering Test Case
    open browser    http://wx.10086.cn/planttree/index?mobile=kdN8cKkYYgX0n6xbcUY%2BSyPsRqE47KLvDCCtR4dRMLGaoWxyNmeJ517lI7b%2BUpBob7lI3BNK%2BZ9%2F25TU6c7yERX8ryaWkdYyan%2F2TEn%2Bw524pxtZnkFPARgBcuuDLPNvweez6Rx9IbLFmK7wY1mhA9iShWZNfm0ZCH%2FJ%2FCHmh5U%3D   chrome  browserOptions={"mobileEmulation":{"deviceName":"Galaxy S5"}}
    wait until page contains element    css:body > div.warp > div  20s   摇钱树页面加载失败
    sleep   2s
    click element       xpath://*[contains(@class,'jsbtn')]
    sleep   0.2s
    ${waterResult}  get text    css:body > div.toast_bubble.bubble_bg.bubble_bg1
    log     ${waterResult}
    sleep   2s