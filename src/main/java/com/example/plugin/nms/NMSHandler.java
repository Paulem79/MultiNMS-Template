package com.example.plugin.nms;

import com.example.plugin.nms.nms_1_19_4.NmsImpl;
import org.bukkit.Bukkit;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class NMSHandler {
    private static String minecraftVersion;

    /**
     * Returns the actual running Minecraft version, e.g. 1.20 or 1.16.5
     *
     * @return Minecraft version
     */
    private static String getMinecraftVersion() throws RuntimeException {
        if (minecraftVersion != null) {
            return minecraftVersion;
        } else {
            String bukkitGetVersionOutput = Bukkit.getVersion();
            Matcher matcher = Pattern.compile("\\(MC: (?<version>[\\d]+\\.[\\d]+(\\.[\\d]+)?)\\)").matcher(bukkitGetVersionOutput);
            if (matcher.find()) {
                return minecraftVersion = matcher.group("version");
            } else {
                throw new RuntimeException("Could not determine Minecraft version from Bukkit.getVersion(): " + bukkitGetVersionOutput);
            }
        }
    }

    public static Nms getNmsImpl() throws RuntimeException {
        // You might want to use reflection to load the class instead of "hardcoding" the classes
        // See https://github.com/mfnalex/Spigot-Maven-Modules-Example/blob/master/core/src/main/java/com/jeff_media/mymultiversionplugin/MyMultiversionPlugin.java#L50
        return switch (getMinecraftVersion()) {
            case "1.20.3", "1.20.4" -> new com.example.plugin.nms.nms_1_20_4.NmsImpl();
            case "1.20.2" -> new com.example.plugin.nms.nms_1_20_2.NmsImpl();
            case "1.20", "1.20.1" -> new com.example.plugin.nms.nms_1_20_1.NmsImpl();
            case "1.19.4" -> new NmsImpl();
            default -> throw new RuntimeException("Unexpected value: " + getMinecraftVersion());
        };
    }
}
