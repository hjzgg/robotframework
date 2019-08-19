*** Settings ***
Library  SeleniumLibrary

#Resource            ../CommonResource.robot
Force Tags          MyTag


*** Variables ***

${robotVar} =            FooBarBaz


*** Testcases ***

#View Market Test Case
#    open browser    http://wx.10085.cn/hackertree/index?mobile=13523056030&nickname=五个臭皮匠   chrome  browserOptions={"mobileEmulation":{"deviceName":"Galaxy S5"}}
#    wait until page contains element    css:body > div.warp > div  20s   摇钱树页面加载失败
#    sleep   2s
#    click element   css:body > div.warp > div > div.tree_rwbtn_box > div.zsBtn_icon.zsbtn_4
#    sleep   2s
