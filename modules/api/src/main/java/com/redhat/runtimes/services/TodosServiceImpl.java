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
import io.vertx.ext.web.handler.HttpException;
import io.vertx.sqlclient.SqlClient;
import org.jooq.Configuration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.InvalidClassException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.UUID;

public class TodosServiceImpl implements TodosService {
	
	private static final Logger LOG = LoggerFactory.getLogger(TodosServiceImpl.class);
	
	SimpleDateFormat ISO8601DATEFORMAT = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ", Locale.US);
	
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
		return Future.failedFuture(new HttpException(500, throwable));
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
		LOG.info("Todo creation");
		try {
			Todos todo = new Todos();
			todo.setAuthor(body.getString("author"));
			Date created = ISO8601DATEFORMAT.parse(body.getString("created"));
			todo.setCreated(LocalDateTime.ofInstant(created.toInstant(), ZoneId.systemDefault()));
			todo.setComplete(Boolean.FALSE);
			todo.setDescription(body.getString("description"));
			Date dueDate = ISO8601DATEFORMAT.parse(body.getString("dueDate"));
			todo.setDuedate(LocalDateTime.ofInstant(dueDate.toInstant(), ZoneId.systemDefault()));
			todo.setTitle(body.getString("title"));
			
			dao.insertReturningPrimary(todo)
					.compose(id -> {
						LOG.info("New Todo ID: {}", id.toString());
						return Future.succeededFuture(id);
					})
					.compose(dao::findOneById)
					.compose(this::mapToServiceResponse, this::mapErrorToServiceResponse);
		} catch (Throwable t) {
			resultHandler.handle(Future.failedFuture(new HttpException(500, t)));
			LOG.error(t.getLocalizedMessage(), t);
		}
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
