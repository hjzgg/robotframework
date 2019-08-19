*** Settings ***
Library  SeleniumLibrary

#Resource            ../CommonResource.robot
Resource    F:/WorkSpace//robotframework/src/test/resources/robotframework/acceptance/YaoqianshuKeywords.robot
Force Tags          MyTag


*** Variables ***

${robotVar} =            FooBarBaz


*** Testcases ***

#Feedback Test Case      #意见反馈
#    open yaoqianshu
#    ${s}    Run Keyword And Return Status   Element Should Be Visible    css:body > div.warp > div > div.tree_model_box > div.tree_model > div.isTree.tree_m
#    run keyword if     ${s}   feedback
#    ...     ELSE    tree add    and     feedback


