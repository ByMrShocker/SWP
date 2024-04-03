package bymrshocker.swp.playerfuncs;

import bymrshocker.swp.ShockerWeaponsPlugin;
import bymrshocker.swp.data.SWeapon;
import com.sun.org.apache.xalan.internal.xsltc.util.IntegerArray;
import de.tr7zw.nbtapi.NBT;
import de.tr7zw.nbtapi.NBTType;
import de.tr7zw.nbtapi.iface.ReadWriteNBT;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import net.kyori.adventure.text.serializer.gson.GsonComponentSerializer;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.apache.commons.lang3.ObjectUtils;
import org.bukkit.Bukkit;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.scheduler.BukkitRunnable;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


//Библиотека всяких функций, которые можно вызвать откуда угодно. pluginInstance.getFunctionLibrary().функция ниже
public class UFunctionLibrary {
    ShockerWeaponsPlugin pluginInstance;

    private static final Pattern COLOR_PATTERN = Pattern.compile("&([0-9a-fk-or])", Pattern.CASE_INSENSITIVE);

    public UFunctionLibrary(ShockerWeaponsPlugin plugin) {
        this.pluginInstance = plugin;
        NamespacedKey key = new NamespacedKey(pluginInstance, "our-custom-key");
    }


    //Выполнение консольной комманды от консоли
    public void executeConsoleCommand(String command){
        Bukkit.dispatchCommand(Bukkit.getConsoleSender(),command);
    }

    public boolean isWeapon(ItemStack item) {
        if (item != null) {
            if (getItemNBTString(item, "weaponID") != "null") {
                return pluginInstance.getConfig().getConfigurationSection("weapons").contains(getItemNBTString(item, "weaponID"));
            }
            return false;
        }
        return false;

    }



    public  void weaponReloadTimer(SWeapon inweaponClass, Player inplayer, int incd, String inoldMag, String innewMag, String cal) {

        new BukkitRunnable(){

            final private SWeapon weaponClass = inweaponClass;
            final private Player player = inplayer;
            final private int cd = incd;
            final private String oldMag = inoldMag;
            final private String newMag = innewMag;

            private int step = 2;



            @Override
            public void run() {

                if (!weaponClass.isStillHolding(player)) {
                    this.cancel();
                    return;
                }


                switch (step) {
                    case 0: {
                        this.cancel();
                        return;
                    }
                    case 1:
                        if (!weaponClass.weaponInsertMag(newMag, player, cal)) {
                            //System.out.println("w_reloadTimer: case 1: false");
                            this.cancel();
                            return;
                        }
                    case 2: {
                        //System.out.println(oldMag);
                        if (oldMag != null && !oldMag.equals("null")) weaponClass.weaponRemoveMag(player, oldMag);
                        else if (newMag.equals(null)) {
                            this.cancel();
                            return;
                        }
                    }

                }

                step--;

            }
        }.runTaskTimer(pluginInstance, incd, incd);

    }




        //Автоматическая/BURST стрельба
    public void weaponFireTimer(SWeapon inweaponClass, Player inPlayer, int incount, int period, int initDelay){

        new BukkitRunnable(){

            final private SWeapon weaponClass = inweaponClass;
            final private Player player = inPlayer;
            final private int count = incount;
            private int remainCount = count;
            private int ammo = pluginInstance.getFunctionLibrary().getItemNBTInt(weaponClass.getWeaponItem(), "ammo");
            private int sn = pluginInstance.getFunctionLibrary().getSerialNumber(player);

            @Override
            public void run(){

                int cursn = getSerialNumber(player);
                pluginInstance.getFunctionLibrary().setItemNBTInt(weaponClass.getSelectedItem(player), player, "ammo", ammo);

                if(remainCount <= 0 || !player.isOnline() || !weaponClass.isFirePossible(player) || ammo <= 0 || sn != cursn){

                    //System.out.println(sn);
                    //System.out.println(cursn);


                    weaponClass.onFireInterrupted(player);
                    pluginInstance.getFunctionLibrary().setItemNBTString(weaponClass.getSelectedItem(player), player, "state", "idle");
                    this.cancel(); //cancel the repeating task
                    return; //exit the method

                }
                remainCount--; //decrement
                ammo--;
                weaponClass.weaponFire(player);


            }
        }.runTaskTimer(pluginInstance, initDelay, period);

    }



