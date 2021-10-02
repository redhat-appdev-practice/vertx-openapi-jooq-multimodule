package com.redhat.runtimes.data;

import com.redhat.runtimes.data.access.tables.Todos;
import com.redhat.runtimes.models.Todo;
import io.github.jklingsporn.vertx.jooq.classic.reactivepg.ReactiveClassicQueryExecutor;
import io.github.jklingsporn.vertx.jooq.shared.internal.GenericVertxDAO;
import io.github.jklingsporn.vertx.jooq.shared.internal.QueryExecutor;
import io.vertx.core.Vertx;
import io.vertx.sqlclient.Row;
import io.vertx.sqlclient.SqlClient;
import org.jooq.*;
import com.redhat.runtimes.data.access.tables.records.TodosRecord;

import java.nio.charset.StandardCharsets;
import java.util.Collection;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.Future;
import java.util.function.Function;

public class TodoRepository implements GenericVertxDAO<TodosRecord, Todo, UUID, Future<List<Todo>>, Future<Todo>, Future<Integer>, Future<Todo>> {
	
	private final DSLContext dsl;
	private final Vertx vertx;
	
	private final ReactiveClassicQueryExecutor<TodosRecord, Todo, UUID> executor;
	
	public TodoRepository(Vertx vertx, SqlClient client, Configuration config) {
		super();
		this.vertx = vertx;
		this.dsl = config.dsl();
		
		Function<Row, Todo> pojoMapper = (Row r) -> r.toJson().mapTo(Todo.class);
		
		executor = new ReactiveClassicQueryExecutor<>(config, client, pojoMapper);
	}
	
	/**
	 * Performs an async <code>INSERT</code> statement for a given POJO. This is the same as calling
	 * #insert(pojo,false).
	 *
	 * @param pojo the pojo
	 * @return the result type returned for all insert, update and delete-operations.
	 */
	@Override
	public Future<Integer> insert(Todo pojo) {
		return dsl
				.insertInto(Todos.TODOS)
				.set(dsl.newRecord(Todos.TODOS, pojo))
				.executeAsync()
				.toCompletableFuture();
	}
	
	/**
	 * Performs an async <code>INSERT</code> statement for a given POJO.
	 *
	 * @param pojo                 the pojo
	 * @param onDuplicateKeyIgnore whether or not to set onDuplicateKeyIgnore option
	 * @return the result type returned for all insert, update and delete-operations.
	 */
	@Override
	public Future<Integer> insert(Todo pojo, boolean onDuplicateKeyIgnore) {
		if (!onDuplicateKeyIgnore) {
			return insert(pojo);
		} else {
			return dsl
					.insertInto(Todos.TODOS)
					.set(dsl.newRecord(Todos.TODOS, pojo))
					.onDuplicateKeyIgnore()
					.executeAsync()
					.toCompletableFuture();
		}
	}
	
	/**
	 * Performs an async <code>INSERT</code> statement for all given POJOs. This is the same as calling
	 * #insert(pojos,false).
	 *
	 * @param pojos the pojos
	 * @return the result type returned for all insert, update and delete-operations.
	 */
	@Override
	public Future<Integer> insert(Collection<Todo> pojos) {
		InsertSetStep<TodosRecord> todosRecordInsertSetStep = dsl
				.insertInto(Todos.TODOS);
		
		InsertSetMoreStep<TodosRecord> setValuesStep = null;
		
		for (Todo p: pojos) {
			TodosRecord r = dsl.newRecord(Todos.TODOS, p);
			if (setValuesStep == null) {
				setValuesStep = todosRecordInsertSetStep.set(r);
			} else {
				setValuesStep = setValuesStep.newRecord().set(r);
			}
		}
		
		return setValuesStep
				.executeAsync()
				.toCompletableFuture();
	}
	
	/**
	 * Performs an async <code>INSERT</code> statement for all given POJOs.
	 *
	 * @param pojos                the pojos
	 * @param onDuplicateKeyIgnore whether or not to set onDuplicateKeyIgnore option
	 * @return the result type returned for all insert, update and delete-operations.
	 */
	@Override
	public Future<Integer> insert(Collection<Todo> pojos, boolean onDuplicateKeyIgnore) {
		if (!onDuplicateKeyIgnore) {
			return insert(pojos);
		} else {
			InsertSetStep<TodosRecord> todosRecordInsertSetStep = dsl
					.insertInto(Todos.TODOS);
			
			InsertSetMoreStep<TodosRecord> setValuesStep = null;
			
			for (Todo p: pojos) {
				TodosRecord r = dsl.newRecord(Todos.TODOS, p);
				if (setValuesStep == null) {
					setValuesStep = todosRecordInsertSetStep.set(r);
				} else {
					setValuesStep = setValuesStep.newRecord().set(r);
				}
			}
			
			return setValuesStep
				        .onDuplicateKeyIgnore()
								.executeAsync()
								.toCompletableFuture();
		}
	}
	
