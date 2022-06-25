# Pamphlets

Available in Patchouli 1.18.2-68 or above.

Sometimes, an entire book with multiple categories is too bloated for a 
small mod or project. For this use-case, Patchouli offers *pamphlets*,
which are a special type of book with only one category.

When the player opens a book marked as a pamphlet, the landing page's right
side will open to a list of the entries in the *root* category, instead of
the normal list of categories.

To indicate that your book is a pamphlet, set the optional `pamphlet`
key in your book.json to `true`, and write exactly one category into
the book. Patchouli will automatically find that single category and
use it.

See:

![](https://i.imgur.com/BoV7U6g.png)
