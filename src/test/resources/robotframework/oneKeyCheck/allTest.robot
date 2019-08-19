*** Settings ***
library  SeleniumLibrary

#Resource            ../CommonResource.robot
Force Tags          MyTag


*** Test Cases ***

OpenOneKeyCheckPage
    open browser    http://wx.10085.cn/website/personalHome/new/onekeyDetection   chrome  browserOptions={"mobileEmulation":{"deviceName":"Galaxy S5"}}
    sleep   5s
    input text     id:phone   18838956395
    sleep   1s
    click element   id:getMsg
    input text     id:code     100085
    sleep   1s
    click button    id:loginBtn
    sleep   1s
    loadNotContainPage   xpath:/html/body/div[4]/div[2]/div[2]

Traffic
    #流量场景
    ${traffic}    get text  xpath://*[@id="wrapper_index"]/div/ul/div[1]/div[1]/div[1]/div[1]/div/div[3]

    run keyword if  '${traffic}'=='余量充足'    trafficEnough
    ...  ELSE IF    '${traffic}'=='余量不足'    log     流量不足
    ...  ELSE   log     检测出现异常

PhoneBill
    #话费场景
    ${charge}  get text  xpath://*[@id="recharge"]
    run keyword if  '${charge}'=='充值'  phoneBillNotEnoughCheck
    ...  ELSE   phoneBillEnoughCheck

SetMeal
    # 套餐场景
    ${setMeal}  get text    jquery:#wrapper_index > div > ul > div.main > div.setMeal > div.card > div.head > div > div.right > span
    run keyword if  '${setMeal}'=='余量充足'    setMealEnough
    ...  ELSE IF    '${setMeal}'=='余量不足'    log     余量不足
    ...  ELSE   log     检测出现异常

Score
    #积分场景
    ${score}  get text  css:html body.bge1e9f7 div#wrapper_index.wrapper_index div.scroller ul div.main div.integral div.card div.head div.title div.right span

    run keyword if  '${score}'=='有可用积分'    scoreEnough
    ...  ELSE IF    '${score}'=='无可用积分'    log     无可用积分
    ...  ELSE   log     检测出现异常

HVB
    #和微币场景
    ${hvb}  get text  css:html body.bge1e9f7 div#wrapper_index.wrapper_index div.scroller ul div.main div.hvb div.card div.head div.title div.right span

    run keyword if  '${hvb}'=='有可兑换商品'    hvbEnough
    ...  ELSE IF    '${hvb}'=='无可兑换商品'    log     无可兑换商品
    ...  ELSE   log     检测出现异常

*** Keywords ***

#页面加载
loadNotContainPage
    [Arguments]    ${element}
    ${boolTest}    wait until element is not visible  ${element}    30s

#流量工具
trafficEnough
    log     流量充足
    click element   id:checkServiceMargin
    sleep  5s
    GO BACK
    loadNotContainPage   xpath:/html/body/div[4]/div[2]/div[2]

#话费工具
phoneBillNotEnoughCheck
    log    话费页面
    # 点击充值
    click element  id:recharge
    sleep  5s
    GO BACK
    loadNotContainPage   xpath:/html/body/div[4]/div[2]/div[2]
    phoneBillEnoughCheck

phoneBillEnoughCheck
    # 点击 扣费详情
    click element  id:chargDetail
    sleep  5s
    GO BACK
    loadNotContainPage   xpath:/html/body/div[4]/div[2]/div[2]
    # X月高额消费告警提示
    ${boolContain}  get text  css:.desc-text
    log  ${boolContain}

#套餐工具
setMealEnough
    log     余量充足
    #点击流量查看
    click element  css:div.row:nth-child(1) > div:nth-child(2) > div:nth-child(1)
    sleep  5s
    GO BACK
    loadNotContainPage   xpath:/html/body/div[4]/div[2]/div[2]
    #点击语音查看
    click element  css:div.row:nth-child(2) > div:nth-child(2) > div:nth-child(1)
    sleep  5s
    GO BACK
    loadNotContainPage   xpath:/html/body/div[4]/div[2]/div[2]

#积分工具
scoreEnough
    log    有可用积分
    #点击积分商城
    click element  css:html body.bge1e9f7 div#wrapper_index.wrapper_index div.scroller ul div.main div.integral div.card div.head div.title div.right span
    sleep  5s
    GO BACK
    loadNotContainPage   xpath:/html/body/div[4]/div[2]/div[2]
    #点击去兑换
    click element  css:html body.bge1e9f7 div#wrapper_index.wrapper_index div.scroller ul div.main div.integral div.card div.result div.button div
    sleep  5s
    GO BACK
    loadNotContainPage   xpath:/html/body/div[4]/div[2]/div[2]

#和微币工具
hvbEnough
    log    有可兑换商品
    #点击和微币主页
    click element  css:html body.bge1e9f7 div#wrapper_index.wrapper_index div.scroller ul div.main div.hvb div.card div.head div.title div.right span
    sleep  3s
    GO BACK
    loadNotContainPage   xpath:/html/body/div[4]/div[2]/div[2]
    # 点击可兑换
    click element  css:html body.bge1e9f7 div#wrapper_index.wrapper_index div.scroller ul div.main div.hvb div.card div.result div.button div
    sleep  1s
    GO BACK
    loadNotContainPage   xpath:/html/body/div[4]/div[2]/div[2]