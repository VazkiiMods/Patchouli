package vazkii.patchouli.client.shader;

import com.mojang.blaze3d.platform.GlStateManager;
import net.fabricmc.fabric.api.resource.IdentifiableResourceReloadListener;
import net.minecraft.client.gl.GlProgram;
import net.minecraft.client.gl.GlProgramManager;
import net.minecraft.client.gl.GlShader;
import net.minecraft.client.gl.GlUniform;
import net.minecraft.resource.Resource;
import net.minecraft.resource.ResourceManager;
import net.minecraft.resource.SynchronousResourceReloadListener;
import net.minecraft.util.Identifier;
import vazkii.patchouli.common.base.Patchouli;

import javax.annotation.Nullable;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class ShaderHelper implements SynchronousResourceReloadListener, IdentifiableResourceReloadListener {
    public static final ShaderHelper INSTANCE = new ShaderHelper();
    public static final Identifier ID = new Identifier(Patchouli.MOD_ID, "shaders");

    private ShaderHelper() {}

    public GlProgram alpha = null;
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
    public void apply(ResourceManager manager) {
        cleanup();
        alpha = loadProgram(manager ,"alpha");
        alphaUniform = new GlUniform("alpha", 4 /* one float */, 1, alpha);
        alphaUniform.setLoc(GlUniform.getUniformLocation(alpha.getProgramRef(), "alpha"));
    }

    private static GlProgram loadProgram(ResourceManager manager, String name) {
        Identifier vertPath = new Identifier(Patchouli.MOD_ID, "shaders/" + name + ".vsh");
        Identifier fragPath = new Identifier(Patchouli.MOD_ID, "shaders/" + name + ".fsh");
        try (Resource vert = manager.getResource(vertPath);
             Resource frag = manager.getResource(fragPath)) {
            GlShader vertShader = GlShader.createFromResource(GlShader.Type.VERTEX, vertPath.toString(), vert.getInputStream());
            GlShader fragShader = GlShader.createFromResource(GlShader.Type.FRAGMENT, fragPath.toString(), frag.getInputStream());
            GlProgram prog = new Program(GlProgramManager.createProgram(), vertShader, fragShader);
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

    private static class Program implements GlProgram {
        private final int program;
        private final GlShader vert;
        private final GlShader frag;

        private Program(int program, GlShader vert, GlShader frag) {
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
        public GlShader getVertexShader() {
            return vert;
        }

        @Override
        public GlShader getFragmentShader() {
            return frag;
        }
    }
}
