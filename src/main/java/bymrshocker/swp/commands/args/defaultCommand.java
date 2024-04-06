package bymrshocker.swp.commands.args;

import bymrshocker.swp.ShockerWeaponsPlugin;
import bymrshocker.swp.commands.BaseCommandArg;
import org.bukkit.entity.Player;

public class defaultCommand implements BaseCommandArg {

    @Override
    public String[] getTabList(String key) {
        argListMap.put("argument", new String[] {"arg1", "arg2"});
        if (!argListMap.containsKey(key)) return new String[0];
        return argListMap.get(key);
    }

    @Override
    public String getName() { return "example"; }

    @Override
    public String getDescription() { return "example"; }

    @Override
    public String getSyntax() { return "/swd example"; }

    @Override
    public void execute(ShockerWeaponsPlugin plugin, Player player, String[] args) {
        if (player.isValid()) {
            //execute code

        }
    }
}
