# 1.17 Upgrade Guide

Several changes were made during the 1.17 update cycle to make Patchouli easier to maintain and resolve longstanding warts and bugs.

This page highlights some of the more major changes you may run into as a book author.

### Fully qualified names

All IDs in Patchouli books must now be fully qualified with a namespace. This includes:

* Template component IDs
* Page types
* Page categories
* Template IDs

The built-in page types and template components provided by Patchouli have namespace
`patchouli`.

[This](https://github.com/VazkiiMods/Patchouli/commit/1ec2e4d6f0f74736a60b292f44c97db59f3eb82a#diff-e0281f985a32f2ce1812733801b9f87b4623c04184eadee5d6bca978d58300f3L2)
gives an example of migrating. Of course, if you are a modder, you will be using your own
mod-id to refer to your own categories, entries, and templates.

### Resource Pack-based books

Books can now specify `use_resource_pack: true` in their book.json to have their contents
loaded through the resource system.

:::note

This now allows book contents to be overrideable with resource packs.

:::

Despite residing in `assets` or `data`, Patchouli books have never loaded properly from
neither resourcepacks nor datapacks, and never responded to attempts to override them via
those two mechanisms. 1.17 includes the first steps towards resolving this. All book
content can now be loaded and overrided freely using resource packs. "Book content"
meaning everything *except* `book.json`, which must remain in the same place as it has
been from 1.14-1.16.

You opt in by specifying the above flag, and moving everything that isn't your book json
from the `data` folder to `assets`.

An example of this migration being done can be seen
[here](https://github.com/VazkiiMods/Botania/commit/3f6266ce88231660da3ff305977edcb2e813e8d6?w=1).

### Convenience recipe type for books

Crafting recipes for Patchouli books can now be provided using the `patchouli:book_recipe` and `patchouli:shapeless_book_recipe` recipe types as a convenience, and to avoid issues related to lack of default support on Fabric for NBT recipe output. These recipes use the syntax based on vanilla `minecraft:crafting_shaped` and `minecraft:crafting_shapeless` types, replacing the `output` object with a `book` string set to the ID of the book.

For an example, see [the recipes for test books](https://github.com/VazkiiMods/Patchouli/tree/63983afeec89bb254c3b9c506ffbd4bd17808d3e/src/main/resources/data/patchouli/recipes).

## Projected Changes in 1.18

We plan to make the following changes in 1.18:

### Remove non-resource pack books
Resource pack-based books are the future of Patchouli, and the old hardcoded classpath
loading system will be removed.

Support for the `.minecraft/patchouli_books` folder will also be removed, as shipping a
book with a modpack will become the same as shipping a resource pack.

### Revamp entire book authoring syntax
JSON is not a suitable markup language. We will be exploring alternative markup languages
such as Apache Freemarker for book authoring.  This is not guaranteed to make it to 1.18,
it's a long term refactor we want to do. It may not even be in Patchouli, but another
successor mod.
 
