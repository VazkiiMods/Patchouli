# Component Processors
**This page is for modders only.**

In cases where data may be stored away in data structures inside your mod code, you can
use a Component Processor to get that data into your components easily instead of having
to duplicate the values in json. Here's how to do it:

* Decide on which variables used by your template can be derived from code and which
  variables you want to create to derive them from. For example, if your template is meant
  to show a recipe with multiple inputs, you could change that to a single "recipe"
  variable and derive all the stack variables using it.
* Create a class that implements
  [IComponentProcessor](https://github.com/Vazkii/Patchouli/blob/master/Common/src/main/java/vazkii/patchouli/api/IComponentProcessor.java),
  and fill in the class's methods according to the javadocs.
    * Here's [an
      example](https://github.com/Vazkii/Patchouli/blob/master/Common/src/main/java/vazkii/patchouli/client/book/template/test/RecipeTestProcessor.java)
      you can look into, as well as the [template
      file](https://github.com/Vazkii/Patchouli/blob/master/Common/src/main/resources/data/patchouli/patchouli_books/testbook2/en_us/templates/include/recipetest.json)
      that uses it.
* Add `"processor": "package.name.ClassName"` to your template file, referencing the class
  you created.
* Use your template in your entry, adding in the variables you need. The example shown
  previously has a bunch of variables in there, but thanks to the processor, you only need
  to declare the "recipe" processor. Notice that the example also has a [nested
  template](/docs/patchouli-advanced/template-nesting), and the variables it uses can also
  be derived.
