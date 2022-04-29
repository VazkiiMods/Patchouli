# Default Page Types

This page specifies the various page types provided by default in Patchouli. If you need
other page types for whatever case, please see [Using
Templates](/docs/patchouli-basics/templates) for information on creating your own.

These pages should be used in Entries, in the "pages" array, via specifying which type you
want by using "type" on each object. You can read more in [Entry JSON
Format](/docs/reference/entry-json).

The following attributes are common to every page type:

* **type** (String, _mandatory_)

What type this page is. This isn't used by the page itself, but rather by the loader to
determine what page should be loaded. For example, if you want a text page, you set this
to `patchouli:text`. This should be fully-qualified in 1.17 and later and of the form
`domain:name`. For the built-in page types defined here, the domain is `patchouli`. In
1.16 or earlier version, you should leave out everything up to and including the colon, so
it would be `text`.

* **advancement** (String)

A resource location to point at, to make a page appear when that advancement is
completed. See [Locking Content with
Advancements](/docs/patchouli-basics/advancement-locking) for more info on locking
content. Excluding this attribute or leaving it empty will leave the page unlocked from
the start. Providing a nonexistent advancement will permanently lock this entry unless the
advancement at the resource location starts existing.

* **flag** (String)

A config flag expression that determines whether this page should exist or not. See [Using
Config Flags](/docs/patchouli-basics/config-gating) for more info on config flags.

* **anchor** (String)

An anchor can be used elsewhere to refer to this specific page in an internal link. See
[Text Formatting 101](/docs/patchouli-basics/text-formatting) for more details about
internal links.

## Example Usage

Here's an example of using a text page:

```json
{
    "type": "patchouli:text",
    "text": "This is an example"
}
```  

