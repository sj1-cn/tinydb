package cn.sj1.nebula.data.jdbc.sample;
import org.objectweb.asm.Handle;
import org.objectweb.asm.Label;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;
import static org.objectweb.asm.Opcodes.*;

import cn.sj1.tinyasm.core.Annotation;
import cn.sj1.tinyasm.core.ClassBody;
import cn.sj1.tinyasm.core.ClassBuilder;
import cn.sj1.tinyasm.core.Clazz;
import cn.sj1.tinyasm.core.MethodCode;

import java.sql.Timestamp;

import cn.sj1.nebula.data.EntityAudit;
import cn.sj1.nebula.data.jdbc.sample.User;

@SuppressWarnings("unused")
public class UserExtendTinyAsmDump {

	public static byte[] dump() throws Exception {
		return new UserExtendTinyAsmDump().build("cn.sj1.nebula.data.jdbc.sample.UserExtend");
	}

	public byte[] build(String className) throws Exception {
		ClassBody classBody = ClassBuilder.class_(className, User.class, EntityAudit.class).body();

		classBody.private_().field("createAt", Clazz.of(Timestamp.class));
		classBody.private_().field("updateAt", Clazz.of(Timestamp.class));
		__init_(classBody);
		__init__long_String_String_Timestamp_Timestamp_void(classBody);
		_getCreateAt(classBody);
		_setCreateAt(classBody);
		_getUpdateAt(classBody);
		_setUpdateAt(classBody);

		return classBody.end().toByteArray();
	}

	protected void __init_(ClassBody classBody) {
		MethodCode code = classBody.public_().method("<init>").begin();

		code.LINE();
		code.LOAD("this");
		code.SPECIAL(User.class, "<init>").INVOKE();

		code.LINE();
		code.RETURN();

		code.END();
	}

	protected void __init__long_String_String_Timestamp_Timestamp_void(ClassBody classBody) {
		MethodCode code = classBody.public_().method("<init>")
			.parameter("id",long.class)
			.parameter("name",String.class)
			.parameter("description",String.class)
			.parameter("createAt",Timestamp.class)
			.parameter("updateAt",Timestamp.class).begin();

		code.LINE();
		code.LOAD("this");
		code.LOAD("id");
		code.LOAD("name");
		code.LOAD("description");
		code.SPECIAL(User.class, "<init>")
			.parameter(long.class)
			.parameter(String.class)
			.parameter(String.class).INVOKE();

		code.LINE();
		code.LOAD("this");
		code.LOAD("createAt");
		code.PUTFIELD("createAt", Timestamp.class);

		code.LINE();
		code.LOAD("this");
		code.LOAD("updateAt");
		code.PUTFIELD("updateAt", Timestamp.class);

		code.LINE();
		code.RETURN();

		code.END();
	}

	protected void _getCreateAt(ClassBody classBody) {
		MethodCode code = classBody.public_().method("getCreateAt")
			.return_(Timestamp.class ).begin();

		code.LINE();
		code.LOAD("this");
		code.GETFIELD("createAt", Timestamp.class);
		code.RETURNTop();

		code.END();
	}

	protected void _setCreateAt(ClassBody classBody) {
		MethodCode code = classBody.public_().method("setCreateAt")
			.parameter("createAt",Timestamp.class).begin();

		code.LINE();
		code.LOAD("this");
		code.LOAD("createAt");
		code.PUTFIELD("createAt", Timestamp.class);

		code.LINE();
		code.RETURN();

		code.END();
	}

	protected void _getUpdateAt(ClassBody classBody) {
		MethodCode code = classBody.public_().method("getUpdateAt")
			.return_(Timestamp.class ).begin();

		code.LINE();
		code.LOAD("this");
		code.GETFIELD("updateAt", Timestamp.class);
		code.RETURNTop();

		code.END();
	}

	protected void _setUpdateAt(ClassBody classBody) {
		MethodCode code = classBody.public_().method("setUpdateAt")
			.parameter("updateAt",Timestamp.class).begin();

		code.LINE();
		code.LOAD("this");
		code.LOAD("updateAt");
		code.PUTFIELD("updateAt", Timestamp.class);

		code.LINE();
		code.RETURN();

		code.END();
	}

}
