*** Settings ***
Library  SeleniumLibrary

*** Keywords ***
water
    click element       xpath://*[contains(@class,'jsbtn')]
    sleep   0.2s
    ${waterResult}  get text    css:body > div.toast_bubble.bubble_bg.bubble_bg1
    log     ${waterResult}
    sleep   2s

*** Testcases ***

First Login Yaoqianshu Test Case        #用户首次进入活动流程检查
    open browser    http://wx.10085.cn/hackertree/index?mobile=13523056035&nickname=五个臭皮匠   chrome  browserOptions={"mobileEmulation":{"deviceName":"Galaxy S5"}}
    wait until page contains element    css:body > div.warp > div  20s   摇钱树页面加载失败
    sleep   1s
    click element   css:body > div.guide_pop    #点击蒙层
    sleep   1s
    click element   css:body > div.warp > div > div.tree_model_box > div.tree_model > div.tree_add       #点击种树
    sleep   2s
    ${TreeAddResult}    Run Keyword And Return Status   Element Should Be Visible    css:body > div.warp > div > div.tree_model_box > div.tree_model > div.isTree.tree_m
    run keyword if     ${TreeAddResult}    log  种树成功
    ...     ELSE    log     种树失败
    ${UserLogoResult}    Run Keyword And Return Status   Element Should Be Visible    css:body > div.warp > div > div.tree_head_box > div.tree_user > div.tree_user_logo > img
    run keyword if     ${UserLogoResult}    log  头像加载成功
    ...     ELSE    log     头像加载失败

    water
