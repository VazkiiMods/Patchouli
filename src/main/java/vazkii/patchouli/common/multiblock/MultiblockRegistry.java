package vazkii.patchouli.common.multiblock;

import net.minecraft.util.Identifier;

import vazkii.patchouli.api.IMultiblock;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class MultiblockRegistry {

	public static final Map<Identifier, IMultiblock> MULTIBLOCKS = new ConcurrentHashMap<>();

	public static IMultiblock registerMultiblock(Identifier location, IMultiblock multiblock) {
		IMultiblock prev = MULTIBLOCKS.put(location, multiblock);
		if (prev != null) {
			throw new IllegalArgumentException("Multiblock " + location + " already registered");
		} else {
			return multiblock.setId(location);
		}
	}

}
