*** Settings ***
Library  SeleniumLibrary

#Resource            ../CommonResource.robot
Force Tags          MyTag


*** Variables ***

${robotVar} =            FooBarBaz


*** Testcases ***

#View Strategy Test Case     #查看攻略
#    open browser    http://wx.10085.cn/hackertree/index?mobile=13523056037&nickname=五个臭皮匠   chrome  browserOptions={"mobileEmulation":{"deviceName":"Galaxy S5"}}
#    wait until page contains element    css:body > div.warp > div  20s   摇钱树页面加载失败
#    sleep   6s
#    click element   css:body > div.warp > div > div.tree_model_box > div.tree_model > div.tree_add       #点击种树
#    sleep   2s
#    wait until element is visible   css:body > div.warp > div > div.tree_model_box > div.tree_model > div.isTree.tree_m    5s     种树失败
#    Click Element   css:body > div.warp > div > div.tree_model_box > div.tree_strategy      #点击攻略
