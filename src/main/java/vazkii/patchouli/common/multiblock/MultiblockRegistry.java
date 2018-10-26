package vazkii.patchouli.common.multiblock;

import java.util.HashMap;

import net.minecraft.block.material.Material;
import net.minecraft.init.Blocks;
import net.minecraft.util.ResourceLocation;
import vazkii.patchouli.Patchouli;
import vazkii.patchouli.common.multiblock.Multiblock.StateMatcher;

public class MultiblockRegistry {

	public static final HashMap<ResourceLocation, Multiblock> MULTIBLOCKS = new HashMap();

	public static Multiblock crucible;

	public static void preInit() {
		crucible = registerMultiblock(new ResourceLocation(Patchouli.MOD_ID, "crucible"), 
				new Multiblock(new String[][] {
					{ "   ", " 0 ", "   " },
					{ "SSS", "SFS", "SSS" }},
						'0', Blocks.CAULDRON,
						'F', Blocks.FIRE,
						'S', StateMatcher.fromPredicate(Blocks.STONEBRICK, (state) -> state.getBlock().isOpaqueCube(state) && state.getMaterial() == Material.ROCK),
						' ', StateMatcher.ANY))
				.setSymmetrical(true);
	}

	public static Multiblock registerMultiblock(ResourceLocation location, Multiblock multiblock) {
		MULTIBLOCKS.put(location, multiblock);
		return multiblock.setResourceName(location);
	}

}
