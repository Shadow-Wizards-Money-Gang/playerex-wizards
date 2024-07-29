package com.bibireden.playerex.registry;

import com.bibireden.playerex.ui.components.MenuComponent;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public final class PlayerEXMenuRegistry {
    @NotNull
    private static final List<Class<? extends MenuComponent>> ENTRIES = new ArrayList<>();

    /**
     * Registers a {@link MenuComponent} to the registry,
     * which will be applied to the {@link com.bibireden.playerex.ui.PlayerEXScreen} as a page.
     */
    public static void register(@NotNull Class<? extends MenuComponent> menu) { ENTRIES.add(menu); }

    @NotNull
    public static List<Class<? extends MenuComponent>> get() {
        return ENTRIES;
    }
}
