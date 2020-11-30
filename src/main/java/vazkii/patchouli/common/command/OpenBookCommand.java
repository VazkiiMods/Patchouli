package vazkii.patchouli.common.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.suggestion.SuggestionProvider;

import net.minecraft.command.CommandSource;
import net.minecraft.command.Commands;
import net.minecraft.command.ISuggestionProvider;
import net.minecraft.command.arguments.EntityArgument;
import net.minecraft.command.arguments.ResourceLocationArgument;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.util.ResourceLocation;

import vazkii.patchouli.api.PatchouliAPI;
import vazkii.patchouli.common.book.Book;
import vazkii.patchouli.common.book.BookRegistry;

import javax.annotation.Nullable;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

public class OpenBookCommand {
	private static final SuggestionProvider<CommandSource> BOOK_ID_SUGGESTER =
			(ctx, builder) -> {
				List<ResourceLocation> ids = new ArrayList<>();
				for (Map.Entry<ResourceLocation, Book> e : BookRegistry.INSTANCE.books.entrySet()) {
					if (!e.getValue().isExtension) {
						ids.add(e.getKey());
					}
				}
				return ISuggestionProvider.suggestIterable(ids, builder);
			};

	public static void register(CommandDispatcher<CommandSource> disp) {
		disp.register(Commands.literal("open-patchouli-book")
				.requires(cs -> cs.hasPermissionLevel(2))
				.then(Commands.argument("targets", EntityArgument.players())
						.then(Commands.argument("book", ResourceLocationArgument.resourceLocation())
								.suggests(BOOK_ID_SUGGESTER)
								.executes(ctx -> doIt(EntityArgument.getPlayers(ctx, "targets"),
										ResourceLocationArgument.getResourceLocation(ctx, "book"),
										null, 0))
								.then(Commands.argument("entry", ResourceLocationArgument.resourceLocation())
										.then(Commands.argument("page", IntegerArgumentType.integer(0))
												.executes(ctx -> doIt(EntityArgument.getPlayers(ctx, "targets"),
														ResourceLocationArgument.getResourceLocation(ctx, "book"),
														ResourceLocationArgument.getResourceLocation(ctx, "entry"),
														IntegerArgumentType.getInteger(ctx, "page"))))))));
	}

	private static int doIt(Collection<ServerPlayerEntity> players, ResourceLocation book, @Nullable ResourceLocation entry, int page) {
		for (ServerPlayerEntity player : players) {
			if (entry != null) {
				PatchouliAPI.get().openBookEntry(player, book, entry, page);
			} else {
				PatchouliAPI.get().openBookGUI(player, book);
			}
		}
		return players.size();
	}
}