## Text Pages
![](https://i.imgur.com/cYGwnxb.png)

Page type: **"patchouli:text"**

**Text pages should always be the first page in any entry**. If a text page is the first
page in an entry, it'll display the header you see in the left page. For all other pages,
it'll display as you can see in the right one.

**Attributes**:
* **text** (String, _mandatory_)

The text to display on this page. This text can be
[formatted](/docs/patchouli-basics/text-formatting).

* **title** (String)

An optional title to display at the top of the page. If you set this, the rest of the text
will be shifted down a bit. You can't use "title" in the first page of an entry.

## Image Pages
![](https://i.imgur.com/NnWb10b.png)

Page type: **"patchouli:image"**

**Attibutes**:
* **images** (String Array, _mandatory_)

An array with images to display. Images should be in resource location format. For
example, the value `botania:textures/gui/entries/banners.png` will point to
`/assets/botania/textures/gui/entries/banners.png` in the resource pack. For modpack
creators, this means that any images you want to use must be loaded with an external
resource pack (or a mod such as Open Loader). For best results, make
your image file 256 by 256, but only place content in the upper left 200 by 200 area. This
area is then rendered at a 0.5x scale compared to the rest of the book in pixel size.

If there's more than one image in this array, arrow buttons are shown like in the picture,
allowing the viewer to switch between images.

* **title** (String)

The title of the page, shown above the image.

* **border** (boolean)

Defaults to false. Set to true if you want the image to be bordered, like in the
picture. It's suggested that border is set to true for images that use the entire canvas,
whereas images that don't touch the corners shouldn't have it.

* **text** (String)

The text to display on this page, under the image. This text can be
[formatted](/docs/patchouli-basics/text-formatting).

## Crafting Recipe Pages

![](https://i.imgur.com/ySSi5zL.png)

Page type: **"patchouli:crafting"**

**Attibutes**:
* **recipe** (String, _mandatory_)

The ID of the first recipe you want to show.

* **recipe2** (String)

The ID of the second recipe you want to show. Displaying two recipes is optional.

* **title** (String)

The title of the page, to be displayed above both recipes. This is optional, but if you
include it, only this title will be displayed, rather than the names of both recipe output
items.

* **text** (String)

The text to display on this page, under the recipes. This text can be
[formatted](/docs/patchouli-basics/text-formatting).

Note: the text will not display if there are two recipes with two different outputs, and
"title" is not set. This is the case of the image displayed, in which both recipes have
the output names displayed, and there's no space for text.

## Smelting Recipe Pages

![](https://i.imgur.com/noBfGCc.png)

Page type: **"patchouli:smelting"**

**Attibutes**:
* **recipe** (String, _mandatory_)

The first recipe you want to show. Given that furnace recipes are only named properly as
of 1.13, instead of using a recipe ID, we use an [ItemStack
String](/docs/patchouli-advanced/itemstack-format) that corresponds to the item being
smelted, so if you wanted to show the recipe for smelting sand to glass, you'd use
"minecraft:sand" here.

* **recipe2** (String)

The second recipe you want to show. See `recipe` above for format. Displaying two recipes
is optional.

* **title** (String)

The title of the page, to be displayed above both recipes. This is optional, but if you
include it, only this title will be displayed, rather than the names of both recipe output
items.

* **text** (String)

The text to display on this page, under the recipes. This text can be
[formatted](/docs/patchouli-basics/text-formatting).

## Multiblock Pages

![](https://i.imgur.com/uTt7Zsc.png)

Page type: **"patchouli:multiblock"**

* **name** (String, _mandatory_)

The name of the multiblock you're displaying. Shows as a header above the multiblock
display.

* **multiblock_id** (String)

For modders only. The ID of the multiblock you want to display. See [this
page](/docs/patchouli-basics/multiblocks) for how to create and register Multiblocks in
code.

_Note: Either this or "multiblock" need to be set for this page type to work._

* **multiblock** (Object)

The multiblock object to display. See [Using
Multiblocks](/docs/patchouli-basics/multiblocks) for how to create this object.

_Note: Either this or "multiblock_id" need to be set for this page type to work._

* **enable_visualize** (boolean)

Defaults to true. Set this to false to disable the "Visualize" button.

* **text** (String)

The text to display on this page, under the multiblock. This text can be
[formatted](/docs/patchouli-basics/text-formatting).

## Entity Pages

![](https://i.imgur.com/suEQTN4.png)

Page type: **patchouli:entity**

**Attibutes**:
* **entity** (String, _mandatory_)

The ID of the entity you want to display. To display a chicken you'd use
`minecraft:chicken`. You can also add NBT data to the entity, in the same way you would in
an [ItemStack String](/docs/patchouli-advanced/itemstack-format).

* **scale** (float)

The scale to display the entity at. Defaults to 1.0. Values lower than 1.0 will have the
entity be smaller than usual, while higher than 1.0 will have it be larger. Negative
values will flip it upside down.

* **offset** (float)

An amount to offset the entity display. Some mod entities have weird renders and won't fit
in the box properly, you can change this to move them up and down.

* **rotate** (boolean)

Defaults to true. Set this to false to make the entity not rotate.

* **default_rotation** (float)

The rotation at which this entity should be rendered. This value is only used if "rotate"
is false. The default is -45.

* **name** (String)

The name to display on top of the frame. If this is empty or not defined, it'll grab the
name of the entity and use that instead.

* **text** (String)

The text to display on this page, under the entity. This text can be
[formatted](/docs/patchouli-basics/text-formatting).

## Spotlight Pages

![](https://i.imgur.com/W7ezngF.png)

Page type: **"patchouli:spotlight"**

**Attibutes**:
* **item** (String, _mandatory_)

An [ItemStack String](/docs/patchouli-advanced/itemstack-format) representing the item to
be spotlighted.

* **title** (String)

A custom title to show instead on top of the item. If this is empty or not defined, it'll
use the item's name instead.

* **link_recipe** (boolean)

Defaults to false. Set this to true to mark this spotlight page as the "recipe page" for
the item being spotlighted. If you do so, when looking at pages that display the item, you
can shift-click the item to be taken to this page. Highly recommended if the spotlight
page has instructions on how to create an item by non-conventional means.

* **text** (String)

The text to display on this page, under the item. This text can be
[formatted](/docs/patchouli-basics/text-formatting).

## Link Pages

![](https://i.imgur.com/AQST9Jf.png)

Page type: **patchouli:link**

**Note:** Link pages are just specialized Text pages, which means they can work just like
text pages, with the additional link button on the bottom. They also have all the
attributes text pages do, and you can use them as the first page in your entry.

**Attributes**:
* **url** (String, _mandatory_)

The URL to open when clicking the button. In theory everything is supported, but please
stick to HTTP/HTTPS addresses.

* **link_text** (String, _mandatory_)

The text to display on the link button.

## Relations Pages

![](https://i.imgur.com/rBaxf4d.png)

Page type: **patchouli:relations**

**Attributes**:
* **entries** (String Array)

An array of the entries that should be linked in this page. These are the IDs of the
entries you want to link to in the same way you'd link an entry to a category's ID.

* **title** (String)

The title of this page, to display above the links. If this is missing or empty, it'll
show "Related Chapters" instead.

* **text** (String)

The text to display on this page, under the links. This text can be
[formatted](/docs/patchouli-basics/text-formatting).

## Quest Pages

![](https://i.imgur.com/LRpRQdn.png)

Page type: **patchouli:quest**

**Notes:**

* Quest pages will make the entry they're in show up with a checkmark once the quest is
  completed.
* Entries that show up with a checkmark will show up at the end of the list to be out of
  the way.
* So it knows to mark it properly, **do not have multiple quest pages in a single entry**.
* If you use a quest page, it's recommended you also use
  "[turnin](/docs/reference/entry-json)" in the entry.

**Attributes**:

* **trigger** (String)

The advancement that should be completed to clear this quest. You may leave this empty
should you want the quest to be completed manually. The image shows a quest with "trigger"
set on the left and one with it unset on the right.

* **title** (String)

The title of this page, to display above the links. If this is missing or empty, it'll
show "Objective" instead.

* **text** (String)

The text to display on this page, under the links. This text can be
[formatted](/docs/patchouli-basics/text-formatting).

## Empty Pages

![](https://i.imgur.com/9gv6Dje.png)

Page type: **patchouli:empty**

**Attributes**:
* **draw_filler** (boolean)

Defaults to true. Set to false to draw a completely empty page, without the page
filler... for whatever reason.
