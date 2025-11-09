package vazkii.patchouli.client.base;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;

/**
 * Simple helper to write distinct Patchouli model debug lines to a separate file
 * so they can be grepped easily when the main game log is noisy.
 */
public final class ModelLog {
    private static final Path LOG = Paths.get("run", "patchouli_models.log");

    private ModelLog() {}

    public static void log(String s) {
        try {
            Path parent = LOG.getParent();
            if (parent != null && !Files.exists(parent)) {
                Files.createDirectories(parent);
            }
            Files.writeString(LOG, s + System.lineSeparator(), StandardOpenOption.CREATE, StandardOpenOption.APPEND);
        } catch (IOException ignored) {
        }
    }
}
