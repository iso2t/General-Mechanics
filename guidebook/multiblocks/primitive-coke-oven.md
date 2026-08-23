---
navigation:
    parent: multiblocks/multiblocks-index.md
    title: Primitive Coke Oven
    icon: coke_oven_controller
    position: 210
item_ids:
- generalmechanics:coke_oven_controller
- generalmechanics:coke_oven_bricks
---

<Column alignItems="center" fullWidth={true}>
    <GameScene zoom="4" interactive={true}>
        <ImportStructure src="../assets/assemblies/coke_oven.nbt" />
        <IsometricCamera yaw="105" pitch="5" />
    </GameScene>
</Column>

# Primitive Coke Oven

## Carbon Improvement, the Traditional Way

Before GenTech developed electrically powered machinery, our engineers improved raw materials using the proven scientific
process of surrounding them with bricks and putting lava in the middle. The result is the **Primitive Coke Oven**, one of
the earliest multiblock factories available to a newly assigned operator.

The Primitive Coke Oven converts coal, coal blocks, charcoal, and burnable logs into **Coal Coke**. Every operation also
produces [Creosote Oil](../fluids/creosote-oil.md), a valuable industrial fluid used to manufacture treated wood and justify
the installation of additional fluid storage.

Once formed, the oven requires no external fuel or power. Its internal lava source provides continuous heat and should not
be removed unless your production targets have been revised to zero.

## Construction Requirements

Construct the 3×3×3 structure shown above using:

* **20 Coke Oven Bricks** — approved thermal containment and structural reassurance.
* **1 Coke Oven Controller** — converts a brick enclosure into a brick enclosure with quarterly objectives.
* **1 lava source** — one bucket is sufficient; direct employee transport is not recommended.

Eligible Coke Oven Brick positions may be replaced with supported [Hatches](../blocks/hatches.md). The oven accepts up to
one Item Input Hatch, one Item Output Hatch, one Fluid Output Hatch, and one Network Hatch. The Network Hatch can expose
item and fluid services directly to the Machine Network.

> **GENTECH FORMATION NOTICE:** The lava is part of the structure, not leftover construction material. Removing it will
> successfully transform the Primitive Coke Oven into a warm brick arrangement.

## Approved Carbon Processing

| Input | Output | Creosote Oil | Processing Time |
| --- | --- | ---: | ---: |
| 1 Coal | 1 Coal Coke | 250 mB | 60 seconds |
| 1 Coal Block | 9 Coal Coke | 2,250 mB | 9 minutes |
| 1 Charcoal | 1 Coal Coke | 500 mB | 60 seconds |
| 1 burnable Log | 1 Coal Coke | 250 mB | 75 seconds |

GenTech acknowledges that converting wood directly into Coal Coke raises several material-science questions. These
questions have been forwarded to the oven, which has declined to answer while continuing to meet production quotas.

---

## Coke Oven Bricks

Combine clay, sand, gravel, and a bucket of water to produce four units of GenTech-certified heat-resistant optimism.

<RecipeFor id="generalmechanics:coke_oven_bricks" />

## Coke Oven Controller

Surround a Blast Furnace with Coke Oven Bricks to install management inside the structure.

<RecipeFor id="generalmechanics:coke_oven_controller" />
