package me.activated.core.menus.grant.procedure;

import lombok.AllArgsConstructor;
import me.activated.core.api.ServerData;
import me.activated.core.api.player.PlayerData;
import me.activated.core.data.grant.GrantProcedureState;
import me.activated.core.enums.Language;
import me.activated.core.menu.menu.AquaMenu;
import me.activated.core.menu.slots.Slot;
import me.activated.core.utilities.Utilities;
import me.activated.core.utilities.chat.Color;
import me.activated.core.utilities.item.ItemBuilder;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class ChooseGrantServerMenu extends AquaMenu {
    private String server = new ArrayList<>(plugin.getServerManagement().getConnectedServers()).get(0).getServerName();

    {
        setUpdateInTask(true);
    }

    @Override
    public List<Slot> getSlots(Player player) {
        List<ServerData> serverData = new ArrayList<>(plugin.getServerManagement().getConnectedServers());

        List<Slot> slots = new ArrayList<>();
        slots.add(new Global());
        slots.add(new ChooseServer(serverData));

        for (int i = 0; i < 27; i++) {
            if (!Slot.hasSlot(slots, i)) {
                slots.add(Slot.getGlass(i));
            }
        }
        return slots;
    }

    @Override
    public String getName(Player player) {
        return Color.translate("&7Choose server");
    }

    private class Global extends Slot {

        @Override
        public ItemStack getItem(Player player) {
            ItemBuilder item = new ItemBuilder(Material.STAINED_CLAY);
            item.setDurability(4);
            item.setName("&b&lGlobal");
            item.addLoreLine(" ");
            item.addLoreLine("&bClick to set this");
            item.addLoreLine("&bgrant globally.");
            item.addLoreLine("");
            return item.toItemStack();
        }

        @Override
        public int getSlot() {
            return 11;
        }

        @Override
        public void onClick(Player player, int slot, ClickType clickType) {
            PlayerData playerData = plugin.getPlayerManagement().getPlayerData(player.getUniqueId());

            if (playerData.getGrantProcedure() == null) {
                player.closeInventory();
                return;
            }

            playerData.getGrantProcedure().setGrantProcedureState(GrantProcedureState.DURATION);
            playerData.getGrantProcedure().setServer("Global");
            player.closeInventory();
            player.sendMessage(Language.GRANT_PROCEDURE_ENTER_DURATION.toString());
        }
    }

    @AllArgsConstructor
    private class ChooseServer extends Slot {
        private final List<ServerData> servers;

        @Override
        public ItemStack getItem(Player player) {
            ItemBuilder item = new ItemBuilder(Material.EMERALD);
            item.setName("&a&lChoose Server");
            item.addLoreLine("");

            StringBuilder builder = new StringBuilder();
            Iterator<ServerData> serverDataIterator = servers.iterator();
            while (serverDataIterator.hasNext()) {
                ServerData serverData = serverDataIterator.next();
                if (serverDataIterator.hasNext()) {
                    ServerData secondServerData = serverDataIterator.next();
                    builder.append(serverData.getServerName().equalsIgnoreCase(server) ? "&a&l" + serverData.getServerName() : "&2" + serverData.getServerName()).append(" ").append(
                            secondServerData.getServerName().equalsIgnoreCase(server) ? "&a&l" + secondServerData.getServerName() : "&2" + secondServerData.getServerName()
                    );
                    builder.append("###");
                    continue;
                }
                builder.append(serverData.getServerName().equalsIgnoreCase(server) ? "&a&l" + serverData.getServerName() : "&2" + serverData.getServerName());
                builder.append("###");
            }
            builder.setLength(builder.length() - 3);
            for (String server : builder.toString().replace(server, "&a&l" + server).split("###")) {
                item.addLoreLine(server);
            }

            item.addLoreLine(" ");
            item.addLoreLine("&7Right click to move to right");
            item.addLoreLine("&7Left click to move to left");
            item.addLoreLine("");
            item.addLoreLine("&2Shift + Left Click &ato confirm server.");
            item.addLoreLine("");
            return item.toItemStack();
        }

        @Override
        public int getSlot() {
            return 15;
        }

        @Override
        public void onClick(Player player, int slot, ClickType clickType) {
            if (clickType == ClickType.RIGHT) {
                ServerData serverData = plugin.getServerManagement().getServerData(server);
                if (serverData == null) return;

                int next = this.servers.indexOf(serverData) + 1;
                if (next >= this.servers.size()) {
                    next = 0;
                }
                server = this.servers.get(next).getServerName();
                Utilities.playSound(player, Sound.NOTE_PLING);
                update(player);
            } else if (clickType == ClickType.LEFT) {
                ServerData serverData = plugin.getServerManagement().getServerData(server);
                if (serverData == null) return;

                int next = this.servers.indexOf(serverData) - 1;
                if (next <= 0) {
                    next = 0;
                }
                server = this.servers.get(next).getServerName();
                Utilities.playSound(player, Sound.NOTE_PLING);
                update(player);
            } else if (clickType == ClickType.SHIFT_LEFT) {
                PlayerData playerData = plugin.getPlayerManagement().getPlayerData(player.getUniqueId());

                if (playerData.getGrantProcedure() == null) {
                    player.closeInventory();
                    return;
                }

                playerData.getGrantProcedure().setGrantProcedureState(GrantProcedureState.DURATION);
                playerData.getGrantProcedure().setServer(server);
                player.closeInventory();
                player.sendMessage(Language.GRANT_PROCEDURE_ENTER_DURATION.toString());
            }
        }
    }
}
