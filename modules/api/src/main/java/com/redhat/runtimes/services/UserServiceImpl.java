package com.redhat.runtimes.services;

import com.redhat.runtimes.exceptions.NotImplementedException;
import io.vertx.core.AsyncResult;
import io.vertx.core.Future;
import io.vertx.core.Handler;
import io.vertx.ext.web.api.service.ServiceRequest;
import io.vertx.ext.web.api.service.ServiceResponse;

public class UserServiceImpl implements UserService {
	@Override
	public void getUserProfile(ServiceRequest context, Handler<AsyncResult<ServiceResponse>> resultHandler) {
		resultHandler.handle(Future.failedFuture(new NotImplementedException("Not yet implemented")));
	}
}
