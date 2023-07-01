# 1.20 Upgrade Guide

### Resource Pack-based books Now Enforced

Historically, Patchouli books have never properly respected resource packs nor datapacks,
despite the files being located in `/assets/` or `/data/`.

In 1.17, "resource pack books" were introduced. These books load all of their content via
the resource pack system, from the `/assets/` folder. However, due to technical reasons,
the `book.json` that declares the book must still be located in `/data/`.

In 1.20, this system is now required. All `book.json`s not loaded via
`.minecraft/patchouli_books` must specify `use_resource_pack: true`, and the book contents
must be located under `/assets/<namespace>/patchouli_books/{categories, entries, ...}`.

People using the "external" `.minecraft/patchouli_books` may continue colocating all files
under that directory.

As a result, "extension books" are now obsolete. Any books that wish to extend another
book can simply add content via the resource pack system into the original book's
paths. Book JSONs that specify the old `extend` property will throw an exception.

This will temporarily regress the "Added By" indicators, which will show up less
often. Detecting which mod or resource pack a file was loaded from is nontrivial in both
Forge and Fabric.

In the future, we may explore options that also move the `book.json` out of `/data/` and
into `/assets/`.

To recap, this is what your resources folder should look like going forward, for modders
and those using resource packs:

```
.
├── assets
│   └── patchouli
│       └── patchouli_books
│           └── comprehensive_test_book
│               ├── en_us
│               │   ├── categories
│               │   ├── entries
│               │   └── templates
│               └── zh_cn
│                   └── entries
└── data
    └── patchouli
        └── patchouli_books
            └── comprehensive_test_book
                └── book.json
```

For those using the external folder, your structure will remain as follows:

```
.minecraft
└── patchouli_books
    └── external_book
        ├── book.json
        └── en_us
            ├── categories
            │   └── testcat.json
            └── entries
                └── testcat
                    └── testentry.json
```

If it sounds confusing, sorry, that's because it is. Patchouli's history of loading stuff
is complicated due to several past mistakes that stuck with us.

In the future, we may investigate two further simplifications:

1. Somehow making books completely clientsided, so that nothing is needed in `/data/`
2. Removing the external folder and requiring resource pack usage for everything.
