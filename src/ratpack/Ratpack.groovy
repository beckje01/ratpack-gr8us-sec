import org.pac4j.oauth.client.TwitterClient
import ratpack.pac4j.RatpackPac4j


import static ratpack.groovy.Groovy.ratpack
import ratpack.session.SessionModule

ratpack {
	bindings {
		module SessionModule
	}

	handlers {

		all(RatpackPac4j.authenticator(new TwitterClient("key", "secret")))

		prefix("auth") {
			//Require all requests past this point to have auth.
			all(RatpackPac4j.requireAuth(TwitterClient))
			get {
				render "An authenticated page."
			}
		}

		all {
			render "Hello World"
		}
	}
}
