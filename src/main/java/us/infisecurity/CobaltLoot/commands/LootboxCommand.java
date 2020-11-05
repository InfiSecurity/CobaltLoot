package us.infisecurity.CobaltLoot.commands;

import net.minecraft.util.com.google.common.primitives.Ints;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import us.infisecurity.CobaltLoot.CobaltLoot;
import us.infisecurity.CobaltLoot.utils.Builder;
import us.infisecurity.CobaltLoot.utils.ColorUtils;

public class LootboxCommand implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String string, String[] args) {
            if (!sender.hasPermission("cobaltloot.give")) {
                sender.sendMessage(ColorUtils.Color("&cYou do not have permission to execute this command."));
                return true;
            }

            if (args.length == 3) {
                if (args[0].equalsIgnoreCase("give")) {
                    if (args[1].equalsIgnoreCase("all")) {
                        if (Ints.tryParse(args[2]) == null) {
                            sender.sendMessage(ColorUtils.Color("&cThe number you entered is invalid."));
                            return true;
                        }

                        int amount = Ints.tryParse(args[2]);

                        ItemStack box = Builder.nameItem(Material.valueOf(CobaltLoot.getInstance().getConfig().getString("OPTIONS.LOOTBOX-ITEM.MATERIAL")), CobaltLoot.getInstance().getConfig().getString("OPTIONS.LOOTBOX-ITEM.DISPLAY-NAME"), (short) CobaltLoot.getInstance().getConfig().getInt("OPTIONS.LOOTBOX-ITEM.ITEM-META"), amount, CobaltLoot.getInstance().getConfig().getStringList("OPTIONS.LOOTBOX-ITEM.LORES"));

                        for (Player target : Bukkit.getOnlinePlayers()) {
                            if (target.getInventory().firstEmpty() == -1) {
                                target.getWorld().dropItemNaturally(target.getLocation(), box);
                                target.sendMessage(ColorUtils.Color(CobaltLoot.getInstance().getConfig().getString("MESSAGES.LOOTBOX-RECEIVED").replace("%amount%", amount + "").replace("%player%", sender.getName())));
                                target.sendMessage(ColorUtils.Color(CobaltLoot.getInstance().getConfig().getString("MESSAGES.LOOTBOX-INVENTORY")));
                            } else {
                                target.getInventory().addItem(box);
                                target.sendMessage(ColorUtils.Color(CobaltLoot.getInstance().getConfig().getString("MESSAGES.LOOTBOX-RECEIVED").replace("%amount%", amount + "").replace("%player%", sender.getName())));
                            }
                        }
                    } else {
                        if (Bukkit.getPlayer(args[1]) == null) {
                            sender.sendMessage(ColorUtils.Color("&cA player with that name was not found."));
                            return true;
                        }

                        if (Ints.tryParse(args[2]) == null) {
                            sender.sendMessage(ColorUtils.Color("&cThe number you entered is invalid."));
                            return true;
                        }

                        Player target = Bukkit.getPlayer(args[1]);
                        int amount = Ints.tryParse(args[2]);

                        ItemStack box = Builder.nameItem(Material.valueOf(CobaltLoot.getInstance().getConfig().getString("OPTIONS.LOOTBOX-ITEM.MATERIAL")), CobaltLoot.getInstance().getConfig().getString("OPTIONS.LOOTBOX-ITEM.DISPLAY-NAME"), (short) CobaltLoot.getInstance().getConfig().getInt("OPTIONS.LOOTBOX-ITEM.ITEM-META"), amount, CobaltLoot.getInstance().getConfig().getStringList("OPTIONS.LOOTBOX-ITEM.LORES"));

                        if (target.getInventory().firstEmpty() == -1) {
                            target.getWorld().dropItemNaturally(target.getLocation(), box);
                            target.sendMessage(ColorUtils.Color(CobaltLoot.getInstance().getConfig().getString("MESSAGES.LOOTBOX-RECEIVED").replace("%amount%", amount + "").replace("%player%", sender.getName())));
                            target.sendMessage(ColorUtils.Color(CobaltLoot.getInstance().getConfig().getString("MESSAGES.LOOTBOX-INVENTORY")));
                        } else {
                            target.getInventory().addItem(box);
                            target.sendMessage(ColorUtils.Color(CobaltLoot.getInstance().getConfig().getString("MESSAGES.LOOTBOX-RECEIVED").replace("%amount%", amount + "").replace("%player%", sender.getName())));
                        }
                    }
                    return true;
                }
            }

            sender.sendMessage(ColorUtils.Color(CobaltLoot.getInstance().getConfig().getString("MESSAGES.LOOTBOX-USAGE")));
        
        return false;
    }

}
