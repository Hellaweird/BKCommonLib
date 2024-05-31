package com.bergerkiller.bukkit.common.internal.logic;

import java.util.Arrays;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

import com.bergerkiller.bukkit.common.protocol.PlayerGameInfo;
import com.viaversion.viaversion.api.Via;
import com.viaversion.viaversion.api.ViaAPI;
import com.viaversion.viaversion.api.protocol.version.ProtocolVersion;
import org.bukkit.entity.Player;

import com.bergerkiller.mountiplex.logic.TextValueSequence;

/**
 * Detects the game version of the player by communicating with the ViaVersion API.
 * Only used when the ViaVersion plugin is enabled.
 */
public class PlayerGameInfoSupplier_ViaVersion implements Function<Player, PlayerGameInfo> {
    @SuppressWarnings("unchecked")
    private final ViaAPI<Player> api = (ViaAPI<Player>) Via.getAPI();
    private final Entry[] entries;

    public PlayerGameInfoSupplier_ViaVersion() {
        // Initialize mapping with enough room to spare
        entries = new Entry[ProtocolVersion.getProtocols().stream()
                .mapToInt(ProtocolVersion::getVersion)
                .max().getAsInt() + 1];

        // Store in mapping, ignore from before netty rewrite, those versions are cringe
        ProtocolVersion.getProtocols().stream()
            .map(Entry::new)
            .filter(e -> e.protocolVersion >= 0) // Filter UNKNOWN
            .filter(e -> TextValueSequence.evaluate(e.minimum, ">=", TextValueSequence.parse("1.8")))
            .forEach(e -> entries[e.protocolVersion] = e);

        // Patch up gaps so we don't need to do dumb null checks
        boolean initial = true;
        for (int i = 0; i < entries.length; i++) {
            if (initial && entries[i] != null) {
                initial = false;
                Arrays.fill(entries, 0, i, entries[i]);
            } else if (!initial && entries[i] == null) {
                entries[i] = entries[i - 1];
            }
        }
    }

    @Override
    public PlayerGameInfo apply(Player player) {
        int protocolVersion = api.getPlayerVersion(player);
        try {
            return entries[protocolVersion];
        } catch (IndexOutOfBoundsException ex) {
            // This really doesn't happen or shouldn't happen at all
            // We don't care, but provide a fallback
            return entries[entries.length - 1];
        }
    }

    private static class Entry implements PlayerGameInfo {
        public final int protocolVersion;
        public final TextValueSequence minimum;
        public final TextValueSequence maximum;

        public Entry(ProtocolVersion version) {
            this.protocolVersion = version.getVersion();
            if (version.isVersionWildcard()) {
                String str = version.getName().substring(0, version.getName().length() - 2);
                this.minimum = TextValueSequence.parse(str);
                this.maximum = TextValueSequence.parse(str + ".9");
            } else if (version.isRange()) {
                List<TextValueSequence> list = version.getIncludedVersions().stream()
                        .map(TextValueSequence::parse)
                        .sorted()
                        .collect(Collectors.toList());
                this.minimum = list.get(0);
                this.maximum = list.get(list.size() - 1);
            } else {
                this.minimum = this.maximum = TextValueSequence.parse(version.getName());
            }
        }

        @Override
        public String version() {
            return this.maximum.toString();
        }

        @Override
        public boolean evaluateVersion(String operand, TextValueSequence rightSide) {
            int len = operand.length();
            if (len == 0 || len > 2) {
                return false;
            }
            char first = operand.charAt(0);
            char second = (len == 2) ? operand.charAt(1) : ' ';
            if (first == '>') {
                // [1.12.1, 1.12.2] > 1.12.1 = true
                int comp = this.minimum.compareTo(rightSide);
                if (second == '=') {
                    return comp >= 0;
                } else {
                    return comp > 0;
                }
            } else if (first == '<') {
                // [1.12.1, 1.12.2] < 1.12.2 = true
                int comp = this.maximum.compareTo(rightSide);
                if (second == '=') {
                    return comp <= 0;
                } else {
                    return comp < 0;
                }
            } else if (first == '=' && second == '=') {
                if (this.minimum == this.maximum) {
                    return this.minimum.equals(rightSide);
                } else {
                    return this.minimum.compareTo(rightSide) >= 0
                            && this.maximum.compareTo(rightSide) <= 0;
                }
            } else if (first == '!' && second == '=') {
                if (this.minimum == this.maximum) {
                    return !this.minimum.equals(rightSide);
                } else {
                    return this.minimum.compareTo(rightSide) < 0
                            || this.maximum.compareTo(rightSide) > 0;
                }
            } else {
                return false;
            }
        }
    }
}
