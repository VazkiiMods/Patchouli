package vazkii.patchouli.xplat;

import vazkii.patchouli.api.PatchouliAPI;

import java.util.ServiceLoader;
import java.util.stream.Collectors;

class ServiceUtil {
	public static <T> T findService(Class<T> clazz) {
		var providers = ServiceLoader.load(clazz, clazz.getClassLoader()).stream().toList();
		if (providers.size() != 1) {
			var names = providers.stream().map(p -> p.type().getName()).collect(Collectors.joining(",", "[", "]"));
			var msg = "There should be exactly one implementation of %s on the classpath. Found: %s"
					.formatted(clazz.getName(), names);
			throw new IllegalStateException(msg);
		} else {
			var provider = providers.get(0);
			PatchouliAPI.LOGGER.debug("Instantiating {} for service {}", provider.type().getName(), clazz.getName());
			return provider.get();
		}
	}
}
