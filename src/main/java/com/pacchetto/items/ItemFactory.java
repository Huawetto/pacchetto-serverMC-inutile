package com.pacchetto.items;

import com.destroystokyo.paper.profile.PlayerProfile;
import com.destroystokyo.paper.profile.ProfileProperty;
import com.pacchetto.core.ServerMCPlugin;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.persistence.PersistentDataType;

import java.util.List;
import java.util.UUID;

public class ItemFactory {
    private final NamespacedKey idKey;

    public ItemFactory(ServerMCPlugin plugin) {
        this.idKey = new NamespacedKey(plugin, "item_id");
    }

    public ItemStack createTechHead(String id, String display, List<String> lore, String texture) {
        ItemStack stack = new ItemStack(Material.PLAYER_HEAD);
        SkullMeta meta = (SkullMeta) stack.getItemMeta();
        PlayerProfile profile = Bukkit.createProfile(UUID.randomUUID());
        profile.setProperty(new ProfileProperty("textures", texture));
        meta.setPlayerProfile(profile);
        meta.setDisplayName(ChatColor.AQUA + display);
        meta.setLore(lore.stream().map(s -> ChatColor.GRAY + s).toList());
        meta.getPersistentDataContainer().set(idKey, PersistentDataType.STRING, id);
        meta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES, ItemFlag.HIDE_POTION_EFFECTS, ItemFlag.HIDE_ENCHANTS);
        stack.setItemMeta(meta);
        return stack;
    }

    public boolean isCustom(ItemStack stack) {
        if (stack == null || stack.getType() != Material.PLAYER_HEAD || !stack.hasItemMeta()) return false;
        ItemMeta meta = stack.getItemMeta();
        return meta.getPersistentDataContainer().has(idKey, PersistentDataType.STRING);
    }

    public String id(ItemStack stack) {
        if (!isCustom(stack)) return null;
        return stack.getItemMeta().getPersistentDataContainer().get(idKey, PersistentDataType.STRING);
    }
}
