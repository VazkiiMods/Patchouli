package vazkii.patchouli.xplat;

import org.apache.commons.io.function.IOSupplier;

import java.io.InputStream;
import java.nio.file.Path;

/**
 * Small cross-loader abstraction over mod containers
 */
public interface XplatModContainer {
	String getId();
	String getName();
	void visit(String basePath, Visitor visitor);

	@FunctionalInterface
	interface Visitor {
		void visit(Path relativePath, IOSupplier<InputStream> file);
	}
}
