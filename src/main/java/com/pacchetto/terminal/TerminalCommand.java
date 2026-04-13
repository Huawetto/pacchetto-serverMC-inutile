package com.pacchetto.terminal;

import com.pacchetto.core.ServerMCPlugin;
import com.pacchetto.items.CustomItem;
import com.pacchetto.recipe.MachineRecipe;
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

import java.util.ArrayList;
import java.util.List;

public class TerminalCommand implements CommandExecutor, TabCompleter, Listener {
    private static final String GUIDE_MAIN = ChatColor.DARK_AQUA + "FUN • Guida";
    private static final String GUIDE_MACHINES = ChatColor.DARK_AQUA + "FUN • Macchine";
    private static final String GUIDE_COMPONENTS = ChatColor.DARK_AQUA + "FUN • Componenti";
    private static final String GUIDE_ENERGY = ChatColor.DARK_AQUA + "FUN • Energia&Cargo";
    private static final String GUIDE_RECIPE = ChatColor.DARK_AQUA + "FUN • Ricetta";
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
            case "guide" -> openGuideMain(sender);
            case "cheat" -> openCheat(sender);
            default -> sender.sendMessage(ChatColor.RED + "Comando sconosciuto. Usa /fun guide oppure /fun cheat");
        }
        return true;
    }

    private void openGuideMain(CommandSender sender) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage(ChatColor.RED + "Solo i player possono aprire la guida.");
            return;
        }
        Inventory inv = Bukkit.createInventory(player, 45, GUIDE_MAIN);
        fillBackground(inv, Material.CYAN_STAINED_GLASS_PANE, " ");
        inv.setItem(20, info(Material.PISTON, ChatColor.AQUA + "Macchine", "Generator, Processor, Advanced Furnace", "Click per aprire"));
        inv.setItem(22, info(Material.REPEATER, ChatColor.AQUA + "Componenti", "CPU, RAM, Moduli e parti", "Click per aprire"));
        inv.setItem(24, info(Material.LIGHTNING_ROD, ChatColor.AQUA + "Energia & Cargo", "Nodi energia, canali cargo", "Click per aprire"));
        inv.setItem(40, info(Material.BOOK, ChatColor.YELLOW + "Navigazione", "Click su categoria per sottocategorie/item craftabili"));
        player.openInventory(inv);
    }

    private void openMachinesGuide(Player player) {
        Inventory inv = Bukkit.createInventory(player, 54, GUIDE_MACHINES);
        fillBackground(inv, Material.GRAY_STAINED_GLASS_PANE, " ");
        putCraftable(inv, 10, "machine_generator", "Produce energia costante");
        putCraftable(inv, 12, "machine_processor", "Processa ricette custom");
        putCraftable(inv, 14, "machine_furnace", "Smelting avanzato");
        inv.setItem(49, backButton());
        player.openInventory(inv);
    }

    private void openComponentsGuide(Player player) {
        Inventory inv = Bukkit.createInventory(player, 54, GUIDE_COMPONENTS);
        fillBackground(inv, Material.LIGHT_BLUE_STAINED_GLASS_PANE, " ");
        String[] ids = {"part_cpu", "part_ram", "part_ssd", "part_gpu", "part_bus", "energy_cell", "module_ai", "module_quantum"};
        int slot = 10;
        for (String id : ids) {
            putCraftable(inv, slot++, id, "Componente craftabile");
            if (slot % 9 == 8) slot += 2;
        }
        inv.setItem(49, backButton());
        player.openInventory(inv);
    }

    private void openEnergyGuide(Player player) {
        Inventory inv = Bukkit.createInventory(player, 45, GUIDE_ENERGY);
        fillBackground(inv, Material.YELLOW_STAINED_GLASS_PANE, " ");
        inv.setItem(20, info(Material.LIGHTNING_ROD, ChatColor.GOLD + "Energy Network", "Connessioni automatiche tra nodi vicini", "Bilanciamento ogni ciclo"));
        inv.setItem(22, info(Material.HOPPER, ChatColor.GOLD + "Cargo", "20 canali con priorità", "Input/Output automatici"));
        inv.setItem(24, info(Material.COMPARATOR, ChatColor.GOLD + "AI", "Ottimizza distribuzione energia", "Prioritizza macchine più cariche"));
        inv.setItem(40, backButton());
        player.openInventory(inv);
    }

    private void openRecipeDetail(Player player, String itemId) {
        Inventory inv = Bukkit.createInventory(player, 45, GUIDE_RECIPE);
        fillBackground(inv, Material.BLACK_STAINED_GLASS_PANE, " ");
        ItemStack out = plugin.getItemRegistry().get(itemId);
        if (out != null) inv.setItem(13, out);
        MachineRecipe recipe = plugin.getRecipeRegistry().findByOutput(itemId).orElse(null);
        if (recipe == null) {
            inv.setItem(22, info(Material.BARRIER, ChatColor.RED + "Ricetta non registrata", "Questo item non ha recipe nel registro."));
        } else {
            List<String> lore = new ArrayList<>();
            lore.add("ID: " + recipe.id());
            lore.add("Tempo: " + recipe.ticks() + " ticks");
            lore.add("Energia/tick: " + recipe.energyPerTick());
            lore.add("Input: " + String.join(", ", recipe.inputIds()));
            inv.setItem(22, info(Material.CRAFTING_TABLE, ChatColor.GREEN + "Dettagli Ricetta", lore.toArray(String[]::new)));
        }
        inv.setItem(40, backButton());
        player.openInventory(inv);
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
        fillBackground(cheat, Material.RED_STAINED_GLASS_PANE, ChatColor.DARK_GRAY + "Click su un item per riceverlo");
        player.openInventory(cheat);
    }

    private void putCraftable(Inventory inv, int slot, String itemId, String note) {
        ItemStack item = plugin.getItemRegistry().get(itemId);
        if (item == null) return;
        ItemMeta meta = item.getItemMeta();
        List<String> lore = meta.hasLore() ? new ArrayList<>(meta.getLore()) : new ArrayList<>();
        lore.add(ChatColor.GREEN + "Click per dettaglio ricetta");
        lore.add(ChatColor.GRAY + note);
        meta.setLore(lore);
        item.setItemMeta(meta);
        inv.setItem(slot, item);
    }

    private ItemStack backButton() {
        return info(Material.ARROW, ChatColor.YELLOW + "Indietro", "Torna al menu guida");
    }

    private ItemStack info(Material material, String title, String... lore) {
        ItemStack stack = new ItemStack(material);
        ItemMeta meta = stack.getItemMeta();
        meta.setDisplayName(title);
        meta.setLore(List.of(lore).stream().map(s -> ChatColor.GRAY + s).toList());
        stack.setItemMeta(meta);
        return stack;
    }

    private void fillBackground(Inventory inv, Material paneType, String name) {
        for (int i = 0; i < inv.getSize(); i++) {
            if (inv.getItem(i) != null) continue;
            ItemStack pane = new ItemStack(paneType);
            ItemMeta meta = pane.getItemMeta();
            meta.setDisplayName(name);
            pane.setItemMeta(meta);
            inv.setItem(i, pane);
        }
    }

    @EventHandler
    public void onGuiClick(InventoryClickEvent event) {
        String title = event.getView().getTitle();
        if (!(event.getWhoClicked() instanceof Player player)) return;

        if (!title.startsWith(ChatColor.DARK_AQUA + "FUN •") && !title.equals(CHEAT_TITLE)) return;
        if (event.getRawSlot() >= event.getView().getTopInventory().getSize()) return;
        event.setCancelled(true);

        ItemStack clicked = event.getCurrentItem();
        if (clicked == null || clicked.getType() == Material.AIR) return;

        if (title.equals(GUIDE_MAIN)) {
            Material type = clicked.getType();
            if (type == Material.PISTON) openMachinesGuide(player);
            if (type == Material.REPEATER) openComponentsGuide(player);
            if (type == Material.LIGHTNING_ROD) openEnergyGuide(player);
            return;
        }

        if (title.equals(GUIDE_MACHINES) || title.equals(GUIDE_COMPONENTS)) {
            if (clicked.getType() == Material.ARROW) {
                openGuideMain(player);
                return;
            }
            String id = plugin.getItemRegistry().id(clicked);
            if (id != null) {
                openRecipeDetail(player, id);
            }
            return;
        }

        if (title.equals(GUIDE_ENERGY) || title.equals(GUIDE_RECIPE)) {
            if (clicked.getType() == Material.ARROW) {
                openGuideMain(player);
            }
            return;
        }

        if (title.equals(CHEAT_TITLE)) {
            if (!player.isOp()) return;
            if (plugin.getItemRegistry().isCustom(clicked)) {
                player.getInventory().addItem(clicked.clone());
                player.sendMessage(ChatColor.GREEN + "Item ricevuto: " + clicked.getItemMeta().getDisplayName());
            }
        }
    }

    @EventHandler
    public void onGuiDrag(InventoryDragEvent event) {
        String title = event.getView().getTitle();
        if (title.startsWith(ChatColor.DARK_AQUA + "FUN •") || title.equals(CHEAT_TITLE)) {
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
