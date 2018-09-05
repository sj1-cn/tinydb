package nebula.data.jdbc;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

class MyClassLoader extends ClassLoader {
	public Class<?> defineClassByName(String name, byte[] b) {
		return defineClassByName(name, b, 0, b.length);
	}

	public Class<?> defineClassByName(String name, byte[] b, int off, int len) {

		File root = new File("target/generated-sources");
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

		Class<?> clazz = super.defineClass(name, b, 0, b.length);
		return clazz;
	}

	private void makesureFolderExists(File file) {
		if (!file.getParentFile().exists()) makesureFolderExists(file.getParentFile());
		file.mkdir();
	}
}