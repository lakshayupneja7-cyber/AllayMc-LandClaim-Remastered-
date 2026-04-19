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
        register(new PerkDefinition(PerkKey.VERDANT_PULSE, "Verdant Pulse", "Crop growth bonus inside this claim.", Tier.I, Material.WHEAT));
        register(new PerkDefinition(PerkKey.IRON_RHYTHM, "Iron Rhythm", "Smelting utility for productive lands.", Tier.I, Material.BLAST_FURNACE));
        register(new PerkDefinition(PerkKey.SKYBOUND, "Skybound", "Jump Boost I inside this claim.", Tier.I, Material.RABBIT_FOOT));
        register(new PerkDefinition(PerkKey.TRAILBLAZER, "Trailblazer", "Minor movement utility.", Tier.II, Material.LEATHER_BOOTS));
        register(new PerkDefinition(PerkKey.STONEHEART, "Stoneheart", "Minor PvE resistance.", Tier.II, Material.IRON_CHESTPLATE));
        register(new PerkDefinition(PerkKey.DEEP_FOCUS, "Deep Focus", "Minor mining focus bonus.", Tier.II, Material.IRON_PICKAXE));
        register(new PerkDefinition(PerkKey.WINDSTEP, "Windstep", "Speed I inside this claim.", Tier.III, Material.SUGAR));
        register(new PerkDefinition(PerkKey.HEARTHWARMTH, "Hearthwarmth", "Food feels lighter on your land.", Tier.III, Material.COOKED_BEEF));
        register(new PerkDefinition(PerkKey.MOONSIGHT, "MoonSight", "Night Vision inside this claim.", Tier.III, Material.ENDER_EYE));
        register(new PerkDefinition(PerkKey.FEATHERFALL_WARD, "Featherfall Ward", "Reduced fall damage inside this claim.", Tier.IV, Material.FEATHER));
        register(new PerkDefinition(PerkKey.BUILDERS_GRACE, "Builder's Grace", "Haste I inside this claim.", Tier.IV, Material.GOLDEN_PICKAXE));
        register(new PerkDefinition(PerkKey.HEARTHLIGHT, "Hearthlight", "Regeneration I inside this claim.", Tier.IV, Material.GLISTERING_MELON_SLICE));
        register(new PerkDefinition(PerkKey.STORMSTRIDE, "Stormstride", "Speed II inside this claim.", Tier.V, Material.LIGHTNING_ROD));
        register(new PerkDefinition(PerkKey.TITAN_BLOOD, "Titan Blood", "Strength I inside this claim.", Tier.V, Material.NETHERITE_SWORD));
        register(new PerkDefinition(PerkKey.EVERGLOW, "Everglow", "Regeneration and Night Vision combined.", Tier.V, Material.BEACON));
    }

    public void register(PerkDefinition definition) { perks.put(definition.key(), definition); }
    public Optional<PerkDefinition> find(PerkKey key) { return Optional.ofNullable(perks.get(key)); }
    public Collection<PerkDefinition> all() { return perks.values(); }
}
