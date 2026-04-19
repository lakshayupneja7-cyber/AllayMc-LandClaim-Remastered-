package com.allaymc.landclaimremastered.model;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

public final class ClaimProfile {

    private final String claimId;
    private final UUID ownerUuid;
    private String name;
    private PerkKey selectedPerk;
    private ClaimTrustMode trustMode;
    private final Set<UUID> perkWhitelist;

    public ClaimProfile(String claimId, UUID ownerUuid) {
        this.claimId = claimId;
        this.ownerUuid = ownerUuid;
        this.name = "Unnamed Claim";
        this.selectedPerk = null;
        this.trustMode = ClaimTrustMode.ALL_TRUSTED;
        this.perkWhitelist = new HashSet<>();
    }

    public String getClaimId() {
        return claimId;
    }

    public UUID getOwnerUuid() {
        return ownerUuid;
    }

    public String getName() {
        return name;
    }

    public PerkKey getSelectedPerk() {
        return selectedPerk;
    }

    public ClaimTrustMode getTrustMode() {
        return trustMode;
    }

    public Set<UUID> getPerkWhitelist() {
        return perkWhitelist;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setSelectedPerk(PerkKey selectedPerk) {
        this.selectedPerk = selectedPerk;
    }

    public void setTrustMode(ClaimTrustMode trustMode) {
        this.trustMode = trustMode;
    }
}
