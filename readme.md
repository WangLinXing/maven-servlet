## maven servlet
构建一个基于maven的servlet工程

说明：

这个工程的目的验证HttpSession的工作原理，使用的是maven工程增加了相关的依赖
外部依赖独立的tomcat.


相关知识点汇总：

**工程方面**
- 1 引入servlet的依赖组件
- 2 引入jsp的依赖组件
- 3 打war包配置
- 4 maven打包指令
- 5 开发的deploy.bat部署工具
- 6 在web.xml中配置servlet、session
- 7 验证了在maven工程中开发jsp的方法

**代码方面**

验证了HttpSession的工作原理：

- 1 写入sesson列表

当用户第一次提交请求时，服务端Servlet中执行到request.getSession()方法后，会自动创建一个Map.Entry对象，key为一个某种算法新生成的JSessionID，value则为新创建的HttpSession对象
这个过程就是创建Session

- 2 服务器生成并发送cookie

Session信息写入Session列表后，系统还会将JSESSION作为name,这个32位长的随机串作为value，以Cookie的形式放在Http Response Header中，把这个cookie 发送到客户端

- 3 客户端接收并发送cookie

客户端收到这个cookie之后会将其存放到浏览器的缓存中，只要客户端浏览器不关闭，浏览器中缓存的cookie就不会消失。

当用户提交第2此请求时，会将缓存中的这个cookie伴随请求头部信息一起发送给服务端。


- 4 从sessoion列表中查找

服务端从请求头中读取到客户端发送来的Cookie,并根据Cookie的JSESSIONID的值，从Map中查找响应key对应的value，即session对象，然后对该Session对象的域属性进行读写操作。


- 5 session失效

Web开发中引入的Session 概念，Session的失效就是指Session的超时，若某个Session在指定的时间范围内未被访问，那么Session将超时，即将失效。

在web.xml中可以通过<session-config>标签设置Session的超时时间，单位是分钟
默认Session的超时时间是30分钟，需要再次强调的是，这个时间并不是从Session被创建开始计时的生命周期时长，而是从最后一个被访问开始计时的，在指定的时长内一直未被访问的时长。

代码结构：
##### 1.创建index.html
在这个文件中创建一个form,提交一个get请求，传递一个username产生，请求发送到some

##### 2.创建一个UserLoginServlet
在doGet()中接收用户发送的username,然后调用
HttpSession session = request.getSession(true);创建一个与当前请求相关的Session
把客户端提交的数据写入session
session.setAttribute("username",username);
然后调用

PrintWriter out = response.getWriter();

out.println("SomeServlet username="+username);

out.println("SomeServlet  session:"+session);

输出数据到页面，这里没有指定Content-Type时PrintWriter就按普通字符串输出



### 1.构建pom
```xml
<?xml version="1.0" encoding="UTF-8"?>
<project xmlns="http://maven.apache.org/POM/4.0.0"
         xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
         xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 http://maven.apache.org/xsd/maven-4.0.0.xsd">
    <modelVersion>4.0.0</modelVersion>

    <groupId>org.abchina</groupId>
    <artifactId>helloservlet</artifactId>
    <version>1.0-SNAPSHOT</version>
    <packaging>war</packaging>

    <dependencies>
        <dependency>
            <groupId>javax.servlet</groupId>
            <artifactId>servlet-api</artifactId>
            <version>2.5</version>
            <scope>provided</scope>
        </dependency>
    </dependencies>

    <build>
        <plugins>
            <plugin>
                <groupId>org.apache.maven.plugins</groupId>
                <artifactId>maven-war-plugin</artifactId>
                <configuration>
                    <webResources>
                        <resource>
                            <directory>${project.basedir}/src/main/resources</directory>
                        </resource>
                    </webResources>
                    <warName>${project.artifactId}</warName>
                </configuration>
            </plugin>
        </plugins>
    </build>
</project>
```
说明：
- 需要引入javax.servlet
```xml
<dependency>
    <groupId>javax.servlet</groupId>
    <artifactId>servlet-api</artifactId>
    <version>2.5</version>
    <scope>provided</scope>
</dependency>
```
这里需要指定范围是provided因为通常容器会提供相关的api，因此这样设置之后打包后不会把这个jar包打到最终包里面
- 指定打包文件格式
```xml
<packaging>war</packaging>
```
这里指定打war包
- 引入打war包插件
```xml
<plugin>
    <groupId>org.apache.maven.plugins</groupId>
    <artifactId>maven-war-plugin</artifactId>
    <configuration>
        <webResources>
            <resource>
                <directory>${project.basedir}/src/main/resources</directory>
            </resource>
        </webResources>
        <warName>${project.artifactId}</warName>
    </configuration>
</plugin>
```

### 2.创建servlet
a.创建一个普通servlet
```java

import javax.servlet.Servlet;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * @Auther: wangyq
 * @Date: 2020/12/25
 * @Description: 一个普通的servlet
 * @Version: 1.0
 */
public class HelloServlet extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        System.out.println("========= HelloServlet");
        String username = req.getParameter("username");
        resp.getWriter().write("hello "+ username);
        resp.getWriter().flush();
    }
}
```

b.构建web.xml
在resource目录下创建WEB-INF\web.xml内容如下：
```xml
<?xml version="1.0" encoding="UTF-8"?>
<web-app xmlns="http://xmlns.jcp.org/xml/ns/javaee"
         xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
         xsi:schemaLocation="http://xmlns.jcp.org/xml/ns/javaee http://xmlns.jcp.org/xml/ns/javaee/web-app_4_0.xsd"
         version="4.0">
    
</web-app>
```

