package pl.asie.foamfix.repack.com.unascribed.ears.common;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

import pl.asie.foamfix.repack.com.unascribed.ears.common.debug.EarsLog;
import pl.asie.foamfix.repack.com.unascribed.ears.common.util.BitInputStream;

class EarsFeaturesParserV1 {

	public static final int MAGIC = 0xEA2501; // EARS01
	
	public static EarsFeatures parse(EarsImage img, Alfalfa alfalfa) {
		ByteArrayOutputStream data = new ByteArrayOutputStream(((4*4)-1)*3);
		for (int y = 0; y < 4; y++) {
			for (int x = 0; x < 4; x++) {
				if (x == 0 && y == 0) continue;
				int c = img.getARGB(x, 32+y);
				data.write((c>>16)&0xFF);
				data.write((c>>8)&0xFF);
				data.write(c&0xFF);
			}
		}
		try {
			@SuppressWarnings("resource")
			BitInputStream bis = new BitInputStream(new ByteArrayInputStream(data.toByteArray()));
			
			// currently, version means nothing. in the future it will indicate additional
			// data that has been added to the end of the format (earlier data mustn't change
			// format!)
			
			// budget: ((4*4)-1)*3 bytes (360 bits)
			int ver = bis.read(8);
			EarsLog.debug("Common:Features", "detect(...): Found v1.{} (Binary) data.", ver);
			
			int ears = bis.read(6);
			// 6 bits has a range of 0-63
			// this means we can have up to 20 ear modes, since we're using "base-3" encoding
			// we're stuck with 3 anchors forever, though
			EarsFeatures.EarMode earMode;
			EarsFeatures.EarAnchor earAnchor;
			if (ears == 0) {
				earMode = EarsFeatures.EarMode.NONE;
				earAnchor = EarsFeatures.EarAnchor.CENTER;
			} else {
				earMode = byOrdinalOr(EarsFeatures.EarMode.class, ((ears-1)/3)+1, EarsFeatures.EarMode.NONE);
				earAnchor = byOrdinalOr(EarsFeatures.EarAnchor.class, (ears-1)%3, EarsFeatures.EarAnchor.CENTER);
			}
			EarsLog.debug("Common:Features", "detect(...): Ears 6yte: {} (mode={} anchor={})", ears, earMode, earAnchor);
			
			boolean claws = bis.readBoolean();
			EarsLog.debug("Common:Features", "detect(...): Claws bit: {}", claws);
			boolean horn = bis.readBoolean();
			EarsLog.debug("Common:Features", "detect(...): Horn bit: {}", horn);
		
			int tailI = bis.read(3);
			// 3 bits has a range of 0-7 - if we run out, a value of 7 can mean "read elsewhere"
			
			EarsFeatures.TailMode tailMode = byOrdinalOr(EarsFeatures.TailMode.class, tailI, EarsFeatures.TailMode.NONE);
			int tailSegments = 0;
			float tailBend0 = 0;
			float tailBend1 = 0;
			float tailBend2 = 0;
			float tailBend3 = 0;
			EarsLog.debug("Common:Features", "detect(...): Tail 3yte: {} ({})", tailI, tailMode);
			if (tailMode != EarsFeatures.TailMode.NONE) {
				tailSegments = bis.read(2)+1;
				EarsLog.debug("Common:Features", "detect(...): Tail segments: {}", tailSegments);
				tailBend0 = bis.readSAMUnit(6)*90;
				tailBend1 = tailSegments > 1 ? bis.readSAMUnit(6)*90 : 0;
				tailBend2 = tailSegments > 2 ? bis.readSAMUnit(6)*90 : 0;
				tailBend3 = tailSegments > 3 ? bis.readSAMUnit(6)*90 : 0;
				EarsLog.debug("Common:Features", "detect(...): Tail bends: {} {} {} {}", tailBend0, tailBend1, tailBend2, tailBend3);
			}
			
			int snoutOffset = 0;
			int snoutWidth = bis.read(3); // 0-7; valid snout widths are 1-7, so this is perfect as we can use 0 to mean "none"
			int snoutHeight = 0;
			int snoutDepth = 0;
			if (snoutWidth > 0) {
				snoutHeight = bis.read(2)+1; // 1-4; perfect
				snoutDepth = bis.read(3)+1; // 1-8; perfect (the limit used to be 6, but why not 8)
				snoutOffset = bis.read(3); // 0-7, but we have to cap it based on height
				if (snoutOffset > 8-snoutHeight) snoutOffset = 8-snoutHeight;
			}
			EarsLog.debug("Common:Features", "detect(...): Snout: {}x{}x{}+0,{}", snoutWidth, snoutHeight, snoutDepth, snoutOffset);
			
			float chestSize = bis.readUnit(5);
			if (chestSize > 0) {
				EarsLog.debug("Common:Features", "detect(...): Chest: {}%", (int)(chestSize*100));
			}
			
			int wingI = bis.read(3);
			// 3 bits has a range of 0-7
			EarsFeatures.WingMode wingMode = byOrdinalOr(EarsFeatures.WingMode.class, wingI, EarsFeatures.WingMode.NONE);
			boolean animateWings = wingMode == EarsFeatures.WingMode.NONE ? false : bis.readBoolean();
			EarsLog.debug("Common:Features", "detect(...): Wing 3yte: {} (mode={} + animated={})", wingI, wingMode, animateWings);
			
			boolean capeEnabled = bis.readBoolean();
			EarsLog.debug("Common:Features", "detect(...): Cape: {}", capeEnabled);
			
			return new EarsFeatures(
					earMode, earAnchor,
					claws, horn,
					tailMode, tailSegments, tailBend0, tailBend1, tailBend2, tailBend3,
					snoutOffset, snoutWidth, snoutHeight, snoutDepth,
					chestSize,
					wingMode, animateWings,
					capeEnabled,
					alfalfa);
		} catch (IOException e) {
			EarsLog.debug("Common:Features", "detect(...): Error while parsing v1 (Binary) data. Disabling", e);
			return EarsFeatures.DISABLED;
		}
	}

	private static <E extends Enum<E>> E byOrdinalOr(Class<E> clazz, int ordinal, E def) {
		if (ordinal < 0) return def;
		E[] cnst = clazz.getEnumConstants();
		if (ordinal >= cnst.length) return def;
		return cnst[ordinal];
	}

}
