package vazkii.patchouli.common.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.suggestion.SuggestionProvider;

import net.minecraft.command.CommandSource;
import net.minecraft.command.argument.EntityArgumentType;
import net.minecraft.command.argument.IdentifierArgumentType;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Identifier;

import vazkii.patchouli.api.PatchouliAPI;
import vazkii.patchouli.common.book.Book;
import vazkii.patchouli.common.book.BookRegistry;

import javax.annotation.Nullable;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

public class OpenBookCommand {
	private static final SuggestionProvider<ServerCommandSource> BOOK_ID_SUGGESTER =
			(ctx, builder) -> {
				List<Identifier> ids = new ArrayList<>();
				for (Map.Entry<Identifier, Book> e : BookRegistry.INSTANCE.books.entrySet()) {
					if (!e.getValue().isExtension) {
						ids.add(e.getKey());
					}
				}
				return CommandSource.suggestIdentifiers(ids, builder);
			};

	public static void register(CommandDispatcher<ServerCommandSource> disp) {
		disp.register(CommandManager.literal("open-patchouli-book")
				.requires(cs -> cs.hasPermissionLevel(2))
				.then(CommandManager.argument("targets", EntityArgumentType.players())
						.then(CommandManager.argument("book", IdentifierArgumentType.identifier())
								.suggests(BOOK_ID_SUGGESTER)
								.executes(ctx -> doIt(EntityArgumentType.getPlayers(ctx, "targets"),
										IdentifierArgumentType.getIdentifier(ctx, "book"),
										null, 0))
								.then(CommandManager.argument("entry", IdentifierArgumentType.identifier())
										.then(CommandManager.argument("page", IntegerArgumentType.integer(0))
												.executes(ctx -> doIt(EntityArgumentType.getPlayers(ctx, "targets"),
														IdentifierArgumentType.getIdentifier(ctx, "book"),
														IdentifierArgumentType.getIdentifier(ctx, "entry"),
														IntegerArgumentType.getInteger(ctx, "page"))))))));
	}

	private static int doIt(Collection<ServerPlayerEntity> players, Identifier book, @Nullable Identifier entry, int page) {
		for (var player : players) {
			if (entry != null) {
				PatchouliAPI.get().openBookEntry(player, book, entry, page);
			} else {
				PatchouliAPI.get().openBookGUI(player, book);
			}
		}
		return players.size();
	}
}
