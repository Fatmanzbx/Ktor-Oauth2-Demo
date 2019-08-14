# Ktor-Oauth2-Demo
 ## 简介
 本程序是Github的客户端演示代码，作为Oauth2的client，基于Kotlin语言的Ktor异步Web框架开发
Github是Oauth2的认证服务器(Authentication Server)，同时也是资源服务器


## 环境
 JDK1.8


## 运行
### 演示普通调用第三方认证服务器登录（认证和授权）

* 首先在GitHub登录，注册一个自己的oauth2 app,  具体啊过程参考https://juejin.im/post/5c98e743e51d45636053b363。然后将生成的client ID 与client secret复制下来放到代码中标志处

* 在IDE中直接运行即可


### 演示单点登录

* 启动项目
运行后输入网址http://0.0.0.0:8080/login，即可进入登录界面

* 操作：
点Github按钮即可通过GitHub认证登陆
登录后会显示用户名与邮箱

*  Refresh token：（未完成）
在登录之后点击refresh token按钮，可以使用旧的token更新 token并再次获取资源


## 关键配置说明
1. 引入io.ktor相关包
2. 配置oauth2 client信息:
Github
	name = "Github",
        authorizeUrl = "https://github.com/login/oauth/authorize",
        accessTokenUrl = "https://github.com/login/oauth/access_token",
        clientId = "********",//@Todo your code here
        clientSecret = "**************",//@Todo your code here
        requestMethod = HttpMethod.Post,
        defaultScopes = listOf("https://www.googleapis.com/auth/plus.login")

        
        
##关于Ktor
Ktor所使用的kotlin本质上与java类似，但是代码更加简洁更加贴近自然语言。
Ktor 是一个使用强大的 Kotlin 语言在互联系统中构建异步服务器与客户端的框架。
