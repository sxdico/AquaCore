package me.activated.core.data.grant;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import me.activated.core.api.player.PlayerData;
import me.activated.core.utilities.general.DateUtils;

@Getter
@Setter
@RequiredArgsConstructor
public class GrantProcedure {

    private final PlayerData targetData;

    private GrantProcedureState grantProcedureState = GrantProcedureState.START;
    private long enteredDuration;
    private String enteredReason, rankName, server;
    private boolean permanent = false;

    public String getNiceDuration() {
        if (isPermanent()) return "Permanent";

        return DateUtils.formatTimeMillis(this.enteredDuration);
    }
}
