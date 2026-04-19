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

    public ClaimProfile(String claimId, UUID ownerUuid, String defaultName, ClaimTrustMode defaultTrustMode) {
        this.claimId = claimId;
        this.ownerUuid = ownerUuid;
        this.name = defaultName;
        this.trustMode = defaultTrustMode;
        this.perkWhitelist = new HashSet<>();
    }

    public String getClaimId() { return claimId; }
    public UUID getOwnerUuid() { return ownerUuid; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public PerkKey getSelectedPerk() { return selectedPerk; }
    public void setSelectedPerk(PerkKey selectedPerk) { this.selectedPerk = selectedPerk; }
    public ClaimTrustMode getTrustMode() { return trustMode; }
    public void setTrustMode(ClaimTrustMode trustMode) { this.trustMode = trustMode; }
    public Set<UUID> getPerkWhitelist() { return perkWhitelist; }
}
