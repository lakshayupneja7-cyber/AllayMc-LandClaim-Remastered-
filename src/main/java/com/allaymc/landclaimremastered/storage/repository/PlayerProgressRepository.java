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

    public void upsert(UUID playerUuid, int totalClaimBlocks, int currentTier) {
        String sql = """
            INSERT INTO player_progress (player_uuid, total_claim_blocks, current_tier, updated_at)
            VALUES (?, ?, ?, CURRENT_TIMESTAMP)
            ON CONFLICT(player_uuid) DO UPDATE SET
                total_claim_blocks = excluded.total_claim_blocks,
                current_tier = excluded.current_tier,
                updated_at = CURRENT_TIMESTAMP
        """;

        try (PreparedStatement statement = databaseManager.connection().prepareStatement(sql)) {
            statement.setString(1, playerUuid.toString());
            statement.setInt(2, totalClaimBlocks);
            statement.setInt(3, currentTier);
            statement.executeUpdate();
        } catch (SQLException exception) {
            throw new RuntimeException("Failed to upsert player progress", exception);
        }
    }
}
