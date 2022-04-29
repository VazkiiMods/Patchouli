# Template Variable Usage

As explained in [Using Templates](/docs/patchouli-basics/templates), variables can be used
to pass data in from the entry files into the template render.

If you're not familiar with templates or using variables therein, please read the Using
Templates page first, as thus page describes more complex use cases. Note that this page
applies only to template design, and not in passing the variables in from the entry json.

## Inline Variables

Variables can be inlined into any strings in the template. You do this by wrapping the
variable name in #, as follows:

```json
{
	"type": "text",
	"text": "String interpolation with #inline_text# in the middle of the string!",
	"x": 20,
	"y": 30
} 
```

Should you then define the variable "inline_text" in your entry json as such:

```json
	"inline_text": "anything you want"
```

The text block you would see displayed ingame would be _"String interpolation with
anything you want in the middle of the string!"_. You can inline as many variables as you
want.

## String Derivation

Variables can have other data derived from them. For example, a variable you'd fill with
an [ItemStack String](/docs/patchouli-advanced/itemstack-format) can be derived to get the
item's name.

Deriving variables can be done by applying a function onto them. To do so, you append
`->func` to the variable, replacing "func" with the function you want to apply. For
example, should "item" be a variable with an ItemStack String, to get its item name, you'd
use `#item->iname`.

**Function List**

* **iname**: Apply to variables that contain ItemStack strings. Derives the item's display
  name.
* **icount**: Apply to variables that contain ItemStack strings. Derives the amount of
  items in the stack.
* **ename**: Apply to variables that contain entity IDs. Derives the entity's display
  name.
* **upper**: Derives the variable's value, but in *UPPERCASE*.
* **lower**: Derives the variable's value, but in *lowercase*.
* **trim**: Derives the variable's value with all leading and trailing whitespace trimmed
  out.
* **capital**: Derives the variable's value, but in *First word only capital*.
* **fcapital**: Derives the variable's value, but in *Fully Capitalized*
* **exists**: Derives "false" if the variable's value is an empty string, "true"
  otherwise.
* **iexists**: Derives "true" if the variable contains an ItemStack string with an
  existing, non-empty stack, "false" otherwise.
* **inv**: Derives "true" if the variable's value is "false" (case insensitive), "false"
  otherwise.

**Other Points**

* You can derive variables that are used inline, so `This recipe produces #item->iname#.`
  is valid.
* You can derive multiple times, so `#item->iname->capital` is valid.
* The functions that derive to "true" and "false" can be used with the "guard" attribute
  in [template components](/docs/patchouli-advanced/default-components).
