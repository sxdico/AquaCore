package me.activated.core.managers.register;

import lombok.Getter;
import me.activated.core.commands.*;
import me.activated.core.commands.coins.CoinsCommand;
import me.activated.core.commands.essentials.*;
import me.activated.core.commands.essentials.gamemode.GamemodeAdventureCommand;
import me.activated.core.commands.essentials.gamemode.GamemodeCommand;
import me.activated.core.commands.essentials.gamemode.GamemodeCreativeCommand;
import me.activated.core.commands.essentials.gamemode.GamemodeSurvivalCommand;
import me.activated.core.commands.essentials.messages.MessageCommand;
import me.activated.core.commands.essentials.messages.ReplyCommand;
import me.activated.core.commands.essentials.messages.ToggleMessagesCommand;
import me.activated.core.commands.essentials.messages.ToggleSoundsCommand;
import me.activated.core.commands.essentials.messages.ignore.IgnoreCommand;
import me.activated.core.commands.essentials.panic.PanicCommand;
import me.activated.core.commands.essentials.panic.UnPanicCommand;
import me.activated.core.commands.essentials.staff.*;
import me.activated.core.commands.essentials.staff.item.AddLoreCommand;
import me.activated.core.commands.essentials.staff.item.RemoveLoreCommand;
import me.activated.core.commands.essentials.staff.item.RenameCommand;
import me.activated.core.commands.essentials.staff.teleport.*;
import me.activated.core.commands.essentials.tps.LagCommand;
import me.activated.core.commands.permission.BlacklistedPermissionsCommand;
import me.activated.core.commands.permission.InfoCommand;
import me.activated.core.commands.permission.SetPermissionCommand;
import me.activated.core.commands.punishments.PunishInfoCommand;
import me.activated.core.commands.punishments.StaffHistoryCommand;
import me.activated.core.commands.punishments.StaffRollBackCommand;
import me.activated.core.commands.punishments.punish.*;
import me.activated.core.commands.punishments.undo.UnBanCommand;
import me.activated.core.commands.punishments.undo.UnBlacklistCommand;
import me.activated.core.commands.punishments.undo.UnMuteCommand;
import me.activated.core.commands.rank.GrantCommand;
import me.activated.core.commands.rank.GrantsCommand;
import me.activated.core.commands.rank.RankCommand;
import me.activated.core.commands.rank.SetRankCommand;
import me.activated.core.commands.tags.TagsCommand;
import me.activated.core.commands.tags.TagsReloadCommand;
import me.activated.core.listeners.*;
import me.activated.core.plugin.AquaCore;
import me.activated.core.utilities.Manager;
import me.activated.core.utilities.RegisterMethod;
import me.activated.core.utilities.command.BaseCommand;
import org.bukkit.event.Listener;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

@Getter
public class RegisterManager {

    private PlayerListener playerListener;

