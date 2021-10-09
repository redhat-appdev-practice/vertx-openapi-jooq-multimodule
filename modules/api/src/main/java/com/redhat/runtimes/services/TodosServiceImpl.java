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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.UUID;

public class TodosServiceImpl extends AbstractService implements TodosService {
	
	private static final Logger LOG = LoggerFactory.getLogger(TodosServiceImpl.class);
	
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
		LOG.info("Todo creation");
		try {
			Todos todo = body.mapTo(Todos.class);
			UUID id = UUID.randomUUID();
			todo.setId(id);
			
			dao.insert(todo)
					.compose(returnedId -> dao.findOneById(id))
					.compose(this::mapToServiceResponse, this::mapErrorToServiceResponse)
					.onComplete(resultHandler);
		} catch (IllegalArgumentException iae) {
			resultHandler.handle(mapErrorToServiceResponse(iae));
			LOG.error(iae.getLocalizedMessage(), iae);
		}
	}

	@Override
	public void getTodo(String todoId, ServiceRequest context, Handler<AsyncResult<ServiceResponse>> resultHandler) {
		UUID id = UUID.fromString(todoId);
		dao.findOneById(id)
				.compose(this::mapToServiceResponse, this::mapErrorToServiceResponse)
				.onComplete(resultHandler);
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
				.compose(this::mapToServiceResponse, this::mapErrorToServiceResponse)
				.onComplete(resultHandler);
	}

	@Override
	public void deleteTodo(String todoId, ServiceRequest context, Handler<AsyncResult<ServiceResponse>> resultHandler) {
		UUID id = UUID.fromString(todoId);
		dao.deleteById(id)
				.compose(this::mapToServiceResponse, this::mapErrorToServiceResponse)
				.onComplete(resultHandler);
	}
}
