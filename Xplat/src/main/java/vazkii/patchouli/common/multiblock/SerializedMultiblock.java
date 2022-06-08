package vazkii.patchouli.common.multiblock;

import com.google.gson.annotations.SerializedName;
import com.mojang.brigadier.exceptions.CommandSyntaxException;

import net.minecraft.core.BlockPos;

import vazkii.patchouli.api.IStateMatcher;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

public class SerializedMultiblock {

	@SerializedName("pattern") private String[][] densePattern = null;
	@SerializedName("sparse_pattern") private Map<String, List<List<Integer>>> sparsePattern = null;
	private Map<String, String> mapping = new HashMap<>();

	private boolean symmetrical = false;
	private int[] offset = new int[] { 0, 0, 0 };

	private static char assertValidMappingKey(String s) {
		if (s.length() != 1) {
			throw new IllegalArgumentException(s + " is an invalid mapping key, every mapping key must be 1 character long");
		}
		return s.charAt(0);
	}

	private static Map<Character, IStateMatcher> deserializeMapping(Map<String, String> mapping) {
		Map<Character, IStateMatcher> ret = new HashMap<>(mapping.size());
		for (Entry<String, String> e : mapping.entrySet()) {
			char key = assertValidMappingKey(e.getKey());
			String value = e.getValue();

			IStateMatcher matcher;
			try {
				matcher = StringStateMatcher.fromString(value);
			} catch (CommandSyntaxException ex) {
				throw new IllegalArgumentException("Failure parsing state matcher", ex);
			}

			ret.put(key, matcher);
		}
		if (!ret.containsKey('_')) {
			ret.put('_', StateMatcher.ANY);
		}
		if (!ret.containsKey(' ')) {
			ret.put(' ', StateMatcher.AIR);
		}
		if (!ret.containsKey('0')) {
			ret.put('0', StateMatcher.AIR);
		}
		return ret;
	}

	private SparseMultiblock deserializeSparse() {
		Map<Character, IStateMatcher> matchers = deserializeMapping(mapping);
		Map<BlockPos, IStateMatcher> data = new HashMap<>();
		for (Entry<String, List<List<Integer>>> e : sparsePattern.entrySet()) {
			char key = assertValidMappingKey(e.getKey());
			assertMappingContains(key);

			List<List<Integer>> positions = e.getValue();
			for (List<Integer> position : positions) {
				if (position.size() != 3) {
					throw new IllegalArgumentException("Position has more than three coordinates: " + position);
				}
				BlockPos pos = new BlockPos(position.get(0), position.get(1), position.get(2));
				data.put(pos, matchers.get(key));
			}
		}

		return new SparseMultiblock(data);
	}

	private void assertMappingContains(char c) {
		if (c != '0' && c != '_' && c != ' ' && !mapping.containsKey(String.valueOf(c))) {
			throw new IllegalArgumentException("Character " + c + " in multiblock isn't mapped to a block");
		}
	}

	public DenseMultiblock deserializeDense() {
		for (String[] line : densePattern) {
			for (String s : line) {
				for (char c : s.toCharArray()) {
					assertMappingContains(c);
				}
			}
		}

		return new DenseMultiblock(densePattern, deserializeMapping(mapping));
	}

	public AbstractMultiblock toMultiblock() {
		if ((densePattern != null) == (sparsePattern != null)) {
			throw new IllegalArgumentException("One and only one of pattern and sparse_pattern should be specified");
		}

		AbstractMultiblock mb;
		if (densePattern != null) {
			mb = deserializeDense();
		} else {
			mb = deserializeSparse();
		}

		mb.setSymmetrical(symmetrical);
		mb.offset(offset[0], offset[1], offset[2]);
		return mb;
	}
}
