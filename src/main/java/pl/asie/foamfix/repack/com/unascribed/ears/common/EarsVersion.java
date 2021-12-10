package pl.asie.foamfix.repack.com.unascribed.ears.common;

public class EarsVersion {

	public static final String COMMON = /*VERSION*/"1.4.2"/*/VERSION*/;
	public static final String PLATFORM;
	public static final String PLATFORM_KIND;
	
	static {
		String v = null;
		String k = null;
		try {
			Class<?> clazz = Class.forName("pl.asie.foamfix.repack.com.unascribed.ears.EarsPlatformVersion");
			v = (String)clazz.getField("VERSION").get(null);
			k = (String)clazz.getField("KIND").get(null);
		} catch (Throwable t) {
		}
		PLATFORM = v;
		PLATFORM_KIND = k;
	}
	
}
