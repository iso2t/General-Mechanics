package general.mechanics.api.block.base;

import general.mechanics.api.item.IBlockTooltipProvider;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.block.Block;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jspecify.annotations.NonNull;

import java.util.List;

public abstract class BaseBlock extends Block implements IBlockTooltipProvider {

    protected BaseBlock (Properties properties) {
        super(properties);
    }

    public void addToCreativeTab (CreativeModeTab.Output output) {
        output.accept(this);
    }

	@Override
	public void appendHoverText (@NotNull ItemStack stack, @NonNull @NotNull Item.TooltipContext context, List<Component> tooltipComponents, @NotNull TooltipFlag tooltipFlag) {
	}

	@Override
    public @NotNull String toString () {
        String regName = this.getRegistryName() != null ? this.getRegistryName().getPath() : "unregistered";
        return this.getClass().getSimpleName() + "[" + regName + "]";
    }

    @Nullable
    public Identifier getRegistryName () {
        var id = BuiltInRegistries.BLOCK.getKey(this);
        return id != BuiltInRegistries.BLOCK.getDefaultKey() ? id : null;
    }

}
