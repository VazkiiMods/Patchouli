package vazkii.patchouli.api;

import java.util.List;

public interface PatchouliConfigAccess {
	boolean disableAdvancementLocking();

	List<String> noAdvancementBooks();

	boolean testingMode();

	String inventoryButtonBook();

	QuickLookupMode quickLookupMode();

  default boolean useShiftForQuickLookup() {
    return quickLookupMode() == QuickLookupMode.SHIFT;
  }

	TextOverflowMode overflowMode();

	int quickLookupTime();

  enum QuickLookupMode {
    CTRL,
    SHIFT,
    ALT
  }

	enum TextOverflowMode {
		OVERFLOW,
		TRUNCATE,
		RESIZE
	}
}
