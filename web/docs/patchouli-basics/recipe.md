# Specifying Crafting Recipes for Books

All books that have no custom item are actually the same item: `patchouli:book`.

This makes crafting recipes a bit annoying, because you have to specify NBT on the output
value. This is a bit involved to do, especially on Fabric, so Patchouli provides
convenience recipe types to abstract over differences between loaders.

Create a JSON like the following:

```json title="/data/yourbooknamespace/recipes/my_book_recipe_shapeless.json"
{
  "type": "patchouli:shapeless_book_recipe",
  "ingredients": [
    {
      "item": "minecraft:gold_ingot"
    },
    {
      "item": "minecraft:dirt"
    }
  ],
  "book": "yourbookid"
}
```

or for shaped recipes:

```json title="/data/yourbooknamespace/recipes/my_book_recipe_shaped.json"
{
  "type": "patchouli:shaped_book_recipe",
  "pattern": [
    "##"
  ],
  "key": {
    "#": {
      "item": "minecraft:dirt"
    }
  },
  "book": "yourbookid"
}
```

And Patchouli will automatically arrange for the correct book to be produced.
