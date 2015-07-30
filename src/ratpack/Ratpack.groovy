import io.netty.handler.codec.http.HttpHeaderNames
import org.pac4j.core.profile.UserProfile
import org.pac4j.http.client.BasicAuthClient
import org.pac4j.http.credentials.SimpleTestUsernamePasswordAuthenticator
import org.pac4j.http.profile.UsernameProfileCreator
import org.pac4j.oauth.client.TwitterClient
import ratpack.pac4j.RatpackPac4j
import ratpack.session.Session
import ratpack.session.clientside.ClientSideSessionModule

import static ratpack.groovy.Groovy.htmlBuilder
import static ratpack.groovy.Groovy.ratpack
import ratpack.session.SessionModule

ratpack {
	bindings {
		module SessionModule
	}

	handlers {
		all(RatpackPac4j.authenticator(new BasicAuthClient(new SimpleTestUsernamePasswordAuthenticator(), new UsernameProfileCreator()), new TwitterClient("key", "secret")))

		prefix("auth") {
			//Require all requests past this point to have auth.
			all({ ctx ->
				RatpackPac4j.userProfile(ctx).then { opUp ->
					if (opUp.isPresent()) {
						ctx.next(single(opUp.get()))
					} else {
						ctx.redirect(302, "/login")
					}
				}
			})

			get { UserProfile userProfile ->
				render "An authenticated page. ${userProfile.getId()}"
			}
		}

		get("twitterLogin") { ctx ->
			RatpackPac4j.login(ctx, TwitterClient).then {
				ctx.redirect(302, "/auth")
			}
		}

		get("basicLogin") { ctx ->
			RatpackPac4j.login(ctx, BasicAuthClient).then {
				ctx.redirect(302, "/auth")
			}
		}

		get("login") {
			response.headers.add(HttpHeaderNames.CONTENT_TYPE, "text/html")
			render """<html>
<body>
<ul>
<li><a href="/twitterLogin">Twitter Login</a></li>
<li><a href="/basicLogin">Basic Login</a></li>
</ul>
</body>
</html>
"""
		}

		get("logout") { ctx ->
			RatpackPac4j.logout(ctx).then {
				render "Logged out"
			}
		}

		all {
			render "Hello World"
		}
	}
}
