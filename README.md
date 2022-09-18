# Ktor-Oauth2-Demo
 ## Intorduction
This is a illustrating code is based on Kotlin language and Kator Web frame, using Oauth2 as client. 


## Environment
 JDK1.8


## Run
### Prepare

* First get your own oauth2 app throuth github(https://juejin.im/post/5c98e743e51d45636053b363), then copy the client ID and client secret to the place that is labeled in the code.

* Directly run in IDE


### Demostrate log in

* Start
After run, insert http://0.0.0.0:8080/login to get to login website. 

* Operation：
Click on github to login through git hub. After loging in, username and email will be shown. 

*  Refresh token：
After log in, click refresh token to refresh and get information again


## Requireents
1. import io.Ktor package
2. set oauth2 client information:
Github
	name = "Github",
        authorizeUrl = "https://github.com/login/oauth/authorize",
        accessTokenUrl = "https://github.com/login/oauth/access_token",
        clientId = "********",//@Todo your code here
        clientSecret = "**************",//@Todo your code here
        requestMethod = HttpMethod.Post,
        defaultScopes = listOf("https://www.googleapis.com/auth/plus.login")

        
        
