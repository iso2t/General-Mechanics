---
navigation:
    parent: blocks/blocks-index.md
    title: Fluid Infuser
    icon: fluid_infuser
    position: 340
categories:
- machines
item_ids:
- generalmechanics:fluid_infuser
---

<Column alignItems="center" fullWidth={true}>
    <GameScene zoom="4">
        <Block id="generalmechanics:fluid_infuser" />
    </GameScene>
</Column>

# Fluid Infuser

## Putting Fluids Where Fluids Were Not

Following several unsuccessful attempts to transport water by hand, GenTech engineers developed the **Fluid Infuser**: a
powered machine that places exactly **one bucket** of fluid into a compatible empty container. The fluid remains inside the
container during ordinary handling, allowing employees to cross the facility without leaving a trail that leads directly
back to the incident.

The Fluid Infuser does not use a fixed recipe list. Whatever fluid is stored in its internal tank becomes the fluid placed
into the next compatible container, provided the tank contains at least one full bucket and the container can accept the
entire amount. Insert empty containers into the input slot and collect the professionally filled results from the output
slot.

> **GENTECH INFUSION NOTICE:** Personnel are not approved fluid containers. This remains true even when a volunteer is
> empty, compatible, or standing in the input area.

## Approved Infusion Procedure

1. Supply the internal tank with the desired fluid.
2. Place a compatible empty fluid container in the input slot.
3. Provide electrical power and leave the output slot available.
4. After **5 seconds**, retrieve one container filled with exactly **one bucket** of fluid.

The input slot can hold up to 64 empty containers. Each operation processes one container and consumes one bucket from the
tank. If the output is blocked or power becomes unavailable, processing pauses without losing progress. If the machine
cannot complete the entire transfer, it leaves the container and tank unchanged; GenTech Accounting refers to this as
“fluid conservation,” while the scientists continue calling it “not spilling.”

## Tank and Power Specifications

| Specification | Standalone Capacity |
| --- | ---: |
| Internal Fluid Tank | 16 buckets |
| Fluid Per Operation | 1 bucket |
| Processing Time | 5 seconds |
| Energy Use | 30 energy per tick |
| Total Energy Per Operation | 3,000 energy |
| Internal Energy Storage | 100,000 energy |
| Maximum Energy Input | 10,000 energy per tick |

To load the tank manually, carry a fluid-filled container on the cursor and click the tank display in the machine
interface. A successful interaction deposits exactly one bucket. Automated fluid input may instead be supplied through a
side configured for Fluid Input or through the Machine Network, freeing the operator to supervise the progress bar with
both hands.

## Configurable Sides

The Fluid Infuser begins with the following GenTech-approved side configuration:

| Machine Face | Default Function |
| --- | --- |
| Front | Disabled and locked |
| Back | Energy Input |
| Left | Item Input |
| Right | Item Output |
| Top | Fluid Input |
| Bottom | Machine Network |

Every face except the front may be configured as disabled, item input, item output, fluid input, energy input, or Machine
Network. Item Input accepts compatible empty containers, Item Output extracts filled containers, and Fluid Input fills the
internal tank. A Machine Network side connected by [Cable](cable.md) can transfer items, fluids, and energy without requiring
three separate pipes, two separate meetings, or one employee who insists they can carry all of it.

## Factory Expansion

The Fluid Infuser can serve as the controller for a 3×3×3
[Fluid Infuser Factory](../multiblocks/fluid-infuser-factory.md). A uniform ring of
[Factory Matrices](matrices.md) increases processing speed while preserving the one-bucket transfer size and total energy
cost. GenTech has confirmed that constructing an entire facility around the machine is faster than waiting five seconds,
provided construction time is excluded from the report.

GenTech congratulates you on successfully placing liquid inside an object. Millions in research funding have finally made
the bucket more complicated.
