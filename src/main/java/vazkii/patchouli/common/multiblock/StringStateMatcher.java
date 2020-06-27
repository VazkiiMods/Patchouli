package vazkii.patchouli.common.multiblock;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.exceptions.CommandSyntaxException;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.command.arguments.BlockStateParser;
import net.minecraft.state.Property;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.ITag;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockReader;
import net.minecraftforge.common.util.TriPredicate;

import vazkii.patchouli.api.IStateMatcher;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class StringStateMatcher {
	public static IStateMatcher fromString(String s) throws CommandSyntaxException {
		s = s.trim();
		if (s.equals("ANY")) {
			return StateMatcher.ANY;
		}
		if (s.equals("AIR")) {
			return StateMatcher.AIR;
		}

		// c.f. BlockPredicateArgument.parse. Similar, but doesn't use vanilla's weird CachedBlockInfo class.
		BlockStateParser parser = new BlockStateParser(new StringReader(s), true).parse(false);
		BlockState state = parser.getState();

		if (state != null) {
			return new ExactMatcher(state, parser.getProperties());
		} else {
			ITag.INamedTag<Block> tag = BlockTags.makeWrapperTag(parser.getTag().toString());
			return new TagMatcher(tag, parser.getStringProperties());
		}
	}

	private static class ExactMatcher implements IStateMatcher {
		private final BlockState state;
		private final Map<Property<?>, Comparable<?>> props;

		private ExactMatcher(BlockState state, Map<Property<?>, Comparable<?>> props) {
			this.state = state;
			this.props = props;
		}

		@Override
		public BlockState getDisplayedState(int ticks) {
			return state;
		}

		@Override
		public TriPredicate<IBlockReader, BlockPos, BlockState> getStatePredicate() {
			return (w, p, s) -> state.getBlock() == s.getBlock() && checkProps(s);
		}

		private boolean checkProps(BlockState state) {
			for (Map.Entry<Property<?>, Comparable<?>> e : props.entrySet()) {
				if (!state.get(e.getKey()).equals(e.getValue())) {
					return false;
				}
			}
			return true;
		}

		@Override
		public boolean equals(Object o) {
			if (this == o) {
				return true;
			}
			if (o == null || getClass() != o.getClass()) {
				return false;
			}
			ExactMatcher that = (ExactMatcher) o;
			return Objects.equals(state, that.state) &&
					Objects.equals(props, that.props);
		}

		@Override
		public int hashCode() {
			return Objects.hash(state, props);
		}
	}

	private static class TagMatcher implements IStateMatcher {
		private final ITag.INamedTag<Block> tag;
		private final Map<String, String> props;

		private TagMatcher(ITag.INamedTag<Block> tag, Map<String, String> props) {
			this.tag = tag;
			this.props = props;
		}

		@Override
		public BlockState getDisplayedState(int ticks) {
			List<Block> all = new ArrayList<>(tag.func_230236_b_());
			if (all.isEmpty()) {
				return Blocks.BEDROCK.getDefaultState(); // show something impossible
			} else {
				int idx = (ticks / 20) % all.size();
				return all.get(idx).getDefaultState();
			}
		}

		@Override
		public TriPredicate<IBlockReader, BlockPos, BlockState> getStatePredicate() {
			return (w, p, s) -> tag.func_230235_a_(s.getBlock()) && checkProps(s);
		}

		private boolean checkProps(BlockState state) {
			for (Map.Entry<String, String> entry : props.entrySet()) {
				Property<?> prop = state.getBlock().getStateContainer().getProperty(entry.getKey());
				if (prop == null) {
					return false;
				}

				Comparable<?> value = prop.parseValue(entry.getValue()).orElse(null);
				if (value == null) {
					return false;
				}

				if (!state.get(prop).equals(value)) {
					return false;
				}
			}
			return true;
		}

		@Override
		public boolean equals(Object o) {
			if (this == o) {
				return true;
			}
			if (o == null || getClass() != o.getClass()) {
				return false;
			}
			TagMatcher that = (TagMatcher) o;
			return Objects.equals(tag.func_230234_a_(), that.tag.func_230234_a_()) &&
					Objects.equals(props, that.props);
		}

		@Override
		public int hashCode() {
			return Objects.hash(tag.func_230234_a_(), props);
		}
	}
}
