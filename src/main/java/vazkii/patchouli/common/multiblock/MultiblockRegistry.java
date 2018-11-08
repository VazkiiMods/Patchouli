package vazkii.patchouli.common.multiblock;

import java.util.HashMap;

import net.minecraft.block.material.Material;
import net.minecraft.init.Blocks;
import net.minecraft.util.ResourceLocation;
import vazkii.patchouli.api.IMultiblock;
import vazkii.patchouli.api.PatchouliAPI;
import vazkii.patchouli.api.PatchouliAPI.IPatchouliAPI;
import vazkii.patchouli.common.base.Patchouli;

public class MultiblockRegistry {

	public static final HashMap<ResourceLocation, IMultiblock> MULTIBLOCKS = new HashMap();

	public static IMultiblock crucible;

	public static void preInit() {
		// This serves as an example for creating multiblocks in code
		// You can check it out ingame by making a multiblock page without
		// a "multiblock" defined, but rather using
		// "multiblock_id": "patchouli:crucible"
		
		IPatchouliAPI api = PatchouliAPI.instance;
		crucible = api.registerMultiblock(new ResourceLocation(Patchouli.MOD_ID, "crucible"), 	
				api.makeMultiblock(new String[][] {
					{ "   ", " 0 ", "   " },
					{ "SSS", "SFS", "SSS" }},
						'0', Blocks.CAULDRON,
						'F', Blocks.FIRE,
						'S', api.predicateMatcher(Blocks.STONEBRICK, (state) -> state.getBlock().isOpaqueCube(state) && state.getMaterial() == Material.ROCK),
						' ', api.anyMatcher()))
				.setSymmetrical(true);
	}

	public static IMultiblock registerMultiblock(ResourceLocation location, IMultiblock multiblock) {
		MULTIBLOCKS.put(location, multiblock);
		return multiblock.setResourceLocation(location);
	}

}
