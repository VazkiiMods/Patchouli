package vazkii.patchouli.common.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.suggestion.SuggestionProvider;

import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.SharedSuggestionProvider;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.commands.arguments.IdentifierArgument;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.permissions.Permissions;

import vazkii.patchouli.api.PatchouliAPI;
import vazkii.patchouli.common.book.BookRegistry;

import org.jetbrains.annotations.Nullable;

import java.util.Collection;

public class OpenBookCommand {
	private static final SuggestionProvider<CommandSourceStack> BOOK_ID_SUGGESTER =
			(ctx, builder) -> SharedSuggestionProvider.suggestResource(
					BookRegistry.INSTANCE.books.keySet(), builder);

	public static void register(CommandDispatcher<CommandSourceStack> disp) {
		disp.register(Commands.literal("open-patchouli-book")
				.requires(cs -> cs.permissions().hasPermission(Permissions.COMMANDS_GAMEMASTER))
				.then(Commands.argument("targets", EntityArgument.players())
						.then(Commands.argument("book", IdentifierArgument.id())
								.suggests(BOOK_ID_SUGGESTER)
								.executes(ctx -> doIt(EntityArgument.getPlayers(ctx, "targets"),
										IdentifierArgument.getId(ctx, "book"),
										null, 0))
								.then(Commands.argument("entry", IdentifierArgument.id())
										.then(Commands.argument("page", IntegerArgumentType.integer(0))
												.executes(ctx -> doIt(EntityArgument.getPlayers(ctx, "targets"),
														IdentifierArgument.getId(ctx, "book"),
														IdentifierArgument.getId(ctx, "entry"),
														IntegerArgumentType.getInteger(ctx, "page"))))))));
	}

	private static int doIt(Collection<ServerPlayer> players, Identifier book, @Nullable Identifier entry, int page) {
		for (ServerPlayer player : players) {
			if (entry != null) {
				PatchouliAPI.get().openBookEntry(player, book, entry, page);
			} else {
				PatchouliAPI.get().openBookGUI(player, book);
			}
		}
		return players.size();
	}
}
