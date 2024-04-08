package bymrshocker.swp.commands.args;

import bymrshocker.swp.ShockerWeaponsPlugin;
import bymrshocker.swp.commands.BaseCommandArg;
import de.tr7zw.nbtapi.NBT;
import de.tr7zw.nbtapi.iface.ReadWriteNBT;
import net.kyori.adventure.text.format.TextDecoration;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.bukkit.Material;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.HashMap;
import java.util.List;
import java.util.Set;

public class swd_give implements BaseCommandArg {

    HashMap<String, Set<String>> itemList = new HashMap<>();

    @Override
    public String[] getTabList(String key) {
        argListMap.put("give", new String[] {"arg1", "arg2"});


        if (!argListMap.containsKey(key)) return new String[0];
        return argListMap.get(key);
    }

    @Override
    public String getName() { return "give"; }

    @Override
    public String getDescription() { return "Giving SWD item"; }

    @Override
    public String getSyntax() { return "/swd give"; }

    @Override
    public String getPermission() {
        return "swd.give";
    }

    @Override
    public void execute(ShockerWeaponsPlugin plugin, Player player, String[] args) {
        if (player.isValid()) {
            //execute code
            if (args.length != 2) return;
            generateItemList(plugin);
            if (itemList.get("weapons").contains(args[1])){
                giveWeapon(plugin, args[1], player);
                plugin.getFunctionLibrary().displaySystemMessage(player, "&eYou got new &6" + args[1]);
                return;
            }
            if (itemList.get("mag").contains(args[1])){
                giveMag(player, args[1], plugin);
                plugin.getFunctionLibrary().displaySystemMessage(player, "&eYou got new &6" + args[1]);
                return;
            }
            if (itemList.get("ammo").contains(args[1])){
                giveAmmo(player, args[1], plugin);
                plugin.getFunctionLibrary().displaySystemMessage(player, "&eYou got new &6" + args[1]);
                return;
            }

            plugin.getFunctionLibrary().displaySystemMessage(player, "&cItem not found! &fUse &e/swd list &fto show list of items");
        }
    }

    private void generateItemList(ShockerWeaponsPlugin plugin) {
        String[] keys = new String[] {"weapons", "mag"};
        for (String key : keys) {
            Set<String> items = plugin.getConfig().getConfigurationSection(key).getKeys(false);
            itemList.put(key, items);
        }
        for (String curcal : plugin.getConfig().getConfigurationSection("ammo").getKeys(false)) {

            itemList.put("ammo", plugin.getConfig().getConfigurationSection("ammo").getConfigurationSection(curcal).getKeys(false));
        }
    }


    private void giveWeapon(ShockerWeaponsPlugin plugin, String string, Player player){

        String id = string;
        int dur = plugin.getConfigWeaponInt(id, "durability");

        Material material;
        String loadedMaterial = plugin.getConfig().getConfigurationSection("Global").getString("weaponMaterial");
        material = Material.valueOf(loadedMaterial);
        ItemStack itemToGive = new ItemStack(material);

        ReadWriteNBT nbt = NBT.itemStackToNBT(itemToGive);
        nbt.getOrCreateCompound("tag").getOrCreateCompound("ShockerWeapons");
        nbt.getCompound("tag").getCompound("ShockerWeapons").setString("weaponID", id);
        nbt.getCompound("tag").getCompound("ShockerWeapons").setString("state", "idle");
        nbt.getCompound("tag").getCompound("ShockerWeapons").setString("ammoType", "null");
        nbt.getCompound("tag").getCompound("ShockerWeapons").setString("mag", "null");
        nbt.getCompound("tag").getCompound("ShockerWeapons").setInteger("ammo", 0);
        nbt.getCompound("tag").getCompound("ShockerWeapons").setInteger("durability", dur);
        nbt.getCompound("tag").getCompound("ShockerWeapons").setInteger("SN", -1);

        itemToGive = NBT.itemStackFromNBT(nbt);
        ItemMeta meta = itemToGive.getItemMeta();

        String name = plugin.getConfig().getConfigurationSection("weapons").getConfigurationSection(id).getString("name");
        meta.displayName(LegacyComponentSerializer.legacy('&').deserialize(name).decoration(TextDecoration.ITALIC, false));
        List<String> loreStrings = plugin.getConfig().getConfigurationSection("weapons").getConfigurationSection(id).getStringList("lore");
        meta = plugin.getFunctionLibrary().setItemLore(meta, loreStrings, itemToGive);

        int model = plugin.getConfig().getConfigurationSection("weapons").getConfigurationSection(id).getInt("modelData");
        meta.setCustomModelData(model);

        itemToGive.setItemMeta(meta);
        player.getInventory().addItem(itemToGive);
    }


