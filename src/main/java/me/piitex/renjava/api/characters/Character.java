package me.piitex.renjava.api.characters;

import me.piitex.engine.ui.color.Color;
import me.piitex.engine.ui.color.Paint;
import me.piitex.renjava.api.saves.data.Data;

public abstract class Character {
    private final String id; // The ID must be unique. The ID system allows you to have multiple characters with the same name.
    @Data private String name; // This is the name display for the character.
    private final Paint color;
    @Data private String displayName;

    public Character(String id, String name, Paint color) {
        this.id = id;
        this.name = name;
        this.color = color;
        this.displayName = name;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
        setDisplayName(name);
    }

    public Paint getColor() {
        return color;
    }

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }
}
