package gregtech.api.items.metaitem.stats;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;

import java.util.List;

import javax.annotation.Nonnull;

public interface IItemBehaviour extends IItemComponent {

    default boolean onLeftClickEntity(ItemStack itemStack, EntityPlayer player, Entity entity) {
        return false;
    }

    default boolean itemInteractionForEntity(ItemStack itemStack, EntityPlayer player, EntityLivingBase target,
                                             EnumHand hand) {
        return false;
    }

    default EnumActionResult onItemUseFirst(EntityPlayer player, World world, BlockPos pos, EnumFacing side, float hitX,
                                            float hitY, float hitZ, EnumHand hand) {
        return EnumActionResult.PASS;
    }

    default ActionResult<ItemStack> onItemUse(EntityPlayer player, World world, BlockPos pos, EnumHand hand,
                                              EnumFacing facing, float hitX, float hitY, float hitZ) {
        return ActionResult.newResult(EnumActionResult.PASS, player.getHeldItem(hand));
    }

    default void addInformation(ItemStack itemStack, List<String> lines) {}

    default void onUpdate(ItemStack itemStack, Entity entity) {}

    default Multimap<String, AttributeModifier> getAttributeModifiers(EntityEquipmentSlot slot, ItemStack stack) {
        return HashMultimap.create();
    }

    default ActionResult<ItemStack> onItemRightClick(World world, EntityPlayer player, EnumHand hand) {
        return ActionResult.newResult(EnumActionResult.PASS, player.getHeldItem(hand));
    }

    default void addPropertyOverride(@Nonnull Item item) {}
}
