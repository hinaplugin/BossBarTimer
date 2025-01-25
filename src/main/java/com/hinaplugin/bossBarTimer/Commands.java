package com.hinaplugin.bossBarTimer;

import com.google.common.collect.Lists;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.Bukkit;
import org.bukkit.NamespacedKey;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarStyle;
import org.bukkit.boss.BossBar;
import org.bukkit.command.*;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class Commands implements CommandExecutor, TabCompleter {
    @Override
    public boolean onCommand(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String s, @NotNull String[] strings) {
        final MiniMessage miniMessage = MiniMessage.miniMessage();
        switch (strings.length){
            case 1 -> {
                if (strings[0].equalsIgnoreCase("reload")){
                    if (commandSender.hasPermission("timer.commands.reload")){
                        BossBarTimer.plugin.reloadConfiguration();
                        commandSender.sendMessage(miniMessage.deserialize("<green>config.ymlを再読み込みしました．</green>"));
                    }else {
                        this.noPermission(commandSender);
                    }
                    return true;
                }
                this.sendUsage(commandSender);
            }
            case 2 -> {
                if (strings[0].equalsIgnoreCase("upcreate")) {
                    if (commandSender.hasPermission("timer.commands.create")) {
                        final String name = strings[1];
                        if (this.isNameUsed(name)){
                            commandSender.sendMessage(miniMessage.deserialize("<red>[" + name + "]は既に使われています．</red>"));
                            return true;
                        }
                        final NamespacedKey key = new NamespacedKey("minecraft", name);
                        final BossBar bossBar = BossBarTimer.plugin.getServer().createBossBar(key, "timer", BarColor.GREEN, BarStyle.SOLID);
                        final boolean all = BossBarTimer.config.getBoolean("default.player", true);
                        CountUpTimer countUpTimer = new CountUpTimer(name, commandSender.getName(), bossBar, all);
                        countUpTimer.runTaskTimerAsynchronously(BossBarTimer.plugin, 0L, 20L);
                        BossBarTimer.countUpTimers.add(countUpTimer);
                    } else {
                        this.noPermission(commandSender);
                    }
                    return true;
                }
                if (strings[0].equalsIgnoreCase("remove")) {
                    if (commandSender.hasPermission("timer.commands.remove")) {
                        final String name = strings[1];
                        for (final CountUpTimer countUpTimer : BossBarTimer.countUpTimers) {
                            if (countUpTimer.getName().equalsIgnoreCase(name)) {
                                if (countUpTimer.getCreator().equalsIgnoreCase(commandSender.getName())) {
                                    countUpTimer.getBossBar().removeAll();
                                    countUpTimer.cancel();
                                    BossBarTimer.countUpTimers.remove(countUpTimer);
                                    commandSender.sendMessage(miniMessage.deserialize("<green>[" + name + "]を削除しました．</green>"));
                                    Bukkit.removeBossBar(new NamespacedKey("minecraft", name));
                                    return true;
                                }
                            }
                        }
                        for (final CountDownTimer countDownTimer : BossBarTimer.countDownTimers) {
                            if (countDownTimer.getName().equalsIgnoreCase(name)) {
                                if (countDownTimer.getCreator().equalsIgnoreCase(commandSender.getName())) {
                                    countDownTimer.getBossBar().removeAll();
                                    countDownTimer.cancel();
                                    BossBarTimer.countDownTimers.remove(countDownTimer);
                                    commandSender.sendMessage(miniMessage.deserialize("<green>[" + name + "]を削除しました．</green>"));
                                    Bukkit.removeBossBar(new NamespacedKey("minecraft", name));
                                    return true;
                                }
                            }
                        }
                        commandSender.sendMessage(miniMessage.deserialize("<red>[" + name + "]が見つかりませんでした．</red>"));
                    } else {
                        this.noPermission(commandSender);
                    }
                    return true;
                }
                if (strings[0].equalsIgnoreCase("delete")){
                    if (commandSender.hasPermission("timer.commands.delete")){
                        final String name = strings[1];
                        for (final CountUpTimer countUpTimer : BossBarTimer.countUpTimers){
                            if (countUpTimer.getName().equalsIgnoreCase(name)){
                                countUpTimer.getBossBar().removeAll();
                                countUpTimer.cancel();
                                BossBarTimer.countUpTimers.remove(countUpTimer);
                                commandSender.sendMessage(miniMessage.deserialize("<green>[" + name + "]を削除しました．</green>"));
                                Bukkit.removeBossBar(new NamespacedKey("minecraft", name));
                                return true;
                            }
                        }
                        for (final CountDownTimer countDownTimer : BossBarTimer.countDownTimers){
                            if (countDownTimer.getName().equalsIgnoreCase(name)){
                                countDownTimer.getBossBar().removeAll();
                                countDownTimer.cancel();
                                BossBarTimer.countDownTimers.remove(countDownTimer);
                                commandSender.sendMessage(miniMessage.deserialize("<green>[" + name + "]を削除しました．</green>"));
                                Bukkit.removeBossBar(new NamespacedKey("minecraft", name));
                                return true;
                            }
                        }
                        commandSender.sendMessage(miniMessage.deserialize("<red>[" + name + "]が見つかりませんでした．</red>"));
                    }else {
                        this.noPermission(commandSender);
                    }
                    return true;
                }
                this.sendUsage(commandSender);
            }
            case 3 -> {
                if (strings[0].equalsIgnoreCase("upcreate")){
                    if (commandSender.hasPermission("timer.commands.create")){
                        final String name = strings[1];
                        if (this.isNameUsed(name)){
                            commandSender.sendMessage(miniMessage.deserialize("<red>[" + name + "]は既に使われています．</red>"));
                            return true;
                        }
                        final NamespacedKey key = new NamespacedKey("minecraft", name);
                        final BossBar bossBar = BossBarTimer.plugin.getServer().createBossBar(key, "timer", BarColor.GREEN, BarStyle.SOLID);
                        if (commandSender instanceof ConsoleCommandSender){
                            final boolean all = BossBarTimer.config.getBoolean("default.console", true);
                            CountUpTimer countUpTimer = new CountUpTimer(name, "Console", bossBar, all);
                            countUpTimer.runTaskTimerAsynchronously(BossBarTimer.plugin, 0L, 20L);
                            BossBarTimer.countUpTimers.add(countUpTimer);
                        }else if (commandSender instanceof BlockCommandSender){
                            final boolean all = BossBarTimer.config.getBoolean("default.command-block", true);
                            CountUpTimer countUpTimer = new CountUpTimer(name, "CommandBlock", bossBar, all);
                            countUpTimer.runTaskTimerAsynchronously(BossBarTimer.plugin, 0L, 20L);
                            BossBarTimer.countUpTimers.add(countUpTimer);
                        }else {
                            final boolean all = Boolean.parseBoolean(strings[2]);
                            CountUpTimer countUpTimer = new CountUpTimer(name, commandSender.getName(), bossBar, all);
                            countUpTimer.runTaskTimerAsynchronously(BossBarTimer.plugin, 0L, 20L);
                            BossBarTimer.countUpTimers.add(countUpTimer);
                        }
                    }else {
                        this.noPermission(commandSender);
                    }
                    return true;
                }
                if (strings[0].equalsIgnoreCase("downcreate")){
                    if (commandSender.hasPermission("timer.commands.create")){
                        final String name = strings[1];
                        final String time = strings[2];
                        if (this.isNameUsed(name)){
                            commandSender.sendMessage(miniMessage.deserialize("<red>[" + name + "]は既に使われています．</red>"));
                            return true;
                        }
                        if (this.isNumeric(time)){
                            final NamespacedKey key = new NamespacedKey("minecraft", name);
                            final BossBar bossBar = BossBarTimer.plugin.getServer().createBossBar(key, "timer", BarColor.GREEN, BarStyle.SOLID);
                            if (commandSender instanceof ConsoleCommandSender){
                                final boolean all = BossBarTimer.config.getBoolean("default.console", true);
                                CountDownTimer countdownTimer = new CountDownTimer(name, "Console", bossBar, Integer.parseInt(time), all);
                                countdownTimer.runTaskTimerAsynchronously(BossBarTimer.plugin, 0L, 20L);
                                BossBarTimer.countDownTimers.add(countdownTimer);
                            }else if (commandSender instanceof BlockCommandSender){
                                final boolean all = BossBarTimer.config.getBoolean("default.command-block", true);
                                CountDownTimer countDownTimer = new CountDownTimer(name, "CommandBlock", bossBar, Integer.parseInt(time), all);
                                countDownTimer.runTaskTimerAsynchronously(BossBarTimer.plugin, 0L, 20L);
                                BossBarTimer.countDownTimers.add(countDownTimer);
                            }else {
                                final boolean all = BossBarTimer.config.getBoolean("default.player", true);
                                CountDownTimer countDownTimer = new CountDownTimer(name, commandSender.getName(), bossBar, Integer.parseInt(time), all);
                                countDownTimer.runTaskTimerAsynchronously(BossBarTimer.plugin, 0L, 20L);
                                BossBarTimer.countDownTimers.add(countDownTimer);
                            }
                        }else {
                            commandSender.sendMessage(miniMessage.deserialize("<red>[" + time + "]は数値ではありません．</red>"));
                        }
                    }else {
                        this.noPermission(commandSender);
                    }
                    return true;
                }
                this.sendUsage(commandSender);
            }
            case 4 -> {
                if (strings[0].equalsIgnoreCase("downcreate")){
                    if (commandSender.hasPermission("timer.commands.create")){
                        final String name = strings[1];
                        final String time = strings[2];
                        if (this.isNameUsed(name)){
                            commandSender.sendMessage(miniMessage.deserialize("<red>[" + name + "]は既に使われています．</red>"));
                            return true;
                        }
                        if (this.isNumeric(time)){
                            final NamespacedKey key = new NamespacedKey("minecraft", name);
                            final BossBar bossBar = BossBarTimer.plugin.getServer().createBossBar(key, "timer", BarColor.GREEN, BarStyle.SOLID);
                            if (commandSender instanceof ConsoleCommandSender){
                                final boolean all = BossBarTimer.config.getBoolean("default.console", true);
                                CountDownTimer countdownTimer = new CountDownTimer(name, "Console", bossBar, Integer.parseInt(time), all);
                                countdownTimer.runTaskTimerAsynchronously(BossBarTimer.plugin, 0L, 20L);
                                BossBarTimer.countDownTimers.add(countdownTimer);
                            }else if (commandSender instanceof BlockCommandSender){
                                final boolean all = BossBarTimer.config.getBoolean("default.command-block", true);
                                CountDownTimer countDownTimer = new CountDownTimer(name, "CommandBlock", bossBar, Integer.parseInt(time), all);
                                countDownTimer.runTaskTimerAsynchronously(BossBarTimer.plugin, 0L, 20L);
                                BossBarTimer.countDownTimers.add(countDownTimer);
                            }else {
                                final boolean all = Boolean.parseBoolean(strings[3]);
                                CountDownTimer countDownTimer = new CountDownTimer(name, commandSender.getName(), bossBar, Integer.parseInt(time), all);
                                countDownTimer.runTaskTimerAsynchronously(BossBarTimer.plugin, 0L, 20L);
                                BossBarTimer.countDownTimers.add(countDownTimer);
                            }
                        }else {
                            commandSender.sendMessage(miniMessage.deserialize("<red>[" + time + "]は数値ではありません．</red>"));
                        }
                    }else {
                        this.noPermission(commandSender);
                    }
                    return true;
                }
                this.sendUsage(commandSender);
            }
            default -> this.sendUsage(commandSender);
        }
        return true;
    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String s, @NotNull String[] strings) {
        final List<String> complete = Lists.newArrayList();
        if (strings.length == 0){
            if (commandSender.hasPermission("timer.commands.create")){
                complete.add("upcreate");
                complete.add("downcreate");
            }
            if (commandSender.hasPermission("timer.commands.remove")){
                complete.add("remove");
            }
            if (commandSender.hasPermission("timer.commands.delete")){
                complete.add("delete");
            }
            if (commandSender.hasPermission("timer.commands.reload")){
                complete.add("reload");
            }
        }else if (strings.length == 1){
            if (strings[0].isEmpty()){
                if (commandSender.hasPermission("timer.commands.create")){
                    complete.add("upcreate");
                    complete.add("downcreate");
                }
                if (commandSender.hasPermission("timer.commands.remove")){
                    complete.add("remove");
                }
                if (commandSender.hasPermission("timer.commands.delete")){
                    complete.add("delete");
                }
                if (commandSender.hasPermission("timer.commands.reload")){
                    complete.add("reload");
                }
            }else if (strings[0].startsWith("u")){
                if (commandSender.hasPermission("timer.commands.create")){
                    complete.add("upcreate");
                }
            }else if (strings[0].startsWith("d")){
                if (strings[0].length() == 1){
                    if (commandSender.hasPermission("timer.commands.create")){
                        complete.add("downcreate");
                    }
                    if (commandSender.hasPermission("timer.commands.delete")){
                        complete.add("delete");
                    }
                }else {
                    if (strings[0].startsWith("do")){
                        if (commandSender.hasPermission("timer.commands.create")){
                            complete.add("downcreate");
                        }
                    }else if (strings[0].startsWith("de")){
                        if (commandSender.hasPermission("timer.commands.delete")){
                            complete.add("delete");
                        }
                    }
                }
            }else if (strings[0].startsWith("r")){
                if (strings[0].length() == 1){
                    if (commandSender.hasPermission("timer.commands.remove")){
                        complete.add("remove");
                    }
                    if (commandSender.hasPermission("timer.commands.reload")){
                        complete.add("reload");
                    }
                }else if (strings[0].length() == 2){
                    if (strings[0].startsWith("re")){
                        if (commandSender.hasPermission("timer.commands.remove")){
                            complete.add("remove");
                        }
                        if (commandSender.hasPermission("timer.commands.reload")){
                            complete.add("reload");
                        }
                    }
                }else if (strings[0].length() > 3){
                    if (strings[0].startsWith("rem")){
                        if (commandSender.hasPermission("timer.commands.remove")){
                            complete.add("remove");
                        }
                    }
                    if (strings[0].startsWith("rel")){
                        if (commandSender.hasPermission("timer.commands.reload")){
                            complete.add("reload");
                        }
                    }
                }
            }
        }else if (strings.length == 2){
            if (strings[0].equalsIgnoreCase("remove")){
                if (commandSender.hasPermission("timer.commands.remove")){
                    if (strings[1].isEmpty()){
                        for (final CountUpTimer countUpTimer : BossBarTimer.countUpTimers){
                            if (countUpTimer.getCreator().equalsIgnoreCase(commandSender.getName())){
                                complete.add(countUpTimer.getName());
                            }
                        }
                        for (final CountDownTimer countDownTimer : BossBarTimer.countDownTimers){
                            if (countDownTimer.getCreator().equalsIgnoreCase(commandSender.getName())){
                                complete.add(countDownTimer.getName());
                            }
                        }
                    }else {
                        for (final CountUpTimer countUpTimer : BossBarTimer.countUpTimers){
                            if (countUpTimer.getCreator().equalsIgnoreCase(commandSender.getName())){
                                if (countUpTimer.getName().startsWith(strings[1])){
                                    complete.add(countUpTimer.getName());
                                }
                            }
                        }
                        for (final CountDownTimer countDownTimer : BossBarTimer.countDownTimers){
                            if (countDownTimer.getCreator().equalsIgnoreCase(commandSender.getName())){
                                if (countDownTimer.getName().startsWith(strings[1])){
                                    complete.add(countDownTimer.getName());
                                }
                            }
                        }
                    }
                }
            }else if (strings[0].equalsIgnoreCase("delete")){
                if (commandSender.hasPermission("timer.commands.delete")){
                    if (strings[1].isEmpty()){
                        for (final CountUpTimer countUpTimer : BossBarTimer.countUpTimers){
                            complete.add(countUpTimer.getName());
                        }
                        for (final CountDownTimer countDownTimer : BossBarTimer.countDownTimers){
                            complete.add(countDownTimer.getName());
                        }
                    }else {
                        for (final CountUpTimer countUpTimer : BossBarTimer.countUpTimers){
                            if (countUpTimer.getName().startsWith(strings[1])){
                                complete.add(countUpTimer.getName());
                            }
                        }
                        for (final CountDownTimer countDownTimer : BossBarTimer.countDownTimers){
                            if (countDownTimer.getName().startsWith(strings[1])){
                                complete.add(countDownTimer.getName());
                            }
                        }
                    }
                }
            }
        }else if (strings.length == 3){
            if (strings[0].equalsIgnoreCase("upcreate")){
                if (commandSender.hasPermission("timer.commands.create")){
                    if (strings[2].isEmpty()){
                        complete.add("true");
                        complete.add("false");
                    }else if (strings[2].startsWith("t")){
                        complete.add("true");
                    }else if (strings[2].startsWith("f")){
                        complete.add("false");
                    }
                }
            }
        }else if (strings.length == 4){
            if (strings[0].equalsIgnoreCase("downcreate")){
                if (commandSender.hasPermission("timer.commands.create")){
                    if (strings[3].isEmpty()){
                        complete.add("true");
                        complete.add("false");
                    }else if (strings[3].startsWith("t")){
                        complete.add("true");
                    }else if (strings[3].startsWith("f")){
                        complete.add("false");
                    }
                }
            }
        }
        return complete;
    }

    private void noPermission(CommandSender commandSender){
        commandSender.sendMessage(MiniMessage.miniMessage().deserialize("<red>あなたにはこのコマンドを実行するための権限がありません．</red>"));
    }

    private boolean isNumeric(String time){
        return time.chars().allMatch(Character::isDigit);
    }

    private boolean isNameUsed(String name){
        final BossBar bossBar = BossBarTimer.plugin.getServer().getBossBar(new NamespacedKey("minecraft", name));
        return bossBar != null;
    }

    private void sendUsage(CommandSender commandSender){
        commandSender.sendMessage(MiniMessage.miniMessage().deserialize("<yellow>----- BossBarTimer Command Usage -----"));
        commandSender.sendMessage(MiniMessage.miniMessage().deserialize("<yellow>/timer upcreate <name> [true | false]"));
        commandSender.sendMessage(MiniMessage.miniMessage().deserialize("<yellow>/timer downcreate <name> <time:second> [true | false]"));
        commandSender.sendMessage(MiniMessage.miniMessage().deserialize("<yellow>/timer remove <name>"));
        commandSender.sendMessage(MiniMessage.miniMessage().deserialize("<yellow>/timer delete <name>"));
        commandSender.sendMessage(MiniMessage.miniMessage().deserialize("<yellow>/timer reload"));
    }
}