	/**
	 * Performs an async <code>INSERT</code> statement for a given POJO and returns it's primary key.
	 *
	 * @param pojo the pojo
	 * @return the result type returned for INSERT_RETURNING.
	 */
	@Override
	public Future<Todo> insertReturningPrimary(Todo pojo) {
		return vertx.<Todo>executeBlocking(p -> {
					try {
						var retVal = dsl
								.insertInto(Todos.TODOS)
								.set(dsl.newRecord(Todos.TODOS, pojo))
								.returning()
								.fetchOne()
		            .into(Todo.class);
						p.complete(retVal);
					} catch (Throwable t) {
						p.fail(t);
					}
				})
				.toCompletionStage().toCompletableFuture();
	}
	
	/**
	 * Performs an async <code>UPDATE</code> statement for a given POJO. For performance reasons, consider writing
	 * your own update-statements by using a <code>QueryExecutor</code> directly.
	 *
	 * @param pojo the pojo
	 * @return the result type returned for all insert, update and delete-operations.
	 */
	@Override
	public Future<Integer> update(Todo pojo) {
		return dsl
						.update(Todos.TODOS)
						.set(dsl.newRecord(Todos.TODOS, pojo))
						.executeAsync()
						.toCompletableFuture();
	}
	
	/**
	 * Performs an async <code>DELETE</code> statement using the given id
	 *
	 * @param id the id
	 * @return the result type returned for all insert, update and delete-operations.
	 */
	@Override
	public Future<Integer> deleteById(UUID id) {
		return dsl
			       .delete(Todos.TODOS)
			       .where(Todos.TODOS.ID.eq(id.toString().getBytes(StandardCharsets.UTF_8)))
			       .executeAsync()
			       .toCompletableFuture();
	}
	
	/**
	 * Performs an async <code>DELETE</code> statement using the given ids
	 *
	 * @param ids the ids
	 * @return the result type returned for all insert, update and delete-operations.
	 */
	@Override
	public Future<Integer> deleteByIds(Collection<UUID> ids) {
		var beforeWhereConditions = dsl.delete(Todos.TODOS);
		
		Condition conditions = Todos.TODOS.ID.ne(Todos.TODOS.ID);
		for (var id: ids) {
			conditions = conditions.or(Todos.TODOS.ID.eq(id.toString().getBytes(StandardCharsets.UTF_8)));
		}
		
		return beforeWhereConditions.where(conditions).executeAsync().toCompletableFuture();
	}
	
	/**
	 * Performs an async <code>DELETE</code> statement using the given <code>Condition</code>
	 *
	 * @param condition the query condition
	 * @return the result type returned for all insert, update and delete-operations.
	 */
	@Override
	public Future<Integer> deleteByCondition(Condition condition) {
		return dsl.delete(Todos.TODOS).where(condition).executeAsync().toCompletableFuture();
	}
	
	/**
	 * Performs an async <code>SELECT</code> using the given condition. If more than one row is found, a
	 * <code>TooManyRowsException</code> is raised.
	 *
	 * @param condition the query condition
	 * @return the result type returned for all find-one-value-operations.
	 */
	@Override
	public Future<Todo> findOneByCondition(Condition condition) {
		return vertx.<Todo>executeBlocking(p -> {
					try {
						var retVal = dsl
								.selectFrom(Todos.TODOS)
								.where(condition)
								.fetchOne()
								.into(Todo.class);
						p.complete(retVal);
					} catch (Throwable t) {
						p.fail(t);
					}
				})
				.toCompletionStage().toCompletableFuture();
	}
	
	/**
	 * Performs an async <code>SELECT</code> using the given primary key.
	 *
	 * @param id the id
	 * @return the result type returned for all find-one-value-operations.
	 */
	@Override
	public Future<Todo> findOneById(UUID id) {
		return vertx.<Todo>executeBlocking(p -> {
					try {
						var retVal = dsl
								.selectFrom(Todos.TODOS)
								.where(Todos.TODOS.ID.eq(id.toString().getBytes(StandardCharsets.UTF_8)))
								.fetchOne()
								.into(Todo.class);
						p.complete(retVal);
					} catch (Throwable t) {
						p.fail(t);
					}
				})
				.toCompletionStage().toCompletableFuture();
	}
	
	/**
	 * Performs an async <code>SELECT</code> using the given primary keys.
	 *
	 * @param ids the ids
	 * @return the result type returned for all find-many-values-operations.
	 */
	@Override
	public Future<List<Todo>> findManyByIds(Collection<UUID> ids) {
		Condition conditions = Todos.TODOS.ID.ne(Todos.TODOS.ID);
		for (var id: ids) {
			conditions = conditions.or(Todos.TODOS.ID.eq(id.toString().getBytes(StandardCharsets.UTF_8)));
		}
		
		final var finalConditions = conditions;
		
		return vertx.<List<Todo>>executeBlocking(p -> {
					try {
						var retVal = dsl
								.selectFrom(Todos.TODOS)
								.where(finalConditions)
								.fetch()
								.into(Todo.class);
						p.complete(retVal);
					} catch (Throwable t) {
						p.fail(t);
					}
				})
				.toCompletionStage().toCompletableFuture();
	}
	
