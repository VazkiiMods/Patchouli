# Template Nesting

In the purposes of allowing generalization and reusing of common elements, templates can be nested within other templates. This page explains you how to this.

To include templates in another template, you simply add an "include" array to your template .json file. Inside this array, you can specify as many "Template Inclusions" as you want. 

For obvious reasons, circular inclusions aren't allowed. This means if template A includes template B, template B can't include template A. If you do this, stuff is going to break.

## Template Inclusion Format

* **template** (String, _mandatory_)

The ID of the template you wish to include.

* **as** (String, _mandatory_)

The name that you want to reference this inclusion by. Each inclusion should have a different name or weird things are gonna happen. This must be a non-empty value.

* **using** (Object)

A mapping of variables to bind in this template. See the following category on Variable Binding on what this means and how to use them.

* **x** (integer)

The horizontal offset of this template within the one including it. All elements in the template will be shifted horizontally by this value. Defaults to 0.

* **y** (integer)

The horizontal offset of this template within the one including it. All elements in the template will be shifted horizontally by this value. Defaults to 0.

## Variable Binding

Variables in an included template can be resolved in the entry that is using the main template, just like any other variable. The referenced by "#as.name". For example, say the main template includes a template with the "as" value set to "incl", and the included template has some text with the variable "#text". To set the value of this variable in your entry, you'd use "incl.text" as the key.

But what if you want a different name? That's doable as well! Open up a "using" object inside your inclusion object, it should look something like this, for our case:

```json
"include": [
    {
        "template": "sometemplate",
        "as": "incl",
        "using": {
             
        }
    }
]
``` 

Inside the "using" block, we can set values for the variables inside our included template. You can either directly set the value of what you want, or just put another variable on the other side to be resolved by the parent (be it either the entry page itself, or another template that may be including yours).

Let's say we want the included template's text to always be "I'm an included template". We can do the following:
```json
"using": {
    "text": "I'm an included template"
}
``` 

We can also bind it to any other variable:
```json
"using": {
    "text": "#text"
}
``` 

This way, instead of "incl.text" in your entry, you would just use "text". Furthermore, if "#text" is also used elsewhere in the main template, the text of the included template will be the same, as it's just reusing the variable. In the same vein, you can bind multiple variables of the included component to the same main component variable.
