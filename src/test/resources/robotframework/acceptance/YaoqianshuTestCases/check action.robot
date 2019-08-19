*** Settings ***
Library  SeleniumLibrary

*** Keywords ***
close action
    log   动态查看成功
    Click Element   css:body > div.pop > div.pop3.dy_pop > div.sm_close1.sm_icon    #关闭动态
close strategy
    log   攻略查看成功
    click element   id:click_index      #返回种树

*** Testcases ***

check action Test Case      #用户首页动态按钮数据展示流程检查（无好友进行偷取、助力浇水时）
    open browser    http://wx.10085.cn/hackertree/index?mobile=13523056619&nickname=YK   chrome  browserOptions={"mobileEmulation":{"deviceName":"Galaxy S5"}}
    wait until page contains element    css:body > div.warp > div  20s   摇钱树页面加载失败
    sleep   2s

    Click Element   css:body > div.warp > div > div.tree_head_box > div.tree_userJB > div.sm_icon.sm_lindan.tree_ld     #点击动态
    sleep   3s
    ${actionResult}    Run Keyword And Return Status   Element Should Be Visible    css:body > div.pop > div.pop3.dy_pop > div.pop3_con
    run keyword if     ${actionResult}   close action
    ...     ELSE    动态查看失败
    sleep   2s

    Click Element   css:body > div.warp > div > div.tree_model_box > div.tree_strategy      #点击攻略
    sleep   3s
    ${strategyResult}    Run Keyword And Return Status   Element Should Be Visible    css:body > div
    run keyword if     ${strategyResult}   close strategy
    ...     ELSE    攻略查看失败
    sleep   1s

    wait until page contains element    css:body > div.warp > div  20s   摇钱树页面加载失败
    sleep   2s

    click element   css:body > div.warp > div > div.tree_model_box > div.tree_proposal      #点击意见反馈
    sleep   1s
    input text  css:body > div.pop > div.pop1.popbg.opi_pop > div.pop1_con > textarea    我的意见反馈       #输入意见
    sleep   1s
    click element   css:body > div.pop > div.pop1.popbg.opi_pop > div.pop1_con > div.btn_icon.btn_5.opi_btn       #点击提交
    sleep   0.2s
    ${FeedbackResult}  get text    css:body > div.pop > div.suggest_bubble.bubble_bg.bubble_bg1
    log     '${FeedbackResult}'
    sleep   2s
    ${FeedbackExist}    Run Keyword And Return Status   Element Should Be Visible    css:body > div.pop > div.pop1.popbg.opi_pop
    run keyword if     ${FeedbackExist}   click element     css:body > div.pop > div.pop1.popbg.opi_pop > div.sm_close1.sm_icon

    sleep   2s
    click element   css:body > div.warp > div > div.tree_rwbtn_box > div.zsBtn_icon.zsbtn_1      #点击邀请好友
    sleep   2s
    click element   css:body > div.pop > div.ranking_pop.pop5 > div.ranking_foot > div     #点击好友排行榜弹窗中的邀请好友
    sleep   2s
    click element   css:body > div.sharePop
    sleep   2s
    click element   css:body > div.pop > div.ranking_pop.pop5 > div.sm_icon.sm_close2