# Gating Content with Advancements

Entries in your Patchouli books can be locked until certain Advancements are
accomplished. This page will give you a quick primer on how to accomplish this.

* First things first, create your advancement. If you're a modder, make it as you usually
  would ([Minecraft Wiki reference](https://minecraft.gamepedia.com/Advancements)), and
  place it in /data/MODID/advancements. If you're a modpack maker, you'll need to create
  your advancements using the [Triumph](https://minecraft.curseforge.com/projects/triumph)
  mod.

* Once your advancement is done, grab its ID (which would be in the namespace:path
  format). For the sake of demonstration, we'll pretend the ID is "mymod:myadv".

* Open the .json file for the entry you want, and fill in the "advancement" field with the ID: `"advancement": "mymod:myadv"`. 
    * See [Entry JSON Format](/docs/patchouli-basics/entry-json) for more info.

* Open the book.json file for the book that contains the entry you just locked. 
    * If the the "advancement_namespaces" array isn't present yet, create it:
      `"advancement_namespaces": []`
    * Add the namespace to the "advancement_namespaces" array: `"advancement_namespaces":
      [ "mymod" ]`
    * Adding the namespace here tells patchouli that it needs to keep track of
      advancements in that namespace. You only have to do it once per namespace. If you
      have multiple books that use the namespace, only one needs it, but it's good
      practice to have it in all of them.
    * As of Patchouli 1.15.2-1.2-28, this is no longer necessary, and any advancement may
      be used to gate your content.
    * See [Book JSON Format](/docs/patchouli-basics/book-json) for more info.

* You may also lock individual pages behind advancements. This is allowed but not exactly
  encouraged, as it can be confusing to players if you aren't conveying the information
  properly.
    * To lock a page, simply fill the "advancement" field next to the page's "type" field,
      the same way you would for the entry itself.
    * Locked pages do not display any "locked" indicator, they are simply completely
      hidden.
    * See [Default Page Types](/docs/patchouli-basics/page-types) for more info.

* Some final pointers:
    * All advancement locks can be disabled by the player with the config.
    * Entries show up locked unless there's at least one unlocked entry within them.
    * You can make invisible advancements by not including a "display" block in your
      advancement .json, which can be useful if you want to make a lot of small locks but
      don't want a horribly cluttered interface.
