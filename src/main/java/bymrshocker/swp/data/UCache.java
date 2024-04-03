package bymrshocker.swp.data;

import bymrshocker.swp.ShockerWeaponsPlugin;

import javax.net.ssl.HandshakeCompletedEvent;
import java.util.HashMap;

public class UCache {
    private final HashMap <String, UPlayer> UPlayersMap;
    public UCache(ShockerWeaponsPlugin plugin) {
        this.UPlayersMap = new HashMap<>();
    }
    public void addPlayer(String nickname) {
        if (UPlayersMap.containsKey(nickname)) return;
        UPlayersMap.put(nickname, new UPlayer(nickname));
    }

    public UPlayer getPlayer(String nickname) {

        return UPlayersMap.getOrDefault(nickname, null);
    }

}
