package vazkii.patchouli.xplat;

import java.nio.file.Path;

/**
 * Small cross-loader abstraction over mod containers
 */
public interface XplatModContainer {
    String getId();
    String getName();
    Path getPath(String s);
    Path getRootPath();
}
