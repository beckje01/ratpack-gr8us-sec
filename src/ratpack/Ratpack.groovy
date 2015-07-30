import ratpack.session.Session
import ratpack.session.clientside.ClientSideSessionModule

import static ratpack.groovy.Groovy.ratpack
import ratpack.session.SessionModule

ratpack {
	bindings {
		module SessionModule
		module(ClientSideSessionModule, { config ->
			config.setSessionCookieName("s1")
			config.setSecretToken("fakeToken")
		})
	}

	handlers {
		all {
			if (request.headers['Authorization'] != "Token faketoken") {
				response.status(401)
				//We must send some response or the request will hang.
				response.send()
			} else {
				//We can choose to do nothing but allow the next handler in the chain to deal with the request.
				def session = context.get(Session)

				//Set some session data if we don't already have it
				session.get("example").then { val ->
					session.set("example", val.orElse("Galaxy")).then {
						next()
					}
				}

			}
		}

		get("add") { Session session ->
			session.get("example").then {
				def str = it.orElse("Pluto") + "+1"
				session.set("example", str).then {
					render "Set: " + str
				}
			}
		}

		get("term") { Session session ->
			session.terminate().then {
				render "Terminated the session"
			}
		}

		all {
			context.get(Session).get("example").then { value ->
				render "Hello " + value.orElse("World")
			}
		}
	}
}
