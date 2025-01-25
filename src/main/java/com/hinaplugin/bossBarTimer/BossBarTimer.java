package com.hinaplugin.bossBarTimer;

import com.google.common.collect.Lists;
import org.bukkit.Bukkit;
import org.bukkit.NamespacedKey;
import org.bukkit.command.PluginCommand;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.util.List;
import java.util.logging.Level;

public final class BossBarTimer extends JavaPlugin {
    public static BossBarTimer plugin;
    public static FileConfiguration config;
    public static List<CountUpTimer> countUpTimers = Lists.newArrayList();
    public static List<CountDownTimer> countDownTimers = Lists.newArrayList();
    public static Level level = Level.INFO;

    @Override
    public void onEnable() {
        // Plugin startup logic
        plugin = this;

        level = this.getLogger().getLevel();

        loadConfiguration();

        final PluginCommand command = this.getCommand("timer");
        if (command != null){
            command.setExecutor(new Commands());
        }
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
        if (!countUpTimers.isEmpty()){
            for (final CountUpTimer countUpTimer : countUpTimers){
                final NamespacedKey key = new NamespacedKey("minecraft", countUpTimer.getName());
                Bukkit.removeBossBar(key);
                countUpTimer.cancel();
            }
        }
        if (!countDownTimers.isEmpty()){
            for (final CountDownTimer countDownTimer : countDownTimers){
                final NamespacedKey key = new NamespacedKey("minecraft", countDownTimer.getName());
                Bukkit.removeBossBar(key);
                countDownTimer.cancel();
            }
        }
    }

    private void loadConfiguration(){
        final File configFile = new File(this.getDataFolder(), "config.yml");
        if (!configFile.exists()){
            this.saveDefaultConfig();
        }
        config = this.getConfig();
    }

    public void reloadConfiguration(){
        this.reloadConfig();
        config = this.getConfig();
    }
}
