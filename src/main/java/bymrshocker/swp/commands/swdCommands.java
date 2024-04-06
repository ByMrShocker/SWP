package bymrshocker.swp.commands;

import bymrshocker.swp.ShockerWeaponsPlugin;
import de.tr7zw.nbtapi.NBT;
import de.tr7zw.nbtapi.iface.ReadWriteNBT;
import net.kyori.adventure.text.format.TextDecoration;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.List;
import java.util.Set;

public class swdCommands implements CommandExecutor {

    final public ShockerWeaponsPlugin plugin;
    private Player player;
    private String[] args;

    public swdCommands(ShockerWeaponsPlugin pluginInstance) {
        this.plugin = pluginInstance;
    }

    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] strings) {
        String[] args = strings;
        if (commandSender instanceof Player) {
            if (command.getName().equalsIgnoreCase("swd") && args.length > 0) {
                player = (Player) commandSender;
                args = strings;
                String arg = args[0];
                switchcommand(arg);
            }
            else displayMessage("&lVladusman лох");
        }
        return true;
    }

    private void switchcommand(String arg){
        switch (arg) {
            default: {
                displayMessage("&cInvalid arguments! &fArguments: &e[give, list, test]");
                break;
            }
            case "give": {
                give();
                displayMessage("give command end");
                break;
            }
            case "list": {
                list();
                break;
            }
            case "test": {
                test();
                break;
            }
        }
    }



    private void test(){
        displayMessage("&lVladusman лох кста");
    }


    private void give(){

        displayMessage("give command");
        ItemStack item = new ItemStack(Material.BAMBOO_BLOCK);
        player.getInventory().addItem(item);
        displayMessage("give command1");

    }

    private void list(){
        System.out.println("list command");
        if (args.length == 1) {
            System.out.println("list command args.length == 1");
            displayMessage(displayList("weapon"));
            displayMessage(displayList("ammo"));
            displayMessage(displayList("mag"));
            displayMessage("list return 1");
            return;
        }
        String arg = args[1];
        System.out.println("list command args.length != 1");
        if (arg == "weapon") displayMessage(displayList("weapon"));
        displayMessage("list return 2");
        System.out.println("list command end");

    }

    private String displayList(String string){
        //if (plugin.getConfig().getConfigurationSection(string) != null) return "ERROR";
        //String toshow = plugin.getConfig().getConfigurationSection(string).getKeys(false).toArray().toString();
        //System.out.println(toshow);
        //return toshow;
        return "none";
    }

    private void displayMessage(String string){
        player.sendMessage(LegacyComponentSerializer.legacy('&').deserialize("&6[SWD] &f" + string).decoration(TextDecoration.ITALIC, false));

    }


    private boolean giveweapon(CommandSender sender, String[] args) {
        if (args.length != 2) {
            Set<String> keys = plugin.getConfig().getConfigurationSection("weapons").getKeys(false);

            if (!keys.contains(args[1])) return false;

            System.out.println(!keys.contains(args[1]));
            System.out.println(args[1]);

            if (true) return false;

            String id = plugin.getConfig().getConfigurationSection("weapons").getString(args[1]);

            Material material;
            String loadedMaterial = plugin.getConfig().getConfigurationSection("Global").getString("weaponMaterial");
            material = Material.valueOf(loadedMaterial);
            ItemStack itemToGive = new ItemStack(material);

            ReadWriteNBT nbt = NBT.itemStackToNBT(itemToGive);
            nbt.getOrCreateCompound("tag").getOrCreateCompound("ShockerWeapons");
            nbt.getCompound("tag").getCompound("ShockerWeapons").setString("magID", id);
            nbt.getCompound("tag").getCompound("ShockerWeapons").setString("durability", plugin.getConfig().getConfigurationSection("weapons").getConfigurationSection(id).getString("durability"));
            nbt.getCompound("tag").getCompound("ShockerWeapons").setString("state", "idle");

            itemToGive = NBT.itemStackFromNBT(nbt);

            ItemMeta meta = itemToGive.getItemMeta();

            meta.displayName(LegacyComponentSerializer.legacy('&').deserialize(plugin.getConfig().getConfigurationSection("weapons").getConfigurationSection(id).getString("name")).decoration(TextDecoration.ITALIC, false));
            List<String> loreStrings = plugin.getConfig().getConfigurationSection("Global").getStringList("lore");
            meta = plugin.getFunctionLibrary().setItemLore(meta, loreStrings, itemToGive);

            itemToGive.setItemMeta(meta);

            return true;


        }
        return false;
    }




}
