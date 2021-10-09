package com.redhat.runtimes.services;

import com.redhat.runtimes.data.access.tables.Users;
import com.redhat.runtimes.data.access.tables.daos.*;
import io.vertx.core.AsyncResult;
import io.vertx.core.Handler;
import io.vertx.ext.web.api.service.ServiceRequest;
import io.vertx.ext.web.api.service.ServiceResponse;
import io.vertx.sqlclient.SqlClient;
import org.jooq.Configuration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class UserServiceImpl extends AbstractService implements UserService {
	
	private static final Logger LOG = LoggerFactory.getLogger(UserServiceImpl.class);
	
	UsersDao dao;
	
	public UserServiceImpl(Configuration config, SqlClient client) {
		dao = new UsersDao(config, client);
	}
	
	@Override
	public void getUserProfile(ServiceRequest context, Handler<AsyncResult<ServiceResponse>> resultHandler) {
		try {
			dao.findOneByCondition(Users.USERS.PREFERRED_USERNAME.eq(context.getUser().getString("username")))
				.compose(this::mapToServiceResponse, this::mapErrorToServiceResponse)
				.onComplete(resultHandler);
		} catch (Exception t) {
			resultHandler.handle(this.mapErrorToServiceResponse(t));
			LOG.error(t.getLocalizedMessage(), t);
		}
	}
}
