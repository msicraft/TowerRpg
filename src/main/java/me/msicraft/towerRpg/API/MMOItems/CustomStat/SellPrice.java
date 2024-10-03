package me.msicraft.towerRpg.API.MMOItems.CustomStat;

import io.lumine.mythic.lib.api.item.ItemTag;
import net.Indyuce.mmoitems.api.item.build.ItemStackBuilder;
import net.Indyuce.mmoitems.api.item.mmoitem.ReadMMOItem;
import net.Indyuce.mmoitems.gui.edition.EditionInventory;
import net.Indyuce.mmoitems.stat.data.random.RandomStatData;
import net.Indyuce.mmoitems.stat.data.type.StatData;
import net.Indyuce.mmoitems.stat.type.ItemStat;
import org.bukkit.Material;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class SellPrice extends ItemStat {

    public SellPrice() {
        super("sellprice", Material.PAPER, "판매 가격", new String[]{"아이템의 판매가격입니다."},
                new String[]{"all"});
    }

    @Override
    public RandomStatData whenInitialized(Object o) {
        return null;
    }

    @Override
    public void whenApplied(@NotNull ItemStackBuilder itemStackBuilder, @NotNull StatData statData) {
    }

    @NotNull
    @Override
    public ArrayList<ItemTag> getAppliedNBT(@NotNull StatData statData) {
        return null;
    }

    @Override
    public void whenClicked(@NotNull EditionInventory editionInventory, @NotNull InventoryClickEvent inventoryClickEvent) {

    }

    @Override
    public void whenInput(@NotNull EditionInventory editionInventory, @NotNull String s, Object... objects) {

    }

    @Override
    public void whenLoaded(@NotNull ReadMMOItem readMMOItem) {
    }

    @NotNull
    @Override
    public StatData getClearStatData() {
        return null;
    }

    @Override
    public void whenDisplayed(List list, Optional optional) {
    }

    @Nullable
    @Override
    public StatData getLoadedNBT(@NotNull ArrayList arrayList) {
        return null;
    }

}
