package com.pacchetto.util;

import org.bukkit.Location;
import org.bukkit.World;

import java.util.Objects;

public final class LocationKey {
    private final String world;
    private final int x;
    private final int y;
    private final int z;

    public LocationKey(String world, int x, int y, int z) {
        this.world = world;
        this.x = x;
        this.y = y;
        this.z = z;
    }

    public static LocationKey of(Location location) {
        return new LocationKey(location.getWorld().getName(), location.getBlockX(), location.getBlockY(), location.getBlockZ());
    }

    public String serialize() {
        return world + ":" + x + ":" + y + ":" + z;
    }

    public static LocationKey deserialize(String raw) {
        String[] parts = raw.split(":");
        return new LocationKey(parts[0], Integer.parseInt(parts[1]), Integer.parseInt(parts[2]), Integer.parseInt(parts[3]));
    }

    public Location toLocation(World worldObj) {
        return new Location(worldObj, x, y, z);
    }

    public String world() { return world; }
    public int x() { return x; }
    public int y() { return y; }
    public int z() { return z; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof LocationKey that)) return false;
        return x == that.x && y == that.y && z == that.z && world.equals(that.world);
    }

    @Override
    public int hashCode() {
        return Objects.hash(world, x, y, z);
    }
}
