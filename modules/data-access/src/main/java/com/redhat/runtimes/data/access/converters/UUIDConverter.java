package com.redhat.runtimes.data.access.converters;

import org.jooq.Converter;

import java.util.UUID;

public class UUIDConverter implements Converter {
	/**
	 * Convert a database object to a user object
	 *
	 * @param databaseObject The database object
	 * @return The user object
	 */
	@Override
	public Object from(Object databaseObject) {
		return UUID.fromString((String)databaseObject);
	}
	
	/**
	 * Convert a user object to a database object
	 *
	 * @param userObject The user object
	 * @return The database object
	 */
	@Override
	public Object to(Object userObject) {
		return userObject.toString();
	}
	
	/**
	 * The database type
	 */
	@Override
	public Class fromType() {
		return UUID.class;
	}
	
	/**
	 * The user type
	 */
	@Override
	public Class toType() {
		return String.class;
	}
}
