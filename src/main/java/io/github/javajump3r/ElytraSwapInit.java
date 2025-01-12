package io.github.javajump3r;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.item.ArmorItem;
import net.minecraft.item.ElytraItem;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.network.packet.c2s.play.ClientCommandC2SPacket;
import net.minecraft.screen.slot.SlotActionType;


public class ElytraSwapInit implements ClientModInitializer {

    public static void tryWearChestplate(MinecraftClient client) {
        if (client.world == null || client.player == null) {
            return;
        }

        if (!client.player.isOnGround() || isSlotChestplate(client, 38)) {
            return;
        }

        for (int slot : slotArray()) {
            if (isSlotChestplate(client, slot)) {
                swap(slot, client);
                return;
            }
        }
    }

    public static void tryWearElytra(MinecraftClient client) {
        if (client.world == null || client.player == null) {
            return;
        }

        if (client.player.getInventory().getStack(38).getItem() == Items.ELYTRA) {
            return;
        }

        for (int slot : slotArray()) {
            if (client.player.getInventory().getStack(slot).getItem() instanceof ElytraItem) {
                wearElytra(slot, client);
                return;
            }
        }
    }


    private static void wearElytra(int slotId, MinecraftClient client) {
        swap(slotId, client);
        try {
            client.getNetworkHandler().sendPacket(new ClientCommandC2SPacket(client.player, ClientCommandC2SPacket.Mode.START_FALL_FLYING));
            client.player.startFallFlying();
        } catch (NullPointerException ex) {
            ex.printStackTrace();
        }
    }


    private static void swap(int slot, MinecraftClient client) {
        int slot2 = slot;
        if (slot2 == 40) slot2 = 45;
        if (slot2 < 9) slot2 += 36;

        try {
            client.interactionManager.clickSlot(0, slot2, 0, SlotActionType.PICKUP, client.player);
            client.interactionManager.clickSlot(0, 6, 0, SlotActionType.PICKUP, client.player);
            client.interactionManager.clickSlot(0, slot2, 0, SlotActionType.PICKUP, client.player);
        } catch (NullPointerException ex) {
            ex.printStackTrace();
        }
    }

    public static boolean isSlotChestplate(MinecraftClient client, int slotId) {

        if (client.player == null) {
            return false;
        }
        ItemStack chestSlot = client.player.getInventory().getStack(slotId);

        return !chestSlot.isEmpty() &&
                chestSlot.getItem() instanceof ArmorItem &&
                ((ArmorItem) chestSlot.getItem()).getSlotType() == EquipmentSlot.CHEST;
    }

    private static int[] slotArray() {
        int[] range = new int[37];
        for (int i = 0; i < 9; i++) range[i] = 8 - i;
        for (int i = 9; i < 36; i++) range[i] = 35 - (i - 9);
        range[36] = 40;
        return range;
    }

    public void onInitializeClient() {
        ClientTickEvents.END_CLIENT_TICK.register(ElytraSwapInit::tryWearChestplate);
    }

}
