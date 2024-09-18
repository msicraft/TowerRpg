package me.msicraft.towerRpg.SkillBook.Data;

import me.msicraft.towerRpg.TowerRpg;
import me.msicraft.towerRpg.Utils.GuiUtil;
import net.Indyuce.mmocore.skill.RegisteredSkill;
import net.kyori.adventure.text.Component;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;

import java.util.ArrayList;
import java.util.List;

public class SkillBook {

    private final String id;
    private RegisteredSkill registeredSkill;
    private ItemStack itemStack = GuiUtil.AIR_STACK;

    public static final NamespacedKey SKILL_BOOK_ID_KEY = new NamespacedKey(TowerRpg.getPlugin(), "SkillBook_ID");

    public SkillBook(RegisteredSkill registeredSkill) {
        this.id = registeredSkill.getHandler().getId();

        update(registeredSkill);
    }

    public void update(RegisteredSkill registeredSkill) {
        this.registeredSkill = registeredSkill;
        this.itemStack = createItemStack(registeredSkill);
    }

    private ItemStack createItemStack(RegisteredSkill registeredSkill) {
        ItemStack itemStack = new ItemStack(Material.BOOK);
        ItemMeta itemMeta = itemStack.getItemMeta();
        PersistentDataContainer dataContainer = itemMeta.getPersistentDataContainer();
        dataContainer.set(SKILL_BOOK_ID_KEY, PersistentDataType.STRING, id);

        itemMeta.displayName(Component.text(registeredSkill.getName() + ChatColor.GOLD + " (스킬 북)"));
        List<String> lore = registeredSkill.getLore();
        List<Component> list = new ArrayList<>();
        list.add(Component.text(ChatColor.YELLOW + "스킬 이름: " + registeredSkill.getName()));
        list.add(Component.text(ChatColor.YELLOW + "우 클릭: 스킬 배우기(레벨 업)"));
        list.add(Component.text(""));
        for (String s : lore) {
            list.add(Component.text(s));
        }
        itemMeta.lore(list);
        itemMeta.setFireResistant(true);
        itemMeta.setMaxStackSize(1);
        itemMeta.addEnchant(Enchantment.UNBREAKING, 1, true);
        itemMeta.addItemFlags(ItemFlag.HIDE_ENCHANTS);

        itemStack.setItemMeta(itemMeta);
        return itemStack;
    }

    public String getId() {
        return id;
    }

    public RegisteredSkill getRegisteredSkill() {
        return registeredSkill;
    }

    public ItemStack getItemStack() {
        return itemStack;
    }

}
