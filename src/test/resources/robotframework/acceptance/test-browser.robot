*** Settings ***
Library    SeleniumLibrary

*** Test Cases ***
Open-Baidu-Close
    Open Browser    https://www.baidu.com     chrome       browserOptions={"mobileEmulation":{"deviceName":"Galaxy S5"}}
    Close Browser

Open-Cnblogs-Capture
    Open Browser    https://www.cnblogs.com     browserName=chrome      browserOptions={"mobileEmulation":{"deviceName":"Nexus 5"}}
    Capture Page Screenshot
    Close Browser