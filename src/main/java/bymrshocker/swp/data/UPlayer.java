package bymrshocker.swp.data;

import org.bukkit.entity.Player;

public class UPlayer {
    private Player player;
    private int drugState;
    private int drugAmplifier;


    public UPlayer(String nickname) {
        drugState = 0;
        drugAmplifier = 0;
    }

    public Player getPlayer() {
        return player;
    }

    public void setPlayer(Player player) {
        this.player = player;
    }

    public int getDrugState() {
        return drugState;
    }

    public void setDrugState(int newState) {
        this.drugState = newState;
    }

    public int getDrugAmplifier() {
        return drugAmplifier;
    }

    public void setDrugAmplifier(int newAmplifier) {
        this.drugAmplifier = newAmplifier;
    }
}
