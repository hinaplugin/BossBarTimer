package com.hinaplugin.bossBarTimer;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import org.bukkit.boss.BarColor;
import org.bukkit.configuration.ConfigurationSection;

import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

public class ColorChanger {
    private final Map<BarColor, List<Integer>> colorMap = Maps.newHashMap();

    public ColorChanger(){
        final ConfigurationSection section = BossBarTimer.config.getConfigurationSection("color");
        if (section == null){
            return;
        }

        final Set<String> keys = section.getKeys(false);
        if (keys.isEmpty()){
            return;
        }

        for (String key : keys){
            final String percentage = BossBarTimer.config.getString("color." + key + ".percentage", "");
            if (percentage.isEmpty()){
                continue;
            }
            final String[] percentageSplit = percentage.split("-");
            if (this.isNumeric(percentageSplit[0]) || this.isNumeric(percentageSplit[1])){
                continue;
            }
            final int min = Integer.min(Integer.parseInt(percentageSplit[0]), Integer.parseInt(percentageSplit[1]));
            final int max = Integer.max(Integer.parseInt(percentageSplit[0]), Integer.parseInt(percentageSplit[1]));
            final BarColor color = BarColor.valueOf(key.toUpperCase(Locale.ROOT));
            final List<Integer> percent = Lists.newArrayList();
            percent.add(min);
            percent.add(max);
            colorMap.put(color, percent);
        }
    }

    public BarColor change(double progress){
        final double progressRound = Math.round(progress * 100);
        for (final BarColor color : colorMap.keySet()){
            final List<Integer> percent = colorMap.get(color);
            if (percent.get(0) <= progressRound && progressRound <= percent.get(1)){
                return color;
            }
        }
        return null;
    }

    private boolean isNumeric(String percentage){
        return !percentage.chars().allMatch(Character::isDigit);
    }
}
