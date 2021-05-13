package nebula.data.jdbc;

import static cn.sj1.tinyasm.tools.TinyAsmTestUtils.dumpTinyAsm;
import static java.sql.JDBCType.INTEGER;
import static java.sql.JDBCType.VARCHAR;
import static nebula.jdbc.builders.schema.ColumnDefinition.*;
import static org.junit.Assert.assertEquals;

import org.junit.Test;

import cn.sj1.tinyasm.tools.TinyAsmTestUtils;
import nebula.data.jdbc.sample.User;
import nebula.data.jdbc.sample.UserExtend;
import nebula.jdbc.builders.schema.ColumnDefinition;

public class UserJdbcRepositoryBuilderTinyASMifierTest {

	@Test
	public void test_UserJdbcRepository_dumpTinyAsm() throws Exception {
		Class<?> expectedClazz = UserJdbcRepository.class;
		String codeExpected = TinyAsmTestUtils.toString(expectedClazz);

		String codeActual = TinyAsmTestUtils.toString(expectedClazz.getName(), dumpTinyAsm(expectedClazz));

		assertEquals("Code", codeExpected, codeActual);

	}

	@Test
	public void test_UserJdbcRepository_build() throws Exception {
		Class<?> expectedClazz = UserJdbcRepository.class;

		FieldList clazzFields = new FieldList();
		clazzFields.push(new PojoFieldJdbcMapper(true, "id", "getId", long.class, new ColumnDefinition("id", INTEGER)));
		clazzFields.push(new PojoFieldJdbcMapper("name", "getName", String.class, new ColumnDefinition("name", VARCHAR)));
		clazzFields.push(new PojoFieldJdbcMapper("description", "getDescription", String.class, new ColumnDefinition("description", VARCHAR)));
		EntityPojoDbMappingDefinitions entityDefinition = new EntityPojoDbMappingDefinitions(User.class.getSimpleName(), User.class.getName(), User.class.getSimpleName(), clazzFields);

		String clazzRepository = UserJdbcRepository.class.getName();

		byte[] codeRepository = JdbcRepositoryBuilder.dumpStatic(clazzRepository, User.class.getName(), UserExtend.class.getName(), entityDefinition);

		String codeExpected = TinyAsmTestUtils.toString(expectedClazz);

		String codeActual = TinyAsmTestUtils.toString(expectedClazz.getName(), codeRepository);

		assertEquals("Code", codeExpected, codeActual);
	}

	@Test
	public void test_UserAutoIncrementJdbcRepository_build() throws Exception {
		Class<?> expectedClazz = UserAutoIncrementJdbcRepository.class;

		FieldList clazzFields = new FieldList();
		clazzFields.push(new PojoFieldJdbcMapper(true, "id", "getId", long.class, INTEGER("id").primarykey().autoIncrement()));
		clazzFields.push(new PojoFieldJdbcMapper("name", "getName", String.class, VARCHAR("name")));
		clazzFields.push(new PojoFieldJdbcMapper("description", "getDescription", String.class, VARCHAR("description")));
		EntityPojoDbMappingDefinitions entityDefinition = new EntityPojoDbMappingDefinitions(User.class.getSimpleName(), User.class.getName(), User.class.getSimpleName(), clazzFields);

		String clazzRepository = UserAutoIncrementJdbcRepository.class.getName();

		byte[] codeRepository = JdbcRepositoryBuilder.dumpStatic(clazzRepository, User.class.getName(), UserExtend.class.getName(), entityDefinition);

		String codeExpected = TinyAsmTestUtils.toString(expectedClazz);

		String codeActual = TinyAsmTestUtils.toString(expectedClazz.getName(), codeRepository);

		assertEquals("Code", codeExpected, codeActual);
	}

}
