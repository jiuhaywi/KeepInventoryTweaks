
package com.Djwid.KInvTweaks.data;

import java.util.UUID;

public class PlayerData {
    public UUID uuid;
    public boolean keepInventoryEnabled;

    public PlayerData() {}

    public PlayerData(UUID uuid, boolean enabled) {
        this.uuid = uuid;
        this.keepInventoryEnabled = enabled;
    }
}
