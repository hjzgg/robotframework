*** Settings ***
Library    SeleniumLibrary

#Resource            ../CommonResource.robot
Resource    F:/WorkSpace//robotframework/src/test/resources/robotframework/acceptance/YaoqianshuKeywords.robot
Force Tags          MyTag


*** Variables ***

${robotVar} =            FooBarBaz


*** Testcases ***
testcase
    open yaoqianshu

#Foo Test Case
#    [tags]              FooTag
#    [Documentation]     Created by John Doe
#    Do An Action        Argument
#    Do Another Action   ${robotVar}

#Tree add Test Case
#    open yaoqianshu
#    tree add

#Feedback Test Case
#    Open yaoqianshu
#    Feedback

#Small bell Test Case
#    Open yaoqianshu
#    small bell
#    Wait Until Element Is Visible   xpath://html/body/div[7]/div[7]/div[2]  10s
#    close small bell

#strategy Test Case
#    open yaoqianshu
#    strategy

#Invite Friends Test Case
#    open yaoqianshu
#    invite friends

#Task Test Case
#    open yaoqianshu
#    task

#Daily get water Task Test Case
#    open yaoqianshu
#    daily get water task
#    close task