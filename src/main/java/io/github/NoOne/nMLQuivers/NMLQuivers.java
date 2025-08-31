package io.github.NoOne.nMLQuivers;

import io.github.NoOne.nMLPlayerStats.NMLPlayerStats;
import io.github.NoOne.nMLPlayerStats.profileSystem.ProfileManager;
import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;

public final class NMLQuivers extends JavaPlugin {
    private NMLQuivers instance;
    private static NMLPlayerStats nmlPlayerStats;
    private ProfileManager profileManager;
    private QuiverGenerator quiverGenerator;

    @Override
    public void onEnable() {
        instance = this;

        Plugin plugin = Bukkit.getPluginManager().getPlugin("NMLPlayerStats");
        if (plugin instanceof NMLPlayerStats statsPlugin) {
            nmlPlayerStats = statsPlugin;
            profileManager = nmlPlayerStats.getProfileManager();
        }

        quiverGenerator = new QuiverGenerator(this);

        getCommand("generateQuiver").setExecutor(new GenerateQuiverCommand(this));
    }

    public NMLQuivers getInstance() {
        return instance;
    }

    public ProfileManager getProfileManager() {
        return profileManager;
    }

    public static NMLPlayerStats getNmlPlayerStats() {
        return nmlPlayerStats;
    }

    public QuiverGenerator getQuiverGenerator() {
        return quiverGenerator;
    }
}
