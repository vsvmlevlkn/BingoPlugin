package com.bingo;

import com.bingo.commands.BingoCommand;
import com.bingo.listeners.InventoryListener;
import com.bingo.listeners.PlayerListener;
import com.bingo.managers.GameManager;
import com.bingo.managers.TeamManager;
import org.bukkit.plugin.java.JavaPlugin;

public class BingoPlugin extends JavaPlugin {

    private static BingoPlugin instance;
    private GameManager gameManager;
    private TeamManager teamManager;

    @Override
    public void onEnable() {
        instance = this;

        saveDefaultConfig();

        this.teamManager = new TeamManager(this);
        this.gameManager = new GameManager(this);

        // Register commands
        BingoCommand bingoCommand = new BingoCommand(this);
        getCommand("bingo").setExecutor(bingoCommand);
        getCommand("bingo").setTabCompleter(bingoCommand);

        // Register listeners
        getServer().getPluginManager().registerEvents(new InventoryListener(this), this);
        getServer().getPluginManager().registerEvents(new PlayerListener(this), this);

        getLogger().info("BingoPlugin 1.0.0 enabled! Good luck teams!");
    }

    @Override
    public void onDisable() {
        if (gameManager != null && gameManager.isGameRunning()) {
            gameManager.stopGame();
        }
        getLogger().info("BingoPlugin disabled.");
    }

    public static BingoPlugin getInstance() {
        return instance;
    }

    public GameManager getGameManager() {
        return gameManager;
    }

    public TeamManager getTeamManager() {
        return teamManager;
    }
}
