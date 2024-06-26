package pl.asie.foamfix.repack.com.unascribed.ears.api.iface;

import pl.asie.foamfix.repack.com.unascribed.ears.api.EarsStateType;
import pl.asie.foamfix.repack.com.unascribed.ears.api.OverrideResult;
import pl.asie.foamfix.repack.com.unascribed.ears.api.registry.EarsStateOverriderRegistry;

public interface EarsStateOverrider {

	/**
	 * @see EarsStateOverriderRegistry#register(String, EarsStateOverrider)
	 */
	OverrideResult isActive(EarsStateType state, Object peer);
	
}