    public int getSerialNumber(Player player){
        ItemStack item = player.getInventory().getItemInMainHand();
        int sn = getItemNBTInt(item, "SN");

        if (sn != -1) return sn;

        if (isWeapon(item)) {
            sn = pluginInstance.getConfig().getConfigurationSection("saved").getInt("lastSN");
            sn++;
            pluginInstance.getConfig().getConfigurationSection("saved").set("lastSN", sn);
            setItemNBTInt(item, player, "SN", sn);
            pluginInstance.saveConfig();
            return sn;
        }
        return -1;

    }


    public ItemMeta setItemLore(ItemMeta meta, List<String> loreStrings, ItemStack itemStack) {

        List<Component> loreComponents = new ArrayList<>();
        for (String loreString : loreStrings) {
            //Component loreComponent = GsonComponentSerializer.gson().deserialize(loreString);
            String modifiedString = replacePlaceholders(loreString, itemStack);
            Component loreComponent = LegacyComponentSerializer.legacy('&').deserialize(modifiedString).decoration(TextDecoration.ITALIC, false);
            loreComponents.add(loreComponent);
        }
        meta.lore(loreComponents);

        return meta;

    }

    public String replacePlaceholders(String inString, ItemStack itemStack) {
        String string = inString;
        ReadWriteNBT nbt = NBT.itemStackToNBT(itemStack);
        Set<String> swdKeys = nbt.getCompound("tag").getCompound("ShockerWeapons").getKeys();

        for(String keyString : swdKeys) {
            if (isValidCompound(nbt, keyString)) {
                String placeholder = "%swd_" + keyString + "%";
                NBTType type = nbt.getCompound("tag").getCompound("ShockerWeapons").getType(keyString);
                if (type != null) switch (type) {
                    case NBTTagString: string = string.replace(placeholder, nbt.getCompound("tag").getCompound("ShockerWeapons").getString(keyString));
                    case NBTTagInt: string = string.replace(placeholder, String.valueOf(nbt.getCompound("tag").getCompound("ShockerWeapons").getInteger(keyString)));
                }
            }
        }
        if (nbt.getCompound("tag").getCompound("ShockerWeapons").getString("ammoType") != "null" && nbt.getCompound("tag").getCompound("ShockerWeapons").getString("cal") != "null") {

            string = string.replace("%swd_ammoTypeName%", pluginInstance.getConfig().getConfigurationSection("ammo").getConfigurationSection(nbt.getCompound("tag").getCompound("ShockerWeapons").getString("cal")).getConfigurationSection(nbt.getCompound("tag").getCompound("ShockerWeapons").getString("ammoType")).getString("name"));

        } else if (nbt.getCompound("tag").getCompound("ShockerWeapons").getString("ammoType") == "null" && nbt.getCompound("tag").getCompound("ShockerWeapons").getString("cal") != "null") {
            string = string.replace("%swd_ammoTypeName%", pluginInstance.getConfig().getConfigurationSection("Global").getString("nullItemName"));
        }

        return string;
    }



    //разраб NBTApi наговнокодил, поэтому приходится делать так, чтоб не срало в консоль
    public boolean isValidCompound(ReadWriteNBT nbt, String key) {
        if (!nbt.getKeys().contains("tag")) return false;
        if (!nbt.getCompound("tag").getKeys().contains("ShockerWeapons")) return false;
        if (!nbt.getCompound("tag").getCompound("ShockerWeapons").getKeys().contains(key)) return false;

        return true;
    }




