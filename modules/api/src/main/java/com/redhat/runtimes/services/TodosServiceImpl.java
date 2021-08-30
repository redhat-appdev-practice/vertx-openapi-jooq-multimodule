package com.redhat.runtimes.services;

import com.redhat.runtimes.exceptions.NotImplementedException;
import io.vertx.core.AsyncResult;
import io.vertx.core.Future;
import io.vertx.core.Handler;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.api.service.ServiceRequest;
import io.vertx.ext.web.api.service.ServiceResponse;

public class TodosServiceImpl implements TodosService {
	@Override
	public void gettodos(ServiceRequest context, Handler<AsyncResult<ServiceResponse>> resultHandler) {
		resultHandler.handle(Future.failedFuture(new NotImplementedException("Not yet implemented: gettodos")));
	}

	@Override
	public void createTodo(JsonObject body, ServiceRequest context, Handler<AsyncResult<ServiceResponse>> resultHandler) {
		resultHandler.handle(Future.failedFuture(new NotImplementedException("Not yet implemented: createTodo")));
	}

	@Override
	public void getTodo(String todoId, ServiceRequest context, Handler<AsyncResult<ServiceResponse>> resultHandler) {
		resultHandler.handle(Future.failedFuture(new NotImplementedException("Not yet implemented: getTodo")));
	}

	@Override
	public void updateTodo(String todoId, JsonObject body, ServiceRequest context, Handler<AsyncResult<ServiceResponse>> resultHandler) {
		resultHandler.handle(Future.failedFuture(new NotImplementedException("Not yet implemented: updateTodo")));
	}

	@Override
	public void deleteTodo(String todoId, ServiceRequest context, Handler<AsyncResult<ServiceResponse>> resultHandler) {
		resultHandler.handle(Future.failedFuture(new NotImplementedException("Not yet implemented: deleteTodo")));
	}
}
