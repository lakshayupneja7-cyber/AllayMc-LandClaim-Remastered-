package com.allaymc.landclaimremastered.storage.repository;

import com.allaymc.landclaimremastered.storage.DatabaseManager;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.UUID;

public final class PlayerProgressRepository {

    private final DatabaseManager databaseManager;

    public PlayerProgressRepository(DatabaseManager databaseManager) {
        this.databaseManager = databaseManager;
    }

    public void upsert(UUID uuid, int totalBlocks, int tier) {
        String sql = """
            INSERT INTO player_progress (player_uuid, total_claim_blocks, current_tier, updated_at)
            VALUES (?, ?, ?, CURRENT_TIMESTAMP)
            ON CONFLICT(player_uuid) DO UPDATE SET
              total_claim_blocks = excluded.total_claim_blocks,
              current_tier = excluded.current_tier,
              updated_at = CURRENT_TIMESTAMP
        """;
        try (PreparedStatement ps = databaseManager.connection().prepareStatement(sql)) {
            ps.setString(1, uuid.toString());
            ps.setInt(2, totalBlocks);
            ps.setInt(3, tier);
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Failed to save player progress", e);
        }
    }
}
