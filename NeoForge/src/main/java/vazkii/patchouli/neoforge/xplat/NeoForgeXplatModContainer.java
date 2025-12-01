package vazkii.patchouli.neoforge.xplat;

import net.neoforged.fml.ModContainer;

import vazkii.patchouli.xplat.XplatModContainer;

import org.jetbrains.annotations.Nullable;

import java.nio.file.Path;
import java.util.Collection;

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
	public @Nullable Path getPath(String s) {
		return container.getModInfo().getOwningFile().getFile().getContents().findFile(s).map(Path::of).orElse(null);
	}

	@Override
	public Collection<Path> getRootPaths() {
		return container.getModInfo().getOwningFile().getFile().getContents().getContentRoots();
	}
}
