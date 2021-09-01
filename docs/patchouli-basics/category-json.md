# Category JSON Format

This page details every key you can have in a Category json file.

While not described as a format key, the "category ID" of a category is the path necessary
to get to it from /en_us/categories. So if your category is in
"/en_us/categories/misc/cool_stuff.json", its ID would be "misc/cool_stuff".

* **name** (String, _mandatory_)

The name of this category.

* **description** (String, _mandatory_)

The description for this category. This displays in the category's main page, and can be
[formatted](/docs/patchouli-basics/text-formatting).

* **icon** (String, _mandatory_)

The icon for this category. This can either be an [ItemStack
String](/docs/patchouli-advanced/itemstack-format), if you want an item to be the icon, or
a resource location pointing to a square texture. If you want to use a resource location,
make sure to end it with .png

* **parent** (String)

The parent category to this one. If this is a sub-category, simply put the name of the
category this is a child to here. If not, don't define it. This should be fully-qualified
and of the form `domain:name` where `domain` is the same as the domain of your Book ID.

* **flag** (String)

A config flag expression that determines whether this category should exist or not. See
[Using Config Flags](/docs/patchouli-basics/config-gating) for more info on config flags.

* **sortnum** (integer)

The sorting number for this category. Defaults to 0. Categories are sorted in the main
page from lowest sorting number to highest, so if you define this in every category you
make, you can set what order they display in.

* **secret** (boolean)

Defaults to false. Set this to true to make this category a secret category. Secret
categories don't display a locked icon when locked, and instead will not display at all
until unlocked.


