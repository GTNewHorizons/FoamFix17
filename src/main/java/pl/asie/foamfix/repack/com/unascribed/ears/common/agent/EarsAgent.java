package pl.asie.foamfix.repack.com.unascribed.ears.common.agent;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.instrument.ClassFileTransformer;
import java.lang.instrument.IllegalClassFormatException;
import java.lang.instrument.Instrumentation;
import java.lang.reflect.Method;
import java.security.ProtectionDomain;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import pl.asie.foamfix.repack.com.unascribed.ears.common.EarsVersion;
import pl.asie.foamfix.repack.com.unascribed.ears.common.agent.mini.MiniTransformer;
import pl.asie.foamfix.repack.com.unascribed.ears.common.debug.EarsLog;

public class EarsAgent {

	private static final List<MiniTransformer> TRANSFORMERS;
	private static final boolean DUMP = false;
	
	public static boolean initialized = false;
	
	public static byte[] transform(String className, byte[] classBytes) {
		byte[] orig = DUMP ? classBytes : null;
		try {
			boolean changed = false;
			for (MiniTransformer mt : TRANSFORMERS) {
				byte[] nw = mt.transform(className, classBytes);
				if (nw != classBytes) {
					changed = true;
				}
				classBytes = nw;
			}
			if (changed && DUMP) {
				writeDump(className, orig, "before");
				writeDump(className, classBytes, "after");
			}
			return classBytes;
		} catch (RuntimeException t) {
			if (DUMP) writeDump(className, orig, "before");
			throw t;
		} catch (Error e) {
			if (DUMP) writeDump(className, orig, "before");
			throw e;
		}
	}
	
	private static void writeDump(String className, byte[] classBytes, String what) {
		File dir = new File("ears-debug-classes", className.replace('/', '.'));
		dir.mkdirs();
		File f = new File(dir, what+".class");
		try {
			FileOutputStream fos = new FileOutputStream(f);
			try {
				fos.write(classBytes);
			} finally {
				fos.close();
			}
		} catch (IOException e) {
			EarsLog.debug(EarsLog.Tag.COMMON_AGENT, "Failed to write before class to {}", f, e);
		}
	}

	static {
		List<MiniTransformer> li = new ArrayList<MiniTransformer>();
		try {
			Class<?> clazz = Class.forName("pl.asie.foamfix.repack.com.unascribed.ears.asm.Patches");
			Method m = clazz.getMethod("addTransformers", List.class);
			m.invoke(null, li);
			EarsLog.debug(EarsLog.Tag.COMMON_AGENT, "Discovered {} patch{} in static binder", li.size(), li.size() == 1 ? "" : "es");
		} catch (Exception e) {
			EarsLog.debug(EarsLog.Tag.COMMON_AGENT, "Failed to discover patches", e);
		}
		TRANSFORMERS = Collections.unmodifiableList(li);
	}
	
}
