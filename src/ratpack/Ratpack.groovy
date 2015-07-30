import org.pac4j.http.client.BasicAuthClient
import org.pac4j.http.credentials.SimpleTestUsernamePasswordAuthenticator
import org.pac4j.http.profile.UsernameProfileCreator
import ratpack.pac4j.RatpackPac4j
import ratpack.session.Session
import ratpack.session.clientside.ClientSideSessionModule

import static ratpack.groovy.Groovy.ratpack
import ratpack.session.SessionModule

ratpack {
	bindings {
		module SessionModule
	}

	handlers {

		all(RatpackPac4j.authenticator(new BasicAuthClient(new SimpleTestUsernamePasswordAuthenticator(), new UsernameProfileCreator())))

		prefix("auth"){
			//Require all requests past this point to have auth.
			all(RatpackPac4j.requireAuth(BasicAuthClient))
			get{
				render "An authenticated page."
			}
		}

		all {
			render "Hello World"
		}
	}
}
