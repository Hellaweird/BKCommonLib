package com.bergerkiller.bukkit.common.internal.logic;

import java.util.concurrent.CompletableFuture;
import java.util.logging.Level;

import org.bukkit.World;

import com.bergerkiller.bukkit.common.Logging;
import com.bergerkiller.bukkit.common.component.LibraryComponentSelector;
import com.bergerkiller.bukkit.common.lighting.LightingHandler;

class LightingHandlerDisabled implements LightingHandler {
    private final Throwable cause;

    public LightingHandlerDisabled(LibraryComponentSelector<?, ?> selector) {
        this(selector.getLastError());
    }

    public LightingHandlerDisabled(Throwable cause) {
        this(cause, true);
    }

    public LightingHandlerDisabled(Throwable cause, boolean logCause) {
        this.cause = cause;
        if (logCause && cause != null) {
            Logging.LOGGER_REFLECTION.log(Level.SEVERE, "Failed to initialize lighting handler", cause);
        }
    }

    @Override
    public void enable() {
    }

    @Override
    public void disable() {
    }

    private UnsupportedOperationException fail() {
        return new UnsupportedOperationException("Failed to initialize lighting handler, BKCommonLib does not support this server", cause);
    }

    @Override
    public boolean isSupported(World world) {
        return false;
    }

    @Override
    public CompletableFuture<Void> setSectionSkyLightAsync(World world, int cx, int cy, int cz, byte[] data) {
        throw fail();
    }

    @Override
    public CompletableFuture<Void> setSectionBlockLightAsync(World world, int cx, int cy, int cz, byte[] data) {
        throw fail();
    }

    @Override
    public byte[] getSectionSkyLight(World world, int cx, int cy, int cz) {
        throw fail();
    }

    @Override
    public byte[] getSectionBlockLight(World world, int cx, int cy, int cz) {
        throw fail();
    }
}
