*** Settings ***
library  SeleniumLibrary

#Resource            ../CommonResource.robot
Force Tags          MyTag


*** Variables ***

${robotVar} =            FooBarBaz


*** Testcases ***

view store Test Case

    open browser    http://wx.10085.cn/hackertree/index?mobile=13910042424&nickname=五个臭皮匠   chrome  browserOptions={"mobileEmulation":{"deviceName":"Galaxy S5"}}
    wait until page contains element    css:body > div.warp > div  20s   摇钱树页面加载失败
    sleep   6s
#    click element   css:body > div.warp > div > div.tree_model_box > div.tree_model > div.tree_add       #点击种树
#    sleep   2s
#    wait until element is visible   css:body > div.warp > div > div.tree_model_box > div.tree_model > div.isTree.tree_m    5s     种树失败

    click element   css:body > div.warp > div > div.tree_rwbtn_box > div.zsBtn_icon.zsbtn_2      #点击做任务
    sleep   2s

    click element   css:body > div.pop > div.task_pop.pop5 > div.pop_conbg.pop5_water_box > div > div:nth-child(4) > div.water_task_con > div.water_task_bot > span.btn_icon.btn_2      #浏览店铺任务——去完成
    sleep   3s

    input text     id:phone     13910042424
    sleep   1s
    click element   id:getMsg
    input text     id:code     159011
    sleep   1s
    click element   id:loginBtn
    sleep   65s
    Go Back