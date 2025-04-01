package com.example.plugin.nms.nms_1_21_3;

import com.example.plugin.nms.Nms;
import org.bukkit.Bukkit;
import org.bukkit.craftbukkit.v1_21_R2.CraftServer;

public class NmsImpl implements Nms {
    @Override
    public double getTPS(int i) {
        return ((CraftServer) Bukkit.getServer()).getServer().recentTps[i];
    }
}