*** Settings ***
Library  SeleniumLibrary

*** Keywords ***
buy water
    log     集市打开成功
    click element   css:body > div.pop > div.pop4.popbg.market_pop > div.ydjs_con > div.ydjs_con_shui.cont_js > div.btn_icon.btn_2      #点击买水
    sleep   0.2s
    ${buyWaterResult}  get text    css:body > div.pop > div.rcWater_bubble.bubble_bg.bubble_bg2 > div
    log     '${buyWaterResult}'
    sleep   1s

buy chuchongji
    log     集市打开成功
    click element   css:body > div.pop > div.pop4.popbg.market_pop > div.ydjs_con > div.ydjs_con_sc.cont_js > div.btn_icon.btn_2      #点击买除虫剂
    sleep   0.2s
    ${buyChuchongjiResult}  get text    css:body > div.pop > div.up_bubble.bubble_bg.bubble_bg1
    log     '${buyChuchongjiResult}'
    sleep   1s

close market
    click element   css:body > div.pop > div.pop4.popbg.market_pop > div.sm_icon.sm_close1
    sleep   2s

*** Testcases ***

market Test Case
    open browser    http://wx.10085.cn/hackertree/index?mobile=13523056619&nickname=YK   chrome  browserOptions={"mobileEmulation":{"deviceName":"Galaxy S5"}}
    wait until page contains element    css:body > div.warp > div  20s   摇钱树页面加载失败
    sleep   2s

    click element   css:body > div.warp > div > div.tree_rwbtn_box > div.zsBtn_icon.zsbtn_4     #点击集市
    sleep   2s

    ${marketExist}    Run Keyword And Return Status   Element Should Be Visible    css:body > div.pop > div.pop4.popbg.market_pop
    run keyword if     ${marketExist}   Run Keywords    buy water   AND     close market
    ...     ELSE    log     集市打开失败

    click element   css:body > div.warp > div > div.tree_rwbtn_box > div.zsBtn_icon.zsbtn_4     #点击集市
    sleep   2s

    ${marketExist}    Run Keyword And Return Status   Element Should Be Visible    css:body > div.pop > div.pop4.popbg.market_pop
    run keyword if     ${marketExist}   Run Keywords    buy chuchongji   AND     close market
    ...     ELSE    log     集市打开失败
