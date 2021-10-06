package com.redhat.runtimes.services;

import com.redhat.runtimes.data.access.tables.daos.TodosDao;
import com.redhat.runtimes.data.access.tables.pojos.Todos;
import io.github.jklingsporn.vertx.jooq.shared.internal.VertxPojo;
import io.vertx.core.AsyncResult;
import io.vertx.core.Future;
import io.vertx.core.Handler;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.api.service.ServiceRequest;
import io.vertx.ext.web.api.service.ServiceResponse;
import io.vertx.sqlclient.SqlClient;
import org.jooq.Configuration;

import java.io.InvalidClassException;
import java.util.List;
import java.util.UUID;

public class TodosServiceImpl implements TodosService {
	
	TodosDao dao;
	
	Future<ServiceResponse> mapToServiceResponse(Object results) {
		if (results instanceof Integer) {
			return Future.succeededFuture(ServiceResponse.completedWithJson(new JsonObject().put("deleted", results)));
		} else if (results instanceof List) {
			List<Object> list = (List<Object>)results;
			return Future.succeededFuture(ServiceResponse.completedWithJson(new JsonArray(list)));
		} else if (results instanceof VertxPojo) {
			return Future.succeededFuture(ServiceResponse.completedWithJson(JsonObject.mapFrom(results)));
		} else {
			return Future.failedFuture(new InvalidClassException("A non-conforming class type was received"));
		}
	}
	
	Future<ServiceResponse> mapErrorToServiceResponse(Throwable throwable) {
		return Future.failedFuture(throwable);
	}
	
	public TodosServiceImpl(Configuration config, SqlClient client) {
		dao = new TodosDao(config, client);
	}
	
	@Override
	public void gettodos(ServiceRequest context, Handler<AsyncResult<ServiceResponse>> resultHandler) {
		dao.findAll()
				.compose(this::mapToServiceResponse, this::mapErrorToServiceResponse)
				.onComplete(resultHandler);
	}
	
	@Override
	public void createTodo(JsonObject body, ServiceRequest context, Handler<AsyncResult<ServiceResponse>> resultHandler) {
		Todos todo = body.mapTo(Todos.class);
		dao.insertReturningPrimary(todo)
				.compose(dao::findOneById)
				.compose(this::mapToServiceResponse, this::mapErrorToServiceResponse);
	}

	@Override
	public void getTodo(String todoId, ServiceRequest context, Handler<AsyncResult<ServiceResponse>> resultHandler) {
		UUID id = UUID.fromString(todoId);
		dao.findOneById(id)
				.compose(this::mapToServiceResponse, this::mapErrorToServiceResponse);
	}

	@Override
	public void updateTodo(String todoId, JsonObject body, ServiceRequest context, Handler<AsyncResult<ServiceResponse>> resultHandler) {
		UUID id = UUID.fromString(todoId);
		dao.findOneById(id)
				.map(JsonObject::mapFrom)
				.map(json -> json.mergeIn(body))
				.map(json -> json.mapTo(Todos.class))
				.compose(dao::update)
				.compose(i -> dao.findOneById(id))
				.compose(this::mapToServiceResponse, this::mapErrorToServiceResponse);
	}
	
	@Override
	public void deleteTodo(String todoId, ServiceRequest context, Handler<AsyncResult<ServiceResponse>> resultHandler) {
		UUID id = UUID.fromString(todoId);
		dao.deleteById(id)
				.compose(this::mapToServiceResponse, this::mapErrorToServiceResponse);
	}
}
