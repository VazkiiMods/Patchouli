## Fabric Information
This is the Fabric port of Patchouli, it will receive support for the latest stable version of Minecraft available on Fabric.
Snapshot releases may happen depending on my mood, but don't count on them.

# Patchouli
Accessible, Data-Driven, Dependency-Free Documentation for Minecraft Modders and Pack Makers

For more information, see the [wiki](https://github.com/Vazkii/Patchouli/wiki).

## What does the mod's name mean???1
https://en.touhouwiki.net/wiki/Patchouli_Knowledge

## Maven info

Maven artifacts are located [here](https://maven.blamejared.com/vazkii/patchouli/Patchouli/), each folder representing a version.

Note: As of 1.16, intermediate (non-release) Maven builds are no longer persisted.
That is, you must either depend on a *released* version of Patchouli, e.g. `1.16-37` or `1.16-37-FABRIC`, or specifically opt in to the bleeding-edge
build of the next version. For example, `1.16-38-SNAPSHOT` or `1.16-38-FABRIC-SNAPSHOT` would be the current bleeding edge version of future version `1.16-38`. 

Note that `-SNAPSHOT` versions can be broken from time to time, and you are strongly discouraged from using them unless you are helping dogfood, test, or contribute to Patchouli. They may also be pruned from time to time to save disk space on the server. Do *not* rely on `-SNAPSHOT` versions for anything important!

In Fabric, add the following to your `build.gradle`
```gradle
repositories {
    maven { url 'https://maven.blamejared.com' }
}

dependencies {
    // 1.14+
    modCompile "vazkii.patchouli:Patchouli:[VERSION]:api"
    modImplementation "vazkii.patchouli:Patchouli:[VERSION]"
}
```

Note to mod developers porting a Patchouli book from 1.12 to 1.14: Patchouli book data now goes into `modid/data/patchouli_books` instead of `modid/assets/patchouli_books`.
