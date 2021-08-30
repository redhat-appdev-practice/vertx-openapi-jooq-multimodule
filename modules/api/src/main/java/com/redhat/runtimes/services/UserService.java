package com.redhat.runtimes.services;

import io.vertx.core.AsyncResult;
import io.vertx.core.Handler;
import io.vertx.ext.web.api.service.ServiceRequest;
import io.vertx.ext.web.api.service.ServiceResponse;
import io.vertx.ext.web.api.service.WebApiServiceGen;

@WebApiServiceGen
public interface UserService {

	void getUserProfile(ServiceRequest context, Handler<AsyncResult<ServiceResponse>> resultHandler);
}
