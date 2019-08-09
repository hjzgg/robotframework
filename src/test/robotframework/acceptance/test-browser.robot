*** Settings ***
Library    SeleniumLibrary

*** Test Cases ***
Open-Baidu-Close
    Open Browser    https://www.baidu.com     chrome
    Close Browser

Open-Cnblogs-Capture
    Open Browser    https://www.cnblogs.com     chrome
    Capture Page Screenshot