    private void giveMag(Player player, String oldMag, ShockerWeaponsPlugin plugin) {

        //("weaponRemoveMag");

        String cal = plugin.getConfigMagString(oldMag, "cal");


        Material material;
        String loadedMaterial = plugin.getConfig().getConfigurationSection("Global").getString("magMaterial");
        material = Material.valueOf(loadedMaterial);
        ItemStack itemToGive = new ItemStack(material);

        ReadWriteNBT nbt = NBT.itemStackToNBT(itemToGive);
        nbt.getOrCreateCompound("tag").getOrCreateCompound("ShockerWeapons");
        nbt.getCompound("tag").getCompound("ShockerWeapons").setString("magID", oldMag);
        nbt.getCompound("tag").getCompound("ShockerWeapons").setString("cal", cal);
        nbt.getCompound("tag").getCompound("ShockerWeapons").setInteger("ammo", 0);
        nbt.getCompound("tag").getCompound("ShockerWeapons").setString("ammoType", "null");



        itemToGive = NBT.itemStackFromNBT(nbt);


        String displayName = plugin.getConfigMagString(oldMag, "name");

        ItemMeta meta = itemToGive.getItemMeta();

        meta.setCustomModelData(plugin.getConfigMagInt(oldMag, "modelData"));
        //meta.displayName(Component.text(plugin.getConfigMagString(oldMag, "name"), styleMagItem()));
        meta.displayName(LegacyComponentSerializer.legacy('&').deserialize(plugin.getConfigMagString(oldMag, "name")).decoration(TextDecoration.ITALIC, false));

        List<String> loreToAdd = plugin.getConfig().getConfigurationSection("mag").getConfigurationSection(oldMag).getStringList("lore");

        meta = plugin.getFunctionLibrary().setItemLore(meta, loreToAdd, itemToGive);

        itemToGive.setItemMeta(meta);

        player.getInventory().addItem(itemToGive);
    }

    private void giveAmmo(Player player, String ammoID, ShockerWeaponsPlugin plugin) {

        String cal = "null";

        Set<String> allcals = plugin.getConfig().getConfigurationSection("ammo").getKeys(false);
        for (String curcal : allcals) {
            if (plugin.getConfig().getConfigurationSection("ammo").getConfigurationSection(curcal).contains(ammoID)) {
                cal = curcal;
                break;
            }
        }

        Material material;
        String loadedMaterial = plugin.getConfig().getConfigurationSection("Global").getString("ammoMaterial");
        material = Material.valueOf(loadedMaterial);
        ItemStack itemToGive = new ItemStack(material);

        ReadWriteNBT nbt = NBT.itemStackToNBT(itemToGive);
        nbt.getOrCreateCompound("tag").getOrCreateCompound("ShockerWeapons");
        nbt.getCompound("tag").getCompound("ShockerWeapons").setString("ammoID", ammoID);
        itemToGive = NBT.itemStackFromNBT(nbt);

        String displayName = plugin.getConfig().getConfigurationSection("ammo").getConfigurationSection(cal).getConfigurationSection(ammoID).getString("name");

        ItemMeta meta = itemToGive.getItemMeta();

        meta.setCustomModelData(plugin.getConfig().getConfigurationSection("ammo").getConfigurationSection(cal).getConfigurationSection(ammoID).getInt("modelData"));
        //meta.displayName(Component.text(plugin.getConfigMagString(oldMag, "name"), styleMagItem()));
        meta.displayName(LegacyComponentSerializer.legacy('&').deserialize(plugin.getConfig().getConfigurationSection("ammo").getConfigurationSection(cal).getConfigurationSection(ammoID).getString("name")).decoration(TextDecoration.ITALIC, false));

        List<String> loreToAdd = plugin.getConfig().getConfigurationSection("ammo").getConfigurationSection(cal).getConfigurationSection(ammoID).getStringList("lore");

        meta = plugin.getFunctionLibrary().setItemLore(meta, loreToAdd, itemToGive);

        itemToGive.setItemMeta(meta);

        player.getInventory().addItem(itemToGive);
    }

}
