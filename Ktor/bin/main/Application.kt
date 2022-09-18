package com.final

import io.ktor.application.*
import io.ktor.request.*
import io.ktor.routing.*
import io.ktor.http.*
import io.ktor.html.*
import kotlinx.html.*
import io.ktor.locations.*
import io.ktor.sessions.*
import io.ktor.features.*
import io.ktor.auth.*
import io.ktor.client.*
import io.ktor.client.engine.apache.*
import io.ktor.client.request.get
import io.ktor.http.content.resources
import io.ktor.http.content.static
import io.ktor.jackson.jackson
import io.ktor.response.respondText

fun main(args: Array<String>): Unit = io.ktor.server.netty.EngineMain.main(args)
val loginProviders = listOf(
    //一个 github的例子。获取clientId 与 clientSecret 可参照https://juejin.im/post/5c98e743e51d45636053b363
    OAuthServerSettings.OAuth2ServerSettings(
        name = "Github",
        authorizeUrl = "https://github.com/login/oauth/authorize",
        accessTokenUrl = "https://github.com/login/oauth/access_token",
        clientId = "********",//@Todo your code here
        clientSecret = "**************",//@Todo your code here
        requestMethod = HttpMethod.Post,
        defaultScopes = listOf("https://www.googleapis.com/auth/plus.login")
    )
    //可添加其它服务器
).associateBy {it.name}
@Location("/login/{type?}") class login(val type: String = "")
@Suppress("unused") // Referenced in application.conf
@kotlin.jvm.JvmOverloads
fun Application.module(testing: Boolean = false) {
    install(Locations)
    install(Sessions) {
        cookie<MySession>("oauthSampleSessionId") {
        }
    }
    install(CallLogging)
    install(ContentNegotiation) {
        jackson {}
    }
    install(Authentication) {
        //由client id  和client secret获取token并导向重定向URL
        oauth("LoginOAuth") {
            client = HttpClient(Apache)
            providerLookup = {
                loginProviders[application.locations.resolve<login>(login::class, this).type]
            }
            urlProvider = { p -> redirectUrl(login(p.name), false) }
        }
    }
    routing {
        static("/static") {
            resources("static")
        }
        authenticate("LoginOAuth") {
            location<login>() {
                //client ID或client secret或redirect URL错误
                param("error") {
                    handle {
                        call.loginFailedPage(call.parameters.getAll("error").orEmpty())
                    }
                }
                handle {
                    val principal = call.authentication.principal<OAuthAccessTokenResponse.OAuth2>()
                    if (principal != null) {
                        //检查登录的是哪个页面
                        val uri = call.request.uri
                        val start = uri.indexOf("login")+6
                        val end = uri.indexOf("?")
                        val place = uri.substring(start until end)
                        lateinit var json:String
                        //call.respondText { principal.accessToken }     //检查是否得到token
                        //以下利用token摘取页面信息
                        when(place) {
                            "Github" -> {
                                json = HttpClient(Apache).get<String>("https://api.github.com/user?") {
                                    headers["Authorization"] = "Bearer " + "${principal.accessToken}"
                                }
                            }
                            //相应的可添加其它服务器的资源URL，注意token要写在header里面
                            else ->{
                                json = " "
                            }
                        }
                        val name= read("name",json)
                        val e = read("email",json)
                        val id = read("id",json)
                        val email = if(e!="ul")e else "No email shown"
                        if (name.length<=20) {
                            call.sessions.set(MySession(name,id,principal.accessToken))
                        }
                        call.loggedInSuccessResponse(name,email)
                    } else {
                        val session = call.sessions.get<MySession>()
                        call.loginPage(session?.name)
                    }
                }
            }
        }
    }
}
//重定向URL
private fun <T : Any> ApplicationCall.redirectUrl(t: T, secure: Boolean = true): String {
    val hostPort = request.host()!! + request.port().let { port -> if (port == 80) "" else ":$port" }
    val protocol = when {
        secure -> "https"
        else -> "http"
    }
    return "$protocol://$hostPort${application.locations.href(t)}"
}
//登陆页
private suspend fun ApplicationCall.loginPage(name: String?) {
    respondHtml {
        head {
            title { +"Login with" }
            link(rel = "stylesheet", href = "/static/styles.css" ,type = "text/css")
        }
        body {
            div ("box1"){
                }
            h1 {
                    +"Hello $name"
                }
            h1{
                    +"Login with:"
                }
            for (p in loginProviders) {
                a(href = application.locations.href(login(p.key))){
                    button(classes = "button"){
                        +p.key
                    }
                }
                h2{
                    "         "
                }
            }
        }
    }
}
//失败
private suspend fun ApplicationCall.loginFailedPage(errors: List<String>) {
    respondHtml {
        head {
            title { +"Login with" }
            link(rel = "stylesheet", href = "/static/styles.css" ,type = "text/css")
        }
        body {
            h1 {
                +"Login error"
            }
            div (classes = "box1"){
            }
            for (e in errors) {
                p {
                    +e
                }
            }
        }
    }
}
//成功
private suspend fun ApplicationCall.loggedInSuccessResponse(name: String, email: String) {
    respondHtml {
        head {
            title { +"Logged in" }
            link(rel = "stylesheet", href = "/static/styles.css" ,type = "text/css")
        }
        body {
            div("box1") {
            }
            h1 {
                +"Welcom"
            }
            val list = listOf<String>(name, email)
            for (i in list) {
                h2 { +i }
            }
            a(href = "/refresh") {
                button(classes = "button") {
                    +"refresh"
                }
            }
        }
    }
}

//摘取信息的方法
inline fun read( para:String, json : String):String{
    val na = json.substringAfter(para)
    val name = na.substringBefore(",")
    return name.substring(3 until name.length-1)
}
