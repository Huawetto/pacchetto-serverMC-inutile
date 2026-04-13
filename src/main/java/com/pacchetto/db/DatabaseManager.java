package com.pacchetto.db;

import com.pacchetto.core.ServerMCPlugin;
import com.pacchetto.machine.MachineState;
import com.pacchetto.machine.MachineType;
import com.pacchetto.util.LocationKey;

import java.io.File;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class DatabaseManager {
    private final ServerMCPlugin plugin;
    private Connection connection;

    public DatabaseManager(ServerMCPlugin plugin) {
        this.plugin = plugin;
    }

    public void init() {
        try {
            File dbFile = new File(plugin.getDataFolder(), "machines.db");
            if (!dbFile.getParentFile().exists()) dbFile.getParentFile().mkdirs();
            this.connection = DriverManager.getConnection("jdbc:sqlite:" + dbFile.getAbsolutePath());
            try (Statement st = connection.createStatement()) {
                st.executeUpdate("CREATE TABLE IF NOT EXISTS machines (loc TEXT PRIMARY KEY, type TEXT NOT NULL, level INTEGER NOT NULL)");
            }
        } catch (SQLException e) {
            throw new IllegalStateException("Database initialization failed", e);
        }
    }

    public void upsertMachine(MachineState state) {
        try (PreparedStatement ps = connection.prepareStatement("INSERT INTO machines(loc,type,level) VALUES(?,?,?) ON CONFLICT(loc) DO UPDATE SET type=excluded.type, level=excluded.level")) {
            ps.setString(1, state.key().serialize());
            ps.setString(2, state.type().name());
            ps.setInt(3, state.level());
            ps.executeUpdate();
        } catch (SQLException e) {
            plugin.getLogger().warning("upsert failed: " + e.getMessage());
        }
    }

    public List<MachineState> loadMachines() {
        List<MachineState> out = new ArrayList<>();
        try (PreparedStatement ps = connection.prepareStatement("SELECT loc,type,level FROM machines");
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                LocationKey key = LocationKey.deserialize(rs.getString("loc"));
                MachineType type = MachineType.valueOf(rs.getString("type"));
                int level = rs.getInt("level");
                out.add(new MachineState(key, type, level));
            }
        } catch (SQLException e) {
            plugin.getLogger().warning("load failed: " + e.getMessage());
        }
        return out;
    }

    public void deleteMachine(LocationKey key) {
        try (PreparedStatement ps = connection.prepareStatement("DELETE FROM machines WHERE loc=?")) {
            ps.setString(1, key.serialize());
            ps.executeUpdate();
        } catch (SQLException e) {
            plugin.getLogger().warning("delete failed: " + e.getMessage());
        }
    }

    public void close() {
        if (connection != null) {
            try { connection.close(); } catch (SQLException ignored) {}
        }
    }
}
