package me.msicraft.towerRpg.Command;

import me.msicraft.towerRpg.Shop.Data.ShopItem;
import me.msicraft.towerRpg.Shop.ShopManager;
import me.msicraft.towerRpg.TowerRpg;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

public class MainCommand implements CommandExecutor {

    private final TowerRpg plugin;

    public MainCommand(TowerRpg plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (command.getName().equals("towerrpg")) {
            if (args.length == 0) {
                sender.sendMessage(ChatColor.RED + "/towerrpg help");
                return true;
            }
            String var = args[0];
            if (var != null) {
                switch (var) {
                    case "reload" -> {
                        plugin.reloadVariables();
                        sender.sendMessage(ChatColor.GREEN + "플러그인 구성이 리로드되었습니다.");
                        return true;
                    }
                    case "shop" -> {
                        ShopManager shopManager = plugin.getShopManager();
                        try {
                            String var2 = args[1];
                            switch (var2) {
                                case "register" -> {
                                    if (sender instanceof Player player) {
                                        ItemStack itemStack = player.getInventory().getItemInMainHand();
                                        if (itemStack != null && itemStack.getType() != Material.AIR) {
                                            String internalName = args[2];
                                            double basePrice = Double.parseDouble(args[3]);
                                            if (shopManager.hasInternalName(internalName)) {
                                                player.sendMessage(ChatColor.RED + "이미 존재하는 내부이름입니다");
                                            } else {
                                                shopManager.addShopItem(internalName, new ShopItem(internalName, itemStack, basePrice));
                                                player.sendMessage(ChatColor.GREEN + "아이템이 등록되었습니다");
                                            }
                                        } else {
                                            player.sendMessage(ChatColor.RED + "공기는 등록할 수 없습니다");
                                        }
                                    }
                                    return true;
                                }
                                case "unregister" -> {
                                    String internalName = args[2];
                                    if (shopManager.hasInternalName(internalName)) {
                                        shopManager.removeShopItem(internalName);
                                    } else {
                                        sender.sendMessage(ChatColor.RED + "해당 내부이름이 존재하지 않습니다");
                                    }
                                    return true;
                                }
                            }
                        } catch (IllegalArgumentException | ArrayIndexOutOfBoundsException e) {
                            sender.sendMessage(ChatColor.RED + "/towerrpg shop [register|unregister] [internalName] [base_price]");
                            return true;
                        }
                        return true;
                    }
                }
            }
        }
        return false;
    }
}
