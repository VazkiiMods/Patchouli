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
    modImplementation "vazkii.patchouli:Patchouli:[VERSION]"
}
```
Note: Any code not located in the package `vazkii.patchouli.api` is strictly implementation detail, and you should not rely on it as it will change without warning.

# License Information

Patchouli's original code and assets are licensed under the CC-BY-NC-SA 3.0 Unported license.
We recognize that this is not ideal, and are open to changing the licensing of the code in the future.

Please note that this mod uses official Mojang mappings (Mojmap). If you depend on Patchouli as normal,
or only consume Patchouli's API, there should be no licensing concerns, as the mod is remapped to Intermediary (or SRG, for Forge) on compile.

There is a license concern, however, if you bundle Patchouli with your mod using Jar-in-Jar.
Building a mod which uses Mixin inserts a refmap, which for Patchouli will contain raw Mojang mappings in a JSON file.
If this presents a licensing problem to you, then do not bundle Patchouli and just depend on it externally.
I recommend using normal dependencies either way, as Jar-in-Jar inflates your archive sizes to store a mod that will probably be in most modpacks anyways.

## Making a Release
1. Pull from remote, test all changes, and commit everything.
2. `git tag -a release-<VERSION>`. All Patchouli versions *must* follow the version format `<MC-VER>-INT`, so it'll
   probably look like `git tag -a release-1.17.1-55`. If the Fabric version, append `-FABRIC`, e.g.
   `git tag -a release-1.17.1-55-FABRIC`. You can check which number is the next one by looking at
   `build.properties`.
3. In the Git editor that pops up, write the changelog. Finish the tag process (usually by saving and closing the
   editor).
4. Run `./gradlew incrementBuildNumber --no-daemon` to increment the build number of the next release. Commit this.
5. Push: `git push origin master --tags`
6. Go to [Jenkins](https://ci.blamejared.com/job/Patchouli/view/tags/) and wait for the tag you just pushed to be co
   mpiled and built
7. Download the JAR and submit it to CurseForge