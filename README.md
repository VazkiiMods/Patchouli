# Patchouli
Accessible, Data-Driven, Dependency-Free Documentation for Minecraft Modders and Pack Makers

For more information, see the [wiki](https://github.com/Vazkii/Patchouli/wiki).

## What does the mod's name mean???
https://en.touhouwiki.net/wiki/Patchouli_Knowledge

## Maven info

Maven artifacts are located [here](https://maven.blamejared.com/vazkii/patchouli/Patchouli/), each folder representing a version.

Note: As of 1.16, intermediate (non-release) Maven builds are no longer persisted.
That is, you must either depend on a *released* version of Patchouli, e.g. `1.16-37` or `1.16-37-FABRIC`, or specifically opt in to the bleeding-edge
build of the next version. For example, `1.16-38-SNAPSHOT` or `1.16-38-FABRIC-SNAPSHOT` would be the current bleeding edge version of future version `1.16-38`. 

Note that `-SNAPSHOT` versions can be broken from time to time, and you are strongly discouraged from using them unless you are helping dogfood, test, or contribute to Patchouli. They may also be pruned from time to time to save disk space on the server. Do *not* rely on `-SNAPSHOT` versions for anything important!

In Forge, add the following to your `build.gradle`
```gradle
repositories {
    maven { url 'https://maven.blamejared.com' }
}

dependencies {
    // 1.14+
    compileOnly fg.deobf("vazkii.patchouli:Patchouli:[VERSION]:api")
    runtimeOnly fg.deobf("vazkii.patchouli:Patchouli:[VERSION]")

    // 1.12
    compile "vazkii.patchouli:Patchouli:[VERSION]"
}
```

## Mixin Troubleshooting

Read this if you get crashes when launching with Patchouli in-dev.
Patchouli uses Mixin to inject a small hook to detect advancements clientside.
This may cause issues when depending on Patchouli in-dev, since ForgeGradle/MixinGradle
do not yet properly support this in-dev like Fabric does.
This can be worked around by specifying the refmap remapping manually: add [these lines](https://github.com/SpongePowered/Mixin/issues/462#issuecomment-791370319) to your `build.gradle`.
You need to regenerate your run configurations in the IDE after making this change.

Note to mod developers porting a Patchouli book from 1.12 to 1.14: Patchouli book data now goes into `modid/data/patchouli_books` instead of `modid/assets/patchouli_books`.
