package general.mechanics.components;

import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.util.ExtraCodecs;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

import java.util.List;
import java.util.Objects;

public record InventoryComponent(List<ItemStack> inventory) {

    public static final Codec<Holder<Item>> ITEM_CODEC = BuiltInRegistries.ITEM.holderByNameCodec().validate(DataResult::success);
    public static final Codec<ItemStack> STACK_CODEC = Codec.lazyInitialized(() -> RecordCodecBuilder.create((instance) -> instance.group(ITEM_CODEC.fieldOf("id").forGetter(ItemStack::typeHolder), ExtraCodecs.intRange(0, 99).fieldOf("count").orElse(1).forGetter(ItemStack::getCount)).apply(instance, ItemStack::new)));
    public static final Codec<InventoryComponent> CODEC = RecordCodecBuilder.create(instance -> instance.group(Codec.list(STACK_CODEC).fieldOf("inventory").forGetter(InventoryComponent::inventory)).apply(instance, InventoryComponent::new));

    @Override
    public int hashCode () {
        return Objects.hash(this.inventory);
    }

    @Override
    public boolean equals (Object obj) {
        if (obj == this) {
            return true;
        } else {
            return obj instanceof InventoryComponent(List<ItemStack> inventory1)
                    && this.inventory == inventory1;
        }
    }
}
