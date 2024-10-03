package me.msicraft.towerRpg.Command;

import me.msicraft.towerRpg.TowerRpg;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class MainTabCompleter implements TabCompleter {

    private final TowerRpg plugin;

    public MainTabCompleter(TowerRpg plugin) {
        this.plugin = plugin;
    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String s, @NotNull String[] args) {
        if (command.getName().equals("towerrpg")) {
            if (args.length == 1) {
                if (sender.isOp()) {
                    return List.of("reload", "shop", "skillbook");
                }
            }
            if (args.length == 2) {
                if (sender.isOp()) {
                    String var = args[0];
                    if (var.equalsIgnoreCase("shop")) {
                        return List.of("register", "unregister");
                    } else if (var.equalsIgnoreCase("skillbook")) {
                        return plugin.getSkillBookManager().getSkillIdsToList();
                    }
                }
            }
            if (args.length == 3) {
                if (sender.isOp()) {
                    String var = args[0];
                    String var2 = args[1];
                    if (var.equalsIgnoreCase("skillbook")) {
                        return List.of("<amount>");
                    }
                    if (var2.equalsIgnoreCase("unregister")) {
                        return plugin.getShopManager().getInternalNameList();
                    }
                }
            }
            if (args.length == 4) {
                if (sender.isOp()) {
                    String var2 = args[1];
                }
            }
        }
        return null;
    }
}
