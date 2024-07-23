package com.bibireden.playerex.registry;

import io.wispforest.owo.ui.core.Component;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public final class AttributesMenuRegistry {
    @NotNull
    private static final List<List<Component>> entries = new ArrayList<>();

    public static void register(@NotNull List<Component> menu) {
        entries.add(menu);
    }

    @NotNull
    public static List<List<Component>> get() {
        return entries;
    }
}
