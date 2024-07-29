package com.bibireden.playerex.mixin;

import com.bibireden.data_attributes.api.item.ItemFields;
import com.bibireden.playerex.PlayerEX;
import com.bibireden.playerex.config.PlayerEXConfigModel;
import com.google.common.collect.Multimap;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.attribute.EntityAttribute;
import net.minecraft.entity.attribute.EntityAttributeInstance;
import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Mixin(ItemStack.class)
abstract class ItemStackMixin {
    @Unique
    private double playerex$modifyValue(double val, @Nullable PlayerEntity player, EntityAttribute attribute, UUID uuid) {
        if (player == null || PlayerEX.CONFIG.getTooltip() == PlayerEXConfigModel.Tooltip.Default) return val;

        double valSubBase = val - player.getAttributeBaseValue(attribute);

        EntityAttributeInstance instance = player.getAttributeInstance(attribute);
        if (instance == null) return val;

        EntityAttributeModifier modifier = instance.getModifier(uuid);
        double value = player.getAttributeValue(attribute) + valSubBase;

        if (modifier != null) value -= modifier.getValue();

        return PlayerEX.CONFIG.getTooltip() == PlayerEXConfigModel.Tooltip.Vanilla ? valSubBase : value;
    }

    @Unique
    private String playerex$value(double e, Map.Entry<EntityAttribute, EntityAttributeModifier> entry, EntityAttributeModifier modifier) {
        if (modifier.getOperation() != EntityAttributeModifier.Operation.ADDITION) return ItemStack.MODIFIER_FORMAT.format(e);
        return ItemStack.MODIFIER_FORMAT.format(e);
    }

    @ModifyVariable(method = "getTooltip", at = @At(value = "STORE", ordinal = 1), ordinal = 0)
    private double playerex$modifyAttackDamage(double original, PlayerEntity player) {
        return playerex$modifyValue(original, player, EntityAttributes.GENERIC_ATTACK_DAMAGE, ItemFields.attackDamageModifierID());
    }

    @ModifyVariable(method = "getTooltip", at = @At(value = "STORE", ordinal = 3), ordinal = 0)
    private double playerex$modifyAttackSpeed(double original, PlayerEntity player) {
        return playerex$modifyValue(original, player, EntityAttributes.GENERIC_ATTACK_SPEED, ItemFields.attackDamageModifierID());
    }

    @ModifyVariable(method = "getTooltip", at = @At(value = "STORE", ordinal = 1), ordinal = 0)
    private boolean playerex$flagAttackDamage(boolean original) {
        return PlayerEX.CONFIG.getTooltip() != PlayerEXConfigModel.Tooltip.Vanilla && original;
    }

    @ModifyVariable(method = "getTooltip", at = @At(value = "STORE", ordinal = 2), ordinal = 0)
    private boolean playerex$flagAttackSpeed(boolean original) {
        return PlayerEX.CONFIG.getTooltip() != PlayerEXConfigModel.Tooltip.Vanilla && original;
    }

    @ModifyVariable(method = "getTooltip", at = @At(value = "STORE", ordinal = 1), ordinal = 1)
    private double playerex$modifyAdditionAttributeKnockback(double original) { return original / 10.0; }

    // todo: not sure about the implementation(s) here...
    @Inject(method = "getTooltip", at = @At(value = "INVOKE", target = "Ljava/util/List;add(Ljava/lang/Object;)Z", ordinal = 7, shift = At.Shift.AFTER), locals = LocalCapture.CAPTURE_FAILHARD)
    private void playerex$insertModifierEqualsTooltip(
        PlayerEntity player, TooltipContext context,
        CallbackInfoReturnable<List<Text>> info,
        List<Text> list,
        MutableText arg3,
        int arg4,
        EquipmentSlot[] arg5,
        int arg6,
        int arg7,
        EquipmentSlot arg8,
        Multimap<?, ?> arg9,
        Iterator<?> arg10,
        Map.Entry<EntityAttribute, EntityAttributeModifier> entry,
        EntityAttributeModifier entityAttributeModifier,
        double arg13, double e
    ) {
        list.set(list.size() - 1, Text.literal(" ")
            .append(Text.translatable("attribute.modifier.equals." + entityAttributeModifier.getOperation().getId(), playerex$value(e, entry, entityAttributeModifier), Text.translatable(entry.getKey().getTranslationKey())))
            .formatted(Formatting.DARK_GREEN)
        );
    }

    @Inject(method = "getTooltip", at = @At(value = "INVOKE", target = "Ljava/util/List;add(Ljava/lang/Object;)Z", ordinal = 8, shift = At.Shift.AFTER), locals = LocalCapture.CAPTURE_FAILHARD)
    private void playerex$insertModifierPlusTooltip(
        PlayerEntity player,
        TooltipContext context,
        CallbackInfoReturnable<List<Text>> info,
        List<Text> list,
        MutableText arg3,
        int arg4,
        EquipmentSlot[] arg5,
        int arg6, int arg7, EquipmentSlot arg8, Multimap<?, ?> arg9, Iterator<?> arg10,
        Map.Entry<EntityAttribute, EntityAttributeModifier> entry, EntityAttributeModifier entityAttributeModifier,
        double arg13, double e
    ) {
        list.set(
            list.size() - 1,
            Text.translatable("attribute.modifier.plus." + entityAttributeModifier.getOperation().getId(),
            playerex$value(e, entry, entityAttributeModifier),
            Text.translatable(entry.getKey().getTranslationKey())).formatted(Formatting.BLUE)
        );
    }

    @Inject(method = "getTooltip", at = @At(value = "INVOKE", target = "Ljava/util/List;add(Ljava/lang/Object;)Z", ordinal = 9, shift = At.Shift.AFTER), locals = LocalCapture.CAPTURE_FAILHARD)
    private void playerex$insertModifierTakeTooltip(
        PlayerEntity player, TooltipContext context,
        CallbackInfoReturnable<List<Text>> info,
        List<Text> list,
        MutableText arg3,
        int arg4,
        EquipmentSlot[] arg5,
        int arg6, int arg7,
        EquipmentSlot arg8,
        Multimap<?, ?> arg9,
        Iterator<?> arg10,
        Map.Entry<EntityAttribute, EntityAttributeModifier> entry,
        EntityAttributeModifier entityAttributeModifier,
        double arg13, double e
    ) {
        list.set(
            list.size() - 1,
            Text.translatable("attribute.modifier.take." + entityAttributeModifier.getOperation().getId(),
            playerex$value(e, entry, entityAttributeModifier),
            Text.translatable(entry.getKey().getTranslationKey())).formatted(Formatting.RED)
        );
    }
}
