# Defining Multiblocks

Patchouli comes with a system that allows you to define multiblocks and then visualize
them in the world. These multiblocks can be either defined in code by modders (using the
API), or defined in JSON inline with the entry data.

This page is written like a tutorial to get you to understand how multiblock objects work,
as writing the object structure formally wouldn't properly convey how to construct it.

The page focuses on creating multiblocks using JSON, but the fundamentals also apply to
doing so using code. Making multiblocks with code allows you to create state matchers
using arbitrary predicates and using the multiblock for server-side validation and
placement (if you make Patchouli a required dependency). You can look into the [Patchouli
API](https://github.com/Vazkii/Patchouli/tree/master/src/main/java/vazkii/patchouli/api)
for how to do this, or see an example of it being done
[here](https://github.com/Vazkii/Patchouli/blob/master/Common/src/main/java/vazkii/patchouli/common/multiblock/MultiblockRegistry.java).

## Multiblock Format

A Multiblock is composed of a 2 dimensional String Array (the pattern), and a mapping of characters present in the pattern to state matchers.

As a demonstration of how to create a multiblock, we'll be using the following... thing as a demonstration:

![](https://i.imgur.com/M6Fw6nP.png)

A multiblock can be defined using a JSON object. Let's begin with an empty one and add in as we go.

```json
"multiblock": {

}
```

### The Mapping

We'll begin by discussing which types of blocks we have here. Looking at the pic we have:

* Gold Blocks
* Red Terracotta
* Magenta Wool (which for this demo we want to allow it to be any color)
* Note Block (tuned to note 4)
* Air
* A few corners we can't see, but let's say they're allowed to be any block

Once we know what types of blocks this structure uses, we assign a letter to each:

* **G**: Gold Blocks
* **B**: Note Block (note 4)
* **R**: Red Terracotta
* **W**: Any Wool

Patchouli already provides built in characters for Air and (Any Block), which are
respectively a space, and an underscore, so we don't have to account for those.

Now, we need to convert these to data the game can understand. Patchouli uses the same
vanilla logic to parse blockstate predicate as, for example, the
`/execute if block ~ ~ ~ <PREDICATE>`
command. This means you can use block ID's, tags, as well as specify
blockstate properties you want to constraint. Therefore, we have:

* **G**: `minecraft:gold_block`
* **B**: `minecraft:note_block[note=4]`
* **R**: `minecraft:red_terracotta`
* **W**: `#minecraft:wool`

Looks good. Let's put these mappings into our json using a "mapping" block:
```json
"multiblock": {
    "mapping": {
         "G": "minecraft:gold_block",
         "B": "minecraft:note_block[note=4]",
         "R": "minecraft:red_terracotta",
         "W": "#minecraft:wool"
     }
}
```

### The Pattern
Now that we told the multiblock what blocks to use, we need to tell it the shape. To do
this, we go from the top to bottom, layer by layer, and translate each layer to a string
array.

Terse explanation of the format: the pattern attribute is an array of array of strings. It
is indexed in the following order: y (top to bottom), x (west to east), then z (north to
south).

Full explanation:
Let's start with the first layer with the plus made of terracotta. It would look something like this:
```json
[
" GRG ",
"GGRGG",
"RRRRR",
"GGRGG",
" GRG "
]
```

Recall that G is gold, R is red terracotta, and empty spaces are air. We can compress this
a little so it doesn't look as bulky now:

```json
[ " GRG ", "GGRGG", "RRRRR", "GGRGG", " GRG " ]
```

It doesn't look as readable now that we aren't projecting it as a bird's eye view, but
don't worry, it'll look good in a second. With that said, we put it into the "pattern"
array, and follow through, adding in all the following layers:

```json
"pattern": [
    [ " GRG ", "GGRGG", "RRRRR", "GGRGG", " GRG " ],
    [
        "GG GG",
        "G   G",
        "     ",
        "G   G",
        "GG GG"
    ],
    [
        "G   G",
        "     ",
        "     ",
        "     ",
        "G   G"
    ], 
    [
        "G   G",
        "     ",
        "     ",
        "     ",
        "G   G"
    ], 
    [
         "_WWW_",
         "WWWWW",
         "WWWWW",
         "WWWWW",
         "_WWW_"
    ]
]
```

...and compressing them:

```json
"pattern": [
    [ " GRG ", "GGRGG", "RRRRR", "GGRGG", " GRG " ], 
    [ "GG GG", "G   G", "     ", "G   G", "GG GG" ],
    [ "G   G", "     ", "     ", "     ", "G   G" ], 
    [ "G   G", "     ", "     ", "     ", "G   G" ], 
    [ "_WWW_", "WWWWW", "WWWWW", "WWWWW", "_WWW_" ]
]
```

Look at that! We now have a side view of the structure. Just one last thing left to do. We need to tell the game where the center of the multiblock is. It might be intuitive that it's in the geometrical center, but in some cases you may have multiblocks that extend outwards, and the center doesn't exactly fall there. 

We specify the center by replacing one of the characters in the pattern with a zero. By default the zero is mapped to Air, but if you need to map it to something else, you can always just put a "0" in the mappings with whatever you want it to be.

The "zero" of the multiblock is placed at the player's world cursor. On less fancy words, if you were to place a block, where it would end up is where the zero ends up. 

Knowing that, the ideal right place for the zero of this structure would be in the geometrical center, but one block down so that it's right above the W:
```json
"pattern": [
    [ " GRG ", "GGRGG", "RRRRR", "GGRGG", " GRG " ], 
    [ "GG GG", "G   G", "     ", "G   G", "GG GG" ],
    [ "G   G", "     ", "     ", "     ", "G   G" ], 
    [ "G   G", "     ", "  0  ", "     ", "G   G" ], 
    [ "_WWW_", "WWWWW", "WWWWW", "WWWWW", "_WWW_" ]
]
```

### Extra Notes on the Pattern
Observe carefully that the pattern directions conform to programming conventions, not to the cardinal directions.
For example, to lay out stairs with their `facing` property set to the appropriate side of the multiblock, you would have to do this:
```
"pattern": [
  [
    " W ",
    "N S",
    " E "
  ]
],
"mapping": {
  "W": "minecraft:oak_stairs[facing=west]",
  "N": "minecraft:oak_stairs[facing=north]",
  "S": "minecraft:oak_stairs[facing=south]",
  "E": "minecraft:oak_stairs[facing=east]",
}
```
This is due to the fact that the inner array is indexed by x (later entries being further east), then the strings are indexed by z (later characters being further south).

### Some Extra Things

There's a few more values a multiblock can have:

* **symmetrical** (boolean)

Defaults to false. Set this to true if the multiblock is symmetrical around the vertical axis of its center (if rotating it around in 90º increments doesn't change its look in the world). It's not obligatory, but if you do it, Patchouli won't check all rotations so it's better for performance.

* **offset** (int Array, 3 values)

An int array of 3 values ([X, Y, Z]) to offset the multiblock relative to its center.

For our case, we'll be setting symmetrical to true, and not specifying offset, as we already put the 0 in the right place.

### Adding Everything Together

Let's add our pattern, mapping, and other values together, and we get:

```json
"multiblock": {
    "pattern": [
        [ " GRG ", "GGRGG", "RRRRR", "GGRGG", " GRG " ], 
        [ "GG GG", "G   G", "     ", "G   G", "GG GG" ],
        [ "G   G", "     ", "     ", "     ", "G   G" ], 
        [ "G   G", "     ", "  0  ", "     ", "G   G" ], 
        [ "_WWW_", "WWWWW", "WWWWW", "WWWWW", "_WWW_" ]
    ],
    "mapping": {
        "G": "minecraft:gold_block",
        "W": "minecraft:wool",
        "R": "minecraft:stained_hardened_clay[color=red]"
    },
    "symmetrical": true
}
```

Which, if we transplant onto a "multiblock" page (see [Default Page Types](/docs/patchouli-basics/page-types#multiblock-pages)), and click Visualize, will show up just fine ingame!

![](https://i.imgur.com/1lfoaA1.png)
