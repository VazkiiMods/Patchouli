# Templating

Aside from the [Default Page Types](/docs/patchouli-basics/page-types) shipped with Patchouli for you to use, you can also create your own! Creating new page types can be done through Patchouli's template system.

Templates are created and referenced in a similar manner to entries and categories, and as such, they go in /patchouli_books/YOURBOOK/en_us/templates/yourtemplate.json, and they can be sorted in sub-folders of /templates.

## Your First Template

Let's start with a small tutorial on how to create a template.

* Go to /patchouli_books/YOURBOOK/en_us/templates, and create a "test_template.json" file, and fill it as such:
```json
{
	"components": [
		{
			"type": "patchouli:text",
			"text": "Hello this is a test of the template system!",
			"x": 20,
			"y": 30
		}
	]
}
```

* Create a new entry for your book, and lay it out in any way you like. In one of the pages, set the page type to be "test_template". 
    * Since there's no default page type with this name, the loader will look for any templates your book has with this name.
    * Naturally, this means you can't have templates named the same way as default page types.
```json
{
    "type": "yourbooknamespace:test_template"
}
```

* Try it out ingame. Your page should show the text you set it to. Feel free to change the text and the x and y positions.
* Go back to your test_template.json file, and change the value of "text" to "#text". This is a _variable_, and we can set its value from the entry!
* Go back to the entry that's using your template, and change the page to this:
```json
{
    "type": "yourbooknamespace:test_template",
    "text": "We just passed in the text from a variable!"
}
```

* Try the edited look ingame, and you'll see your template took the "text" value from your page and put it where "#text" was. This is how you load in variable values onto your template. 
   
Some notes on variables:
 * You can have as many variables as you want, so you could have two text components in different positions with either the same "#text" value (if you want them to say the same thing), or for example, "#upper_text" and "#lower_text".
 * You can't use any variables such that their names are already common keys for pages. This means you can't use any of the names described in the first section of [Default Page Types](/docs/patchouli-basics/page-types).
 * You can learn about more advanced variable usage such as inlining them in strings or deriving them in the [Template Variable Usage](/docs/patchouli-advanced/template-variable-usage) page.

## Template JSON Format

* **components** (Object Array, _mandatory_)

The array of components this template comes with. In the following format:

```
[
    {
        "type": "TYPE",
        (type specific data ...)
    },
    {
        "type": "TYPE",
        (type specific data...)
    }
    (...)
]
```

See [Template Components](/docs/patchouli-advanced/default-components) for the components that Patchouli comes with and what data each one requires.

Note that the components are drawn in the order they appear here, so if you want one to overlap another, put them in the right order.

* **include** (Object Array)

A list of templates to include in this one. See [Template Nesting](/docs/patchouli-advanced/template-nesting) for how to do this.

* **processor** (String)

For modders only: A class name for the processor class that takes care of this template. A processor class can derive data from your code into variables defined in the template. See [Component Processors](/docs/patchouli-advanced/component-processors) for how to use them.
