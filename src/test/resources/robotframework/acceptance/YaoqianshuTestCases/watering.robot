*** Settings ***
Library  SeleniumLibrary

*** Keywords ***
task
    click element   css:body > div.warp > div > div.tree_rwbtn_box > div.zsBtn_icon.zsbtn_2      #点击做任务
    sleep   3s
    click element   css:body > div.pop > div.task_pop.pop5 > div.pop_conbg.pop5_water_box > div > div:nth-child(1) > div.water_task_con > div.water_task_bot > span.btn_icon.btn_2      #点击领取
    sleep   3s
    click element   css:body > div.pop > div.task_pop.pop5 > div.sm_icon.sm_close2  #关闭做任务弹窗
    sleep   3s
    click element   css:body > div.warp > div > div.tree_jsBox > div.reloadBtn      #点击刷新
    sleep   2s

water
    click element       xpath://*[contains(@class,'jsbtn')]
    sleep   0.2s
    ${waterResult}  get text    css:body > div.toast_bubble.bubble_bg.bubble_bg1
    log     ${waterResult}
    sleep   2s

*** Testcases ***

watering Test Case
    open browser    http://wx.10085.cn/hackertree/index?mobile=13523056619&nickname=YK   chrome  browserOptions={"mobileEmulation":{"deviceName":"Galaxy S5"}}
    wait until page contains element    css:body > div.warp > div  20s   摇钱树页面加载失败
    sleep   2s
    ${enableWater}  get text    xpath://*[contains(@class,'jsbtn')]
    run keyword if     '${enableWater}'=='0g 浇水'    Run Keywords    task    AND     water
    ...     ELSE    water

