<p align="center"><img src="https://raw.githubusercontent.com/iso2t/General-Mechanics/refs/heads/master/src/main/resources/logo.png" alt="Logo"></p>

<h1 align="center">General Mechanics</h1>
<p align="center"><strong>Realistic technology for the modern era. Prosperity assignment included.</strong></p>

## Welcome, Prospective Success Asset

Congratulations! GenTech has assigned you to **General Mechanics**, a NeoForge technology mod focused on industrial
machines, configurable automation, resource processing, and multiblock factories. From your first Coke Oven to a
64× Electric Furnace Factory, every system is designed to convert natural resources into measurable progress.

You provide the materials, energy, floor space, and continued possession of your extremities. GenTech provides the
equipment and a statistically reassuring quantity of documentation.

> **PRE-ALPHA OPERATIONS NOTICE:** General Mechanics is under active development. Features, recipes, and workplace safety
> classifications may change as research continues and witnesses become available.

## Approved Capabilities

- Configurable electric machines with item, energy, and Machine Network access.
- Cable-based networks for transferring items, blocks, fluids, and energy.
- Multiblock factories with hatches, shared-wall support, and increasingly unreasonable processing speeds.
- Primitive carbon processing through the lava-powered Coke Oven.
- Rubber Trees, industrial fluids, specialized tools, and several products created after incidents.
- In-game GuideME documentation written by GenTech's Liability-Aware Communications Division.

## Developer Integration

To register General Mechanics as an approved external dependency, add the ISO2T Maven repository to `build.gradle`:

```groovy
repositories {
    maven {
        name = "ISO2T Maven"
        url = uri("https://maven.iso2t.com")
        content {
            includeGroup("general.mechanics")
        }
    }
}
```

Then add the NeoForge dependency. Replace `${mc_version}` with the target Minecraft version and `${version}` with the
required General Mechanics version:

```groovy
dependencies {
    implementation "general.mechanics:gm-${mc_version}:${version}"
}
```

Successful dependency resolution confirms only that the artifact was found. Correct usage remains the responsibility of
the integrating department.

## Local Development

### Approved Workstation Equipment

- JDK 25
- [IntelliJ IDEA Community Edition](https://www.jetbrains.com/idea/download/) or another Java 25-compatible IDE
- [Project Lombok plugin](https://plugins.jetbrains.com/plugin/6317-lombok)
- [Minecraft Development plugin](https://plugins.jetbrains.com/plugin/8327-minecraft-development) — recommended for
  reducing the number of Minecraft-specific mysteries

### Build Verification

On Windows:

```powershell
.\gradlew.bat build
```

On macOS or Linux:

```bash
./gradlew build
```

A successful build indicates that the current source compiled. It does not imply that the resulting machinery should be
left unattended.

## Employee Orientation

The [GenTech Guidebook](guidebook/index.md) documents machines, multiblocks, fluids, network infrastructure, and lessons
recovered from previous successful employees.

## Texture Development

Many textures were created in Adobe Photoshop. Editable source files are available in the [`Photoshop`](Photoshop) folder
for authorized visual maintenance and unauthorized layer archaeology.

## Contributing

External research proposals are welcome. Review [CONTRIBUTING.md](CONTRIBUTING.md) before submitting changes so your work
can be processed with the minimum required paperwork.

## License

General Mechanics is distributed under the terms described in [LICENSE.md](LICENSE.md). GenTech reminds all personnel that
“open source” does not mean “close enough.”
