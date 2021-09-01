# Extending Other Books

These are some additional keys you can have in your [book json file](https://github.com/Vazkii/Patchouli/wiki/Book-JSON-Format/) that are used for extending another book's content easily.

* **extend** (String)

Define this to set this book as an extension of another. Extension books do not create a book item and don't really "exist". All they serve to do is to add more content to another book that already exists. This is mainly here for addon mods that want to add stuff to the book they're extending.

The value you put here is simply the ID of the book you want to extend. In the form of "modid:path", where modid is the ID of the mod that owns the book, and path the folder where it is. For example, should you want to extend the book owned by "patchouli" that's in "/data/patchouli/patchouli_books/coolbook/book.json", the value you'd put here would be "patchouli:coolbook".

If this value is set, every single other value in the file is ignored. This book will inherit any entries, categories, templates, and macros from the original one, so feel free to use them at will. 

* **allow_extensions** (boolean)

Defaults to true. Set it to false if you want to not play nice and lock your book from being extended by other books.
