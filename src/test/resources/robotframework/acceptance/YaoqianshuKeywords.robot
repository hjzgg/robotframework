*** Settings ***
Library  SeleniumLibrary

*** Variables ***
${logSuccess}   摇钱树页面加载成功

*** Keywords ***
Open Yaoqianshu
    open browser    http://wx.10085.cn/hackertree/index?mobile=13523056036&nickname=五个臭皮匠   chrome  browserOptions={"mobileEmulation":{"deviceName":"Galaxy S5"}}
    set window size  775    1030
    wait until page contains element    css:body > div.warp > div  20s   摇钱树页面加载失败
    sleep   1s

Tree add
    click element   css:body > div.guide_pop    #点击蒙层
    sleep   1s
    click element   css:body > div.warp > div > div.tree_model_box > div.tree_model > div.tree_add

Feedback    #意见反馈
    click element   css:body > div.warp > div > div.tree_model_box > div.tree_proposal      #点击意见反馈
    sleep   1s
    input text  css:body > div.pop > div.pop1.popbg.opi_pop > div.pop1_con > textarea    我的意见反馈       #输入意见
    sleep   0.5s
    click element   css:body > div.pop > div.pop1.popbg.opi_pop > div.pop1_con > div.btn_icon.btn_5.opi_btn       #点击提交
    sleep   0.2s
    ${FeedbackResult}  get text    css:body > div.pop > div.suggest_bubble.bubble_bg.bubble_bg1
    log     ${FeedbackResult}
    sleep   2s
##    wait until page contains element    xpath://html/body/div[7]/div[17]/p  20s 您今天已经提交一次建议
#    ${success}    set variable    成功
#    ${feedbackResult}    Run Keyword And Return Status   Page Should Contain    '${success}'
#    run keyword if  ${feedbackResult}   log     反馈成功

Small bell
    Click Element   css:body > div.warp > div > div.tree_head_box > div.tree_userJB > div.sm_icon.sm_lindan.tree_ld
    sleep   3s

Close Small bell
    Click Element   css:body > div.pop > div.pop3.dy_pop > div.sm_close1.sm_icon
    sleep   1s

Strategy    #攻略
    Click Element   css:body > div.warp > div > div.tree_model_box > div.tree_strategy      #点击攻略
    sleep   5s
    click element   id:click_index      #返回种树

Invite Friends
    click element   css:body > div.warp > div > div.tree_rwbtn_box > div.zsBtn_icon.zsbtn_1      #点击邀请好友
    sleep   2s
    click element   css:body > div.pop > div.ranking_pop.pop5 > div.ranking_foot > div     #点击好友排行榜弹窗中的邀请好友

Task
    click element   css:body > div.warp > div > div.tree_rwbtn_box > div.zsBtn_icon.zsbtn_2      #点击做任务
    sleep   2s

Daily get water Task
    click element   css:body > div.pop > div.task_pop.pop5 > div.pop_conbg.pop5_water_box > div > div:nth-child(1) > div.water_task_con > div.water_task_bot > span.btn_icon.btn_2      #点击领取
    sleep   3s
    click element   css:body > div.pop > div.task_pop.pop5 > div.sm_icon.sm_close2  #关闭做任务弹窗
    sleep   3s
    click element   css:body > div.warp > div > div.tree_jsBox > div.reloadBtn
    sleep   2s

Three Days login
    click element   xpath://html/body/div[8]/div[9]/div[3]/div/div[2]/div[2]/div[2]/span[2]     #本周累计三天登录-领取
    sleep   2s

View Personal Home Page
    click element   css:body > div.pop > div.task_pop.pop5 > div.pop_conbg.pop5_water_box > div > div:nth-child(3) > div.water_task_con > div.water_task_bot > span.btn_icon.btn_2      #点击查看个人主页——去完成
    sleep   2s
    ${login}    set variable    登录
    ${title}    Get Title
    ${s}    Run Keyword And Return Status   Current Frame Contains    ${login}
    run keyword if     ${s}   login    13910042424     159011
    ...     ELSE    log     ${title}
    sleep   5s
    go back
    sleep   1s
    close task

Login
    [Arguments]    ${phone}    ${code}
    log     登录...
    input text     id:phone     ${phone}
    sleep   1s
    click element   id:getMsg
    sleep   1s
    input text     id:code     ${code}
    sleep   1s
    click element   id:loginBtn
    sleep   5s
    Go Back

Close Task
    click element   css:body > div.pop > div.task_pop.pop5 > div.sm_icon.sm_close2   #点击关闭
    sleep   2s
    click element   css:body > div.warp > div > div.tree_jsBox > div.reloadBtn    #点击刷新

water
    click element   css:body > div.warp > div > div.tree_jsBox > div.tree_jsBtn.tree_jsbtn1
