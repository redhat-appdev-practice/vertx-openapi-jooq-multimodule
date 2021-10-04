package com.redhat.runtimes.services;

import com.redhat.runtimes.data.access.tables.daos.*;
import io.vertx.core.AsyncResult;
import io.vertx.core.Future;
import io.vertx.core.Handler;
import io.vertx.ext.web.api.service.ServiceRequest;
import io.vertx.ext.web.api.service.ServiceResponse;
import io.vertx.sqlclient.SqlClient;
import org.jooq.Configuration;

public class UserServiceImpl implements UserService {
	
	UsersDao dao;
	
	public UserServiceImpl(Configuration config, SqlClient client) {
		dao = new UsersDao(config, client);
	}
	
	@Override
	public void getUserProfile(ServiceRequest context, Handler<AsyncResult<ServiceResponse>> resultHandler) {
		resultHandler.handle(Future.failedFuture("Not yet implemented: getUserProfile"));
	}
}
