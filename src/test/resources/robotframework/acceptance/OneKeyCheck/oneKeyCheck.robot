*** Settings ***
library  SeleniumLibrary

#Resource            ../CommonResource.robot
Force Tags          MyTag


*** Test Cases ***

OpenOneKeyCheckPage
    open browser    http://wx.10085.cn/website/personalHome/new/onekeyDetection   chrome  browserOptions={"mobileEmulation":{"deviceName":"Galaxy S5"}}
    sleep   5s
    input text     id:phone   13969006536
    sleep   1s
    click element   id:getMsg
    input text     id:code     159011
    sleep   1s
    click button    id:loginBtn
    sleep   1s
    loadNotContainPage   xpath:/html/body/div[4]/div[2]/div[2]

Traffic
    #流量场景
    ${traffic}    get text  xpath://*[@id="wrapper_index"]/div/ul/div[1]/div[1]/div[1]/div[1]/div/div[3]/span   [text()]

    run keyword if  '${traffic}'=='流量充足'    trafficEnough
    ...  ELSE IF    '${traffic}'=='流量不足'    log     流量不足
    ...  ELSE   log     检测出现异常

PhoneBill
    #话费场景
    ${phoneBill}  get text  xpath://*[@id="wrapper_index"]/div/ul/div[1]/div[2]/div[1]/div[1]/div/div[3]/span   [text()]
    run keyword if  '${phoneBill}'=='话费充足'    log   话费充足
    ...  ELSE IF    '${phoneBill}'=='话费不足'    log  话费不足
    ...  ELSE   phoneBillCheck
SetMeal
    # 套餐场景
    ${setMeal}  get text  xpath://*[@id="wrapper_index"]/div/ul/div[1]/div[3]/div[1]/div[1]/div/div[3]/span   [text()]

    run keyword if  '${traffic}'=='余量充足'    setMealEnough
    ...  ELSE IF    '${traffic}'=='余量不足'    log     余量不足
    ...  ELSE   log     检测出现异常

Score
    #积分场景
    ${score}  get text  xpath://*[@id="wrapper_index"]/div/ul/div[1]/div[4]/div[1]/div[1]/div/div[3]/span   [text()]

    run keyword if  '${traffic}'=='有可用积分'    scoreEnough
    ...  ELSE IF    '${traffic}'=='无可用积分'    log     无可用积分
    ...  ELSE   log     检测出现异常

HVB
    #和微币场景
    ${hvb}  get text  xpath://*[@id="wrapper_index"]/div/ul/div[1]/div[6]/div[1]/div[1]/div/div[3]/span   [text()]

    run keyword if  '${traffic}'=='有可兑换商品'    hvbEnough
    ...  ELSE IF    '${traffic}'=='无可兑换商品'    log     无可兑换商品
    ...  ELSE   log     检测出现异常