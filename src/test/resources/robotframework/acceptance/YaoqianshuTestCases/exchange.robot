*** Settings ***
Library  SeleniumLibrary

*** Variables ***
${yaoqianshuURL} =            http://wx.10085.cn/hackertree/index?mobile=13523056619&nickname=YK

*** Keywords ***
get reward
    click element   css:body > div.pop > div.excheng_pop.pop5 > div.pop_conbg.pop_exchange_box > div > div:nth-child(1) > div.exchange_btn > div
    wait until page contains element    css:body > div.warp > div > div.game_bor  20s   摇钱转盘页面加载失败
    sleep   1s
    click element   css:body > div.warp > div > div.game_bor > div.geme_clickBtn
    sleep   1s
    ${notEnoughExist}    Run Keyword And Return Status   Element Should Be Visible    css:body > div.pop > div.pop2.popbg.wbbz_pop
    ${noSessionExist}    Run Keyword And Return Status   Element Should Be Visible    css:body > div.pop > div.pop2.popbg.nosession_pop
    ${notGainExist}    Run Keyword And Return Status   Element Should Be Visible    css:body > div.pop > div.pop2.popbg.wzj_pop
    run keyword if  ${notEnoughExist}   Run Keywords    click gainJinbi    AND     return tree
    ...     ELSE IF     ${notEnoughExist}   Run Keywords   click OK    AND     return tree
    ...     ELSE IF     ${notGainExist}   Run Keywords     click again    AND     return tree
    ...     ELSE    return tree

close getReward pop
    [Arguments]    ${logcontent}    ${locator}
    log    ${logcontent}
    click element   ${locator}

click gainJinbi
    click element   css:body > div.pop > div.pop2.popbg.wbbz_pop > div.pop2_con > div.btn_icon.btn_5
    sleep   2s

click OK
    click element   css:body > div.pop > div.pop2.popbg.nosession_pop > div.pop2_con > div.btn_icon.btn_5
    sleep   2s

click again
    click element   css:body > div.pop > div.pop2.popbg.wzj_pop > div.pop2_con > div.btn_icon.btn_5.btn_close
    sleep   2s

return tree
    go to   ${yaoqianshuURL}
    wait until page contains element    css:body > div.warp > div  20s   摇钱树页面加载失败
    sleep   2s

*** Testcases ***

exchange Test Case
    open browser    ${yaoqianshuURL}   chrome  browserOptions={"mobileEmulation":{"deviceName":"Galaxy S5"}}
    wait until page contains element    css:body > div.warp > div  20s   摇钱树页面加载失败
    sleep   2s

    click element   css:body > div.warp > div > div.tree_rwbtn_box > div.zsBtn_icon.zsbtn_3     #点击兑换
    sleep   2s

    ${exchangeExist}    Run Keyword And Return Status   Element Should Be Visible    css:body > div.pop > div.excheng_pop.pop5
    run keyword if     ${exchangeExist}   get reward
    ...     ELSE    log     摇钱树页面打开失败
