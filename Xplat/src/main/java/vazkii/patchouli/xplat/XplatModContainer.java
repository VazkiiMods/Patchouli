package vazkii.patchouli.xplat;

import org.jetbrains.annotations.Nullable;

import java.nio.file.Path;
import java.util.Collection;

/**
 * Small cross-loader abstraction over mod containers
 */
public interface XplatModContainer {
	String getId();
	String getName();
	@Nullable
	Path getPath(String s);
	Collection<Path> getRootPaths();
}
