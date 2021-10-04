package com.redhat.runtimes.services;

import com.redhat.runtimes.data.access.tables.daos.TodosDao;
import com.redhat.runtimes.data.access.tables.pojos.Todos;
import io.vertx.core.AsyncResult;
import io.vertx.core.Handler;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.api.service.ServiceRequest;
import io.vertx.ext.web.api.service.ServiceResponse;
import io.vertx.sqlclient.SqlClient;
import org.jooq.Configuration;

import java.util.UUID;

public class TodosServiceImpl extends AbstractService implements TodosService {
	
	TodosDao dao;
	
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
