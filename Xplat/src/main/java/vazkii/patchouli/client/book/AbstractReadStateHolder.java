package vazkii.patchouli.client.book;

import java.util.Comparator;
import java.util.stream.Stream;

public abstract class AbstractReadStateHolder {

	EntryDisplayState readState;
	boolean readStateDirty = true;

	public EntryDisplayState getReadState() {
		if (readStateDirty) {
			readState = computeReadState();
			readStateDirty = false;
		}

		return readState;
	}

	public void markReadStateDirty() {
		readStateDirty = true;
	}

	protected abstract EntryDisplayState computeReadState();

	public static EntryDisplayState mostImportantState(Stream<EntryDisplayState> stream) {
		return stream.min(Comparator.comparingInt(EntryDisplayState::ordinal))
				.orElse(EntryDisplayState.DEFAULT_TYPE);
	}

}
