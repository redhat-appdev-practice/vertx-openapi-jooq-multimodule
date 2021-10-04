package com.redhat.runtimes.services;

import io.github.jklingsporn.vertx.jooq.shared.internal.VertxPojo;
import io.vertx.core.Future;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.api.service.ServiceResponse;

import java.io.InvalidClassException;
import java.util.List;

public class AbstractService<T> {
	
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
}
