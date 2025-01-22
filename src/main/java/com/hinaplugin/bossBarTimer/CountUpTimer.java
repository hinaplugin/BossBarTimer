package com.hinaplugin.bossBarTimer;

import com.google.common.collect.Lists;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BossBar;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.List;
import java.util.Objects;

public class CountUpTimer extends BukkitRunnable {
    private final String name;
    private final String creator;
    private final BossBar bossBar;
    private int nowTime = 0;

    public CountUpTimer(String name, String creator, BossBar bossBar, boolean all) {
        this.name = name;
        this.creator = creator;
        this.bossBar = bossBar;
        this.bossBar.setColor(BarColor.GREEN);
        if (all){
            bossBar.setVisible(true);
            for (final Player player : BossBarTimer.plugin.getServer().getOnlinePlayers()){
                bossBar.addPlayer(player);
            }
        }else {
            bossBar.setVisible(true);
            bossBar.addPlayer(Objects.requireNonNull(BossBarTimer.plugin.getServer().getPlayer(creator)));
        }
    }

    @Override
    public void run() {
        bossBar.setProgress(1);
        if (nowTime >= 3600){
            bossBar.setTitle(this.replace(BossBarTimer.config.getString("count-up-title"), nowTime, 1));
        }else if (nowTime >= 60){
            bossBar.setTitle(this.replace(BossBarTimer.config.getString("count-up-title"), nowTime, 2));
        }else {
            bossBar.setTitle(this.replace(BossBarTimer.config.getString("count-up-title"), nowTime, 3));
        }
        nowTime++;
    }

    public String getName(){
        return name;
    }

    public String getCreator(){
        return creator;
    }

    public BossBar getBossBar(){
        return bossBar;
    }

    private String replace(String title, int time, int mode){
        switch (mode) {
            case 1 -> {
                int time_hour = time / 3600;
                int time_min = (time % 3600) / 60;
                int time_sec = time % 60;
                String hour = BossBarTimer.config.getString("hour", "").replace("[hour]", String.valueOf(time_hour));
                String min = BossBarTimer.config.getString("minute", "").replace("[min]", String.valueOf(time_min));
                String sec = BossBarTimer.config.getString("second", "").replace("[sec]", String.valueOf(time_sec));
                return title.replace("[hour]", hour).replace("[min]", min).replace("[sec]", sec);
            }
            case 2 -> {
                int time_min = time / 60;
                int time_sec = time % 60;
                String min = BossBarTimer.config.getString("minute", "").replace("[min]", String.valueOf(time_min));
                String sec = BossBarTimer.config.getString("second", "").replace("[sec]", String.valueOf(time_sec));
                return title.replace("[hour]", "").replace("[min]", min).replace("[sec]", sec);
            }
            case 3 -> {
                String sec = BossBarTimer.config.getString("second", "").replace("[sec]", String.valueOf(time));
                return title.replace("[hour]", "").replace("[min]", "").replace("[sec]", sec);
            }
            default -> {
                return "";
            }
        }
    }
}
