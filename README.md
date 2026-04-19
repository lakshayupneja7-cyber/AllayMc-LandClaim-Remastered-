# AllayMc Land Claim Remastered

A progression-driven claims enhancement plugin for Paper servers using GriefPrevention.

## Features

- Claim tier system based on total player claim blocks
- Per-claim active perk selection
- 15 launch perks across Tier I to Tier V
- Tier VI to Tier X marked as Coming Soon
- GriefPrevention integration
- SQLite by default, MySQL-ready architecture
- GUI-first design
- Placeholder/TAB-ready structure

## Current design

- Tier unlocks come from the player's total claim blocks
- Perks are selected per claim area
- Only one perk can be active per claim at a time
- Perks only apply inside the selected claim
- Trusted members can benefit based on claim settings

## Build

```bash
./gradlew build
