*** Settings ***
Library  SeleniumLibrary

#Resource            ../CommonResource.robot
Force Tags          MyTag


*** Variables ***

${robotVar} =            FooBarBaz


*** Testcases ***

#Invite Friends Test Case        #种树页面点击邀请好友按钮功能流程检查
#    open browser    http://wx.10085.cn/hackertree/index?mobile=13523056036&nickname=五个臭皮匠   chrome  browserOptions={"mobileEmulation":{"deviceName":"Galaxy S5"}}
#    wait until page contains element    css:body > div.warp > div  20s   摇钱树页面加载失败
#    sleep   6s
#    click element   css:body > div.warp > div > div.tree_model_box > div.tree_model > div.tree_add       #点击种树
#    sleep   2s
#    wait until element is visible   css:body > div.warp > div > div.tree_model_box > div.tree_model > div.isTree.tree_m    5s     种树失败
#    click element   css:body > div.warp > div > div.tree_rwbtn_box > div.zsBtn_icon.zsbtn_1      #点击邀请好友
#    sleep   2s
#    click element   css:body > div.pop > div.ranking_pop.pop5 > div.ranking_foot > div     #点击好友排行榜弹窗中的邀请好友

