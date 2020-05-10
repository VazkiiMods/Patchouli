package vazkii.patchouli.client.book;

public enum EntryDisplayState {

	UNREAD(true, true, true, 140),
	PENDING(true, false, true, 148),
	NEUTRAL(false, false, false, 0),
	COMPLETED(true, false, false, 156);

	private EntryDisplayState(boolean hasIcon, boolean showInInventory, boolean hasAnimation, int u) {
		this.hasIcon = hasIcon;
		this.showInInventory = showInInventory;
		this.hasAnimation = hasAnimation;
		this.u = u;
	}

	public static final EntryDisplayState DEFAULT_TYPE = EntryDisplayState.NEUTRAL;

	public final boolean hasIcon, showInInventory, hasAnimation;
	public final int u;

	public static EntryDisplayState fromOrdinal(int ord) {
		return EntryDisplayState.values()[ord];
	}

}
