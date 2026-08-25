---
navigation:
    parent: multiblocks/multiblocks-index.md
    title: Fluid Infuser Factory
    icon: fluid_infuser
    position: 250
---

<Column alignItems="center" fullWidth={true}>
    <GameScene zoom="4" interactive={true}>
        <ImportStructure src="../assets/assemblies/fluid_infuser_factory.nbt" />
        <IsometricCamera yaw="105" pitch="5" />
    </GameScene>
</Column>

# Fluid Infuser Factory

## Bucket Filling at an Institutional Scale

The standalone [Fluid Infuser](../blocks/fluid-infuser.md) can place one bucket of fluid into one compatible container in
five seconds. GenTech leadership reviewed this achievement, identified four and a half seconds of unacceptable waiting,
and authorized construction of the **Fluid Infuser Factory**.

The factory is a 3×3×3 multiblock that accelerates the Fluid Infuser using a uniform ring of
[Factory Matrices](../blocks/matrices.md). It retains the controller's 16-bucket tank and still fills each container with
exactly one bucket. The larger building does not produce a larger bucket; it produces ordinary buckets with considerably
greater organizational support.

## Construction Requirements

Before substituting [Hatches](../blocks/hatches.md), construct the factory shown above using:

* **1 Fluid Infuser** — controller, operator interface, and licensed authority on which liquid goes where.
* **8 Machine Frames** — the lower structural ring.
* **8 matching middle-ring blocks** — use Machine Frames for 1× speed or one uniform Factory Matrix tier for an upgrade.
* **9 Machine Casings** — the upper structural layer.
* **1 block of air** — the center of the matrix ring must remain empty.

The air block is a required component. Filling the center with fluid does not increase tank capacity; it prevents the
factory from forming and creates an indoor water feature outside the Facilities budget.

> **GENTECH PRESSURE NOTICE:** The empty center is not a reservoir, overflow chamber, employee hydration station, or
> suitable location from which to observe industrial infusion.

## Industrial Infusion Performance

Forming the factory does not change the basic transaction. Every operation still consumes one empty compatible container,
drains exactly one bucket from the internal tank, and produces one filled container. It also preserves the standalone
machine's all-or-nothing transfer policy: if the complete operation cannot succeed, the tank and container remain
unchanged.

The factory improves processing speed only. Tank capacity remains **16 buckets**, total energy use remains **3,000 energy
per container**, and incompatible fluids remain incompatible regardless of facility size, matrix tier, or managerial
confidence.

## Hatch Integration

Eligible Machine Frame and Machine Casing positions may be replaced with up to one of each supported hatch:

* **Item Input Hatch** — inserts compatible empty fluid containers.
* **Item Output Hatch** — extracts professionally filled containers.
* **Fluid Input Hatch** — supplies the controller's internal tank.
* **Power Hatch** — supplies energy to the factory.
* **Network Hatch** — exposes item, fluid, energy, and data services directly to the Machine Network.

When the structure forms, direct connections to the controller are disabled and its side configuration is locked. Install
the dedicated hatches needed for external automation, or connect a Network Hatch to handle the factory's resources through
one properly authorized access point. Hatches are structurally optional; a factory with no way to receive fluid, containers,
or power remains a valid and exceptionally well-armored waiting room.

## Matrix Performance

| Middle-Ring Material | Processing Speed |
| --- | ---: |
| Machine Frame | 1× |
| Iron Core Matrix | 1.25× |
| Steel Core Matrix | 2× |
| Titanium Core Matrix | 4× |
| Tungsten Core Matrix | 32× |
| Quantum Core Matrix | 64× |

All eight middle-ring blocks must match. Mixing tiers invalidates the structure because fluids may mix under approved
conditions; corporate performance expectations may not.

Faster matrices reduce processing time without reducing total energy use. The same 3,000 energy is delivered over fewer
ticks, so higher tiers require a faster power supply. If the supply cannot keep pace, processing pauses until sufficient
energy becomes available and the Power Department completes its investigation into why “64×” also applies to demand.

> **GENTECH STRUCTURAL NOTICE:** Breaking the factory returns the Fluid Infuser to standalone operation and resets active
> work. Any partially scheduled bucket will be returned to the beginning of the approved five-second experience.

GenTech congratulates you on surrounding a bucket-filling machine with 25 additional blocks until it became an industry.
