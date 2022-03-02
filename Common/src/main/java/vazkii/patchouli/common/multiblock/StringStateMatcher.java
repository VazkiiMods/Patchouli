package vazkii.patchouli.common.multiblock;

import com.google.common.collect.ImmutableList;
import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.exceptions.CommandSyntaxException;

import net.minecraft.commands.arguments.blocks.BlockStateParser;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Registry;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.Property;

import vazkii.patchouli.api.IStateMatcher;
import vazkii.patchouli.api.TriPredicate;

import java.util.Map;
import java.util.Map.Entry;
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
		BlockStateParser parser = new BlockStateParser(new StringReader(s), true).parse(false);
		BlockState state = parser.getState();

		if (state != null) {
			return new ExactMatcher(state, parser.getProperties());
		} else {
			return new TagMatcher(parser.getTag(), parser.getVagueProperties());
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
		public TriPredicate<BlockGetter, BlockPos, BlockState> getStatePredicate() {
			return (w, p, s) -> state.getBlock() == s.getBlock() && checkProps(s);
		}

		private boolean checkProps(BlockState state) {
			for (Entry<Property<?>, Comparable<?>> e : props.entrySet()) {
				if (!state.getValue(e.getKey()).equals(e.getValue())) {
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
		private final TagKey<Block> tag;
		private final Map<String, String> props;

		private TagMatcher(TagKey<Block> tag, Map<String, String> props) {
			this.tag = tag;
			this.props = props;
		}

		@Override
		public BlockState getDisplayedState(int ticks) {
			var all = ImmutableList.copyOf(Registry.BLOCK.getTagOrEmpty(this.tag));
			if (all.isEmpty()) {
				return Blocks.BEDROCK.defaultBlockState(); // show something impossible
			} else {
				int idx = (ticks / 20) % all.size();
				return all.get(idx).value().defaultBlockState();
			}
		}

		@Override
		public TriPredicate<BlockGetter, BlockPos, BlockState> getStatePredicate() {
			return (w, p, s) -> s.getBlock().builtInRegistryHolder().is(tag) && checkProps(s);
		}

		private boolean checkProps(BlockState state) {
			for (Entry<String, String> entry : props.entrySet()) {
				Property<?> prop = state.getBlock().getStateDefinition().getProperty(entry.getKey());
				if (prop == null) {
					return false;
				}

				Comparable<?> value = prop.getValue(entry.getValue()).orElse(null);
				if (value == null) {
					return false;
				}

				if (!state.getValue(prop).equals(value)) {
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
			return Objects.equals(tag, that.tag) && Objects.equals(props, that.props);
		}

		@Override
		public int hashCode() {
			return Objects.hash(tag, props);
		}
	}
}
