package bymrshocker.swp.commands.args;

import bymrshocker.swp.ShockerWeaponsPlugin;
import bymrshocker.swp.commands.BaseCommandArg;
import org.bukkit.entity.Player;

public class swd_reload implements BaseCommandArg {

    @Override
    public String[] getTabList(String key) {
        argListMap.put("argument", new String[] {"arg1", "arg2"});
        if (!argListMap.containsKey(key)) return new String[0];
        return argListMap.get(key);
    }

    @Override
    public String getName() { return "reload"; }

    @Override
    public String getDescription() { return "reloading config"; }

    @Override
    public String getSyntax() { return "/swd reload"; }

    @Override
    public String getPermission() {
        return "swd.reload";
    }

    @Override
    public void execute(ShockerWeaponsPlugin plugin, Player player, String[] args) {
        if (player.isValid()) {
            plugin.reloadConfig();
            plugin.getFunctionLibrary().displaySystemMessage(player, "&aconfig has been reloaded.");
        }
    }
}
