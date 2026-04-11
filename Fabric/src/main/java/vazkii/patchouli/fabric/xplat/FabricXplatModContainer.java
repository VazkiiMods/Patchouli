package vazkii.patchouli.fabric.xplat;

import net.fabricmc.loader.api.ModContainer;

import vazkii.patchouli.xplat.XplatModContainer;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Iterator;

public class FabricXplatModContainer implements XplatModContainer {
	private final ModContainer container;

	public FabricXplatModContainer(ModContainer container) {
		this.container = container;
	}

	@Override
	public String getId() {
		return container.getMetadata().getId();
	}

	@Override
	public String getName() {
		return container.getMetadata().getName();
	}

	@Override
	public void visit(String basePath, Visitor visitor) {
		for (Path rootPath : container.getRootPaths()) {
			Path path = rootPath.resolve(basePath);
			if (!Files.exists(path)) {
				continue;
			}
			try (var stream = Files.walk(path, 2)) {
				Iterator<Path> itr = stream.iterator();

				while (itr.hasNext()) {
					Path file = itr.next();
					if (!Files.isRegularFile(file))
						continue;
					visitor.visit(file, () -> Files.newInputStream(file));
				}
			} catch (IOException e) {
				throw new UncheckedIOException(e);
			}
		}
	}
}
