package com.starshootercity.packetsenders;

import com.destroystokyo.paper.entity.ai.Goal;
import net.kyori.adventure.util.TriState;
import net.minecraft.Optionull;
import net.minecraft.network.chat.RemoteChatSession;
import net.minecraft.network.protocol.game.ClientboundPlayerInfoUpdatePacket;
import net.minecraft.network.protocol.game.ClientboundSetEntityDataPacket;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.goal.AvoidEntityGoal;
import net.minecraft.world.level.GameType;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeInstance;
import org.bukkit.attribute.AttributeModifier;
import org.bukkit.craftbukkit.v1_20_R1.block.CraftBlockState;
import org.bukkit.craftbukkit.v1_20_R1.entity.CraftEntity;
import org.bukkit.craftbukkit.v1_20_R1.entity.CraftLivingEntity;
import org.bukkit.craftbukkit.v1_20_R1.entity.CraftPlayer;
import org.bukkit.craftbukkit.v1_20_R1.inventory.CraftItemStack;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Creeper;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffectType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.function.Predicate;

public class NMSInvokerV1_20_1 extends NMSInvoker {
    @Override
    public void sendEntityData(Player player, Entity entity, byte bytes) {
        ServerPlayer serverPlayer = ((CraftPlayer) player).getHandle();
        net.minecraft.world.entity.Entity target = ((CraftEntity) entity).getHandle();

        List<SynchedEntityData.DataValue<?>> eData = new ArrayList<>();
        eData.add(SynchedEntityData.DataValue.create(new EntityDataAccessor<>(0, EntityDataSerializers.BYTE), bytes));
        ClientboundSetEntityDataPacket metadata = new ClientboundSetEntityDataPacket(target.getId(), eData);
        serverPlayer.connection.send(metadata);
    }

    @Override
    public Goal<Creeper> getCreeperAfraidGoal(LivingEntity creeper, Predicate<Player> hasAbility, Predicate<LivingEntity> hasKey) {
        return new AvoidEntityGoal<>(
                (PathfinderMob) ((CraftEntity) creeper).getHandle(),
                net.minecraft.world.entity.player.Player.class,
                6,
                1,
                1.2,
                livingEntity -> {
                    if (livingEntity.getBukkitEntity() instanceof org.bukkit.entity.Player player) {
                        if (hasAbility.test(player)) {
                            return (!hasKey.test(creeper));
                        }
                    }
                    return false;
                }

        ).asPaperVanillaGoal();
    }

    @Override
    public boolean wasTouchingWater(Player player) {
        return ((CraftPlayer) player).getHandle().wasTouchingWater;
    }

    @Override
    @SuppressWarnings("UnstableApiUsage")
    public float getDestroySpeed(Material block) {
        return ((CraftBlockState) block.createBlockData().createBlockState()).getHandle().destroySpeed;
    }

    @Override
    @SuppressWarnings("UnstableApiUsage")
    public float getDestroySpeed(ItemStack item, Material block) {
        net.minecraft.world.level.block.state.BlockState b = ((CraftBlockState) block.createBlockData().createBlockState()).getHandle();
        net.minecraft.world.item.ItemStack handle = CraftItemStack.asNMSCopy(item);
        return handle.getDestroySpeed(b);
    }

    @Override
    public void setNoPhysics(Player player, boolean noPhysics) {
        ((CraftPlayer) player).getHandle().noPhysics = noPhysics;
    }

    @Override
    public void sendPhasingGamemodeUpdate(Player player, GameMode gameMode) {
        ServerPlayer serverPlayer = ((CraftPlayer) player).getHandle();
        GameType gameType = switch (gameMode) {
            case CREATIVE -> GameType.CREATIVE;
            case SURVIVAL -> GameType.SURVIVAL;
            case ADVENTURE -> GameType.ADVENTURE;
            case SPECTATOR -> GameType.SPECTATOR;
        };
        ClientboundPlayerInfoUpdatePacket.Entry entry = new ClientboundPlayerInfoUpdatePacket.Entry(serverPlayer.getUUID(), serverPlayer.getGameProfile(), true, 1, gameType, serverPlayer.getTabListDisplayName(), Optionull.map(serverPlayer.getChatSession(), RemoteChatSession::asData));
        ClientboundPlayerInfoUpdatePacket packet = new ClientboundPlayerInfoUpdatePacket(EnumSet.of(ClientboundPlayerInfoUpdatePacket.Action.UPDATE_GAME_MODE), entry);
        serverPlayer.connection.send(packet);
    }

