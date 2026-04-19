package com.allaymc.landclaimremastered.perks;

import com.allaymc.landclaimremastered.model.PerkDefinition;
import com.allaymc.landclaimremastered.model.PerkKey;
import com.allaymc.landclaimremastered.model.Tier;
import org.bukkit.Material;

import java.util.Collection;
import java.util.EnumMap;
import java.util.Map;
import java.util.Optional;

public final class PerkRegistry {

    private final Map<PerkKey, PerkDefinition> perks = new EnumMap<>(PerkKey.class);

    public void registerDefaults() {
        register(new PerkDefinition(PerkKey.VERDANT_PULSE, "Verdant Pulse", "Crop growth bonus inside this claim.", Tier.I, Material.WHEAT, false));
        register(new PerkDefinition(PerkKey.IRON_RHYTHM, "Iron Rhythm", "Smelting utility for productive lands.", Tier.I, Material.BLAST_FURNACE, false));
        register(new PerkDefinition(PerkKey.SKYBOUND, "Skybound", "Jump higher inside your claim.", Tier.I, Material.RABBIT_FOOT, false));

        register(new PerkDefinition(PerkKey.TRAILBLAZER, "Trailblazer", "Minor movement utility perk.", Tier.II, Material.LEATHER_BOOTS, false));
        register(new PerkDefinition(PerkKey.STONEHEART, "Stoneheart", "Minor PvE resistance inside your claim.", Tier.II, Material.IRON_CHESTPLATE, false));
        register(new PerkDefinition(PerkKey.DEEP_FOCUS, "Deep Focus", "Minor work and mining bonus.", Tier.II, Material.IRON_PICKAXE, false));

        register(new PerkDefinition(PerkKey.WINDSTEP, "Windstep", "Speed I inside your claim.", Tier.III, Material.SUGAR, false));
        register(new PerkDefinition(PerkKey.HEARTHWARMTH, "Hearthwarmth", "Reduced hunger drain.", Tier.III, Material.COOKED_BEEF, false));
        register(new PerkDefinition(PerkKey.MOONSIGHT, "MoonSight", "Night vision inside your claim.", Tier.III, Material.ENDER_EYE, false));

        register(new PerkDefinition(PerkKey.FEATHERFALL_WARD, "Featherfall Ward", "Reduced fall damage inside your claim.", Tier.IV, Material.FEATHER, false));
        register(new PerkDefinition(PerkKey.BUILDERS_GRACE, "Builder's Grace", "Haste I inside your claim.", Tier.IV, Material.GOLDEN_PICKAXE, false));
        register(new PerkDefinition(PerkKey.HEARTHLIGHT, "Hearthlight", "Regeneration I inside your claim.", Tier.IV, Material.GLISTERING_MELON_SLICE, false));

        register(new PerkDefinition(PerkKey.STORMSTRIDE, "Stormstride", "Speed II inside your claim.", Tier.V, Material.LIGHTNING_ROD, false));
        register(new PerkDefinition(PerkKey.TITAN_BLOOD, "Titan Blood", "Strength I inside your claim.", Tier.V, Material.NETHERITE_SWORD, false));
        register(new PerkDefinition(PerkKey.EVERGLOW, "Everglow", "Regeneration and night vision combined.", Tier.V, Material.BEACON, false));
    }

    public void register(PerkDefinition definition) {
        perks.put(definition.key(), definition);
    }

    public Optional<PerkDefinition> find(PerkKey key) {
        return Optional.ofNullable(perks.get(key));
    }

    public Collection<PerkDefinition> all() {
        return perks.values();
    }
}
