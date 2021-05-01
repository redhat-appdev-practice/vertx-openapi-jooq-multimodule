package com.zanclus.openapi.vertx.api;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.Promise;

/**
 * Main Vert.x Verticle, entrypoint for this application
 */
public class MainVerticle extends AbstractVerticle {

	@Override
	public void start(Promise<Void> startPromise) throws Exception {
		vertx.createHttpServer().listen(res -> {
			if (res.succeeded()) {
				startPromise.complete();
				return;
			}
			startPromise.fail(res.cause());
		});
	}

}
