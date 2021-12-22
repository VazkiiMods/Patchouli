package vazkii.patchouli.forge.xplat;

import net.minecraftforge.fml.ModContainer;

import vazkii.patchouli.xplat.XplatModContainer;

import java.nio.file.Path;

public class ForgeXplatModContainer implements XplatModContainer {
	private final ModContainer container;

	public ForgeXplatModContainer(ModContainer container) {
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
	public Path getPath(String s) {
		return container.getModInfo().getOwningFile().getFile().findResource(s);
	}

	@Override
	public Path getRootPath() {
		return container.getModInfo().getOwningFile().getFile().getSecureJar().getRootPath();
	}
}
