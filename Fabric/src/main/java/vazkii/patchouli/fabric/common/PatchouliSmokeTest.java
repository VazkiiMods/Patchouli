package vazkii.patchouli.fabric.common;

import net.fabricmc.fabric.api.gametest.v1.FabricGameTest;
import net.minecraft.gametest.framework.GameTest;
import net.minecraft.gametest.framework.GameTestHelper;

public class PatchouliSmokeTest {
	@GameTest(template = FabricGameTest.EMPTY_STRUCTURE)
	public void doesItRun(GameTestHelper helper) {
		helper.succeed();
	}
}
