---
sidebar_position: 1
---

# Getting Started

This entry serves as a quick guide of what to do to get started making your own Patchouli
books, read on and follow carefully!

### 0. Install Patchouli
Pretty obvious. For modders, you can load it as a lib to your mod project or just drop it
in your workspace's mods folder. The preferred way is to use maven, which you find the
address for in the repo's readme. For non-modders, obtain the mod through the usual
distribution channels.

### 1. Locate your patchouli_books directory
Your books and their contents will go in your `patchouli_books` directory, so you need to
find it.
* For modpack makers, it'll be in your instance folder (next to mods, config, etc). Note
  that you need to run the game once with Patchouli installed for it to show up
  (or just make it yourself).
* For modders, it'll be `/data/_YOURMODID_/patchouli_books`, you'll have to make it
  yourself.

### 2. Create your folder structure
Once you find `patchouli_books`, decide on a name for your book. Only lower case characters
and underscores are allowed. This is the internal name for your book, and we encourage you
to make it distinctive. For mods, you should name it after what the book is for
(e.g. `lexicon` for Botania). For modpacks, name it something with your modpack's name,
for example `crucial_2_guide_book`. After you have a name, create folders and files inside
patchouli_books so that it looks like this:

* `patchouli_books`
    * `<the name you just chose>` (folder)
        * `book.json` (empty file)
        * `en_us` (folder)
            * `entries` (empty folder)
            * `categories` (empty folder)
            * `templates` (empty folder)

You may have as many books as you want, even within the same mod(pack). Each book has a
[Namespaced ID](https://minecraft.fandom.com/wiki/Namespaced_ID). If the book is part of a
mod, the namespace is that mod's mod ID. If the book is loaded from the external
`patchouli_books` folder, the namespace is `patchouli`. The name part of the namespaced ID
is the name you chose above.

Note that creating an `en_us` folder means you're creating the "English" version of your
book. The contents you put in the `en_us` folder are always the "main" ones loaded, so
even if your book isn't meant to be natively in english, you need to put your main stuff
there.

Any translators may create folders with their languages and override any files they
wish. They're automatically loaded if the game language is changed. For translators:
Please don't include in your folder anything you aren't overriding.

### 3. Populate book.json
Open up your book.json using your favorite text editor, and fill it in as follows:

```json
{
	"name": "BOOK NAME",
	"landing_text": "LANDING TEXT",
	"version": 1
}
```

where "BOOK NAME" is the name your book will be displayed as, and "LANDING TEXT" the text
that will show up on the left page of your book ingame. The `version` field specifies which
edition your book is. Whenever you update your book, you should also update the edition
value. For modders, you can use localization keys in both `name` and `landing_text`.

See:

![](https://i.imgur.com/lsdDrrk.png)

For more customization options, please read [Book JSON
Format](/docs/reference/book-json). (highly recommended!)

### 4. Check ingame
Load your game and check if your book is there. Unless you specified otherwise, it should
be in the Miscellaneous creative tab, but you can also search for it.

If you don't see it, check if Patchouli is properly loaded and if there's any errors in
your log.

Everything after this can be hot loaded without closing the game, so feel free to keep it
open as you do further edits.

### 5. Add Stub Content
Time to add some content to your book. Go to your book's folder and then `en_us`, and create
folders and files so that it looks like this:

* en_us
    * entries (folder)
        * test (folder)
            * test_entry.json (empty file)
    * categories (folder)
        * test_category.json (empty file)
    * templates (empty folder)

Open `test_entry.json` and `test_category.json` and fill them in as follows:

```json title="test_entry.json"
{
    "name": "Test Entry",
    "icon": "minecraft:writable_book",
    "category": "yourbooknamespace:test_category",
    "pages": [
        {
            "type": "patchouli:text",
            "text": "This is a test entry, but it should show up!"
        }
    ]
}
```

```json title="test_category.json"
{
	"name": "Test Category",
	"description": "This is a test category for testing!",
	"icon": "minecraft:writable_book"
}
```

You'll need to edit the "category" key in the test entry. If you are a modder, "yourbooknamespace:test_category" should be replaced with the namespace for your mod (with test_category tacked on, of course). If you're just making a modpack, it should be replaced with "patchouli:test_category"

Save your files, then return ingame and open your book. Shift-click the pencil in the
bottom-left corner. When you do so, it will reload the book contents, and you should see
the category and entry you just defined appear.

### 6. Learn More!

You're done getting set up, now it's time to learn more about what you can do with
Patchouli's book system. Check the following pages out:

* [Locking Content with Advancements](/docs/patchouli-basics/advancement-locking)
* [Text Formatting 101](/docs/patchouli-basics/text-formatting)
* [Book JSON Format](/docs/reference/book-json)
* [Category JSON Format](/docs/reference/category-json)
* [Entry JSON Format](/docs/reference/entry-json)
    * [Default Page Types](/docs/patchouli-basics/page-types)
* [Using Templates](/docs/patchouli-basics/templates)

### 7. Use the Book Item

Here's a few examples of how to use your book's item. As a refresher, your book's
*namespace* is your mod id, if you're a modder, and `patchouli`, if you're a pack
maker. Then, if your folder is called "coolbook" then your *book ID* is
`yourbooknamespace:coolbook`.

**CraftTweaker**:  
`<patchouli:guide_book>.withTag({"patchouli:book": "YOURBOOKID"});`  
or just use `/ct hand`

**Vanilla Recipes/Advancements**:
```json
"item": "patchouli:guide_book",
"nbt": {
    "patchouli:book": "YOURBOOKID"
}
```

### 8. Some Pointers

* For modpack makers, if you want to use your own images, textures, sounds, or other
  assets, you'll need a tool to load them in, such as [Resource
  Loader](https://minecraft.curseforge.com/projects/resource-loader).
* To grant your book to new players automatically, see [this
  page](/docs/patchouli-basics/giving-new)
* You need to reload the game to load new book.json files, but not the book contents.
* Book contents are purely client sided, whereas book.json files are loaded by the server
  too.
* You don't have to put your entries in folders corresponding to the categories they
  belong in, but it helps with organization!
* No dependencies are needed if you're a modder, not even any code. Patchouli
  automatically finds your files in your assets if you put them in the right place.
