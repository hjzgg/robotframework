*** Settings ***
library  SeleniumLibrary

#Resource            ../CommonResource.robot
Force Tags          MyTag


*** Keywords ***

#页面加载
loadNotContainPage
    [Arguments]    ${element}
    ${boolTest}    wait until page does not contain element    ${element}    5s
    run keyword if  ${boolTest}  log  页面正在加载,请等待
    ...  ELSE   loadNotContainPage     ${element}

loadContainPage
    [Arguments]    ${element}
    ${boolTest}    wait until page contains element    ${element}
    run keyword if  ${boolTest}  log  页面正在加载,请等待
    ...  ELSE   loadContainPage   ${element}

#流量工具
trafficEnough
    log     流量充足
    click element   id:checkServiceMargin
    sleep  5s
    GO BACK

#话费工具
phoneBillCheck
    log    话费页面
    # 点击充值
    click element  id:recharge
    sleep  5s
    GO BACK
    loadNotContainPage   xpath:/html/body/div[4]/div[2]/div[2]
    # 点击 扣费详情
    click element  id:chargDetail
    sleep  5s
    GO BACK
    loadNotContainPage   xpath:/html/body/div[4]/div[2]/div[2]
    # X月高额消费告警提示
    ${boolContain}  get text  xpath://*[@id="wrapper_index"]/div/ul/div[1]/div[2]/div[1]/div[4]/div[1]/span[2]   [text()]
    log  ${boolContain}

#套餐工具
setMealEnough
    log     余量充足
    #点击流量查看
    click element  xpath://*[@id="wrapper_index"]/div/ul/div[1]/div[3]/div[3]/div[1]/div[2]/div
    sleep  5s
    GO BACK
    loadNotContainPage   xpath:/html/body/div[4]/div[2]/div[2]
    #点击语言查看
    click element  xpath://*[@id="wrapper_index"]/div/ul/div[1]/div[3]/div[3]/div[2]/div[2]/div
    sleep  5s
    GO BACK
    loadNotContainPage   xpath:/html/body/div[4]/div[2]/div[2]

#积分工具
scoreEnough
    log    有可用积分
    #点击积分商城
    click element  xpath://*[@id="wrapper_index"]/div/ul/div[1]/div[4]/div[1]/div[1]/div/div[3]/span
    sleep  5s
    GO BACK
    loadNotContainPage   xpath:/html/body/div[4]/div[2]/div[2]
    #点击去兑换
    click element  xpath://*[@id="wrapper_index"]/div/ul/div[1]/div[4]/div[1]/div[3]/div[2]/div
    sleep  5s
    GO BACK
    loadNotContainPage   xpath:/html/body/div[4]/div[2]/div[2]

#和微币工具
hvbEnough
    log    有可兑换商品
    #点击和微币主页
    click element  xpath://*[@id="wrapper_index"]/div/ul/div[1]/div[6]/div[1]/div[1]/div/div[3]/span
    sleep  3s
    GO BACK
    loadNotContainPage   xpath:/html/body/div[4]/div[2]/div[2]
    # 点击可兑换
    click element  //*[@id="wrapper_index"]/div/ul/div[1]/div[6]/div[3]/div[1]/div[4]/div
    sleep  1s
    GO BACK
    loadNotContainPage   xpath:/html/body/div[4]/div[2]/div[2]