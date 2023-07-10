# 1.20 Upgrade Guide

This guides describes how to upgrade Patchouli books from 1.19.x to 1.20.x.

### Resource Pack-based books Now Enforced

Historically, Patchouli books have never properly respected resource packs nor datapacks,
despite the files being located in `/assets/` or `/data/`.

In 1.17, "resource pack books" were introduced. These books load all of their content via
the resource pack system, from the `/assets/` folder. However, due to technical reasons,
the `book.json` that declares the book must still be located in `/data/`.

In 1.20, this system is now required. All `book.json`s not loaded via
`.minecraft/patchouli_books` must specify `use_resource_pack: true`, and the book contents
must be located under `/assets/<namespace>/patchouli_books/{categories, entries,
templates}`.

People using the "external" `.minecraft/patchouli_books` may continue colocating all files
under that directory.

As a result, "extension books" are now obsolete. Any books that wish to extend another
book can simply add content via the resource pack system into the original book's
paths. Book JSONs that specify the old `extend` property will throw an exception.

This will temporarily regress the "Added By" indicators, which will show up less
often. Detecting which mod a resource was loaded from is nontrivial in both Forge and
Fabric, because they both group all mod resources under one virtual resource pack.

To recap, this is what your `resources` folder should look like going forward, for
modders:

```
.
├── assets
│   └── your_namespace
│       └── patchouli_books
│           └── your_book_name
│               ├── en_us
│               │   ├── categories
│               │   ├── entries
│               │   └── templates
│               └── zh_cn
│                   └── entries
└── data
    └── your_namespace
        └── patchouli_books
            └── your_book_name
                └── book.json
```

Addon modders and modpackers modifying an existing book will only need the `/assets/` part
above, and should ship it in mod resources or a resource pack, respectively. And to
clarify, `your_namespace` above would actually be the namespace of the book you're trying
to modify.

For those using the external folder, your structure will remain as follows:

```
.minecraft
└── patchouli_books
    └── your_book_name
        ├── book.json
        └── en_us
            ├── categories
            └── entries
```

If it sounds confusing, sorry, that's because it is. Patchouli's history of loading stuff
is complicated due to several past mistakes that stuck with us. These changes already
drastically simplify the internals of how extensions and overriding work, so we appreciate
your patience.

In the future, we may investigate two further simplifications:

1. Somehow making books completely clientsided, so that nothing needs to be put in
   `/data/`.  This has some difficulties as some data (such as which creative tab a book
   goes in) needs to be known on startup before resources are fully loaded.
2. Removing the external folder and requiring resource pack usage for all usecases. This
   requires the previous point to be done first.
