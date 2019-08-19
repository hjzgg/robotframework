*** Settings ***
Library  SeleniumLibrary

#Resource            ../CommonResource.robot
Force Tags          MyTag


*** Variables ***

${robotVar} =            FooBarBaz


*** Testcases ***
#
#Feedback Test Case      #意见反馈
#    open browser    http://wx.10085.cn/hackertree/index?mobile=13523056020&nickname=五个臭皮匠   chrome  browserOptions={"mobileEmulation":{"deviceName":"Galaxy S5"}}
#    wait until page contains element    xpath://html/body/div[5]/div  20s   摇钱树页面加载失败
#    sleep   6s
#    click element   xpath://html/body/div[5]/div/div[2]/div[3]/div[1]       #点击种树
#    sleep   2s
#    wait until element is visible   xpath://html/body/div[5]/div/div[2]/div[3]/div[3]    5s     种树失败
#    click element   xpath://html/body/div[5]/div/div[2]/div[1]      #点击意见反馈
#    sleep   1s
#    input text  xpath://html/body/div[8]/div[4]/div[2]/textarea    我的意见反馈       #输入意见
#    sleep   0.5s
#    click element   xpath://html/body/div[8]/div[4]/div[2]/div[3]       #点击提交

