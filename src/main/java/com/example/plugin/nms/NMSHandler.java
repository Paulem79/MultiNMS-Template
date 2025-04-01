package com.example.plugin.nms;

import org.bukkit.Bukkit;

import java.util.Map;
import java.util.Objects;
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

    /**
     * Redirects the Minecraft version to the latest version of the same major version.
     * Eg : 1.20 -> 1.20.1, 1.20.3 -> 1.20.4, 1.20.5 -> 1.20.6...
     * This is important because if the server has a CraftBukkit version package-named like the previous/next version, then we need to load the correct NMS class instead of throwing an error.
     */
    private static final Map<String, String> redirectors = Map.of(
            "1.20", "1.20.1",
            "1.20.3", "1.20.4",
            "1.20.5", "1.20.6",
            "1.21", "1.21.1",
            "1.21.2", "1.21.3"
    );

    public static Nms getNmsImpl() throws RuntimeException {
        // Get the NMS implementation class name based on the Minecraft version using reflection
        // If the version isn't in redirectors, then it is null and we use the actual version
        String clazzName = "com.example.plugin.nms.nms_" + redirectors.getOrDefault(getMinecraftVersion(), getMinecraftVersion())
                .replace(".", "_") + ".NmsImpl";
        try {
            Class<? extends Nms> clazz = (Class<? extends Nms>) Class.forName(clazzName);
            return clazz.getConstructor().newInstance();
        } catch (ClassNotFoundException exception) {
            throw new RuntimeException("This Minecraft version (" + getMinecraftVersion() +
                    ") is not supported by this version of the plugin)", exception);
        } catch (ReflectiveOperationException exception) {
            throw new RuntimeException(exception);
        }
    }
}
