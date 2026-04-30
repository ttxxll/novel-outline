package com.noveloutline.common.dto;

import lombok.Data;

import java.util.List;

@Data
public class Items {
    public List<WeaponEntry> weapons;
    public List<TechniqueEntry> techniques;
    public List<ElixirEntry> elixirs;
    public List<SpiritBeastEntry> spiritBeasts;
    public List<SpiritHerbEntry> spiritHerbs;
}
