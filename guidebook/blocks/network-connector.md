---
navigation:
    parent: blocks/blocks-index.md
    title: Network Connector
    icon: network_connector
    position: 210
categories:
- network
item_ids:
- generalmechanics:network_connector
---

<Column alignItems="center" fullWidth={true}>
    <GameScene zoom="4">
        <Block id="generalmechanics:network_connector" />
    </GameScene>
</Column>

# Network Connector
The Network Connector is a block that exposes a connected blocks inventory to the Machine Network. This allows automation
with blocks that are not inherently supported by General Mechanics.

The interfacing block **MUST** be placed on top of the Network Connector; a network cable may connect to any side of the
block, excluding the top. 



<Column alignItems="center" fullWidth={true}>
    <GameScene zoom="4" interactive={true}>
        <ImportStructure src="../assets/assemblies/network_connector.nbt" />
        <IsometricCamera yaw="195" pitch="5" />  
    </GameScene>
</Column>