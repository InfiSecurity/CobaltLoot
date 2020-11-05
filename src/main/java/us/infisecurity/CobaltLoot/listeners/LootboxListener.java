package us.infisecurity.CobaltLoot.listeners;

import net.minecraft.util.com.google.common.primitives.Ints;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import us.infisecurity.CobaltLoot.CobaltLoot;
import us.infisecurity.CobaltLoot.utils.Builder;
import us.infisecurity.CobaltLoot.utils.ColorUtils;

import java.util.*;

public class LootboxListener implements Listener {
    public static HashMap<UUID, Integer> OPENED_BOXES = new HashMap<>();
    public static ArrayList<String> REGULAR_ITEMS = new ArrayList<>();
    public static ArrayList<String> FINAL_ITEMS = new ArrayList<>();

    @EventHandler
    public void onRightClick(PlayerInteractEvent event) {
        if (event.getAction() != Action.RIGHT_CLICK_BLOCK && event.getAction() != Action.RIGHT_CLICK_AIR) return;
        if (event.getItem() == null) return;
        if (event.getItem().getType() == null) return;
        if (event.getItem().getType() != Material.valueOf(CobaltLoot.getInstance().getConfig().getString("OPTIONS.LOOTBOX-ITEM.MATERIAL")))
            return;
        if (event.getItem().getItemMeta() == null) return;
        if (event.getItem().getItemMeta().getDisplayName() == null) return;
        if (!event.getItem().getItemMeta().getDisplayName().equalsIgnoreCase(ColorUtils.Color(CobaltLoot.getInstance().getConfig().getString("OPTIONS.LOOTBOX-ITEM.DISPLAY-NAME"))))
            return;

        setupChances();

        event.getPlayer().sendMessage(ColorUtils.Color(CobaltLoot.getInstance().getConfig().getString("MESSAGES.LOOTBOX-OPEN")));

        Inventory inv = Bukkit.createInventory(null, 54, ColorUtils.Color(CobaltLoot.getInstance().getConfig().getString("MESSAGES.INVENTORY.TITLE")));

        ItemStack chest = Builder.nameItem(Material.CHEST, ColorUtils.Color(CobaltLoot.getInstance().getConfig().getString("MESSAGES.INVENTORY.SPECIAL-PRIZE.TITLE")), (short) 0, 1, Arrays.asList(CobaltLoot.getInstance().getConfig().getString("MESSAGES.INVENTORY.SPECIAL-PRIZE.LORE")));
        ItemStack enderchest = Builder.nameItem(Material.ENDER_CHEST, ColorUtils.Color(CobaltLoot.getInstance().getConfig().getString("MESSAGES.INVENTORY.COMMON-PRIZE.TITLE")), (short) 0, 1, Arrays.asList(CobaltLoot.getInstance().getConfig().getString("MESSAGES.INVENTORY.COMMON-PRIZE.LORE")));
        ItemStack spacer = Builder.nameItem(Material.STAINED_GLASS_PANE, ColorUtils.Color(" "), (short) 8, 1, Arrays.asList());

        for (int i = 0; i < 54; i++) {
            inv.setItem(i, spacer);
        }

        inv.setItem(12, chest);
        inv.setItem(13, chest);
        inv.setItem(14, chest);

        inv.setItem(21, chest);
        inv.setItem(22, chest);
        inv.setItem(23, chest);

        inv.setItem(30, chest);
        inv.setItem(31, chest);
        inv.setItem(32, chest);

        inv.setItem(40, enderchest);

        ItemStack box = Builder.nameItem(Material.valueOf(CobaltLoot.getInstance().getConfig().getString("OPTIONS.LOOTBOX-ITEM.MATERIAL")), CobaltLoot.getInstance().getConfig().getString("OPTIONS.LOOTBOX-ITEM.DISPLAY-NAME"), (short) CobaltLoot.getInstance().getConfig().getInt("OPTIONS.LOOTBOX-ITEM.ITEM-META"), 1, CobaltLoot.getInstance().getConfig().getStringList("OPTIONS.LOOTBOX-ITEM.LORES"));

        event.getPlayer().getInventory().removeItem(box);

        event.getPlayer().openInventory(inv);

    }

