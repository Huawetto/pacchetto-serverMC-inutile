package com.pacchetto.terminal;

import com.pacchetto.core.ServerMCPlugin;
import com.pacchetto.items.CustomItem;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.List;

public class TerminalCommand implements CommandExecutor, TabCompleter, Listener {
    private static final String GUIDE_TITLE = ChatColor.DARK_AQUA + "FUN • Guida Tecnica";
    private static final String CHEAT_TITLE = ChatColor.DARK_RED + "FUN • Cheat Vault";

    private final ServerMCPlugin plugin;

    public TerminalCommand(ServerMCPlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (args.length == 0) {
            sender.sendMessage(ChatColor.AQUA + "/fun <guide|cheat>");
            return true;
        }
        switch (args[0].toLowerCase()) {
            case "guide" -> openGuide(sender);
            case "cheat" -> openCheat(sender);
            default -> sender.sendMessage(ChatColor.RED + "Comando sconosciuto. Usa /fun guide oppure /fun cheat");
        }
        return true;
    }

    private void openGuide(CommandSender sender) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage(ChatColor.RED + "Solo i player possono aprire la guida.");
            return;
        }
        Inventory guide = Bukkit.createInventory(player, 45, GUIDE_TITLE);
        fillBorder(guide, Material.GRAY_STAINED_GLASS_PANE, " ");
        guide.setItem(11, info(Material.REDSTONE_BLOCK, "Generatore", "Produce energia nella rete", "Supporta crescita con livelli"));
        guide.setItem(13, info(Material.SMITHING_TABLE, "Processor", "Esegue ricette custom", "Usa input multipli + energia"));
        guide.setItem(15, info(Material.BLAST_FURNACE, "Advanced Furnace", "Smelting vanilla accelerato", "Consumo energetico dinamico"));
        guide.setItem(20, info(Material.ENDER_CHEST, "Cargo", "20 canali di routing", "Priorità + filtri"));
        guide.setItem(22, info(Material.LIGHTNING_ROD, "Energy Network", "Nodi per location", "Bilanciamento automatico"));
        guide.setItem(24, info(Material.COMMAND_BLOCK, "Comandi", "/fun guide", "/fun cheat (solo OP)"));
        guide.setItem(31, info(Material.BOOK, "Nota", "Questa GUI è di sola consultazione", "Nessun oggetto è prelevabile"));
        player.openInventory(guide);
    }

    private void openCheat(CommandSender sender) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage(ChatColor.RED + "Solo i player possono usare /fun cheat");
            return;
        }
        if (!player.isOp()) {
            player.sendMessage(ChatColor.RED + "Permesso negato: /fun cheat è riservato agli OP.");
            return;
        }
        Inventory cheat = Bukkit.createInventory(player, 54, CHEAT_TITLE);
        int slot = 0;
        for (CustomItem customItem : plugin.getItemRegistry().allItems()) {
            if (slot >= 45) break;
            cheat.setItem(slot++, customItem.stack().clone());
        }
        fillBorder(cheat, Material.BLACK_STAINED_GLASS_PANE, ChatColor.DARK_GRAY + "Click su un item per riceverlo");
        player.openInventory(cheat);
    }

    private ItemStack info(Material material, String title, String... lore) {
        ItemStack stack = new ItemStack(material);
        ItemMeta meta = stack.getItemMeta();
        meta.setDisplayName(ChatColor.AQUA + title);
        meta.setLore(List.of(lore).stream().map(s -> ChatColor.GRAY + s).toList());
        stack.setItemMeta(meta);
        return stack;
    }

    private void fillBorder(Inventory inv, Material paneType, String name) {
        for (int i = 0; i < inv.getSize(); i++) {
            boolean border = i < 9 || i >= inv.getSize() - 9 || i % 9 == 0 || i % 9 == 8;
            if (!border || inv.getItem(i) != null) continue;
            ItemStack pane = new ItemStack(paneType);
            ItemMeta meta = pane.getItemMeta();
            meta.setDisplayName(name);
            pane.setItemMeta(meta);
            inv.setItem(i, pane);
        }
    }

    @EventHandler
    public void onGuiClick(InventoryClickEvent event) {
        if (event.getView().getTitle().equals(GUIDE_TITLE)) {
            event.setCancelled(true);
            return;
        }
        if (event.getView().getTitle().equals(CHEAT_TITLE)) {
            event.setCancelled(true);
            if (!(event.getWhoClicked() instanceof Player player)) return;
            if (event.getCurrentItem() == null || event.getCurrentItem().getType() == Material.AIR) return;
            if (!player.isOp()) return;
            ItemStack clicked = event.getCurrentItem();
            if (plugin.getItemRegistry().isCustom(clicked)) {
                player.getInventory().addItem(clicked.clone());
                player.sendMessage(ChatColor.GREEN + "Item ricevuto: " + clicked.getItemMeta().getDisplayName());
            }
        }
    }

    @EventHandler
    public void onGuiDrag(InventoryDragEvent event) {
        if (event.getView().getTitle().equals(GUIDE_TITLE) || event.getView().getTitle().equals(CHEAT_TITLE)) {
            event.setCancelled(true);
        }
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        if (args.length == 1) {
            return List.of("guide", "cheat");
        }
        return List.of();
    }
}
