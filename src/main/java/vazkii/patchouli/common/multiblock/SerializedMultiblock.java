package vazkii.patchouli.common.multiblock;

import com.mojang.brigadier.exceptions.CommandSyntaxException;

import vazkii.patchouli.api.IStateMatcher;

import java.util.HashMap;
import java.util.Map;

public class SerializedMultiblock {

	public String[][] pattern = new String[0][0];
	public Map<String, String> mapping = new HashMap<>();

	boolean symmetrical = false;
	int[] offset = new int[] { 0, 0, 0 };

	public DenseMultiblock toMultiblock() {
		final String allowed = "0_ ";

		for (String[] line : pattern) {
			for (String s : line) {
				for (char c : s.toCharArray()) {
					if (allowed.indexOf(c) == -1 && !mapping.containsKey(String.valueOf(c))) {
						throw new IllegalArgumentException("Character " + c + " in multiblock isn't mapped to a block");
					}
				}
			}
		}

		Object[] targets = new Object[mapping.size() * 2];

		int i = 0;
		for (Map.Entry<String, String> e : mapping.entrySet()) {
			String key = e.getKey();
			String value = e.getValue();

			if (key.length() != 1) {
				throw new IllegalArgumentException(key + " is an invalid mapping key, every mapping key must be 1 character long");
			}

			char keyChar = key.charAt(0);
			IStateMatcher matcher;
			try {
				matcher = StringStateMatcher.fromString(value);
			} catch (CommandSyntaxException ex) {
				throw new IllegalArgumentException("Failure parsing state matcher", ex);
			}

			targets[i] = keyChar;
			targets[i + 1] = matcher;
			i += 2;
		}

		DenseMultiblock mb = new DenseMultiblock(pattern, targets);
		mb.setSymmetrical(symmetrical);
		mb.offset(offset[0], offset[1], offset[2]);
		return mb;
	}
}