    //получение NBT тега типа STRING.
    public String getItemNBTString(ItemStack item, String key) {

        ReadWriteNBT nbt = NBT.itemStackToNBT(item);
        if (!isValidCompound(nbt, key)) return "null";

        return nbt.getCompound("tag").getCompound("ShockerWeapons").getString(key);


        //NamespacedKey nkey = new NamespacedKey(pluginInstance, key);
        //ItemMeta itemMeta = item.getItemMeta();
        //PersistentDataContainer container = itemMeta.getPersistentDataContainer();
//
        //if(container.has(nkey, PersistentDataType.STRING)) {
        //    return container.get(nkey, PersistentDataType.STRING);
        //}
        //return null;
    }


    //установка NBT тега типа STRING.
    public void setItemNBTString(ItemStack item, Player player, String key, String value) {

        ReadWriteNBT nbt = NBT.itemStackToNBT(item);
        nbt.getCompound("tag").getCompound("ShockerWeapons").setString(key, value);
        player.getInventory().setItemInMainHand(NBT.itemStackFromNBT(nbt));

        //NamespacedKey nkey = new NamespacedKey(pluginInstance, key);
        //ItemMeta itemMeta = item.getItemMeta();
        //itemMeta.getPersistentDataContainer().set(nkey, PersistentDataType.STRING, value);
        //item.setItemMeta(itemMeta);
    }

    //получение NBT тега типа DOUBLE.
    public Double getItemNBTDouble(ItemStack item, String key) {

        ReadWriteNBT nbt = NBT.itemStackToNBT(item);
        if (!isValidCompound(nbt, key)) return -1.0;

        return nbt.getCompound("tag").getCompound("ShockerWeapons").getDouble(key);
    }

    //установка NBT тега типа DOUBLE.
    public void setItemNBTDouble(ItemStack item, Player player, String key, Double value) {

        ReadWriteNBT nbt = NBT.itemStackToNBT(item);
        nbt.getCompound("tag").getCompound("ShockerWeapons").setDouble(key, value);
        player.getInventory().setItemInMainHand(NBT.itemStackFromNBT(nbt));
    }

    //получение NBT тега типа BYTE.
    public Byte getItemNBTByte(ItemStack item, String key) {

        ReadWriteNBT nbt = NBT.itemStackToNBT(item);
        if (!isValidCompound(nbt, key)) return -1;

        return nbt.getCompound("tag").getCompound("ShockerWeapons").getByte(key);
    }

    //установка NBT тега типа BYTE.
    public void setItemNBTByte(ItemStack item, Player player, String key, Byte value) {

        ReadWriteNBT nbt = NBT.itemStackToNBT(item);
        nbt.getCompound("tag").getCompound("ShockerWeapons").setByte(key, value);
        player.getInventory().setItemInMainHand(NBT.itemStackFromNBT(nbt));
    }

    //получение NBT тега типа INT.
    public int getItemNBTInt(ItemStack item, String key) {

        ReadWriteNBT nbt = NBT.itemStackToNBT(item);
        if (!isValidCompound(nbt, key)) return -1;

        return nbt.getCompound("tag").getCompound("ShockerWeapons").getInteger(key);
    }

    //установка NBT тега типа INT.
    public void setItemNBTInt(ItemStack item, Player player, String key, int value) {

        ReadWriteNBT nbt = NBT.itemStackToNBT(item);
        nbt.getCompound("tag").getCompound("ShockerWeapons").setInteger(key, value);
        player.getInventory().setItemInMainHand(NBT.itemStackFromNBT(nbt));
    }



    //получение NBT тега типа IntArray.
    public int[] getItemNBTIntArray(ItemStack item, String key) {

        ReadWriteNBT nbt = NBT.itemStackToNBT(item);
        int[] ints = new int[0];
        if (!isValidCompound(nbt, key)) return ints;

        return nbt.getCompound("tag").getCompound("ShockerWeapons").getIntArray(key);
    }

    //установка NBT тега типа IntArray.
    public void setItemNBTIntArray(ItemStack item, Player player, String key, int[] value) {

        ReadWriteNBT nbt = NBT.itemStackToNBT(item);
        nbt.getCompound("tag").getCompound("ShockerWeapons").setIntArray(key, value);
        player.getInventory().setItemInMainHand(NBT.itemStackFromNBT(nbt));
    }




}