    @EventHandler
    public void place(BlockPlaceEvent event) {
        if (event.getBlockPlaced().getType() == Material.ENDER_CHEST && event.getPlayer().getGameMode() != GameMode.CREATIVE) {
            event.getPlayer().sendMessage(ColorUtils.Color(CobaltLoot.getInstance().getConfig().getString("MESSAGES.LOOTBOX-PLACE")));
            event.setCancelled(true);
        }

    }

    @EventHandler
    public void click(InventoryClickEvent event) {
        if (event.getSlotType() == InventoryType.SlotType.OUTSIDE) return;
        if (!event.getClickedInventory().getName().equalsIgnoreCase(ColorUtils.Color(CobaltLoot.getInstance().getConfig().getString("MESSAGES.INVENTORY.TITLE")))) return;
        if (event.getCurrentItem() == null) return;
        if (event.getCurrentItem().getType() == null) return;
        if (event.getCurrentItem().getItemMeta() == null) return;
        if (event.getCurrentItem().getItemMeta().getDisplayName() == null) return;

        if (event.getCurrentItem().getType() == Material.CHEST) {
            event.setCancelled(true);
            Random ran = new Random();
            int rewardCount = REGULAR_ITEMS.size();
            int random = ran.nextInt(rewardCount);

            int chance = CobaltLoot.getInstance().getConfig().getInt("REWARDS.REGULAR-ITEMS." + REGULAR_ITEMS.get(random) + ".CHANCE");
            String displayName = CobaltLoot.getInstance().getConfig().getString("REWARDS.REGULAR-ITEMS." + REGULAR_ITEMS.get(random) + ".DISPLAY-NAME");
            Material material = Material.valueOf(CobaltLoot.getInstance().getConfig().getString("REWARDS.REGULAR-ITEMS." + REGULAR_ITEMS.get(random) + ".MATERIAL"));
            List<String> commands = CobaltLoot.getInstance().getConfig().getStringList("REWARDS.REGULAR-ITEMS." + REGULAR_ITEMS.get(random) + ".COMMANDS");
            int amount = CobaltLoot.getInstance().getConfig().getInt("REWARDS.REGULAR-ITEMS." + REGULAR_ITEMS.get(random) + ".AMOUNT");
            short meta = (short) CobaltLoot.getInstance().getConfig().getInt("REWARDS.REGULAR-ITEMS." + REGULAR_ITEMS.get(random) + ".ITEM-META");
            List<String> enchants = CobaltLoot.getInstance().getConfig().getStringList("REWARDS.REGULAR-ITEMS." + REGULAR_ITEMS.get(random) + ".ENCHANTS");
            List<String> lores = CobaltLoot.getInstance().getConfig().getStringList("REWARDS.REGULAR-ITEMS." + REGULAR_ITEMS.get(random) + ".LORES");

            ItemStack reward = Builder.nameItem(material, displayName, meta, amount, lores);

            if (enchants.size() != 0) {
                for (String str : enchants) {
                    Enchantment enchantment = Enchantment.getByName(str.split(":")[0]);
                    int level = Ints.tryParse(str.split(":")[1]);

                    reward.addUnsafeEnchantment(enchantment, level);
                }
            }

            event.getClickedInventory().setItem(event.getSlot(), reward);

            for (String str : commands) {
                Bukkit.dispatchCommand(Bukkit.getConsoleSender(), str.replace("%player%", event.getWhoClicked().getName()));
            }

            Player player = (Player) event.getWhoClicked();
            player.sendMessage(ColorUtils.Color(CobaltLoot.getInstance().getConfig().getString("MESSAGES.LOOTBOX-REWARDS").replace("%reward%", displayName)));

            player.playSound(player.getLocation(), Sound.NOTE_BASS, 5, 5);
            if (OPENED_BOXES.containsKey(event.getWhoClicked().getUniqueId())) {
                OPENED_BOXES.put(event.getWhoClicked().getUniqueId(), OPENED_BOXES.get(event.getWhoClicked().getUniqueId()) + 1);
            } else {
                OPENED_BOXES.put(event.getWhoClicked().getUniqueId(), 1);
            }
        } else if (event.getCurrentItem().getType() == Material.ENDER_CHEST) {
            event.setCancelled(true);
            if (OPENED_BOXES.containsKey(event.getWhoClicked().getUniqueId()) && OPENED_BOXES.get(event.getWhoClicked().getUniqueId()) < 9) {
                ((Player) event.getWhoClicked()).sendMessage(ColorUtils.Color(CobaltLoot.getInstance().getConfig().getString("MESSAGES.LOOTBOX-OPEN-BEFORE")));
                return;
            }
            Random ran = new Random();
            int rewardCount = FINAL_ITEMS.size();
            int random = ran.nextInt(rewardCount);

            int chance = CobaltLoot.getInstance().getConfig().getInt("REWARDS.FINAL-ITEMS." + FINAL_ITEMS.get(random) + ".CHANCE");
            String displayName = CobaltLoot.getInstance().getConfig().getString("REWARDS.FINAL-ITEMS." + FINAL_ITEMS.get(random) + ".DISPLAY-NAME");
            Material material = Material.valueOf(CobaltLoot.getInstance().getConfig().getString("REWARDS.FINAL-ITEMS." + FINAL_ITEMS.get(random) + ".MATERIAL"));
            List<String> commands = CobaltLoot.getInstance().getConfig().getStringList("REWARDS.FINAL-ITEMS." + FINAL_ITEMS.get(random) + ".COMMANDS");
            int amount = CobaltLoot.getInstance().getConfig().getInt("REWARDS.FINAL-ITEMS." + FINAL_ITEMS.get(random) + ".AMOUNT");
            short meta = (short) CobaltLoot.getInstance().getConfig().getInt("REWARDS.FINAL-ITEMS." + FINAL_ITEMS.get(random) + ".ITEM-META");
            List<String> enchants = CobaltLoot.getInstance().getConfig().getStringList("REWARDS.FINAL-ITEMS." + FINAL_ITEMS.get(random) + ".ENCHANTS");
            List<String> lores = CobaltLoot.getInstance().getConfig().getStringList("REWARDS.FINAL-ITEMS." + FINAL_ITEMS.get(random) + ".LORES");

            ItemStack reward = Builder.nameItem(material, displayName, meta, amount, lores);

            if (enchants.size() != 0) {
                for (String str : enchants) {
                    Enchantment enchantment = Enchantment.getByName(str.split(":")[0]);
                    int level = Ints.tryParse(str.split(":")[1]);

                    reward.addUnsafeEnchantment(enchantment, level);
                }
            }

            event.getClickedInventory().setItem(event.getSlot(), reward);

            for (String str : commands) {
                Bukkit.dispatchCommand(Bukkit.getConsoleSender(), str.replace("%player%", event.getWhoClicked().getName()));
            }

            Player player = (Player) event.getWhoClicked();
            player.sendMessage(ColorUtils.Color(CobaltLoot.getInstance().getConfig().getString("MESSAGES.LOOTBOX-REWARDS").replace("%reward%", displayName)));
            OPENED_BOXES.remove(player.getUniqueId());
            player.playSound(player.getLocation(), Sound.NOTE_BASS, 5, 5);
        } else {
            event.setCancelled(true);
        }

    }

