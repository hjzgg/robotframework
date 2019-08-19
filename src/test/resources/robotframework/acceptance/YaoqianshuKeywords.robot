*** Settings ***
Library  SeleniumLibrary

*** Variables ***
${logSuccess}   摇钱树页面加载成功

*** Keywords ***
Open Yaoqianshu
    open browser    http://wx.10085.cn/hackertree/index?mobile=13523056001&nickname=五个臭皮匠   chrome  browserOptions={"mobileEmulation":{"deviceName":"Galaxy S5"}}
    set window size  775    1030
    wait until page contains element    xpath://html/body/div[5]/div  20s   摇钱树页面加载失败

Tree add
    click element   xpath://html/body/div[4]    #点击蒙层
    click element   xpath://html/body/div[5]/div/div[2]/div[3]/div[1]

Feedback
    Click Element   xpath://html/body/div[4]/div/div[2]/div[1]
    input text  xpath://html/body/div[7]/div[4]/div[2]/textarea    我的意见反馈
    click element   xpath://html/body/div[7]/div[4]/div[2]/div[3]
#    wait until page contains element    xpath://html/body/div[7]/div[17]/p  20s 您今天已经提交一次建议

Small bell
    Click Element   xpath://html/body/div[4]/div/div[1]/div[2]/div[2]

Close Small bell
    Click Element   xpath://html/body/div[7]/div[7]/div[1]

Strategy    #攻略
    Click Element   xpath://html/body/div[4]/div/div[2]/div[2]

Invite Friends
    click element   xpath://html/body/div[4]/div/div[4]/div[1]
    wait until element is visible   xpath://html/body/div[7]/div[11]/div[4]/div     5s
    click element   xpath://html/body/div[7]/div[11]/div[4]/div

Task
    click element   xpath://html/body/div[4]/div/div[4]/div[2]      #点击做任务
    sleep   2s

Daily get water Task
    click element   xpath://html/body/div[7]/div[9]/div[3]/div/div[1]/div[2]/div[2]/span[2]    #每日免费领取-领取
    sleep   2s

Three Days login
    click element   xpath://html/body/div[8]/div[9]/div[3]/div/div[2]/div[2]/div[2]/span[2]     #本周累计三天登录-领取
    sleep   2s

View Personal Home Page
    click element   xpath://html/body/div[8]/div[9]/div[3]/div/div[3]/div[2]/div[2]/span[2]     #查看个人主页

Close Task
    click element   xpath://html/body/div[7]/div[9]/div[1]   #点击关闭
    sleep   2s
    click element   xpath://html/body/div[4]/div/div[3]/div[2]    #点击刷新
