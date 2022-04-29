# Default Template Components

This page specifies the various template components provided by Patchouli. These
components should be used in Templates, in the "components" array, via specifying which
type you want by using "type" on each object. You can read more in [Using
Templates](/docs/patchouli-basics/templates).

The following attributes are common to every component type:

* **type** (String, _mandatory_)

What type this component is. This isn't used by the component itself, but rather by the
loader to determine what component should be loaded. For example, if you want a text
component, you set this to "patchouli:text". This should be fully-qualified and of the
form `domain:name` where `domain` is the same as the domain of your Book ID.

* **x** (integer)

Defaults to 0. The horizontal position of this component in the page.

* **y** (integer)

Defaults to 0. The vertical position of this component in the page.

* **advancement** (String)

A resource location to point at, to make a component appear when that advancement is
completed. See [Locking Content with
Advancements](/docs/patchouli-basics/advancement-locking) for more info on locking
content. Excluding this attribute or leaving it empty will make the component always
display.

* **negate_advancement** (boolean)

Defaults to false. If set to true, the *advancement* field will be negated, making the
component only show if the advancement hasn't been gotten yet.

* **guard** (String)

A [Variable Function](/docs/patchouli-advanced/template-variable-usage#string-derivation)
that determines whether the component should show. If this function resolves to "false" or
an empty string, the component will not display.

* **flag** (String)

A config flag expression that determines whether this component should show or not. See
[Using Config Flags](/docs/patchouli-basics/config-gating) for more info on config flags.

* **group** (String)

Modders only: The group this element belongs to. Groups are only used to allow [Component
Processors](/docs/patchouli-advanced/component-processors) to hide individual components
based on code-level conditions.

## Example Usage

Here's an example of using a text component:

```json
{
    "type": "patchouli:text",
    "text": "This is an example",
    "x": 20,
    "y": 30
}
```

## Text Components
Component type: **"patchouli:text"**

Draws a text block, which supports [formatting](/docs/patchouli-basics/text-formatting).

**Attributes:**
* **text** (String, _mandatory_)

The text to display: Can be a variable.

* **color** (String)

The color of the text, in hex (e.g. "FF0000" would be pure red). If not set it defaults to
the book's text color. Can be a variable.

* **max_width** (integer)

The maximum width allowed for a line in this block. If not set, it defaults to the full
width of a page.

* **line_height** (integer)

The height of each line. The default is 9, which is what patchouli uses everywhere
else. If you set this to more than that, there will be space between the text lines. If
you set it to less, the lines will overlap and look bad.

## Item Components
Component type: **"patchouli:item"**

Draws an item, which you can hover over to see its tooltip, or click to open the page that
shows how to craft it, if there's one.

**Attributes:**

* **item** (String, _mandatory_)

An [ItemStack String](/docs/patchouli-advanced/itemstack-format) representing the item you
want to show. Can be a variable.

Advanced usage of this value is possible. You may use `ore:ORENAME`, to display all items
matching the ore dictionary key ORENAME, or you may display multiple stacks at once by
separating them with commas (e.g. `minecraft:diamond,minecraft:emerald`). In both cases,
they alternate with time.
 
* **framed** (boolean)

Defaults to false. If true, it'll draw a small frame around the item in the same way that
the recipe pages does for the output.

* **link_recipe** (boolean)

Defaults to false. Set this to true to mark any page this component is used in as the
"recipe page" for the item being shown. If you do so, when looking at pages that display
the item, you can shift-click the item to be taken to the page that uses this component.

## Image Components
Component type: **"patchouli:image"**

Draws an image, or part of it.

**Attributes:**

* **image** (String, _mandatory)

A resource location pointing to the image you want to draw. Can be a
variable. **Warning**: Using images whose width and height aren't powers of two (16, 32,
64, 128, 256, 1024...) will cause things to break. Do not do it.

* **width** (integer, _mandatory_)

The width of the area that you want to draw from your image. This starts counting from the
left.

* **height** (integer, _mandatory_)

The width of the area that you want to draw from your image. This starts counting from the
top.

* **u** (integer)

How many pixels to shift rightwards before reading the image's pixel data. Default is 0,
meaning to start at the very left of the image.

