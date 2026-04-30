package com.noveloutline.common.dto;

import java.util.List;

public class ChapterAnalysisResult {

    public static class CharacterEntry {
        public String name;
        public String role;
        public String relationshipToProtagonist;
        public String action;
    }

    public static class FactionEntry {
        public String name;
        public String type;
        public String stanceTowardProtagonist;
        public List<String> members;
        public String description;
    }

    public static class WeaponEntry {
        public String name;
        public String grade;
        public String significance;
    }

    public static class TechniqueEntry {
        public String name;
        public String grade;
        public String significance;
    }

    public static class ElixirEntry {
        public String name;
        public String grade;
        public String significance;
    }

    public static class SpiritBeastEntry {
        public String name;
        public String level;
        public String significance;
    }

    public static class Items {
        public List<WeaponEntry> weapons;
        public List<TechniqueEntry> techniques;
        public List<ElixirEntry> elixirs;
        public List<SpiritBeastEntry> spiritBeasts;
    }

    public static class LocationEntry {
        public String name;
        public String type;
        public String events;
    }

    public static class ConflictEntry {
        public String type;
        public List<String> parties;
        public String cause;
        public String result;
    }

    public static class ForeshadowingEntry {
        public String description;
        public String hint;
        public String likelihood;
    }

    public String summary;
    public List<CharacterEntry> characters;
    public List<FactionEntry> factions;
    public Items items;
    public List<LocationEntry> locations;
    public List<ConflictEntry> conflicts;
    public List<ForeshadowingEntry> foreshadowing;
}
