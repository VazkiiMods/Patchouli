package vazkii.patchouli.common.multiblock;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.exceptions.CommandSyntaxException;

import net.fabricmc.fabric.impl.tag.extension.TagDelegate;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.command.argument.BlockArgumentParser;
import net.minecraft.state.property.Property;
import net.minecraft.tag.BlockTags;
import net.minecraft.tag.Tag;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.BlockView;

import vazkii.patchouli.api.IStateMatcher;
import vazkii.patchouli.api.TriPredicate;

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

		// c.f. BlockPredicateArgumentType. Similar, but doesn't use vanilla's weird caching class.
		BlockArgumentParser parser = new BlockArgumentParser(new StringReader(s), true).parse(false);
		BlockState state = parser.getBlockState();

		if (state != null) {
			return new ExactMatcher(state, parser.getBlockProperties());
		} else {
			Tag.Identified<Block> tag = new TagDelegate<>(Objects.requireNonNull(parser.getTagId()), BlockTags::getTagGroup);
			return new TagMatcher(tag, parser.getProperties());
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
		public TriPredicate<BlockView, BlockPos, BlockState> getStatePredicate() {
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
		private final Tag.Identified<Block> tag;
		private final Map<String, String> props;

		private TagMatcher(Tag.Identified<Block> tag, Map<String, String> props) {
			this.tag = tag;
			this.props = props;
		}

		@Override
		public BlockState getDisplayedState(int ticks) {
			List<Block> all = new ArrayList<>(tag.values());
			if (all.isEmpty()) {
				return Blocks.BEDROCK.getDefaultState(); // show something impossible
			} else {
				int idx = (ticks / 20) % all.size();
				return all.get(idx).getDefaultState();
			}
		}

		@Override
		public TriPredicate<BlockView, BlockPos, BlockState> getStatePredicate() {
			return (w, p, s) -> tag.contains(s.getBlock()) && checkProps(s);
		}

		private boolean checkProps(BlockState state) {
			for (Map.Entry<String, String> entry : props.entrySet()) {
				Property<?> prop = state.getBlock().getStateManager().getProperty(entry.getKey());
				if (prop == null) {
					return false;
				}

				Comparable<?> value = prop.parse(entry.getValue()).orElse(null);
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
			return Objects.equals(tag.getId(), that.tag.getId()) &&
					Objects.equals(props, that.props);
		}

		@Override
		public int hashCode() {
			return Objects.hash(tag.getId(), props);
		}
	}
}
