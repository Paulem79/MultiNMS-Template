package com.example.plugin.nms.nms_1_20_6;

import com.example.plugin.nms.Nms;
import org.bukkit.Bukkit;
import org.bukkit.craftbukkit.v1_20_R4.CraftServer;

public class NmsImpl implements Nms {
    @Override
    public double getTPS(int i) {
        return ((CraftServer) Bukkit.getServer()).getServer().recentTps[i];
    }
}