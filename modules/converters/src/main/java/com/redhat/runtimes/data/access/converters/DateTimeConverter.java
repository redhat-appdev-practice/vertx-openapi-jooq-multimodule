package com.redhat.runtimes.data.access.converters;

import org.jooq.Converter;

import java.time.Instant;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;

public class DateTimeConverter implements Converter<OffsetDateTime, Instant> {
	/**
	 * Convert a database object to a user object
	 *
	 * @param databaseObject The database object
	 * @return The user object
	 */
	@Override
	public Instant from(OffsetDateTime databaseObject) {
		return databaseObject.toInstant();
	}
	
	/**
	 * Convert a user object to a database object
	 *
	 * @param userObject The user object
	 * @return The database object
	 */
	@Override
	public OffsetDateTime to(Instant userObject) {
		return OffsetDateTime.ofInstant(userObject, ZoneOffset.UTC);
	}
	
	/**
	 * The database type
	 */
	@Override
	public Class<OffsetDateTime> fromType() {
		return OffsetDateTime.class;
	}
	
	/**
	 * The user type
	 */
	@Override
	public Class<Instant> toType() {
		return Instant.class;
	}
}