	/**
	 * Performs an async <code>SELECT</code> using the given condition.
	 *
	 * @param condition the query condition
	 * @return the result type returned for all find-many-values-operations.
	 */
	@Override
	public Future<List<Todo>> findManyByCondition(Condition condition) {
		return vertx.<List<Todo>>executeBlocking(p -> {
					try {
						var retVal = dsl
								.selectFrom(Todos.TODOS)
								.where(condition)
								.fetch()
								.into(Todo.class);
						p.complete(retVal);
					} catch (Throwable t) {
						p.fail(t);
					}
				})
				.toCompletionStage().toCompletableFuture();
	}
	
	/**
	 * Performs an async <code>SELECT</code> using the given condition and limit.
	 *
	 * @param condition the query condition
	 * @param limit     the limit
	 * @return the result type returned for all find-many-values-operations.
	 */
	@Override
	public Future<List<Todo>> findManyByCondition(Condition condition, int limit) {
		return vertx.<List<Todo>>executeBlocking(p -> {
					try {
						var retVal = dsl
								.selectFrom(Todos.TODOS)
								.where(condition)
								.fetchSize(limit)
		            .fetch()
								.into(Todo.class);
						p.complete(retVal);
					} catch (Throwable t) {
						p.fail(t);
					}
				})
				.toCompletionStage().toCompletableFuture();
	}
	
	/**
	 * Performs an async <code>SELECT</code> using the given condition with specific order.
	 *
	 * @param condition   the query condition
	 * @param orderFields the order fields (optional)
	 * @return the result type returned for all find-many-values-operations.
	 */
	@Override
	public Future<List<Todo>> findManyByCondition(Condition condition, OrderField<?>... orderFields) {
		return vertx.<List<Todo>>executeBlocking(p -> {
					try {
						var retVal = dsl
								.selectFrom(Todos.TODOS)
								.where(condition)
	              .orderBy(orderFields)
								.fetch()
								.into(Todo.class);
						p.complete(retVal);
					} catch (Throwable t) {
						p.fail(t);
					}
				})
				.toCompletionStage().toCompletableFuture();
	}
	
	/**
	 * Performs an async <code>SELECT</code> using the given condition with specific order and limit.
	 *
	 * @param condition   the query condition
	 * @param limit       the limit
	 * @param orderFields the order fields (optional)
	 * @return the result type returned for all find-many-values-operations.
	 */
	@Override
	public Future<List<Todo>> findManyByCondition(Condition condition, int limit, OrderField<?>... orderFields) {
		return vertx.<List<Todo>>executeBlocking(p -> {
					try {
						var retVal = dsl
								.selectFrom(Todos.TODOS)
								.where(condition)
								.orderBy(orderFields)
								.fetchSize(limit)
								.fetch()
								.into(Todo.class);
						p.complete(retVal);
					} catch (Throwable t) {
						p.fail(t);
					}
				})
				.toCompletionStage().toCompletableFuture();
	}
	
	/**
	 * Performs an async <code>SELECT</code> using the given condition with specific order and limit.
	 *
	 * @param condition   the query condition
	 * @param limit       the limit
	 * @param offset      the offset
	 * @param orderFields the order fields (optional)
	 * @return the result type returned for all find-many-values-operations.
	 */
	@Override
	public Future<List<Todo>> findManyByCondition(Condition condition, int limit, int offset, OrderField<?>... orderFields) {
		return vertx.<List<Todo>>executeBlocking(p -> {
					try {
						var retVal = dsl
								.selectFrom(Todos.TODOS)
								.where(condition)
								.orderBy(orderFields)
	              .offset(offset)
								.fetchSize(limit)
								.fetch()
								.into(Todo.class);
						p.complete(retVal);
					} catch (Throwable t) {
						p.fail(t);
					}
				})
				.toCompletionStage().toCompletableFuture();
	}
	
	/**
	 * Performs an async <code>SELECT</code>.
	 *
	 * @return the result type returned for all find-many-values-operations.
	 */
	@Override
	public Future<List<Todo>> findAll() {
		return vertx.<List<Todo>>executeBlocking(p -> {
					try {
						var retVal = dsl
								.selectFrom(Todos.TODOS)
								.fetch()
								.into(Todo.class);
						p.complete(retVal);
					} catch (Throwable t) {
						p.fail(t);
					}
				})
				.toCompletionStage().toCompletableFuture();
	}
	
	/**
	 * @return the underlying {@code QueryExecutor}
	 */
	@Override
	public QueryExecutor<TodosRecord, UUID, Future<List<Todo>>, Future<Todo>, Future<Integer>, Future<Todo>> queryExecutor() {
		return this.executor;
	}
}
