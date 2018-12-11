package vazkii.patchouli.client.book;

import java.util.stream.Stream;

import com.google.common.collect.Streams;

import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public abstract class AbstractReadStateHolder {

	transient ReadState readState;
	transient boolean readStateDirty = true;
	
	public ReadState getReadState() {
		if(readStateDirty) {
			readState = computeReadState();
			readStateDirty = false;
		}
		
		return readState;
	}
	
	public void markReadStateDirty() {
		readStateDirty = true;
	}
	
	protected abstract ReadState computeReadState();
	
	public static ReadState mostImportantState(Stream<ReadState>... streams) {
		Stream<ReadState> stream = Streams.concat(streams);
		return ReadState.fromOrdinal(stream.mapToInt(ReadState::ordinal).min().orElse(ReadState.LEAST_IMPORTANT.ordinal()));
	}
	
}
