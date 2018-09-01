package nebula.data.jdbc;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import nebula.jdbc.builders.schema.ColumnDefinition;
import nebula.jdbc.builders.schema.ColumnList;
import nebula.jdbc.builders.schema.JDBCConfiguration;

public class UserJdbcSelectRepository implements JdbcRepository<User> {
	private Connection conn;
	private UserJdbcRowMapper mapper = new UserJdbcRowMapper();

	ColumnList columnList = new ColumnList();
	@Override
	public void setConnection(Connection conn) {
		this.conn = conn;
	}

	@Override
	public void initJdbc() throws SQLException {
		columnList.push(ColumnDefinition.valueOf("id INTEGER(10) PRIMARY KEY"));
		columnList.push(ColumnDefinition.valueOf("name VARCHAR(256)"));
		columnList.push(ColumnDefinition.valueOf("description VARCHAR(256)"));
		if (!JDBCConfiguration.mergeIfExists(conn, "user", columnList)) {
			conn.prepareStatement("CREATE TABLE user(id INTEGER(10),name VARCHAR(256),description VARCHAR(256),PRIMARY KEY(id))").execute();
		}
	}

	@Override
	public List<User> listJdbc(int start, int max) throws SQLException {
		List<User> datas = new ArrayList<>();
				
		ResultSet resultSet = conn.prepareStatement("SELECT id,name,description FROM user").executeQuery();

		while (resultSet.next()) {
			datas.add(mapper.map(resultSet));
		}

		return datas;
	}

	@Override
	public User findByIdJdbc(Object... keys) throws SQLException {
		PreparedStatement preparedStatement;
		ResultSet resultSet;
		List<User> datas;
		datas = new ArrayList<>();
		preparedStatement = conn.prepareStatement("SELECT id,name,description FROM user WHERE id=?");

		preparedStatement.setLong(1, ((Long) keys[0]).longValue());

		resultSet = preparedStatement.executeQuery();

		while (resultSet.next()) {
			datas.add(mapper.map(resultSet));
		}
		return datas.get(0);
	}

	@Override
	public User insertJdbc(User data) throws SQLException {
		PreparedStatement preparedStatement = conn.prepareStatement("INSERT INTO user(id,name,description) VALUES(?,?,?)");

		preparedStatement.setLong(1, data.getId());
		preparedStatement.setString(2, data.getName());
		preparedStatement.setString(3, data.getDescription());

		if (preparedStatement.executeUpdate() > 0) {
			return this.findById(data.getId());
		}
		return null;
	}

	@Override
	public User updateJdbc(User data) throws SQLException {
		PreparedStatement preparedStatement = conn.prepareStatement("UPDATE user SET name=?,description=? WHERE id=?");

		preparedStatement.setString(1, data.getName());
		preparedStatement.setString(2, data.getDescription());
		preparedStatement.setLong(3, data.getId());

		if (preparedStatement.executeUpdate() > 0) {
			return findById(data.getId());
		}
		return null;
	}

	@Override
	public int deleteJdbc(Object... keys) throws SQLException {
		PreparedStatement preparedStatement = conn.prepareStatement("DELETE user WHERE id=?");

		preparedStatement.setLong(1, ((Long) keys[0]).longValue());

		return preparedStatement.executeUpdate();
	}

}