package com.redhat.runtimes.api;

import com.redhat.runtimes.services.TodosService;
import com.redhat.runtimes.services.TodosServiceImpl;
import com.redhat.runtimes.services.UserService;
import com.redhat.runtimes.services.UserServiceImpl;
import io.vertx.config.ConfigChange;
import io.vertx.config.ConfigRetriever;
import io.vertx.config.ConfigRetrieverOptions;
import io.vertx.config.ConfigStoreOptions;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.Future;
import io.vertx.core.json.JsonObject;
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
	
	public static final Configuration JOOQ_CONFIG = new DefaultConfiguration().set(SQLDialect.POSTGRES);
	
	private JsonObject currentConfig = new JsonObject();
	
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
		
		vertx.fileSystem()                 // Use the Vert.x Async Filesystem object
				.exists("./config.json")      // to check if the ./config.json file exists
				.onSuccess(this::initConfig); // and pass the result (true/false) to the
		
		SqlClient client = getSqlClient();
		
		bindWebServices(client);
		
		RouterBuilder.create(vertx, "openapi.yml")
				.compose(this::mountRoutes)
				.compose(this::buildParentRouter)
				.compose(this::buildHttpServer)
				.onComplete(startPromise);
	}
	
	private Future<Void> initConfig(boolean hasConfigJsonFile) {
		ConfigRetrieverOptions configOpts = new ConfigRetrieverOptions();
		if (hasConfigJsonFile) {
			// If the config file exists
			// add that config store to our Config
			configOpts.addStore(initConfigWatcher());
		}
		ConfigRetriever.create(vertx, configOpts)       // Create the config retriever
				.listen(this::loadNewConfig);    // As create a callback to listen for configuration changes
		return Future.succeededFuture();                // Return a completed future
	}
	
	private ConfigStoreOptions initEnvironmentStore() {
		return new ConfigStoreOptions()
				.setType("env");
	}
	
	private void loadNewConfig(ConfigChange change) {
		this.currentConfig.mergeIn(change.getNewConfiguration());
	}
	
	private void bindWebServices(SqlClient client) {
		TodosService todoService = new TodosServiceImpl(JOOQ_CONFIG, client);
		ServiceBinder todoSvcBinder = new ServiceBinder(vertx);
		todoSvcBinder.setAddress("api.todos").register(TodosService.class, todoService);
		
		UserService userService = new UserServiceImpl(JOOQ_CONFIG, client);
		ServiceBinder userSvcBinder = new ServiceBinder(vertx);
		userSvcBinder.setAddress("api.user").register(UserService.class, userService);
	}
	
	private ConfigStoreOptions initConfigWatcher() {
		return new ConfigStoreOptions()        // New ConfigStoreOptions object
				.setType("file")          // of type 'file'
				.setFormat("json")        // and of format 'json'
				.setConfig(new JsonObject().put("path", "./config.json"));
	}
	
	private SqlClient getSqlClient() {
		PgConnectOptions poolConfig = new PgConnectOptions(currentConfig.getJsonObject("db"));
		
		PoolOptions poolOptions = new PoolOptions()
				.setMaxSize(5);
		
		SqlClient client = PgPool.client(poolConfig, poolOptions);
		return client;
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
