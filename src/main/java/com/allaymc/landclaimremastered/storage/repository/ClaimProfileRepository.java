package com.allaymc.landclaimremastered.storage.repository;

import com.allaymc.landclaimremastered.model.ClaimProfile;
import com.allaymc.landclaimremastered.model.ClaimTrustMode;
import com.allaymc.landclaimremastered.model.PerkKey;
import com.allaymc.landclaimremastered.storage.DatabaseManager;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Optional;
import java.util.UUID;

public final class ClaimProfileRepository {

    private final DatabaseManager databaseManager;

    public ClaimProfileRepository(DatabaseManager databaseManager) {
        this.databaseManager = databaseManager;
    }

    public Optional<ClaimProfile> findById(String claimId) {
        String sql = "SELECT claim_id, owner_uuid, display_name, selected_perk, trust_mode FROM claim_profile WHERE claim_id = ?";

        try (PreparedStatement statement = databaseManager.connection().prepareStatement(sql)) {
            statement.setString(1, claimId);

            try (ResultSet resultSet = statement.executeQuery()) {
                if (!resultSet.next()) {
                    return Optional.empty();
                }

                ClaimProfile profile = new ClaimProfile(
                        resultSet.getString("claim_id"),
                        UUID.fromString(resultSet.getString("owner_uuid"))
                );

                profile.setName(resultSet.getString("display_name"));

                String perk = resultSet.getString("selected_perk");
                if (perk != null && !perk.isBlank()) {
                    profile.setSelectedPerk(PerkKey.valueOf(perk));
                }

                profile.setTrustMode(ClaimTrustMode.valueOf(resultSet.getString("trust_mode")));
                return Optional.of(profile);
            }
        } catch (SQLException exception) {
            throw new RuntimeException("Failed to load claim profile", exception);
        }
    }

    public void save(ClaimProfile profile) {
        String sql = """
            INSERT INTO claim_profile (claim_id, owner_uuid, display_name, selected_perk, trust_mode)
            VALUES (?, ?, ?, ?, ?)
            ON CONFLICT(claim_id) DO UPDATE SET
                owner_uuid = excluded.owner_uuid,
                display_name = excluded.display_name,
                selected_perk = excluded.selected_perk,
                trust_mode = excluded.trust_mode
        """;

        try (PreparedStatement statement = databaseManager.connection().prepareStatement(sql)) {
            statement.setString(1, profile.getClaimId());
            statement.setString(2, profile.getOwnerUuid().toString());
            statement.setString(3, profile.getName());
            statement.setString(4, profile.getSelectedPerk() == null ? null : profile.getSelectedPerk().name());
            statement.setString(5, profile.getTrustMode().name());
            statement.executeUpdate();
        } catch (SQLException exception) {
            throw new RuntimeException("Failed to save claim profile", exception);
        }
    }
}