* **v** (integer)


How many pixels to shift downwards before reading the image's pixel data. Default is 0,
meaning to start at the very top of the image.

* **texture_width** (integer)

The width of the image you want to draw. If you don't set this it'll default to 256. If
your image's width is different from 256, you need to set the value, or it'll look weird.

* **texture_height** (integer)

The height of the image you want to draw. If you don't set this it'll default to 256. If
your image's height is different from 256, you need to set the value, or it'll look weird.

* **scale** (float)

The scale at which you want to display this image. Defaults to 1.

## Header Components
Component type: **"patchouli:header"**

Draws a text header, much like you'd see in the titles of categories and entries. Does not
draw the separator line that appears below those.

**Attributes:**
* **text** (String, _mandatory_)

The text to show here. This *can't* be formatted. Can be a variable.

* **color** (String)

The color of the text, in hex (e.g. "FF0000" would be pure red). If not set it defaults to
the book's header color. Can be a variable.

* **centered** (boolean)

Defaults to true. Set to false to align the text to the left rather than center.

* **scale** (float)

The scale at which you want to display this headear. Defaults to 1.

* The "x" value of this component can be set to -1, and it'll default to the horizontal
  center of the page.
* The "y" value can also be set to -1, and if you do, it'll default to the same position
  where headers are for the default pages.

## Entity Components
Component type: **"patchouli:entity"**

Renders an entity which rotates around.

**Attributes:**

* **entity** (String, _mandatory_)

The ID of the entity you want to display. To display a chicken you'd use
"minecraft:chicken". You can also add NBT data to the entity, in the same way you would in
an [ItemStack String](/docs/patchouli-advanced/itemstack-format). Can be a variable.

* **render_size** (integer)

The size of the canvas to render this entity on. Defaults to 100, which means the entity
will be rendered in a 100x100 box.

* **rotate** (boolean)

Defaults to true. Set this to false to make the entity not rotate.

* **default_rotation** (float)

The rotation at which this entity should be rendered. This value is only used if "rotate"
is false. The default is -45.

## Separator Components
Component type: **"patchouli:separator"**

Draws a separator line using your book's texture.

**Does not have any additional attributes.**
* The "x" value of this component can be set to -1, and it'll default to the horizontal
  center of the page.
* The "y" value can also be set to -1, and if you do, it'll default to the same position
  where separators are for the default pages.

## Frame Components
Component type: **"patchouli:frame"**

Draws a frame for a 200x200 image using your book's texture. This looks just like the
frame on framed images in image pages.

**Does not have any additional attributes.**

* The "x" value of this component can be set to -1, and it'll default to the horizontal
  center of the page.
* The "y" value can also be set to -1, and if you do, it'll default to the same position
  where frames are for the default pages.

## Tooltip Components
Component type: **"patchouli:tooltip"**

Makes the GUI render a tooltip when the cursor is over the section specified here.


**Attributes:**

* **tooltip** (String Array, _mandatory_)

An array of lines for the tooltip to display. All strings inside the array can be
variables individually. Empty strings or missing variables are ignored. You can use
control/color codes by using & instead of §.

* **width** (integer, _mandatory_)

The width of this tooltip area.

* **height** (integer, _mandatory_)

The height of this tooltip area.

## Custom Components
Component type: **"patchouli:custom"**

Modders only: Does whatever you'd like! You can pass in an instance of an interface in the
patchouli API for it to instantiate and pass stuff to.

Here's a [test
example](https://github.com/Vazkii/Patchouli/blob/master/Common/src/main/java/vazkii/patchouli/client/book/template/test/ComponentCustomTest.java)
you can refer to.

**Attributes:**

* **class** (String, _mandatory_)

A full class name (package.name.ClassName) pointing to a subtype of
[ICustomComponent](https://github.com/Vazkii/Patchouli/blob/master/Common/src/main/java/vazkii/patchouli/api/ICustomComponent.java). There's
no need to register this class anywhere in code, just create it and the loader will take
care of finding and loading it.

* More values! Any non-transient values you put in your ICustomComponent implementation
  will also be read as component values.
