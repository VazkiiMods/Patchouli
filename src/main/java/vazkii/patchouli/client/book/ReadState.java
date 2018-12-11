package vazkii.patchouli.client.book;

public enum ReadState {

	UNREAD(true, true, 140), 
	PENDING(true, false, 148), 
	DONE(false, false, 0);
	
	private ReadState(boolean hasIcon, boolean showInInventory, int u) {
		this.hasIcon = hasIcon;
		this.showInInventory = showInInventory;
		this.u = u;
	}
	
	public static final ReadState LEAST_IMPORTANT = DONE;
	
	public final boolean hasIcon, showInInventory;
	public final int u;
	
	public static ReadState fromOrdinal(int ord) {
		return ReadState.values()[ord];
	}
	
}
