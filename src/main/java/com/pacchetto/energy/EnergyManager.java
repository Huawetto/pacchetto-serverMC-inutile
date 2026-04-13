package com.pacchetto.energy;

import com.pacchetto.core.ServerMCPlugin;
import com.pacchetto.util.LocationKey;
import org.bukkit.Bukkit;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class EnergyManager {
    private final Map<LocationKey, EnergyNode> nodes = new ConcurrentHashMap<>();

    public EnergyManager(ServerMCPlugin plugin) {
        Bukkit.getScheduler().runTaskTimer(plugin, this::balanceNetwork, 20L, 20L);
    }

    public void createNode(LocationKey key) {
        nodes.computeIfAbsent(key, EnergyNode::new);
        for (LocationKey nearby : nearby(key)) {
            EnergyNode n = nodes.get(nearby);
            if (n != null) {
                nodes.get(key).links().add(nearby);
                n.links().add(key);
            }
        }
    }

    public void removeNode(LocationKey key) {
        EnergyNode removed = nodes.remove(key);
        if (removed != null) {
            for (LocationKey link : removed.links()) {
                EnergyNode n = nodes.get(link);
                if (n != null) n.links().remove(key);
            }
        }
    }

    public void inject(LocationKey key, int amount) {
        EnergyNode node = nodes.get(key);
        if (node != null) node.add(amount);
    }

    public int pull(LocationKey key, int amount) {
        EnergyNode node = nodes.get(key);
        return node == null ? 0 : node.take(amount);
    }

    public int totalStored() {
        return nodes.values().stream().mapToInt(EnergyNode::stored).sum();
    }

    private void balanceNetwork() {
        for (EnergyNode node : nodes.values()) {
            if (node.links().isEmpty() || node.stored() < 100) continue;
            int split = node.stored() / Math.max(2, node.links().size() + 1);
            if (split < 20) continue;
            for (LocationKey link : node.links()) {
                EnergyNode target = nodes.get(link);
                if (target == null || target.stored() > node.stored()) continue;
                int moved = node.take(split / 2);
                target.add(moved);
            }
        }
    }

    private List<LocationKey> nearby(LocationKey key) {
        List<LocationKey> out = new ArrayList<>();
        for (int dx = -2; dx <= 2; dx++) {
            for (int dy = -1; dy <= 1; dy++) {
                for (int dz = -2; dz <= 2; dz++) {
                    if (Math.abs(dx) + Math.abs(dy) + Math.abs(dz) == 0) continue;
                    out.add(new LocationKey(key.world(), key.x() + dx, key.y() + dy, key.z() + dz));
                }
            }
        }
        return out;
    }
}