    @Override
    @SuppressWarnings("deprecation")
    public void sendResourcePacks(Player player, String pack, Map<?, OriginsRebornResourcePackInfo> extraPacks) {
        player.setResourcePack(pack);
    }

    @Override
    public @NotNull PotionEffectType getNauseaEffect() {
        return PotionEffectType.CONFUSION;
    }

    @Override
    public @NotNull PotionEffectType getMiningFatigueEffect() {
        return PotionEffectType.SLOW_DIGGING;
    }

    @Override
    public @NotNull PotionEffectType getHasteEffect() {
        return PotionEffectType.FAST_DIGGING;
    }

    @Override
    public @NotNull Enchantment getUnbreakingEnchantment() {
        return Enchantment.DURABILITY;
    }

    @Override
    public @NotNull Enchantment getEfficiencyEnchantment() {
        return Enchantment.DIG_SPEED;
    }

    @Override
    public @NotNull PotionEffectType getJumpBoostEffect() {
        return PotionEffectType.JUMP;
    }

    @Override
    public @NotNull Enchantment getAquaAffinityEnchantment() {
        return Enchantment.WATER_WORKER;
    }

    @Override
    public @NotNull PotionEffectType getSlownessEffect() {
        return PotionEffectType.SLOW;
    }

    @Override
    public @NotNull Enchantment getBaneOfArthropodsEnchantment() {
        return Enchantment.DAMAGE_ARTHROPODS;
    }

    @Override
    public @NotNull PotionEffectType getStrengthEffect() {
        return PotionEffectType.INCREASE_DAMAGE;
    }

    @Override
    public @Nullable Location getRespawnLocation(Player player) {
        return player.getBedSpawnLocation();
    }

    @Override
    public void resetRespawnLocation(Player player) {
        player.setBedSpawnLocation(null);
    }

    @Override
    public @Nullable AttributeModifier getAttributeModifier(AttributeInstance instance, NamespacedKey key) {
        UUID u = UUID.nameUUIDFromBytes(key.toString().getBytes());
        for (AttributeModifier am : instance.getModifiers()) {
            if (am.getUniqueId().equals(u)) return am;
        }
        return null;
    }

    @Override
    public void addAttributeModifier(AttributeInstance instance, NamespacedKey key, String name, double amount, AttributeModifier.Operation operation) {
        instance.addModifier(new AttributeModifier(UUID.nameUUIDFromBytes(key.toString().getBytes()), name, amount, operation));
    }

    @Override
    public boolean isUnderWater(LivingEntity entity) {
        return entity.isUnderWater();
    }

    @Override
    public Attribute getBlockInteractionRangeAttribute() {
        return null;
    }

    @Override
    public Attribute getEntityInteractionRangeAttribute() {
        return null;
    }

    @Override
    public void knockback(LivingEntity entity, double strength, double x, double z) {
        entity.knockback(strength, x, z);
    }

    @Override
    public void dealDryOutDamage(LivingEntity entity, int amount) {
        net.minecraft.world.entity.LivingEntity livingEntity = ((CraftLivingEntity) entity).getHandle();
        livingEntity.hurt(livingEntity.damageSources().dryOut(), amount);
    }

    @Override
    public void dealFreezeDamage(LivingEntity entity , int amount) {
        net.minecraft.world.entity.LivingEntity livingEntity = ((CraftLivingEntity) entity).getHandle();
        livingEntity.hurt(livingEntity.damageSources().freeze(), amount);
    }

    @Override
    public void setFlyingFallDamage(Player player, TriState state) {
        player.setFlyingFallDamage(state);
    }

    @Override
    public void broadcastSlotBreak(Player player, EquipmentSlot slot, Collection<Player> players) {
        player.broadcastSlotBreak(slot, players);
    }

    @Override
    public void sendBlockDamage(Player player, Location location, float damage, Entity entity) {
        player.sendBlockDamage(location, damage, entity);
    }

    @Override
    public Attribute getBlockBreakSpeedAttribute() {
        return null;
    }
}