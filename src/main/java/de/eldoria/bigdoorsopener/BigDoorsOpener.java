package de.eldoria.bigdoorsopener;

import de.eldoria.bigdoorsopener.commands.BigDoorsOpenerCommand;
import de.eldoria.bigdoorsopener.config.Config;
import de.eldoria.bigdoorsopener.config.TimedDoor;
import de.eldoria.bigdoorsopener.localization.Localizer;
import de.eldoria.bigdoorsopener.scheduler.DoorApproachScheduler;
import de.eldoria.bigdoorsopener.scheduler.TimedDoorScheduler;
import de.eldoria.bigdoorsopener.util.UpdateChecker;
import nl.pim16aap2.bigDoors.BigDoors;
import nl.pim16aap2.bigDoors.Commander;
import org.bstats.bukkit.Metrics;
import org.bukkit.Bukkit;
import org.bukkit.configuration.serialization.ConfigurationSerialization;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitScheduler;
import org.jetbrains.annotations.NotNull;

import java.util.Locale;
import java.util.logging.Logger;

public class BigDoorsOpener extends JavaPlugin {

    private static @NotNull Logger logger;
    private final BukkitScheduler scheduler = Bukkit.getScheduler();
    private Config config;
    private boolean initialized;
    private Localizer localizer;

    private BigDoors doors;
    private Commander commander;

    @Override
    public void onDisable() {
        super.onDisable();
    }

    @Override
    public void onEnable() {
        if (!initialized) {
            logger = this.getLogger();
            UpdateChecker.performAndNotifyUpdateCheck(this, 80805);
            ConfigurationSerialization.registerClass(TimedDoor.class, "timedDoor");
            PluginManager pm = Bukkit.getPluginManager();
            Plugin bigDoorsPlugin = pm.getPlugin("BigDoors");
            doors = (BigDoors) bigDoorsPlugin;
            commander = doors.getCommander();
            config = new Config(this);
            localizer = new Localizer(this, config.getLanguage(), "messages",
                    "messages", Locale.US, "de_DE", "en_US");
            if (config.isEnableMetrics()) {
                enableMetrics();
            }
        }

        TimedDoorScheduler timedDoorScheduler = new TimedDoorScheduler(doors, config, localizer);

        if (!initialized) {
            getCommand("bigdoorsopener")
                    .setExecutor(new BigDoorsOpenerCommand(this, commander, config, localizer, timedDoorScheduler));
        }

        if (initialized) {
            localizer.setLocale(config.getLanguage());
            scheduler.cancelTasks(this);
        }

        scheduler.scheduleSyncRepeatingTask(this, new DoorApproachScheduler(config, doors, localizer), 100, config.getApproachRefreshRate());
        scheduler.scheduleSyncRepeatingTask(this, timedDoorScheduler, 100, config.getTimedRefreshRate());

        initialized = true;
    }


    @NotNull
    public static Logger logger() {
        return logger;
    }

    private void enableMetrics() {
        Metrics metrics = new Metrics(this, 8015);

        logger().info(localizer.getMessage("general.metrics"));

        metrics.addCustomChart(new Metrics.SimplePie("big_doors_version",
                () -> doors.getDescription().getVersion()));
    }
}
