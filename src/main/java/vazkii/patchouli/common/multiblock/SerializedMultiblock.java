package vazkii.patchouli.common.multiblock;

import java.util.HashMap;
import java.util.Map;

import com.google.gson.annotations.SerializedName;

import net.minecraft.block.Block;
import net.minecraft.util.ResourceLocation;
import vazkii.patchouli.common.multiblock.Multiblock.StateMatcher;

public class SerializedMultiblock {

	String[][] pattern = new String[0][0];
	Map<String, String> mapping = new HashMap();
	
	boolean symmetrical = false;
	int[] offset = new int[] { 0, 0, 0 };
	@SerializedName("view_offset")
	int[] viewOffset = new int[] { 0, 0, 0 };
	
	public Multiblock toMultiblock() {
		for(String[] line : pattern)
			for(String s : line)
				for(char c : s.toCharArray())
					if(!mapping.containsKey(String.valueOf(c)))
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
		mb.offsetView(viewOffset[0], viewOffset[1], viewOffset[2]);
		return mb;
	}
	
	private StateMatcher getStateMatcher(String s) {
		s = s.trim();
		if(s.equals("ANY"))
			return StateMatcher.ANY;
		if(s.equals("AIR"))
			return StateMatcher.AIR;
		
		Block block = Block.REGISTRY.getObject(new ResourceLocation(s));
		if(block != null)
			return StateMatcher.fromBlockLoose(block);
		
		// TODO support block states
		
		return null;
	}
	
}
