package com.redhat.runtimes.services;

import io.github.jklingsporn.vertx.jooq.shared.internal.VertxPojo;
import io.netty.handler.codec.http.HttpResponseStatus;
import io.vertx.core.Future;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.api.service.ServiceResponse;
import io.vertx.pgclient.PgException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.InvalidClassException;
import java.util.List;

import static io.netty.handler.codec.http.HttpResponseStatus.*;

public abstract class AbstractService {
	
	private static final Logger LOG = LoggerFactory.getLogger(AbstractService.class);

	Future<ServiceResponse> mapToServiceResponse(Object results) {
		if (results instanceof Integer) {
			return Future.succeededFuture(ServiceResponse.completedWithJson(new JsonObject().put("deleted", results)));
		} else if (results instanceof List l) {
			JsonArray retVal = new JsonArray();
			for (Object o: l) {
				if (o instanceof VertxPojo p) {
					retVal.add(JsonObject.mapFrom(p));
				}
			}
			return Future.succeededFuture(ServiceResponse.completedWithJson(retVal));
		} else if (results instanceof VertxPojo p) {
			return Future.succeededFuture(ServiceResponse.completedWithJson(JsonObject.mapFrom(p)));
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
		if (throwable instanceof IllegalArgumentException iae) {
			return Future.succeededFuture(errorResponse(BAD_REQUEST, Buffer.buffer(iae.getLocalizedMessage())));
		}
		LOG.error(throwable.getLocalizedMessage(), throwable);
		return Future.succeededFuture(errorResponse(INTERNAL_SERVER_ERROR, new JsonObject().put("error", throwable.getLocalizedMessage())));
	}
}
