*** Settings ***
Library  SeleniumLibrary

#Resource            ../CommonResource.robot
Force Tags          MyTag


*** Variables ***

${robotVar} =            FooBarBaz


*** Testcases ***

#check action Test Case      #用户首页动态按钮数据展示流程检查（无好友进行偷取、助力浇水时）
#    open browser    http://wx.10085.cn/hackertree/index?mobile=13523056014&nickname=五个臭皮匠   chrome  browserOptions={"mobileEmulation":{"deviceName":"Galaxy S5"}}
#    wait until page contains element    xpath://html/body/div[5]/div  20s   摇钱树页面加载失败
#    sleep   6s
#    click element   xpath://html/body/div[5]/div/div[2]/div[3]/div[1]       #点击种树
#    sleep   2s
#    wait until element is visible   xpath://html/body/div[5]/div/div[2]/div[3]/div[3]    5s     种树失败
#    Click Element   xpath://html/body/div[5]/div/div[1]/div[2]/div[2]
#    sleep   3s