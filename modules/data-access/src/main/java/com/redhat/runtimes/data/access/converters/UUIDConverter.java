package com.redhat.runtimes.data.access.converters;

import org.jooq.Converter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.UUID;

public class UUIDConverter implements Converter {
	
	private static final Logger LOG = LoggerFactory.getLogger(UUIDConverter.class);
	
	/**
	 * Convert a database object to a user object
	 *
	 * @param databaseObject The database object
	 * @return The user object
	 */
	@Override
	public Object from(Object databaseObject) {
		LOG.info("Object Type: {}", databaseObject.getClass().getCanonicalName());
		if (databaseObject instanceof String s) {
			try {
				UUID retVal = UUID.fromString(s);
				return retVal;
			} catch (IllegalArgumentException iae) {
				LOG.error("Unable to parse UUID from string '{}'", s);
				return null;
			}
		} else {
			return null;
		}
	}
	
	/**
	 * Convert a user object to a database object
	 *
	 * @param userObject The user object
	 * @return The database object
	 */
	@Override
	public Object to(Object userObject) {
		if (userObject != null) {
			return userObject.toString();
		} else {
			return "";
		}
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
