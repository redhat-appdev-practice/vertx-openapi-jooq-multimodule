package com.redhat.runtimes.data.access.converters;

import org.jooq.Converter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.UUID;

public class UUIDConverter implements Converter<String, UUID> {
	
	private static final Logger LOG = LoggerFactory.getLogger(UUIDConverter.class);
	
	/**
	 * Convert a database object to a user object
	 *
	 * @param s The database string object
	 * @return The user object
	 */
	@Override
	public UUID from(String s) {
		try {
			UUID retVal = UUID.fromString(s);
			return retVal;
		} catch (IllegalArgumentException iae) {
			LOG.error("Unable to parse UUID from string '{}'", s);
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
	public String to(UUID userObject) {
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
	public Class<String> fromType() {
		return String.class;
	}
	
	/**
	 * The user type
	 */
	@Override
	public Class<UUID> toType() {
		return UUID.class;
	}
}