    @EventHandler
    public void onClose(InventoryCloseEvent event) {
        if (event.getInventory().getName().equalsIgnoreCase(ColorUtils.Color(CobaltLoot.getInstance().getConfig().getString("MESSAGES.INVENTORY.TITLE")))) {
            if (OPENED_BOXES.containsKey(event.getPlayer().getUniqueId())) {
                OPENED_BOXES.remove(event.getPlayer().getUniqueId());
            }
        }

    }

    public void setupChances() {
        for (String str : CobaltLoot.getInstance().getConfig().getConfigurationSection("REWARDS.REGULAR-ITEMS").getKeys(false)) {
            int chance = CobaltLoot.getInstance().getConfig().getInt("REWARDS.REGULAR-ITEMS." + str + ".CHANCE");

            for (int i = 0; i < chance; i++) {
                REGULAR_ITEMS.add(str);
            }
        }

        for (String str : CobaltLoot.getInstance().getConfig().getConfigurationSection("REWARDS.FINAL-ITEMS").getKeys(false)) {
            int chance = CobaltLoot.getInstance().getConfig().getInt("REWARDS.FINAL-ITEMS." + str + ".CHANCE");

            for (int i = 0; i < chance; i++) {
                FINAL_ITEMS.add(str);
            }
        }

    }

}
