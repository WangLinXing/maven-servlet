echo 'start deploy helloservlet.war'

rem '删除tomcat下的helloservlet 目录下所有内容，使用安静删除模式'
rmdir /q /s E:\wangyq\software_tools\apache-tomcat-8.5.57\apache-tomcat-8.5.57\webapps\helloservlet

rem '删除旧的war文件'
del /Q /S E:\wangyq\software_tools\apache-tomcat-8.5.57\apache-tomcat-8.5.57\webapps\helloservlet.war

rem '复制新的war文件'
copy target\helloservlet.war   E:\wangyq\software_tools\apache-tomcat-8.5.57\apache-tomcat-8.5.57\webapps\helloservlet.war
echo 'deploy ok.'