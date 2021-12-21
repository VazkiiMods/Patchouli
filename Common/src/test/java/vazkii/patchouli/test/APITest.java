package vazkii.patchouli.test;

import org.junit.jupiter.api.Test;

import vazkii.patchouli.api.PatchouliAPI;
import vazkii.patchouli.common.base.PatchouliAPIImpl;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * Basic smoke test to ensure the class name strings in the API are still accurate
 */
public class APITest {
	@Test
	public void testMainAPI() {
		assertEquals(PatchouliAPI.get().getClass(), PatchouliAPIImpl.class);
	}
}
