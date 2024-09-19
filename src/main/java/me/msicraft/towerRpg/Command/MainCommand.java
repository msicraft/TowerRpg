package me.msicraft.towerRpg.Command;

import me.msicraft.towerRpg.Shop.Data.ShopItem;
import me.msicraft.towerRpg.Shop.ShopManager;
import me.msicraft.towerRpg.SkillBook.Data.SkillBook;
import me.msicraft.towerRpg.TowerRpg;
import net.Indyuce.mmocore.MMOCore;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
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
                    case "test" -> {
                        MMOCore.plugin.skillManager.getAll().forEach(registeredSkill -> {
                            System.out.println("Id: " + registeredSkill.getHandler().getId() + " Name: " + registeredSkill.getName()
                                    + " Lore: " + registeredSkill.getLore());
                        });
                    }
                    case "skillbook" -> {
                        try {
                            String skillBookId = args[1];
                            int amount = Integer.parseInt(args[2]);
                            Player target = Bukkit.getPlayer(args[3]);
                            if (target == null) {
                                if (sender instanceof Player p) {
                                    target = p;
                                } else {
                                    sender.sendMessage(ChatColor.RED + "대상 플레이어가 존재하지않습니다.");
                                    return false;
                                }
                            }
                            SkillBook skillBook = plugin.getSkillBookManager().getSkillBook(skillBookId);
                            if (skillBook == null) {
                                sender.sendMessage(ChatColor.RED + "해당 스킬북이 존재하지 않습니다.");
                                return false;
                            }
                            ItemStack itemStack = skillBook.getItemStack();
                            for (int i = 0; i < amount; i++) {
                                target.getInventory().addItem(itemStack);
                            }
                            return true;
                        } catch (ArrayIndexOutOfBoundsException | NumberFormatException  e) {
                            sender.sendMessage(ChatColor.RED + "/towerrpg skillbook <skillBookId> <amount> <target>");
                            return false;
                        }
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
                                                return false;
                                            } else {
                                                FileConfiguration config = shopManager.getShopDataFile().getConfig();
                                                ShopItem shopItem = new ShopItem(internalName, itemStack, basePrice);
                                                shopManager.addShopItem(internalName, shopItem);
                                                String path = "Items." + shopItem.getId();
                                                config.set(path + ".ItemStack", shopItem.getItemStack());
                                                config.set(path + ".Stock", shopItem.getStock());
                                                config.set(path + ".BasePrice", shopItem.getBasePrice());
                                                config.set(path + ".Price", shopItem.getPrice(false));
                                                config.set(path + ".BuyQuantity", shopItem.getBuyQuantity());
                                                config.set(path + ".SellQuantity", shopItem.getSellQuantity());
                                                shopManager.getShopDataFile().saveConfig();
                                                player.sendMessage(ChatColor.GREEN + "아이템이 등록되었습니다");
                                                return true;
                                            }
                                        } else {
                                            player.sendMessage(ChatColor.RED + "공기는 등록할 수 없습니다");
                                            return false;
                                        }
                                    }
                                    return false;
                                }
                                case "unregister" -> {
                                    String internalName = args[2];
                                    if (shopManager.hasInternalName(internalName)) {
                                        shopManager.removeShopItem(internalName);
                                        shopManager.getShopDataFile().getConfig().set("Items." + internalName, null);
                                        shopManager.getShopDataFile().saveConfig();
                                        sender.sendMessage(ChatColor.GREEN + "상점 아이템이 제거되었습니다.");
                                        return true;
                                    } else {
                                        sender.sendMessage(ChatColor.RED + "해당 내부이름이 존재하지 않습니다");
                                        return false;
                                    }
                                }
                            }
                        } catch (IllegalArgumentException | ArrayIndexOutOfBoundsException e) {
                            sender.sendMessage(ChatColor.RED + "/towerrpg shop [register|unregister] [internalName] [base_price]");
                            return false;
                        }
                        return false;
                    }
                }
            }
        }
        return false;
    }
}
