# Book JSON Format

This page details every key you can have in a book.json file.

## **name** (String, _mandatory_)

The name of the book that will be displayed in the book item and the GUI. For modders,
this can be a localization key.

## **landing_text** (String, _mandatory_)

The text that will be displayed in the landing page of your book. This text can be
[formatted](/docs/patchouli-basics/text-formatting). For modders, this can be a
localization key.

## **use_resource_pack** (Boolean)
If true, book contents (categories, entries, and templates) are loaded via the resource
system, and thus must live in a resource pack or mod resources under
`/assets/book_namespace/patchouli_books/book_name`. See [1.17 upgrade
guide](/docs/upgrading/upgrade-guide-117), and [1.20 upgrade
guide](/docs/upgrading/upgrade-guide-120) for more information.

Starting in 1.20, must be set to true for books declared outside of
`.minecraft/patchouli_books`.

## **book_texture** (String)

The texture for the background of the book GUI. You can use any resource location for
this, but it is *highly recommended* you use one of the built in ones so that if new
elements get added, you have them right away. The ones that Patchouli comes with are the
following:

1. `patchouli:textures/gui/book_blue.png`
2. `patchouli:textures/gui/book_brown.png` (default value)
3. `patchouli:textures/gui/book_cyan.png`
4. `patchouli:textures/gui/book_gray.png`
5. `patchouli:textures/gui/book_green.png`
6. `patchouli:textures/gui/book_purple.png`
7. `patchouli:textures/gui/book_red.png`

In the advent you want to use a custom one, you can copy any of these and make any
modifications you need.

## **filler_texture** (String)

The texture for the page filler (the cube thing that shows up on entries with an odd
number of pages). Define if you want something else than the cube to fill your empty
pages.

## **crafting_texture** (String)

The texture for the crafting entry elements. Define if you want custom backdrops for
these. Not really worth defining in most cases but if you want to be cool and stylish, you
can.

## **model** (String)

The model for the book's item, in the same format as they appear in resource packs. That
is, `foo:bar` means `/assets/foo/models/item/bar.json`.

Patchouli provides a few you can use:

* `patchouli:book_blue`
* `patchouli:book_brown` (default value)
* `patchouli:book_cyan`
* `patchouli:book_gray`
* `patchouli:book_green`
* `patchouli:book_purple`
* `patchouli:book_red`

FOR MODDERS: Do NOT use any of the above models. In the sake of books being
distinguishable, having multiple mods using the same base textures is a bad idea, make
your own! They're provided for modpack makers. Patchouli automatically takes care of
registering any models you pass in to this, so you don't have to mess with any code for
them yourself.

