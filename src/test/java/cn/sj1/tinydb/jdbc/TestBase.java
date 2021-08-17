package cn.sj1.tinydb.jdbc;

import static org.junit.Assert.assertEquals;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

import org.junit.Rule;
import org.junit.rules.TestName;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

import cn.sj1.tinyasm.tools.TinyAsmTestUtils;

public class TestBase {
	@Rule
	public TestName name = new TestName();

	public Connection openConnection() {
		return openConnection(this.getClass().getName() + "_" + name.getMethodName());
	}

	static public void assertListtoString(List<?> expected, List<?> actual) {
		assertEquals(expected.size(), actual.size());
		for (int i = 0; i < expected.size(); i++) {
			assertEquals(expected.get(i).toString(), actual.get(i).toString());
		}
	}

	public Connection openConnection(String name) {
		try {
			HikariConfig config = new HikariConfig();
			config.setDataSourceClassName("org.h2.jdbcx.JdbcDataSource");
			config.setConnectionTestQuery("VALUES 1");
			config.addDataSourceProperty("URL", // jdbc:h2:mem:test
					"jdbc:h2:mem:" + name + ";DB_CLOSE_DELAY=-1;DATABASE_TO_UPPER=false;MODE=MSSQLServer");
			config.addDataSourceProperty("user", "sa");
			config.addDataSourceProperty("password", "sa");

			dataSource = new HikariDataSource(config);
			connection = dataSource.getConnection();
			return connection;
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	HikariDataSource dataSource;
	Connection connection;

	public void closeConnection() {
		try {
			connection.close();
			dataSource.close();
		} catch (SQLException e) {
			throw new RuntimeException(e);
		}
	}

	protected MyClassLoader classLoader = new MyClassLoader();

	protected class MyClassLoader extends ClassLoader {
		public Class<?> defineClassByName(String name, byte[] b, int off, int len) {
			{
				File root = new File("target/generated-test-sources");
				if (!root.exists()) root.mkdirs();

				File file = new File(root, name.replace(".", "/") + ".class");
				if (!file.getParentFile().exists()) {
					makesureFolderExists(file.getParentFile());
				}

				try {
					FileOutputStream o = new FileOutputStream(file);
					o.write(b);
					o.close();
				} catch (FileNotFoundException e) {
					throw new RuntimeException(e);
				} catch (IOException e) {
					throw new RuntimeException(e);
				}
			}

			Class<?> clazz = super.defineClass(name, b, off, len);
			return clazz;
		}

		private void makesureFolderExists(File file) {
			if (!file.getParentFile().exists()) makesureFolderExists(file.getParentFile());
			file.mkdir();
		}

		public Class<?> defineClassByName(String name, byte[] b) {
			Class<?> clazz = defineClassByName(name, b, 0, b.length);
			return clazz;
		}
	}

	public static String toString(String targetClazz, byte[] code) {
		return TinyAsmTestUtils.toString(targetClazz, code);
	}
}