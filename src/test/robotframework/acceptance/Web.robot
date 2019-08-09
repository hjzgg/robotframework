*** Settings ***
Library    SeleniumLibrary

*** Test Cases ***
Open cnblogs
    Open Browser    https://www.cnblogs.com     chrome
#    Close Browser
    Capture Page Screenshot