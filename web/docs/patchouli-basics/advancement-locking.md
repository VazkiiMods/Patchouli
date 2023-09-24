# Gating Content with Advancements

Entries in your Patchouli books can be locked until certain Advancements are
accomplished. This page will give you a quick primer on how to accomplish this.

* First things first, create your advancement. If you're a modder, make it as you usually
  would ([Minecraft Wiki reference](https://minecraft.wiki/w/Advancement/JSON_format)), and
  place it in `/data/MODID/advancements`. If you're a modpack maker, you'll need to load
  your advancements using another mod, such as
  [OpenLoader](https://www.curseforge.com/minecraft/mc-mods/open-loader).

* Once your advancement is done, grab its ID (which would be in the namespace:path
  format). For the sake of demonstration, we'll pretend the ID is `mymod:myadv`.

* Open the JSON file for the entry you want, and fill in the `"advancement"` field with
  the ID: `"advancement": "mymod:myadv"`. 
    * See [Entry JSON Format](/docs/reference/entry-json) for more info.

* You may also lock individual pages behind advancements. This is allowed but not exactly
  encouraged, as it can be confusing to players if you aren't conveying the information
  properly.
    * To lock a page, simply fill the `"advancement"` field next to the page's `"type"`
      field, the same way you would for the entry itself.
    * Locked pages do not display any indicator that they are locked, they are simply
      hidden completely.
    * See [Default Page Types](/docs/patchouli-basics/page-types) for more info.

* Some final pointers:
    * All advancement locks can be disabled by the player in Patchouli's config.
    * Entries show up locked unless there's at least one unlocked entry within them.
    * You can make invisible advancements by not including a `"display"` block in your
      advancement JSON, which can be useful if you want to make a lot of small locks but
      don't want a horribly cluttered interface.
