package com.redhat.runtimes.services;

import com.redhat.runtimes.data.access.tables.daos.TodosDao;
import io.vertx.core.AsyncResult;
import io.vertx.core.Future;
import io.vertx.core.Handler;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.api.service.ServiceRequest;
import io.vertx.ext.web.api.service.ServiceResponse;
import io.vertx.sqlclient.SqlClient;
import org.jooq.Configuration;

public class TodosServiceImpl implements TodosService {
	
	TodosDao dao;
	
	public TodosServiceImpl(Configuration config, SqlClient client) {
		dao = new TodosDao(config, client);
	}
	
	@Override
	public void gettodos(ServiceRequest context, Handler<AsyncResult<ServiceResponse>> resultHandler) {
		resultHandler.handle(Future.failedFuture("Not yet implemented: gettodos"));
	}

	@Override
	public void createTodo(JsonObject body, ServiceRequest context, Handler<AsyncResult<ServiceResponse>> resultHandler) {
		resultHandler.handle(Future.failedFuture("Not yet implemented: createTodo"));
	}

	@Override
	public void getTodo(String todoId, ServiceRequest context, Handler<AsyncResult<ServiceResponse>> resultHandler) {
		resultHandler.handle(Future.failedFuture("Not yet implemented: getTodo"));
	}

	@Override
	public void updateTodo(String todoId, JsonObject body, ServiceRequest context, Handler<AsyncResult<ServiceResponse>> resultHandler) {
		resultHandler.handle(Future.failedFuture("Not yet implemented: updateTodo"));
	}

	@Override
	public void deleteTodo(String todoId, ServiceRequest context, Handler<AsyncResult<ServiceResponse>> resultHandler) {
		resultHandler.handle(Future.failedFuture("Not yet implemented: deleteTodo"));
	}
}
