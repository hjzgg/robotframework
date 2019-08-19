*** Settings ***
Library  SeleniumLibrary

#Resource            ../CommonResource.robot
Force Tags          MyTag


*** Variables ***

${robotVar} =            FooBarBaz


*** Testcases ***

#View Strategy Test Case     #查看攻略
#    open browser    http://wx.10085.cn/hackertree/index?mobile=13523056016&nickname=五个臭皮匠   chrome  browserOptions={"mobileEmulation":{"deviceName":"Galaxy S5"}}
#    wait until page contains element    xpath://html/body/div[5]/div  20s   摇钱树页面加载失败
#    sleep   6s
#    click element   xpath://html/body/div[5]/div/div[2]/div[3]/div[1]       #点击种树
#    sleep   2s
#    wait until element is visible   xpath://html/body/div[5]/div/div[2]/div[3]/div[3]    5s     种树失败
#    Click Element   xpath://html/body/div[5]/div/div[2]/div[2]      #点击攻略
