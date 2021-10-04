package com.redhat.runtimes.api;

import com.redhat.runtimes.services.TodosService;
import com.redhat.runtimes.services.TodosServiceImpl;
import com.redhat.runtimes.services.UserService;
import com.redhat.runtimes.services.UserServiceImpl;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.Future;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.RoutingContext;
import io.vertx.core.Promise;
import io.vertx.ext.web.openapi.RouterBuilder;
import io.vertx.pgclient.PgConnectOptions;
import io.vertx.pgclient.PgPool;
import io.vertx.serviceproxy.ServiceBinder;
import io.vertx.sqlclient.PoolOptions;
import io.vertx.sqlclient.SqlClient;
import org.jooq.Configuration;
import org.jooq.SQLDialect;
import org.jooq.impl.DefaultConfiguration;

/**
 * Main Vert.x Verticle, entrypoint for this application
 */
public class MainVerticle extends AbstractVerticle {

	/**
	 * Iterate over the operations in the API Spec and delegate the handlers to the
	 * Web API Services
	 * @param routeBuilder The OpenAPIv3 RouterBuilder instance
	 */
	Future<RouterBuilder> mountRoutes(RouterBuilder routeBuilder) {
		routeBuilder.mountServicesFromExtensions();

		routeBuilder.securityHandler("KeyCloak", this::authHandler);

		return Future.succeededFuture(routeBuilder);
	}

	/**
	 * Given an instance of {@link RoutingContext}, try to authenticate the user
	 * @param ctx The {@link RoutingContext} of the current request
	 */
	private void authHandler(RoutingContext ctx) {
		// TODO: Implement Authentication handler
		ctx.next();
	}

	@Override
	public void start(Promise<Void> startPromise) throws Exception {
		
		
		Configuration jooqConfig = new DefaultConfiguration();
		jooqConfig.set(SQLDialect.POSTGRES);
		
		PgConnectOptions poolConfig = new PgConnectOptions(vertx.getOrCreateContext().config().getJsonObject("db"));
		
		PoolOptions poolOptions = new PoolOptions()
				.setMaxSize(5);
		
		SqlClient client = PgPool.client(poolConfig, poolOptions);
		
		
		TodosService todoService = new TodosServiceImpl(jooqConfig, client);
		ServiceBinder todoSvcBinder = new ServiceBinder(vertx);
		todoSvcBinder.setAddress("api.todos").register(TodosService.class, todoService);

		UserService userService = new UserServiceImpl(jooqConfig, client);
		ServiceBinder userSvcBinder = new ServiceBinder(vertx);
		userSvcBinder.setAddress("api.user").register(UserService.class, userService);

		RouterBuilder.create(vertx, "openapi.yml")
				.compose(this::mountRoutes)
				.compose(this::buildParentRouter)
				.compose(this::buildHttpServer)
				.onComplete(startPromise);
	}

	/**
	 * Given a {@link Router}, use it as the handler for a newly created HTTP Server
	 * @param router The {@link Router} for handling requests
	 * @return A {@link Future} of type {@link Void} indicating success or failure
	 */
	private Future<Void> buildHttpServer(Router router) {
		return vertx.createHttpServer().requestHandler(router).listen(8080).mapEmpty();
	}

	/**
	 * Builds a parent router into which the OpenAPI Router will be mounted as a subrouter
	 * @param routerBuilder The {@link RouterBuilder} created from the OpenAPIv3 Spec
	 * @return A {@link Router} with the OpenAPIv3 Router mounted
	 */
	private Future<Router> buildParentRouter(RouterBuilder routerBuilder) {
		Router parentRouter = Router.router(vertx);
		parentRouter.mountSubRouter("/api/v1", routerBuilder.createRouter());
		return Future.succeededFuture(parentRouter);
	}
}
