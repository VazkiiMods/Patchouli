package vazkii.patchouli.neoforge.xplat;

import net.neoforged.fml.ModContainer;

import vazkii.patchouli.xplat.XplatModContainer;

import java.nio.file.Path;

public class NeoForgeXplatModContainer implements XplatModContainer {
	private final ModContainer container;

	public NeoForgeXplatModContainer(ModContainer container) {
		this.container = container;
	}

	@Override
	public String getId() {
		return container.getModId();
	}

	@Override
	public String getName() {
		return container.getModInfo().getDisplayName();
	}

	@Override
	public void visit(String basePath, Visitor visitor) {
		container.getModInfo().getOwningFile().getFile().getContents().visitContent(basePath, (relativePath, resource) -> visitor.visit(Path.of(relativePath), resource::open));
	}
}
