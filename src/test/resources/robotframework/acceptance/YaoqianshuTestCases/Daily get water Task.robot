*** Settings ***
Library  SeleniumLibrary

#Resource            ../CommonResource.robot
Force Tags          MyTag


*** Variables ***

${robotVar} =            FooBarBaz


*** Testcases ***

#Daily get water Task Test Case      #验证活动做任务中每日任务每日领水任务功能流程检查
#    open browser    http://wx.10085.cn/hackertree/index?mobile=13523056030&nickname=五个臭皮匠   chrome  browserOptions={"mobileEmulation":{"deviceName":"Galaxy S5"}}
#    wait until page contains element    css:body > div.warp > div  20s   摇钱树页面加载失败
#    sleep   6s
#    click element   css:body > div.warp > div > div.tree_model_box > div.tree_model > div.tree_add       #点击种树
#    sleep   2s
#    wait until element is visible   css:body > div.warp > div > div.tree_model_box > div.tree_model > div.isTree.tree_m    5s     种树失败
#
#    click element   css:body > div.warp > div > div.tree_rwbtn_box > div.zsBtn_icon.zsbtn_2      #点击做任务
#    sleep   3s
#    click element   css:body > div.pop > div.task_pop.pop5 > div.pop_conbg.pop5_water_box > div > div:nth-child(1) > div.water_task_con > div.water_task_bot > span.btn_icon.btn_2      #点击领取
#    sleep   3s
#    click element   css:body > div.pop > div.task_pop.pop5 > div.sm_icon.sm_close2  #关闭做任务弹窗
#    sleep   3s
#    click element   css:body > div.warp > div > div.tree_jsBox > div.reloadBtn
#    sleep   2s