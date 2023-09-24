# Giving Books to New Players

Patchouli does not have native functionality to give books to players as they
join. However, this functionality is easily replicated using vanilla advancements.

What we will do is to provide a hidden advancement that triggers for a player immediately
whose reward is the book. Advancement rewards are one-time-only, so unless the advancement
is revoked from the player, this should happen once, immediately when the player joins the
world for the first time.

Please check the [vanilla wiki](https://minecraft.wiki/w/Advancement/JSON_format)
for details.

Below is a brief example:

Replace `yourbooknamespace` with the namespace part of your book ID (see [Getting
Started](/docs/patchouli-basics/getting-started)) for information about your book ID.

```json title="/data/yourbooknamespace/advancements/grant_book_on_first_join.json"
{
  "criteria": {
    "tick": {
      "trigger": "minecraft:tick"
    }
  },
  "rewards": {
    "loot": [
      "yourbooknamespace:grant_book_on_first_join"
    ]
  }
}
```

```json title="/data/yourbooknamespace/loot_tables/grant_book_on_first_join.json"
{
  "type": "advancement_reward",
  "pools": [
    {
      "rolls": 1,
      "entries": [
        {
          "type": "item",
          "name": "patchouli:guide_book",
          "functions": [
            {
              "function": "set_nbt",
              "tag": "{\"patchouli:book\": \"YOURBOOKIDHERE\"}"
            }
          ]
        }
      ]
    }
  ]
}
```

Fill in your book ID where it says to. Modders: if you have a custom book item, obviously
replace the item id and delete the function.
