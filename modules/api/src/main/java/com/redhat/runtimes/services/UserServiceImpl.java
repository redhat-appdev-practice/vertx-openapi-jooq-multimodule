package com.redhat.runtimes.services;

import com.redhat.runtimes.data.access.tables.Todos;
import com.redhat.runtimes.data.access.tables.Users;
import com.redhat.runtimes.data.access.tables.daos.*;
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

public class UserServiceImpl implements UserService {
	
	UsersDao dao;
	
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
	
	public UserServiceImpl(Configuration config, SqlClient client) {
		dao = new UsersDao(config, client);
	}
	
	@Override
	public void getUserProfile(ServiceRequest context, Handler<AsyncResult<ServiceResponse>> resultHandler) {
		dao.findOneByCondition(Users.USERS.PREFERREDUSERNAME.eq(context.getUser().getString("username")))
				.compose(this::mapToServiceResponse, this::mapErrorToServiceResponse);
	}
}
