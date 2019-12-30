package vazkii.patchouli.common.multiblock;

import java.util.HashMap;

import net.minecraft.util.Identifier;
import vazkii.patchouli.api.IMultiblock;

public class MultiblockRegistry {

	public static final HashMap<Identifier, IMultiblock> MULTIBLOCKS = new HashMap<>();

	public static void preInit() {
		// This serves as an example for creating multiblocks in code
		// You can check it out ingame by making a multiblock page without
		// a "multiblock" defined, but rather using
		// "multiblock_id": "patchouli:crucible"
//		
//		IPatchouliAPI api = PatchouliAPI.instance;
//		crucible = api.registerMultiblock(new Identifier(Patchouli.MOD_ID, "crucible"),
//				api.makeMultiblock(new String[][] {
//					{ "   ", " 0 ", "   " },
//					{ "SSS", "SFS", "SSS" }},
//						'0', Blocks.CAULDRON,
//						'F', Blocks.FIRE,
//						'S', api.predicateMatcher(Blocks.STONE_BRICKS, (state) -> state.isOpaqueCube() && state.getMaterial() == Material.ROCK),
//						' ', api.anyMatcher()))
//				.setSymmetrical(true);
	}

	public static IMultiblock registerMultiblock(Identifier location, IMultiblock multiblock) {
		MULTIBLOCKS.put(location, multiblock);
		return multiblock.setIdentifier(location);
	}

}