An [item predicate](https://minecraft.wiki/w/Tutorials/Models#Item_predicates)
called `patchouli:completion` is provided for book models, whose value is equal to the
fraction of entries unlocked in the book, which allows book models to change their display
as they're more completed.

## **text_color** (String)

The color of regular text, in hex ("RRGGBB", # not necessary). Defaults to "000000".

## **header_color** (String)

The color of header text, in hex ("RRGGBB", # not necessary). Defaults to "333333".

## **nameplate_color** (String)

The color of the book nameplate in the landing page, in hex ("RRGGBB", # not
necessary). Defaults to "FFDD00".

## **link_color** (String)

The color of link text, in hex ("RRGGBB", # not necessary). Defaults to "0000EE".

## **link_hover_color** (String)

The color of hovered link text, in hex ("RRGGBB", # not necessary). Defaults to "8800EE".

## **progress_bar_color** (String)

The color of advancement progress bar, in hex ("RRGGBB", # not necessary). Defaults to
"FFFF55".

## **progress_bar_background** (String)

The color of advancement progress bar's background, in hex ("RRGGBB", # not
necessary). Defaults to "DDDDDD".

## **open_sound** (String)

The sound effect played when opening this book. This is a resource location pointing to
the sound (which, for modders, needs to be properly registered).

## **flip_sound** (String)

The sound effect played when flipping through pages in this book. This is a resource
location pointing to the sound (which, for modders, needs to be properly registered).

## **index_icon** (String)

The icon to display for the Book Index. This can either be an [ItemStack
String](/docs/patchouli-advanced/itemstack-format), if you want an item to be the icon, or
a resource location pointing to a square texture. If you want to use a resource location,
make sure to end it with .png. This is optional, and if you don't include it, it'll
default to the book's icon (which is the recommended value).

## **pamphlet** (String)

Available in Patchouli 1.18.2-68 or above.

Defaults to false. If true, marks this book as a [pamphlet](../patchouli-basics/pamphlets.md).

## **show_progress** (boolean)

Defaults to true. Set to false to disable the advancement progress bar, even if
advancements are enabled.

## **version** (String)

The "edition" of the book. This defaults to "0", and setting this to any other numerical
value (let's call it X) will display "X Edition" in the book's tooltip and landing page
(e.g. X being 3 would display "3rd Edition"). Setting this to "0" or not modifying it will
instead display whatever you set the "subtitle" key as. If the value is non-numerical,
it'll display "Writer's Edition".

For modders. This is a good place you can expand with gradle. You can use something like
${book_version} here and set it your build script. As non-numerical values are accepted,
this won't cause any issues.

## **subtitle** (String)

A subtitle for your book, which will display in the tooltip and below the book name in the
landing page if "version" is set to "0" or not set.

## **creative_tab** (String)

The creative tab to display your book in. Defaults to null (no tab).

### 1.20
Here are the vanilla tabs:

* `building_blocks`
* `colored_blocks`
* `natural_blocks`
* `functional_blocks`
* `redstone_blocks`
* `tools_and_utilities`
* `combat`
* `food_and_drinks`
* `ingredients`
* `spawn_eggs`

For modders, put the full namespaced ID of the tab that you used when registering it.

### 1.19.3-1.19.4
The names of the vanilla tabs for 1.19.3 and 1.19.4 depend on your target platform, see
[this](https://github.com/MinecraftForge/MinecraftForge/blob/1.19.x/src/main/java/net/minecraftforge/common/CreativeModeTabRegistry.java#L124)
for Forge and
[this](https://github.com/FabricMC/fabric/blob/1.19.4/fabric-item-group-api-v1/src/main/java/net/fabricmc/fabric/impl/itemgroup/MinecraftItemGroups.java#L28)
for Fabric.

### 1.19.2 and Below
On versions 1.19.2 and below, this defaults to Miscellaneous, but you can move it to any
tab you wish. Here are the names for the vanilla tabs on 1.19.2 and below:

1. buildingBlocks
2. decorations
3. redstone
4. transportation
5. misc (default value)
6. food
7. tools
8. combat
9. brewing

For modders, simply put in the same string you use when constructing your creative tab here, and the book will show up there.
On Fabric, use the name of the `Identifier` you registered your tab with, but with the `:`
replaced with a `.`.

## **advancements_tab** (String)

The ID of the advancements tab you want this book to be associated to. If defined, an
Advancements button will show up in the landing page that will open that tab.

## **dont_generate_book** (boolean)

Defaults to false. Set this to true if you don't want Patchouli to make a book item for
your book. Use only if you're a modder and you really need a custom Item class for
whatever reason.

## **custom_book_item** (String)

Following from the previous key, if you do have a custom book, set it here. This is an
[ItemStack String](/docs/patchouli-advanced/itemstack-format).

## **show_toasts** (boolean)

Defaults to true. Set it to false if you don't want your book to show toast notifications
when new entries are available.

## **use_blocky_font** (boolean)

Defaults to false. Set it to true to use the vanilla blocky font rather than the slim
font. If you have a font mod, it'll use whichever font that mod is providing instead.

## **i18n** (boolean)

Default false. If set to true, attempts to look up category, entry, and page titles as
well as any page text in the lang files before rendering.

## **macros** (Object)

Formatting macros this book should use. See [Text Formatting
101](/docs/patchouli-basics/text-formatting) for more info on how you can define these and
what they do.

## **pause_game** (boolean)

Default false. When set to true, opening any GUI from this book will pause the game in
singleplayer.

## **text_overflow_mode** (`overflow`, `resize`, `truncate`)

Added in Patchouli 1.18.2-71

Allows the `textOverflow` config option to be customized per-book, controlling how text
pages that have overflowing contents are handled:

`overflow` lets the text run off the page, `truncate` discards all overflowed text, and `resize`
attempts to shrink the text to fit in the page.


## Extension Keys
This section only applies to versions before 1.20. Extension books have been replaced with
resource-pack-based overrides in 1.20 and above.

### **extend** (String)

Marks this book as an extension to the specified target book. Extension books do not create a
book item and don't really "exist". All they serve to do is to add more content to another
book that already exists. This is mainly here for addon mods that want to add stuff to the
book they're extending.

The value you put here is simply the ID of the book you want to extend. In the form of
`modid:path`, where modid is the ID of the mod that owns the book, and path the folder
where it is. For example, should you want to extend the book owned by "patchouli" that's
in `/data/patchouli/patchouli_books/coolbook/book.json`, the value you'd put here would be
`patchouli:coolbook`.

If this value is set, every other value in the file is ignored. This book will
inherit any entries, categories, templates, and macros from the original one, so feel free
to use them at will.

### **allow_extensions** (boolean)

Defaults to true. Set it to false if you want to not play nice and lock your book from
being extended by other books.
