package vazkii.patchouli.common.multiblock;

import java.util.HashMap;
import java.util.Map;

import net.minecraft.block.Block;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.state.IBlockState;
import net.minecraft.util.ResourceLocation;
import vazkii.patchouli.api.VariableHolder;

public class SerializedMultiblock {

	@VariableHolder
	public String[][] pattern = new String[0][0];
	@VariableHolder
	public Map<String, String> mapping = new HashMap<>();

	boolean symmetrical = false;
	int[] offset = new int[] { 0, 0, 0 };
	
	public Multiblock toMultiblock() {
		final String allowed = "0_ ";
		
		for(String[] line : pattern)
			for(String s : line)
				for(char c : s.toCharArray())
					if(allowed.indexOf(c) == -1 && !mapping.containsKey(String.valueOf(c)))
						throw new IllegalArgumentException("Character " + c + " in multiblock isn't mapped to a block");

		Object[] targets = new Object[mapping.size() * 2];

		int i = 0;
		for(Map.Entry<String, String> e : mapping.entrySet()) {
			String key = e.getKey();
			String value = e.getValue();

			if(key.length() != 1)
				throw new IllegalArgumentException(key + " is an invalid mapping key, every mapping key must be 1 character long");

			char keyChar = key.charAt(0);
			StateMatcher matcher = getStateMatcher(value);

			if(matcher == null)
				throw new IllegalArgumentException(value + " could not be resolved into a proper state for mapping '" + keyChar + "'");

			targets[i] = keyChar;
			targets[i + 1] = matcher;
			i += 2;
		}

		Multiblock mb = new Multiblock(pattern, targets);
		mb.setSymmetrical(symmetrical);
		mb.offset(offset[0], offset[1], offset[2]);
		return mb;
	}

	private StateMatcher getStateMatcher(String s) {
		s = s.trim();
		if(s.equals("ANY"))
			return StateMatcher.ANY;
		if(s.equals("AIR"))
			return StateMatcher.AIR;

		String[] split = s.split("\\[");
		Block block = Block.REGISTRY.getObject(new ResourceLocation(split[0]));
		if (block != null) {
			if (split.length > 1) {
				IBlockState state = block.getDefaultState();
				for (String part : split[1].replace("]", "").split(",")) {
					String[] keyValue = part.split("=");
					for (IProperty<?> prop : state.getProperties().keySet()) {
						IBlockState changed = findProperty(state, prop, keyValue[0], keyValue[1]);
						if (changed != null) {
							state = changed;
							break;
						}
					}
				}
				return StateMatcher.fromState(state);
			} else
				return StateMatcher.fromBlockLoose(block);
		}

		return null;
	}

	private <T extends Comparable<T>> IBlockState findProperty(IBlockState state, IProperty<T> prop, String key, String newValue) {
		if (key.equals(prop.getName())) {
			for (T value : prop.getAllowedValues()) {
				if (prop.getName(value).equals(newValue)) {
					return state.withProperty(prop, value);
				}
			}
		}
		return null;
	}
}
