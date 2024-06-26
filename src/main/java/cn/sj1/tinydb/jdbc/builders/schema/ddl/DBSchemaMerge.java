package cn.sj1.tinydb.jdbc.builders.schema.ddl;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.JDBCType;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import cn.sj1.tinydb.jdbc.builders.schema.ColumnDefinition;
import cn.sj1.tinydb.jdbc.builders.schema.ColumnList;
import cn.sj1.tinydb.jdbc.builders.schema.JDBC;
import cn.sj1.tinydb.jdbc.builders.schema.SqlHelper;
import cn.sj1.tinydb.jdbc.builders.schema.db.JdbcDababaseMetadata;

public class DBSchemaMerge {
	static Logger logger = LoggerFactory.getLogger(DBSchemaMerge.class);

	private static DBSchemaMerge schemaMerge = new DBSchemaMerge(SqlHelper.H2_JDBC_DRIVER);

	public static boolean mergeColumns(Connection conn, String tableName, ColumnList columnsExpected) throws SQLException {
		DatabaseMetaData metaData = conn.getMetaData();
		schemaMerge.sqlHelper = SqlHelper.get(metaData.getDriverName(), metaData.getDriverMajorVersion(), metaData.getDriverMinorVersion());
		return schemaMerge.mergeTable(conn, tableName, columnsExpected);
	}

	public boolean merge(Connection conn, String tableName, ColumnList columnsExpected) throws SQLException {
		DatabaseMetaData metaData = conn.getMetaData();
		schemaMerge.sqlHelper = SqlHelper.get(metaData.getDriverName(), metaData.getDriverMajorVersion(), metaData.getDriverMinorVersion());
		return schemaMerge.mergeTable(conn, tableName, columnsExpected);
	}

	public static boolean mergeColumnsRight(Connection conn, String tableName, ColumnList columnsExpected) throws SQLException {
		DatabaseMetaData metaData = conn.getMetaData();
		schemaMerge.sqlHelper = SqlHelper.get(metaData.getDriverName(), metaData.getDriverMajorVersion(), metaData.getDriverMinorVersion());
		return schemaMerge.mergeTableRight(conn, tableName, columnsExpected);
	}

	public boolean mergeRight(Connection conn, String tableName, ColumnList columnsExpected) throws SQLException {
		DatabaseMetaData metaData = conn.getMetaData();
		schemaMerge.sqlHelper = SqlHelper.get(metaData.getDriverName(), metaData.getDriverMajorVersion(), metaData.getDriverMinorVersion());
		return schemaMerge.mergeTableRight(conn, tableName, columnsExpected);
	}

	SqlHelper sqlHelper;

	public DBSchemaMerge(String driverName) {
		super();
		sqlHelper = SqlHelper.get(driverName, 0, 0);
	}

	public DBSchemaMerge(Connection conn) throws SQLException {
		super();
		DatabaseMetaData metaData = conn.getMetaData();
		sqlHelper = SqlHelper.get(metaData.getDriverName(), metaData.getDriverMajorVersion(), metaData.getDriverMinorVersion());
	}

	boolean mergeTable(Connection conn, String tableName, ColumnList columnsExpected) throws SQLException {
		{
			ColumnList columnsActual = JdbcDababaseMetadata.getColumns(conn, tableName);
			if (columnsActual.size() == 0) {
				Statement statement = conn.createStatement();
				createTable(tableName, columnsExpected);
				statement.executeBatch();
				return false;
			}

			List<AlterTableColumnCommand> commandBus = compare(columnsExpected, columnsActual, true);
			logger.info("commandBus {}", commandBus);
			Statement statement = conn.createStatement();
			prepareMerge(statement, tableName, commandBus);
			statement.executeBatch();
			statement.close();
		}
		{
			ColumnList columnsActual = JdbcDababaseMetadata.getColumns(conn, tableName);
			List<AlterTableColumnCommand> commandBus = compare(columnsExpected, columnsActual, true);
			if(commandBus.size()>0) {
				logger.debug("error {} {}",commandBus,columnsExpected,columnsActual);
			}
			assert commandBus.size() == 0;
			return true;
		}
	}

	boolean mergeTableRight(Connection conn, String tableName, ColumnList columnsExpected) throws SQLException {
		{
			ColumnList columnsActual = JdbcDababaseMetadata.getColumns(conn, tableName);
			if (columnsActual.size() == 0) {
				String sql = createTable(tableName, columnsExpected);
				try {
					Statement statement = conn.createStatement();
					statement.addBatch(sql);
					statement.executeBatch();
				} catch (SQLException e) {
					logger.debug("throw exception for sql: {}", sql);
					throw e;
				}
				return false;
			}

			List<AlterTableColumnCommand> commandBus = compare(columnsExpected, columnsActual, false);
			logger.info("commandBus {}", commandBus);
			Statement statement = conn.createStatement();
			prepareMerge(statement, tableName, commandBus);
			statement.executeBatch();
			statement.close();
		}
		{
			ColumnList columnsActual = JdbcDababaseMetadata.getColumns(conn, tableName);
			List<AlterTableColumnCommand> commandBus = compare(columnsExpected, columnsActual, false);
			if (commandBus.size() > 0) {
				logger.debug("{}", columnsExpected, columnsActual);
			}
			assert commandBus.size() == 0;
			return true;
		}
	}

