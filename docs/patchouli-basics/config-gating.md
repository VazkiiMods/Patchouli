# Gating Content with Config Flags

Config Flags are a system that allows content in the book to be dynamically hot swapped based on configuration and the game environment. In a few places across the book json structures, you'll find "flag" tags, which can be filled with a config flag expression.

Flags are boolean values (true or false), and expressions can be a few flags connected to create a statement based on multiple flags. More often than not, you won't need to write an expression, and can just write the flag itself. Should you write the name of a flag that's not defined, it defaults to false.

Any content (all the way from category, entry, page, or even template component) will be disabled if it has a flag expression assigned to it, and said expression happens to evaluate to false.

## Default Flags

* **debug**: Is true when the game is being loaded from an IDE Debug mode
* **advancements_disabled**: Is true when the "Disable Advancement Locking" option in the Patchouli config is true
* **testing_mode**: Is true when the "Testing Mode" option in the Patchouli config is true
* **mod:MODID**: Is true when the mod MODID is loaded in the game. (e.g. "mod:quark" would be true if Quark is loaded)

## Flag Expressions

* `!flag`: Is true when flag is false (negation)
* `&flag1,flag2,flag3...`: Is true when flag1, flag2, flag3... are all true (conjuction)
* `|flag2,flag2,flag3...`: Is true when at least one of flag1, flag2, flag3... is true (disjunction)

## Adding your own Flags

This system is more in line for modders than mod pack makers, as it allows them to dynamically change the book based on their own config needs. This can be accomplished through hooks in the [Patchouli API](https://github.com/Vazkii/Patchouli/tree/master/src/main/java/vazkii/patchouli/api).
