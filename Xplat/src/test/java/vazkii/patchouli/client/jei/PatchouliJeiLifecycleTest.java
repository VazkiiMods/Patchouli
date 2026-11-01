package vazkii.patchouli.client.jei;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;

import java.lang.reflect.Proxy;
import mezz.jei.api.runtime.IJeiRuntime;
import org.junit.jupiter.api.Test;

class PatchouliJeiLifecycleTest {
    @Test
    void clearsStoppedRuntimeAndAcceptsItsReplacement() throws Exception {
        var plugin = new PatchouliJeiPlugin();
        var runtimeField = PatchouliJeiPlugin.class.getDeclaredField("jeiRuntime");
        runtimeField.setAccessible(true);
        IJeiRuntime first = proxy();
        IJeiRuntime second = proxy();
        try {
            plugin.onRuntimeAvailable(first);
            assertSame(first, runtimeField.get(null));
            plugin.onRuntimeUnavailable();
            assertNull(runtimeField.get(null));
            assertFalse(PatchouliJeiPlugin.handleRecipeKeybind(0, 0, null));
            plugin.onRuntimeAvailable(second);
            assertSame(second, runtimeField.get(null));
        } finally {
            runtimeField.set(null, null);
        }
    }

    private static IJeiRuntime proxy() {
        return (IJeiRuntime) Proxy.newProxyInstance(IJeiRuntime.class.getClassLoader(),
            new Class<?>[]{IJeiRuntime.class}, (ignored, method, arguments) -> null);
    }
}
