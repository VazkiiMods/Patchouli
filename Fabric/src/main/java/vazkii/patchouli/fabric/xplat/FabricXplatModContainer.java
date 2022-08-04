package vazkii.patchouli.fabric.xplat;

import net.fabricmc.loader.api.ModContainer;

import vazkii.patchouli.xplat.XplatModContainer;

import java.nio.file.Path;
import java.util.List;

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
	public Path getPath(String file) {
		return container.getPath(file);
	}

	@Override
	public List<Path> getRootPaths() {
		return container.getRootPaths();
	}
}
