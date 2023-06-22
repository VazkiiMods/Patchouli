# 1.20 Upgrade Guide

### Resource Pack-based books Now Required

Historically, Patchouli books have never properly respected resource packs nor datapacks,
despite the files being located in `/assets/` or `/data/`.

In 1.17, "resource pack books" were introduced. These books load all of their content via
the resource pack system, from the `/assets/` folder. However, due to technical reasons,
the `book.json` that declares the book must still be located in `/data/`.

In 1.20, this system is now required. All `book.json`s must specify `use_resource_pack:
true`, and the book contents must be located under
`/assets/<namespace>/patchouli_books/{categories, entries, ...}`.

As a result, "extension books" are now obsolete. Any books that wish to extend another
book can simply add content via the resource pack system into the original book's
paths. Book JSONs that specify the old `extend` property will throw an exception.

In the future, we may explore options that also move the `book.json` out of `/data/` and
into `/assets/`.
