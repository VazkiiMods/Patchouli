## Important Note
NOTE: This is an experimental, for-fun, and WIP Fabric port of Patchouli.
You will receive NO support at all if you run this.
Please don't bother anyone if you encounter issues running this.

## Why?
* Various issues with the forge ecosystem are tiring me out
* I want to see how Fabric is coming along
* I can't make an educated judgment on Forge vs. Fabric if I haven't used both
* The best way to get so called "educated" is to port a real world use-case to Fabric

## Will this ever get released?
If I get it to work, and not under the official Patchouli banner. It'll be like "Botania Unofficial",
derived from but unaffiliated with the original.

# Patchouli
Accessible, Data-Driven, Dependency-Free Documentation for Minecraft Modders and Pack Makers

For more information, see the [wiki](https://github.com/Vazkii/Patchouli/wiki).

## Maven info

```gradle
repositories {
    maven { url 'https://maven.blamejared.com' }
}

dependencies {
    compile fg.deobf("vazkii.patchouli:Patchouli:[VERSION]") // 1.14
    compile "vazkii.patchouli:Patchouli:[VERSION]"           // 1.12
}
```

Don't forget to replace `[VERSION]` with the version of the mod that you want to work with! The available versions can be seen on [Jared's maven](https://maven.blamejared.com/vazkii/patchouli/Patchouli/).

Note to mod developers porting a Patchouli book from 1.12 to 1.14: Patchouli book data now goes into `modid/data/patchouli_books` instead of `modid/assets/patchouli_books`.
