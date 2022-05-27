# Pamphlets

Sometimes, an entire book with multiple categories is too bloated for a 
small mod or project. For this use-case, Patchouli offers *pamphlets*,
which are a special type of book with only one category.

When the player opens a book marked as a pamphlet, the landing page's right
side will open to a list of the entries in the *root* category, instead of
the normal list of categories.

To indicate that your book is a pamphlet, set the optional `pamphlet_category`
key in your book.json to the resource location of the category you want to
be the root. (Although you *can* have more than one category in a pamphlet,
only the one marked in the book.json will actually be displayed, so there's no
reason to have more than one.)

See:

![](https://i.imgur.com/BoV7U6g.png)
