package me.activated.core.punishments.api;

import lombok.RequiredArgsConstructor;
import me.activated.core.plugin.AquaCore;
import me.activated.core.punishments.player.PunishPlayerData;
import me.activated.core.punishments.utilities.punishments.Alt;
import me.activated.core.punishments.utilities.punishments.Punishment;
import me.activated.core.punishments.utilities.punishments.PunishmentType;

import java.rmi.UnexpectedException;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@RequiredArgsConstructor
public class PunishmentsAPI {
    private final AquaCore plugin;


    public boolean isUserLoaded(UUID uuid) {
        return getPlayerData(uuid) != null;
    }

    public PunishPlayerData loadUser(UUID uuid, String name) throws UnexpectedException {
        if (isUserLoaded(uuid)) {
            throw new UnexpectedException("User is already loaded!");
        }
        plugin.getPunishmentPlugin().getProfileManager().createPlayerDate(uuid, name);
        plugin.getPunishmentPlugin().getProfileManager().getPlayerDataFromUUID(uuid).load();
        plugin.getPunishmentPlugin().getProfileManager().getPlayerDataFromUUID(uuid).getPunishData().load();
        return getPlayerData(uuid);
    }

    public PunishPlayerData getPlayerData(UUID uuid) {
        return plugin.getPunishmentPlugin().getProfileManager().getPlayerDataFromUUID(uuid);
    }

    public boolean isBanned(UUID uuid) {
        PunishPlayerData playerData = getPlayerData(uuid);
        return playerData.getPunishData().isBanned();
    }

    public boolean isIPBanned(UUID uuid) {
        PunishPlayerData playerData = getPlayerData(uuid);
        return playerData.getPunishData().isIPBanned();
    }

    public boolean isMuted(UUID uuid) {
        PunishPlayerData playerData = getPlayerData(uuid);
        return playerData.getPunishData().isMuted();
    }

    public boolean isBlacklisted(UUID uuid) {
        PunishPlayerData playerData = getPlayerData(uuid);
        return playerData.getPunishData().isBlacklisted();
    }

    public Set<Punishment> getPunishments(UUID uuid) {
        PunishPlayerData playerData = getPlayerData(uuid);
        return playerData.getPunishData().getPunishments();
    }

    public Set<Punishment> getPunishments(UUID uuid, PunishmentType punishmentType) {
        PunishPlayerData playerData = getPlayerData(uuid);
        return playerData.getPunishData().getPunishments().stream().filter(punishment -> punishment.getPunishmentType() == punishmentType).collect(Collectors.toSet());
    }

    public List<Alt> getAlts(UUID uuid) {
        PunishPlayerData playerData = getPlayerData(uuid);
        return playerData.getAlts();
    }

    public List<Alt> getPotentialAlts(UUID uuid) {
        PunishPlayerData playerData = getPlayerData(uuid);
        return playerData.getPotentialAlts();
    }

    public String getAddress(UUID uuid) {
        PunishPlayerData playerData = getPlayerData(uuid);
        return playerData.getAddress();
    }

    public List<String> getAddresses(UUID uuid) {
        PunishPlayerData playerData = getPlayerData(uuid);
        return playerData.getAddresses();
    }
}
