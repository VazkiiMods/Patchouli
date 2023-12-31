package vazkii.patchouli.common.advancement;

import vazkii.patchouli.mixin.AccessorCriteriaTriggers;

public class PatchouliCriteriaTriggers {
	public static void init() {
		AccessorCriteriaTriggers.patchouli$register(PatchouliBookOpenTrigger.INSTANCE);
	}
}
