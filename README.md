## Important Note
NOTE: This is an experimental, for-fun Fabric port of Patchouli.
You will receive NO support at all if you run this.
Please don't bother anyone if you encounter issues running this.

## Why?
* Various issues with the forge ecosystem are tiring me out
* I (williewillus) want to see how Fabric is coming along
* I can't make an educated judgment on Forge vs. Fabric if I haven't used both
* The best way to get so called "educated" is to port a real world use-case to Fabric

# Patchouli
Accessible, Data-Driven, Dependency-Free Documentation for Minecraft Modders and Pack Makers

For more information, see the [wiki](https://github.com/Vazkii/Patchouli/wiki).

## What does the mod's name mean???1
https://en.touhouwiki.net/wiki/Patchouli_Knowledge

## Maven info

```gradle
repositories {
    maven { url 'https://maven.blamejared.com' }
}

dependencies {
    // 1.14+
    compileOnly fg.deobf("vazkii.patchouli:Patchouli:[VERSION]:api")
    runtimeOnly fg.deobf("vazkii.patchouli:Patchouli:[VERSION]")

    compile "vazkii.patchouli:Patchouli:[VERSION]"           // 1.12
}
```

Don't forget to replace `[VERSION]` with the version of the mod that you want to work with!
The available versions are the folder names seen on [Jared's maven](https://maven.blamejared.com/vazkii/patchouli/Patchouli/).
For 1.14 and up, the version is prepended with the Minecraft version.

Note to mod developers porting a Patchouli book from 1.12 to 1.14: Patchouli book data now goes into `modid/data/patchouli_books` instead of `modid/assets/patchouli_books`.
