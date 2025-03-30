package com.example.plugin.nms.nms_1_20_1;

import com.example.plugin.nms.Nms;
import net.minecraft.server.MinecraftServer;

public class NmsImpl implements Nms {
    @Override
    public double getTPS(int i) {
        return MinecraftServer.getServer().recentTps[i];
    }
}