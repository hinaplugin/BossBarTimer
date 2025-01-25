package com.hinaplugin.bossBarTimer;

import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.Bukkit;
import org.bukkit.NamespacedKey;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BossBar;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.Objects;

public class CountDownTimer extends BukkitRunnable {
    private final String name;
    private final String creator;
    private final BossBar bossBar;
    private final int startTime;
    private int nowTime;
    private final ColorChanger colorChanger = new ColorChanger();

    public CountDownTimer(String name, String creator, BossBar bossBar, int startTime, boolean all) {
        this.name = name;
        this.creator = creator;
        this.bossBar = bossBar;
        this.startTime = startTime;
        this.nowTime = startTime;
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
        double progress = (double) nowTime / startTime;
        if (nowTime > 0){
            bossBar.setProgress(progress);
        }
        if (nowTime >= 3600){
            bossBar.setTitle(this.replace(BossBarTimer.config.getString("count-down-title"), nowTime, 1));
        }else if (nowTime >= 60){
            bossBar.setTitle(this.replace(BossBarTimer.config.getString("count-down-title"), nowTime, 2));
        }else {
            bossBar.setTitle(this.replace(BossBarTimer.config.getString("count-down-title"), nowTime, 3));
        }
        nowTime--;
        if (nowTime < 0) {
            bossBar.setVisible(false);
            for (Player player : bossBar.getPlayers()) {
                player.sendMessage(MiniMessage.miniMessage().deserialize(BossBarTimer.config.getString("end", "").replace("[timer]", name)));
            }
            bossBar.removeAll();
            final NamespacedKey key = new NamespacedKey("minecraft", name);
            Bukkit.removeBossBar(key);
            this.cancel();
            BossBarTimer.countDownTimers.remove(this);
        }
        final BarColor color = colorChanger.change(progress);
        if (color != null){
            bossBar.setColor(color);
        }
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
