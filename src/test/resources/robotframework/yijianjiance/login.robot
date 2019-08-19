*** Settings ***
Library  SeleniumLibrary

*** Test Cases ***
LOGIN-CHECK
    Open Browser    http://wx.10085.cn/website/personalHome/new/onekeyDetection     chrome      browserOptions={"mobileEmulation":{"deviceName":"Galaxy S5"}}
    登录
    input text     id:phone     13910042424
    sleep   1s
    click element   id:getMsg
    sleep   1s
    input text     id:code     159011
    sleep   1s
    click element   id:loginBtn
    sleep   5s