c.在web.xml中创建servlet配置
```xml
<servlet>
    <servlet-name>helloServlet</servlet-name>
    <servlet-class>com.abchina.servlet.HelloServlet</servlet-class>
</servlet>

<servlet-mapping>
    <servlet-name>helloServlet</servlet-name>
    <url-pattern>/hello</url-pattern>
</servlet-mapping>

```
### 3.打包

两种方式：
- a.Maven->Lifecycle->package

- b.执行mvn命令
在IDEA中点击窗口下面的Terminal 进入命令行界面，然后执行
mvn clean package
返回下面的结果：
```text
...
...
[INFO] Webapp assembled in [51 msecs]
[INFO] Building war: D:\IDEA_workspce\servlet-2\target\helloservlet.war
[INFO] WEB-INF\web.xml already added, skipping
[INFO] ------------------------------------------------------------------------
[INFO] BUILD SUCCESS
[INFO] ------------------------------------------------------------------------
[INFO] Total time: 3.064 s
[INFO] Finished at: 2020-12-25T08:46:37+08:00
[INFO] Final Memory: 17M/194M
[INFO] ------------------------------------------------------------------------

```
表示打包成功,可以看到打包后产生的target\helloservlet.war，进入tartet目录
使用jar指令查看这个war文件内容：

```text
D:\IDEA_workspce\servlet-2\target>jar -tf helloservlet.war
META-INF/
META-INF/MANIFEST.MF
WEB-INF/
WEB-INF/classes/
WEB-INF/classes/com/
WEB-INF/classes/com/abchina/
WEB-INF/classes/com/abchina/servlet/
WEB-INF/classes/WEB-INF/
WEB-INF/classes/com/abchina/servlet/HelloServlet.class
WEB-INF/classes/WEB-INF/web.xml
WEB-INF/web.xml
META-INF/maven/
META-INF/maven/org.abchina/
META-INF/maven/org.abchina/helloservlet/
META-INF/maven/org.abchina/helloservlet/pom.xml
META-INF/maven/org.abchina/helloservlet/pom.properties

D:\IDEA_workspce\servlet-2\target>

```
上面结果中 以/结尾的是目录
如果没有使用过jar指令，在终端里直接执行jar按回车，可以显示帮助信息
```text
D:\IDEA_workspce\servlet-2\target>jar
用法: jar {ctxui}[vfmn0PMe] [jar-file] [manifest-file] [entry-point] [-C dir] files ...
选项:
    -c  创建新档案
    -t  列出档案目录
    -x  从档案中提取指定的 (或所有) 文件
    -u  更新现有档案
    -v  在标准输出中生成详细输出
    -f  指定档案文件名
    -m  包含指定清单文件中的清单信息
    -n  创建新档案后执行 Pack200 规范化
    -e  为捆绑到可执行 jar 文件的独立应用程序
        指定应用程序入口点
    -0  仅存储; 不使用任何 ZIP 压缩
    -P  保留文件名中的前导 '/' (绝对路径) 和 ".." (父目录) 组件
    -M  不创建条目的清单文件
    -i  为指定的 jar 文件生成索引信息
    -C  更改为指定的目录并包含以下文件
如果任何文件为目录, 则对其进行递归处理。
清单文件名, 档案文件名和入口点名称的指定顺序
与 'm', 'f' 和 'e' 标记的指定顺序相同。

示例 1: 将两个类文件归档到一个名为 classes.jar 的档案中:
       jar cvf classes.jar Foo.class Bar.class
示例 2: 使用现有的清单文件 'mymanifest' 并
           将 foo/ 目录中的所有文件归档到 'classes.jar' 中:
       jar cvfm classes.jar mymanifest -C foo/ .

D:\IDEA_workspce\servlet-2\target>
```
### 5.部署
1.把打包好的helloservlet.war文件复制到tomcat的webapps目录下
2.启动tomcat

启动tomcat后会解压helloservlet.war创建helloservlet目录
进入helloservlet目录进入dos模式，执行tree /f可以看到该目录的详细结构以及各目录下的文件，其实内容和用jar -tf
看到的内容是一致的，下面的index.html是我为了测试html后来增加的

```text
E:\software_tools\apache-tomcat-6.0.43\webapps\helloservlet>tree /f
卷 新加卷 的文件夹 PATH 列表
卷序列号为 DCC9-C4D6
E:.
│  index.html
│
├─META-INF
│  │  MANIFEST.MF
│  │
│  └─maven
│      └─org.abchina
│          └─helloservlet
│                  pom.properties
│                  pom.xml
│
└─WEB-INF
    │  web.xml
    │
    └─classes
        ├─com
        │  └─abchina
        │      └─servlet
        │              HelloServlet.class
        │
        └─WEB-INF
                web.xml
E:\software_tools\apache-tomcat-6.0.43\webapps\helloservlet>
```
### 6.测试
- 1.启动tomcat，假如tomcat监听的端口是8080

- 2.启动浏览器
  在地址栏输入:http://localhost:8080/helloservlet/hello?username=zhangsasn
  
  浏览器返回：
  hello zhangsan
  
**注意**

url中的helloservlet其实是pom.xml中的artifactId
打包之后产生的文件是helloservlet.war,在tomcat中部署之后产生的路径是helloservlet
因此访问时需要带helloservlet


### 7.增加jsp依赖
```xml
<dependency>
    <groupId>javax.servlet.jsp</groupId>
    <artifactId>jsp-api</artifactId>
    <version>2.1</version>
    <scope>provided</scope>
</dependency>
```