package nebula.data.jdbc;

import java.sql.Connection;

import nebula.jdbc.builders.schema.ColumnList;
import nebula.jdbc.builders.schema.JDBC;

public class JdbcRepositoryBase {

	public JdbcRepositoryBase() {
		super();
	}

	protected boolean checkIsExist(Connection conn, String tableName, ColumnList columnList) {
		return !JDBC.mergeIfExists(conn, tableName, columnList);
	}

}