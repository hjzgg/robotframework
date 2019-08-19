*** Settings ***
Library  SeleniumLibrary

#Resource            ../CommonResource.robot
Force Tags          MyTag


*** Variables ***

${robotVar} =            FooBarBaz


*** Testcases ***

#Invite Friends Test Case        #种树页面点击邀请好友按钮功能流程检查
#    open browser    http://wx.10085.cn/hackertree/index?mobile=13523056022&nickname=五个臭皮匠   chrome  browserOptions={"mobileEmulation":{"deviceName":"Galaxy S5"}}
#    wait until page contains element    xpath://html/body/div[5]/div  20s   摇钱树页面加载失败
#    sleep   6s
#    click element   xpath://html/body/div[5]/div/div[2]/div[3]/div[1]       #点击种树
#    sleep   2s
#    wait until element is visible   xpath://html/body/div[5]/div/div[2]/div[3]/div[3]    5s     种树失败
#    click element   xpath://html/body/div[5]/div/div[4]/div[1]      #点击邀请好友
#    sleep   2s
#    click element   xpath://html/body/div[8]/div[11]/div[4]/div     #点击好友排行榜弹窗中的邀请好友

