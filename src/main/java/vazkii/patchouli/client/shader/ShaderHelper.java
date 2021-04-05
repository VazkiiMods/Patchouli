package vazkii.patchouli.client.shader;

import net.fabricmc.fabric.api.resource.IdentifiableResourceReloadListener;
import net.minecraft.client.gl.GLImportProcessor;
import net.minecraft.client.gl.GlProgramManager;
import net.minecraft.client.gl.GlShader;
import net.minecraft.client.gl.GlUniform;
import net.minecraft.client.gl.Program;
import net.minecraft.resource.Resource;
import net.minecraft.resource.ResourceManager;
import net.minecraft.resource.SynchronousResourceReloader;
import net.minecraft.util.Identifier;

import vazkii.patchouli.common.base.Patchouli;

import java.io.IOException;

public class ShaderHelper implements SynchronousResourceReloader, IdentifiableResourceReloadListener {
	public static final ShaderHelper INSTANCE = new ShaderHelper();
	public static final Identifier ID = new Identifier(Patchouli.MOD_ID, "shaders");

	private ShaderHelper() {}

	public GlShader alpha = null;
	public GlUniform alphaUniform = null;

	private void cleanup() {
		if (alpha != null) {
			GlProgramManager.deleteProgram(alpha);
			alpha = null;
		}
		if (alphaUniform != null) {
			alphaUniform.close();
			alphaUniform = null;
		}
	}

	@Override
	public void reload(ResourceManager manager) {
		cleanup();
		alpha = loadProgram(manager, "alpha");
		alphaUniform = new GlUniform("alpha", 4 /* one float */, 1, alpha);
		alphaUniform.setLoc(GlUniform.getUniformLocation(alpha.getProgramRef(), "alpha"));
	}

	private static GlShader loadProgram(ResourceManager manager, String name) {
		GLImportProcessor processor = new GLImportProcessor() {

			@Override
			public String loadImport(boolean inline, String name) {
				return "change this :tater:";
			}	
		};
		Identifier vertPath = new Identifier(Patchouli.MOD_ID, "shaders/" + name + ".vsh");
		Identifier fragPath = new Identifier(Patchouli.MOD_ID, "shaders/" + name + ".fsh");
		try (Resource vert = manager.getResource(vertPath);
				Resource frag = manager.getResource(fragPath)) {
			Program vertShader = Program.createFromResource(Program.Type.VERTEX, vertPath.toString(), vert.getInputStream(), Patchouli.MOD_ID, processor);
			Program fragShader = Program.createFromResource(Program.Type.FRAGMENT, fragPath.toString(), frag.getInputStream(), Patchouli.MOD_ID, processor);
			GlShader prog = new InnerProgram(GlProgramManager.createProgram(), vertShader, fragShader);
			GlProgramManager.linkProgram(prog);
			return prog;
		} catch (IOException ex) {
			Patchouli.LOGGER.error("Shader error", ex);
			return null;
		}
	}

	@Override
	public Identifier getFabricId() {
		return ID;
	}

	private static class InnerProgram implements GlShader {
		private final int program;
		private final Program vert;
		private final Program frag;

		private InnerProgram(int program, Program vert, Program frag) {
			this.program = program;
			this.vert = vert;
			this.frag = frag;
		}

		@Override
		public int getProgramRef() {
			return program;
		}

		@Override
		public void markUniformsDirty() {

		}

		@Override
		public Program getVertexShader() {
			return vert;
		}

		@Override
		public Program getFragmentShader() {
			return frag;
		}

		@Override
		public void attachReferencedShaders() {
			vert.attachTo(this);
			frag.attachTo(this);
		}
	}
}
