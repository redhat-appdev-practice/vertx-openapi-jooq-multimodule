package com.redhat.runtimes.services;

import com.redhat.runtimes.data.access.tables.Todos;
import com.redhat.runtimes.data.access.tables.Users;
import com.redhat.runtimes.data.access.tables.daos.*;
import io.vertx.core.AsyncResult;
import io.vertx.core.Future;
import io.vertx.core.Handler;
import io.vertx.ext.web.api.service.ServiceRequest;
import io.vertx.ext.web.api.service.ServiceResponse;
import io.vertx.sqlclient.SqlClient;
import org.jooq.Configuration;

public class UserServiceImpl extends AbstractService implements UserService {
	
	UsersDao dao;
	
	public UserServiceImpl(Configuration config, SqlClient client) {
		dao = new UsersDao(config, client);
	}
	
	@Override
	public void getUserProfile(ServiceRequest context, Handler<AsyncResult<ServiceResponse>> resultHandler) {
		dao.findOneByCondition(Users.USERS.PREFERREDUSERNAME.eq(context.getUser().getString("username")))
				.compose(this::mapToServiceResponse, this::mapErrorToServiceResponse);
	}
}
