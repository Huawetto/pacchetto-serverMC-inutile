package com.pacchetto.energy;

import com.pacchetto.util.LocationKey;

import java.util.HashSet;
import java.util.Set;

public class EnergyNode {
    private final LocationKey key;
    private int stored;
    private final Set<LocationKey> links = new HashSet<>();

    public EnergyNode(LocationKey key) {
        this.key = key;
    }

    public LocationKey key() { return key; }
    public int stored() { return stored; }
    public void add(int amount) { stored = Math.min(100000, stored + Math.max(0, amount)); }
    public int take(int amount) {
        int v = Math.min(stored, Math.max(0, amount));
        stored -= v;
        return v;
    }
    public Set<LocationKey> links() { return links; }
}
