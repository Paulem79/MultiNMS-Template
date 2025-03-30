package com.example.plugin;

import com.example.plugin.nms.NMSHandler;
import com.example.plugin.nms.Nms;
import org.bukkit.plugin.java.JavaPlugin;

public class MyPlugin extends JavaPlugin {
    public static Nms nmsImpl;

    @Override
    public void onEnable() {
        nmsImpl = NMSHandler.getNmsImpl();

        getLogger().info("Last TPS: " + nmsImpl.getTPS(0));

        // Rest of plugin startup logic
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }
}