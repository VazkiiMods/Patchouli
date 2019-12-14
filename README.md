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