	String createTable(String tableName, ColumnList columnsExpected) throws SQLException {
		StringBuilder sb = new StringBuilder();
		sb.append("CREATE TABLE ").append(tableName).append(" (");
		for (ColumnDefinition columnDefinition : columnsExpected) {
			sb.append(columnDefinition.getName()).append(" ").append(sqlHelper.toTypeSQL(columnDefinition)).append(",");
		}
		if (sb.charAt(sb.length() - 1) == ',') {
			sb.setCharAt(sb.length() - 1, ')');
		} else {
			sb.append(')');
		}
		return sb.toString();
	}

	void prepareMerge(Statement statement, String tableName, List<AlterTableColumnCommand> commandBus) throws SQLException {
		for (AlterTableColumnCommand command : commandBus) {
			prepareMerge(statement, tableName, command);
		}
	}

	void prepareMerge(Statement statement, String tableName, AlterTableColumnCommand command) throws SQLException {
		String sql = sqlHelper.toSql(tableName, command);
		statement.addBatch(sql);

//		if (command instanceof AlterTable.AlterColumnTypeCommand) {
//			ColumnDefinition column = command.getColumn();
//			String sql = JDBC.sql("ALTER TABLE ${tablename} ALTER COLUMN ${columnname} ${columndefinition}", tableName, column.name(), makeColumnDefinition(column));
//			statement.addBatch(sql);
//		} else if (command instanceof AlterTable.AddColumnCommand) {
//			ColumnDefinition column = command.getColumn();
//			String sql = JDBC.sql("ALTER TABLE ${tablename} ADD COLUMN ${columnname} ${columndefinition}", tableName, column.name(), this.makeColumnDefinition(column));
//			statement.addBatch(sql);
//		} else if (command instanceof AlterTable.DropColumnCommand) {
//			String sql = JDBC.sql("ALTER TABLE ${tablename} DROP COLUMN ${columnname}", tableName, command.getColumn().name());
//			statement.addBatch(sql);
//		} else if (command instanceof AlterTable.AlterColumnNullableCommand) {
//			String sql = JDBC.sql("ALTER TABLE ${tablename} ALTER COLUMN ${oldname} SET ${nullable}", tableName, command.getColumn().name(), command.getColumn().getNullable() == ResultSetMetaData.columnNoNulls ? "NOT NULL" : "NULL");
//			statement.addBatch(sql);
//		} else if (command instanceof AlterTable.AlterColumnRemarksCommand) {
//			String sql = JDBC.sql("ALTER TABLE ${tablename} ALTER COLUMN ${columnname} REMARKS ${remarks}", tableName, command.getColumn().name(), command.getColumn().getRemarks().replaceAll("'", "''"));
//			statement.addBatch(sql);
//		}
	}

	/**
	 * 名字没有想好，应该说实际的能够放得下expect的就可以。不需要完全相等
	 * 
	 * @param columnsExpected
	 * @param columnsActual
	 * @return
	 * @throws SQLException
	 */

	List<AlterTableColumnCommand> compare(ColumnList columnsExpected, ColumnList columnsActual, boolean dropUnused) throws SQLException {

		List<AlterTableColumnCommand> commandBus = new ArrayList<>();

		for (ColumnDefinition exptected : columnsExpected) {
			ColumnDefinition actual = columnsActual.get(exptected.getColumnName());
			if (actual == null) {
				commandBus.add(new AlterTable.AddColumnCommand(exptected));
				continue;
			}

			if (compareType(exptected, actual)) {
				commandBus.add(new AlterTable.ChangeColumnTypeCommand(exptected));
			} else if (actual.getAutoIncrment() != exptected.getAutoIncrment()) {
				commandBus.add(new AlterTable.ChangeColumnTypeCommand(exptected));
			} else if (!sqlHelper.ignoreSize(exptected.getDataType()) && (exptected.getColumnSize() > actual.getColumnSize() || exptected.getDecimalDigits() > actual.getDecimalDigits())) {
				commandBus.add(new AlterTable.ChangeColumnTypeCommand(exptected));
			}

			if (exptected.getNullable() != actual.getNullable()) {
				commandBus.add(new AlterTable.AlterColumnNullableCommand(exptected));
			}

			String actualRemarks = actual.getRemarks();
			if (actualRemarks != null && actualRemarks.isEmpty()) {
				actualRemarks = null;
			}
			String expectedRemarks = exptected.getRemarks();
			if (expectedRemarks != null && expectedRemarks.isEmpty()) {
				expectedRemarks = null;
			}
			if (actualRemarks != expectedRemarks && !Objects.equals(expectedRemarks, actualRemarks)) {
				commandBus.add(new AlterTable.AlterColumnRemarksCommand(exptected));
			}
		}

		if (dropUnused) {
			for (ColumnDefinition actual : columnsActual) {
				ColumnDefinition exptected = columnsExpected.get(actual.getName());
				if (exptected == null) {
					commandBus.add(new AlterTable.DropColumnCommand(actual));
					continue;
				}
			}
		}

		return commandBus;

	}

	private boolean compareType(ColumnDefinition exptected, ColumnDefinition actual) {
		JDBCType actualDataType = actual.getDataType();
		JDBCType expectedDataType = exptected.getDataType();
		if (actualDataType == JDBCType.DECIMAL)
			actualDataType = JDBCType.NUMERIC;
		if (expectedDataType == JDBCType.DECIMAL)
			expectedDataType = JDBCType.NUMERIC;

		if (actualDataType == JDBCType.BOOLEAN)
			actualDataType = JDBCType.BIT;
		if (expectedDataType == JDBCType.BOOLEAN)
			expectedDataType = JDBCType.BIT;

		return actualDataType != expectedDataType;
	}

	public ColumnList getColumns(Connection conn, String tableName) throws SQLException {
		return JdbcDababaseMetadata.getColumns(conn, tableName);
	}

}
