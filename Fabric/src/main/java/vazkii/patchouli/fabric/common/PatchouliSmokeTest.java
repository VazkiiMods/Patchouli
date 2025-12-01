package vazkii.patchouli.fabric.common;

import net.fabricmc.fabric.api.gametest.v1.GameTest;
import net.minecraft.gametest.framework.GameTestHelper;

public class PatchouliSmokeTest {
	@GameTest()
	public void doesItRun(GameTestHelper helper) {
		helper.succeed();
	}
}
