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
        try (PreparedStatement ps = databaseManager.connection().prepareStatement(
                "SELECT claim_id, owner_uuid, display_name, selected_perk, trust_mode FROM claim_profile WHERE claim_id = ?")) {
            ps.setString(1, claimId);
            try (ResultSet rs = ps.executeQuery()) {
                if (!rs.next()) return Optional.empty();
                ClaimProfile profile = new ClaimProfile(
                        rs.getString("claim_id"),
                        UUID.fromString(rs.getString("owner_uuid")),
                        rs.getString("display_name"),
                        ClaimTrustMode.valueOf(rs.getString("trust_mode"))
                );
                String perk = rs.getString("selected_perk");
                if (perk != null && !perk.isBlank()) {
                    profile.setSelectedPerk(PerkKey.valueOf(perk));
                }
                return Optional.of(profile);
            }
        } catch (SQLException e) {
            throw new RuntimeException("Failed to load claim profile", e);
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
        try (PreparedStatement ps = databaseManager.connection().prepareStatement(sql)) {
            ps.setString(1, profile.getClaimId());
            ps.setString(2, profile.getOwnerUuid().toString());
            ps.setString(3, profile.getName());
            ps.setString(4, profile.getSelectedPerk() == null ? null : profile.getSelectedPerk().name());
            ps.setString(5, profile.getTrustMode().name());
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Failed to save claim profile", e);
        }
    }
}