    private CoinsCommand coinsCommand;
    private GamemodeAdventureCommand gamemodeAdventureCommand;
    private GamemodeCommand gamemodeCommand;
    private GamemodeCreativeCommand gamemodeCreativeCommand;
    private GamemodeSurvivalCommand gamemodeSurvivalCommand;
    private IgnoreCommand ignoreCommand;
    private MessageCommand messageCommand;
    private ReplyCommand replyCommand;
    private ToggleMessagesCommand toggleMessagesCommand;
    private ToggleSoundsCommand toggleSoundsCommand;
    private PanicCommand panicCommand;
    private UnPanicCommand unPanicCommand;
    private AddLoreCommand addLoreCommand;
    private RemoveLoreCommand removeLoreCommand;
    private RenameCommand renameCommand;
    private TeleportCommand teleportCommand;
    private TeleportHereCommand teleportHereCommand;
    private TeleportPositionCommand teleportPositionCommand;
    private TeleportWorldCommand teleportWorldCommand;
    private TopCommand topCommand;
    private AdminChatCommand adminChatCommand;
    private AlertCommand alertCommand;
    private BroadcastCommand broadcastCommand;
    private ChatCommand chatCommand;
    private ClearCommand clearCommand;
    private CraftCommand craftCommand;
    private FeedCommand feedCommand;
    private FlyCommand flyCommand;
    private FreezeCommand freezeCommand;
    private HealCommand healCommand;
    private InvseeCommand invseeCommand;
    private IPsCommand iPsCommand;
    private MassayCommand massayCommand;
    private MoreCommand moreCommand;
    private RepairCommand repairCommand;
    private ReportsCommand reportsCommand;
    private ServerManager serverManager;
    private SetJoinLocationCommand setJoinLocationCommand;
    private SkullCommand skullCommand;
    private SpeedCommand speedCommand;
    private StaffChatCommand staffChatCommand;
    private StaffModeCommand staffModeCommand;
    private SudoCommand sudoCommand;
    private VanishCommand vanishCommand;
    private LagCommand lagCommand;
    private DiscordCommand discordCommand;
    private GodCommand godCommand;
    private HelpopCommand helpopCommand;
    private PingCommand pingCommand;
    private PlaytimeCommand playtimeCommand;
    private ReportCommand reportCommand;
    private StoreCommand storeCommand;
    private TeamspeakCommand teamspeakCommand;
    private TwitterCommand twitterCommand;
    private BlacklistedPermissionsCommand blacklistedPermissionsCommand;
    private InfoCommand infoCommand;
    private SetPermissionCommand setPermissionCommand;
    private BanCommand banCommand;
    private BanIPCommand banIPCommand;
    private BlacklistCommand blacklistCommand;
    private CheckCommand checkCommand;
    private KickCommand kickCommand;
    private MuteCommand muteCommand;
    private WarnCommand warnCommand;
    private UnBanCommand unBanCommand;
    private UnBlacklistCommand unBlacklistCommand;
    private UnMuteCommand unMuteCommand;
    private PunishInfoCommand punishInfoCommand;
    private StaffHistoryCommand staffHistoryCommand;
    private StaffRollBackCommand staffRollBackCommand;
    private GrantCommand grantCommand;
    private GrantsCommand grantsCommand;
    private RankCommand rankCommand;
    private SetRankCommand setRankCommand;
    private TagsCommand tagsCommand;
    private TagsReloadCommand tagsReloadCommand;
    private AquaCommand AquaCommand;
    private ColorCommand colorCommand;
    private ListCommand listCommand;
    private SettingsCommand settingsCommand;
    private ChatColorCommand chatColorCommand;
    private NotesCommand notesCommand;
    private AuthCommand authCommand;
    private BackCommand backCommand;
    private StaffAlertsCommand staffAlertsCommand;
    private DayCommand dayCommand;
    private NightCommand nightCommand;
    private GlobalListCommand globalListCommand;
    private GUIFreezeCommand guiFreezeCommand;

    @RegisterMethod
    private void loadCommands() {
        for (Field field : this.getClass().getDeclaredFields()) {
            if (BaseCommand.class.isAssignableFrom(field.getType()) && field.getType().getSuperclass() == BaseCommand.class) {
                field.setAccessible(true);
                try {
                    Constructor constructor = field.getType().getDeclaredConstructor();
                    constructor.newInstance();
                } catch (ReflectiveOperationException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    @RegisterMethod
    private void loadManagers() {
        for (Field field : AquaCore.INSTANCE.getClass().getDeclaredFields()) {
            if (Manager.class.isAssignableFrom(field.getType()) && field.getType().getSuperclass() == Manager.class) {
                field.setAccessible(true);
                try {
                    Constructor constructor = field.getType().getDeclaredConstructor(AquaCore.INSTANCE.getClass());
                    field.set(AquaCore.INSTANCE, constructor.newInstance(AquaCore.INSTANCE));
                } catch (ReflectiveOperationException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public void loadListeners(AquaCore plugin) {
        List<Listener> listeners = new ArrayList<>();

        listeners.add(new ChatListener());
        listeners.add(new FreezeListener());
        listeners.add(new GodModeListener());
        listeners.add(new MenuListener());
        listeners.add(new NameTagListener());
        listeners.add(new PanicListener());
        listeners.add(this.playerListener = new PlayerListener());
        listeners.add(new PunishmentsListener());
        listeners.add(new QuickAccessListener());
        listeners.add(new StaffModeListener());
        listeners.add(new WorldListener());
        listeners.add(new StaffAuthListener());

        listeners.forEach(listener -> plugin.getServer().getPluginManager().registerEvents(listener, plugin));
    }
}
