package nebula.data.jdbc;

import cn.sj1.tinyasm.MethodCode;

public interface Argument {
	void apply(MethodCode mv);
}
