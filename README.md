

# RenJava
Visual novel game engine based off of [RenPy](https://www.renpy.org/) built with [RenGL]()

This is a passion project, which means I'm learning as I go. There are a lot of mistakes and bugs which will be addressed eventually. This is not meant for production.

## IMPORTANT NOTICE
RenJava is being migrated to RenGL. It was originally created with JavaFX. RenGL is new engine built with LWJGL and Skiko.
It is widely untested and may cause issues for some users. Please report any issues you find to this projects issue page or on [RenGL]() issue page.

## Disclaimer
RenJava does not claim ownership over any copyrighted material such as music, video, or assets. RenJava is only the framework not the game made by an author.
Any copyright claims must be filed against the author. RenJava does not own or distribute any game(s). It would be similar to suing YouTube because someone else uploaded an entire movie without permission.

As an author you must have expressed consent to use any copyrighted material like music. Alternatively, you could look for royalty free options.

## Project Requirements
- [Java-25](https://www.oracle.com/java/technologies/downloads/#java21) (You can typically download this within your IDE)
- [intellij](https://www.jetbrains.com/idea/download/?section=windows) or [Eclipse](https://www.eclipse.org/downloads/)
- [Maven](https://maven.apache.org/download.cgi) (Might come with your IDE)

## Application Requirements
- The OS has to support Java 25 JDK.
- System supports x64 or aarch64
- System supports OpenGL 3.3 or Metal.
- 4GB RAM
- 512MB VRAM
- Dual Core CPU

## Compile from source
There are different branches you can clone from; Master, Experimental. Master is the stable branch, this is the recommended branch for cloning.

Experimental is the branch that has the latest untested features and bug fixes. Once the development branch is tested, it is merged with Master and a new build is released.
- Download RenJava via clone or file.
- Extract file and open RenJava with any IDE that supports Maven.
- Run `mvn clean install` from the IDE or command line inside the root directory.

## Getting started
Follow this [guide](https://github.com/HackusatePvP/RenJava/wiki/Getting-Started) to get an easy start. You can use the [renjava-template](https://github.com/HackusatePvP/renjava-template) to get started or use [HeroAdventure](https://github.com/HackusatePvP/HeroAdventure). Also explore our [wiki](https://github.com/HackusatePvP/RenJava/wiki) to learn more about the framework and all of its features.

## Updating Framework
When updating to a newer version of RenJava it is recommended to delete default 'settings.properties' and 'build.info'.
These files are located in the 'renjava' folder for your game directory. These files need to be reset before you distribute the game.
RenJava will automatically re-create these files with newer settings and info.

```
Hero-Adventure-1.0
  addons
  game
  jdk
  logs
  renjava
  HeroAdventure-1.0-SNAPSHOT.jar
  start.bat
  start.sh
```

## Developers
- View our api changes [here]().
- Documentation can be found [here](https://github.com/HackusatePvP/RenJava/wiki).
- If you are struggling with anything at all join our discord, and we will help.
