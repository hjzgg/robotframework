*** Settings ***
Library  SeleniumLibrary

#Resource            ../CommonResource.robot#
Force Tags          MyTag


*** Variables ***

${robotVar} =            FooBarBaz


*** Testcases ***

#First Login Yaoqianshu Test Case        #用户首次进入活动流程检查
#    open browser    http://wx.10085.cn/hackertree/index?mobile=13523056012&nickname=五个臭皮匠   chrome  browserOptions={"mobileEmulation":{"deviceName":"Galaxy S5"}}
#    wait until page contains element    xpath://html/body/div[5]/div  20s   摇钱树页面加载失败
#    sleep   6s
#    click element   xpath://html/body/div[5]/div/div[2]/div[3]/div[1]       #点击种树
#    sleep   2s
#    wait until element is visible   xpath://html/body/div[5]/div/div[2]/div[3]/div[3]    5s     种树失败
#    sleep   2s
#    wait until element is visible   xpath://html/body/div[5]/div/div[1]/div[1]/div[1]/img   3s      头像加载失败
