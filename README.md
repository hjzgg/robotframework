
使用Robot Framework Maven Plugin（http://robotframework.org/MavenPlugin/）执行自动化测试

chromedriver下载： http://chromedriver.storage.googleapis.com/index.html
chromedriver和chrome版本对应关系：https://sites.google.com/a/chromium.org/chromedriver/downloads
chromedriver直接放到chrome所在目录

插件执行，需要指定chromedriver位置
robotframework:run "-Dwebdriver.chrome.driver=C:\Program Files (x86)\Google\Chrome\Application/chromedriver.exe"
