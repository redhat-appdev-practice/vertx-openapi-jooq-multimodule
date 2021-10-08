package com.redhat.runtimes.services;

import com.redhat.runtimes.data.access.tables.daos.TodosDao;
import com.redhat.runtimes.data.access.tables.pojos.Todos;
import io.github.jklingsporn.vertx.jooq.shared.internal.VertxPojo;
import io.netty.handler.codec.http.HttpResponseStatus;
import io.vertx.core.AsyncResult;
import io.vertx.core.Future;
import io.vertx.core.Handler;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.api.service.ServiceRequest;
import io.vertx.ext.web.api.service.ServiceResponse;
import io.vertx.pgclient.PgException;
import io.vertx.sqlclient.SqlClient;
import org.jooq.Configuration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.InvalidClassException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.UUID;

import static io.netty.handler.codec.http.HttpResponseStatus.CONFLICT;
import static io.netty.handler.codec.http.HttpResponseStatus.INTERNAL_SERVER_ERROR;

public class TodosServiceImpl implements TodosService {
	
	private static final Logger LOG = LoggerFactory.getLogger(TodosServiceImpl.class);
	
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
	
	ServiceResponse errorResponse(HttpResponseStatus status, JsonObject body) {
		return errorResponse(status, body.toBuffer());
	}
	
	ServiceResponse errorResponse(HttpResponseStatus status, Buffer body) {
		ServiceResponse response = new ServiceResponse();
		response.setStatusMessage(status.reasonPhrase())
				.setStatusCode(status.code())
				.putHeader("Content-Type", "application/json")
				.setPayload(body);
		return response;
	}
	
	Future<ServiceResponse> mapErrorToServiceResponse(Throwable throwable) {
		if (throwable instanceof PgException pge) {
			LOG.error("Failed interaction with the database: {}", pge.getMessage());
			if (pge.getErrorMessage().startsWith("duplicate key value violates unique constraint")) {
				return Future.succeededFuture(errorResponse(CONFLICT, Buffer.buffer(pge.getMessage())));
			}
		}
		LOG.error(throwable.getLocalizedMessage(), throwable);
		return Future.succeededFuture(errorResponse(INTERNAL_SERVER_ERROR, new JsonObject().put("error", throwable.getLocalizedMessage())));
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
			UUID id;
			if (body.containsKey("id")) {
			  id = UUID.fromString(body.getString("id"));
			} else {
				id = UUID.randomUUID();
			}
			todo.setId(id);
			todo.setAuthor(body.getString("author"));
			LocalDateTime created = LocalDateTime.parse(body.getString("created"), DateTimeFormatter.ISO_DATE_TIME);
			todo.setCreated(created);
			todo.setComplete(Boolean.FALSE);
			todo.setDescription(body.getString("description"));
			LocalDateTime dueDate = LocalDateTime.parse(body.getString("due_date"), DateTimeFormatter.ISO_DATE_TIME);
			todo.setDuedate(dueDate);
			todo.setTitle(body.getString("title"));
			
			dao.insertReturningPrimary(todo)
					.compose(returnedId -> {
						LOG.info("New Todo ID: {}", id.toString());
						return Future.succeededFuture(id);
					})
					.compose(dao::findOneById)
					.compose(this::mapToServiceResponse, this::mapErrorToServiceResponse)
					.onComplete(resultHandler);
		} catch (Throwable t) {
			resultHandler.handle(Future.failedFuture(t));
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
