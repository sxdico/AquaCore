package me.activated.core.enums;

import lombok.Getter;
import me.activated.core.plugin.AquaCore;
import me.activated.core.utilities.chat.Color;
import me.activated.core.utilities.chat.Replacement;
import me.activated.core.utilities.file.ConfigFile;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public enum Language {

    PREFIX("PREFIX", "&8[&bAqua&8] "),

    NOT_ONLINE("PLAYER_NOT_ONLINE", "{prefix} &cThat player is currently offline."),
    NOT_CONNECTED("PLAYER_NOT_CONNECTED", "{prefix} &cPlayer by name &c&l<name> &cis not connected to any of our data base servers."),
    NO_PERMISSION("NO_PERMISSION", "{prefix} &cYou don't have permission to use that command."),
    USE_NUMBERS("USE_NUMBERS", "{prefix} &cPlease use numbers."),
    DOESNT_HAVE_DATA("DOESNT-HAVE-DATA", "{prefix} &cWe don't have any documentation saved about that user. It seems that the user never joined any of our data base servers."),
    LOADING_OFFLINE_DATA("LOADING-OFFLINE-DATA", "{prefix} &aUser is not online, loading data, please wait.."),

    LIST_ALL_FORMAT("LIST-ALL-FORMAT", "&eThere are &b<players>&7/&b<max> &eplayers online&7: {0}&e<list>"),
    INVALID_TIME_DURAITON("INVALID-TIME-DURATION", "{prefix} &eYou've entered invalid time duration. Please use something like&7: &b1d&7,&b1d&7,&b1m&7,&b1s&e."),
    RANK_ALREADY_EXISTS("RANK.ALEADY-EXISTS", "{prefix} &eRank &a<rank> &ealready exists."),
    RANK_CREATED("RANK.CREATED", "{prefix} &eRank &a<rank> &eis now &asuccessfully &bcreated&e."),
    RANK_NOT_EXISTS("RANK.DOESNT-EXISTS", "{prefix} &eRank &a<rank> &edoesn't exists."),
    RANK_HAS_NO_PERMISSIONS("RANK.HAS-NO-PERMISSIONS", "{prefix} &eRank &a<rank> &edoesn't have any permissions set."),
    RANK_PERMISSIONS("RANK.PERMISSIONS", "{prefix} &eRank &a<rank> &ehave the following permissions&7: &b<permissions>&7 &8[&b<total> total&8]&7."),

    RANK_DELETED("RANK.DELETED", "{prefix} &eRank &a<rank> &eis now &asuccessfully &bdeleted&e."),
    RANK_PREFIX_SET("RANK.PREFIX-SET", "{prefix} &ePrefix for &a<rank> &erank has been &asuccessfully &eupdated to &f<prefix>&e."),
    RANK_SUFFIX_SET("RANK.SUFFIX-SET", "{prefix} &eSuffix for &a<rank> &erank has been &asuccessfully &eupdated to &f<suffix>&e."),
    RANK_WEIGHT_SET("RANK.WEIGHT-SET", "{prefix} &eWeight for &a<rank> &erank has been &asuccessfully &eupdated to &f<weight>&e."),
    RANK_COLOR_SET("RANK.COLOR-SET", "{prefix} &eColor for &a<rank> &erank has been &asuccessfully &eupdated to &f<color>&e."),
    RANK_CHAT_COLOR_SET("RANK.CHAT-COLOR-SET", "{prefix} &eChat color for &a<rank> &erank has been &asuccessfully &eupdated to &f<color>&e."),

    RANK_INVALID_COLOR("RANK.INVALID-COLOR", "{prefix} &eColor &a<color> &eis not a valid color for a rank."),
    RANK_INVALID_WEIGHT("RANK.INVALID-WEIGHT", "{prefix} &eRank weight &a<weight> &eis not a valid weight for a rank."),
    RANK_COINS_COST_SET("RANK.COINS-COST-SET", "{prefix} &eCoins cost for &a<rank> &erank has been &asuccessfully &eupdated to &f<amount>&e."),
    RANK_INVALID_CHAT_COLOR("RANK.INVALID-CHAT-COLOR", "{prefix} &eColor &a<color> &eis not a valid chat color for a rank."),
    RANK_INVALID_TYPE("RANK.INVALID-TYPE", "{prefix} &eYou have entered invalid rank type, available types: &b<types>&e."),
    RANK_TYPE_SET("RANK.TYPE-SET", "{prefix} &eYou have set &b<rank>'s &etype to &b<type>&e."),

    RANK_ALREADY_HAVE_PERMISSION("RANK.ALREADY-HAVE-PERMISSION", "{prefix} &eRank &a<rank> &ealready have permission &a<permission>&e."),
    RANK_NOT_HAVE_PERMISSION("RANK.DOESNT-HAVE-PERMISSION", "{prefix} &eRank &a<rank> &edoesn't have permission &a<permission>&e."),
    RANK_PERMISSION_SET("RANK.PERMISSION-SET", "{prefix} &ePermission &a<permission> &ehave been set to &btrue &efor &a<rank> &erank."),
    RANK_PERMISSION_REMOVE("RANK.PERMISSION-REMOVE", "{prefix} &ePermission &a<permission> &ehave been set to &cfalse &efor &a<rank> &erank."),

    RANK_ALREADY_HAVE_INHERITANCE("RANK.ALREADY-HAVE-INHERITANCE", "{prefix} &eRank &a<rank> &ealready inherits &a<inheritance> &erank."),
    RANK_NOT_HAVE_INHERITANCE("RANK.DOESNT-HAVE-INHERITANCE", "{prefix} &eRank &a<rank> &edoesn't have &a<inheritance> &erank inherits."),
    RANK_INHERITANCE_SET("RANK.INHERITANCE-SET", "{prefix} &eRank &a<rank> &enow inherits &a<inheritance> &erank."),
    RANK_INHERITANCE_REMOVE("RANK.INHERITANCE-REMOVE", "{prefix} &eRank &a<rank> &eno longer inherits &a<inheritance> &erank."),
    RANK_DEFAULT_SET("RANK.DEFAULT-SET", "{prefix} &eYou have set &a<rank> &erank as default rank."),

    RANK_NOT_BOLD("RANK.NOT-BOLD", "{prefix} &eThat rank is not currently BOLD!"),
    RANK_NOT_ITALIC("RANK.NOT-ITALIC", "{prefix} &eThat rank is not currently ITALIC!"),
    RANK_ALREADY_BOLD("RANK.ALREADY-BOLD", "{prefix} &eThat rank is already BOLD!"),
    RANK_ALREADY_ITALIC("RANK.ALREADY-ITALIC", "{prefix} &eThat rank is already ITALIC!"),
    RANK_BOLD_SET("RANK.BOLD-SET", "{prefix} &eYou have set &b<rank> &erank to &bBOLD&e!"),
    RANK_BOLD_UN_SET("RANK.BOLD-UN-SET", "{prefix} &eYou have removed &bBOLD &efrom &b<rank> &erank!"),

    RANK_NOT_PURCHASABLE("RANK.NOT-PURCHASABLE", "{prefix} &eThat rank is not currently PURCHASABLE!"),
    RANK_ALREADY_PURCHASABLE("RANK.ALREADY-PURCHASABLE", "{prefix} &eThat rank is already PURCHASABLE!"),
    RANK_PURCHASABLE_SET("RANK.PURCHASABLE-SET", "{prefix} &eYou have set &b<rank> &erank to be &bPURCHASABLE&e!"),
    RANK_PURCHASABLE_UN_SET("RANK.PURCHASABLE-UN-SET", "{prefix} &eYou have removed &b<rank> &erank to be &bPURCHASABLE&e!"),

    RANK_NOT_BUNGEE("RANK.NOT-BUNGEE", "{prefix} &eThat rank is not currently BUNGEE!"),
    RANK_ALREADY_BUNGEE("RANK.ALREADY-BUNGEE", "{prefix} &eThat rank is already BUNGEE!"),
    RANK_BUNGEE_SET("RANK.BUNGEE-SET", "{prefix} &eYou have set &b<rank> &erank to be &bBUNGEE&e!"),
    RANK_BUNGEE_UN_SET("RANK.BUNGEE-UN-SET", "{prefix} &eYou have removed &b<rank> &erank to be &bBUNGEE&e!"),

    RANK_ITALIC_SET("RANK.ITALIC-SET", "{prefix} &eYou have set &b<rank> &erank to &bITALIC&e!"),
    RANK_ITALIC_UN_SET("RANK.ITALIC-UN-SET", "{prefix} &eYou have removed &bITALIC &efrom &b<rank> &erank!"),


    GRANT_PROCEDURE_ENTER_DURATION("GRANT-PROCEDURE.ENTER-DURATION", "{prefix} &ePlease enter duration time for this grant! Use &c'permanent' &eor &c'perm' &efor permenent."),
    GRANT_PROCEDURE_ENTER_REASON("GRANT-PROCEDURE.ENTER-REASON", "{prefix} &ePlease enter reason for this grant!"),
    //GRANT_PROCEDURE_PLAYER_WENT_OFFLINE("GRANT-PROCEDURE.PLAYER-WENT-OFFLINE", "{prefix} &ePlayer in your grant procedure went offline! Procedure is now cancelled."),
    GRANT_PROCEDURE_ALREADY_HAVE_RANK("GRANT-PROCEDURE.ALREADY-HAVE-RANK", "{prefix} &ePlayer already have active grant with that rank, use &c'/grants <player>' &eto h it out."),
    GRANT_PROCEDURE_DURATION_RECORDED("GRANT-PROCEDURE.DURATION-RECORDED", "{0} {prefix} &aDuration has been &a&lrecorded&a.{0} &7[&b*&7] &eDuration&7: &a<duration> {0} "),
    GRANT_PROCEDURE_CANT_GRANT("GRANT-PROCEDURE.CANT-GRANT", "{prefix} &eYou can not grant that rank as it is higher than yours!"),
    GRANT_PROCEDURE_CANT_GRANT_DISALLOWED("GRANT-PROCEDURE.CANT-GRANT-DISALLOWED", "{prefix} &cYou do not have permission to grant that rank!"),
    GRANT_PROCEDURE_CANT_GRANT_DEFAULT("GRANT-PROCEDURE.CANT-GRANT-DEFAULT", "{prefix} &eThat rank can't be granted as it is default rank!"),

    PLAYER_ALREADY_HAVE_PERMISSION("PLAYER.ALREADY-HAVE-PERMISSION", "{prefix} &ePlayer &a<player> &ealready have permission &a<permission>&e."),
    PLAYER_PERMISSION_SET("PLAYER.PERMISSION-SET", "{prefix} &eSuccessfully added &a<permission> &epermission to &a<player>'s &edata."),
    PLAYER_NOT_HAVE_PERMISSION("PLAYER.DOESNT-HAVE-PERMISSION", "{prefix} &ePlayer &a<player> &edoesn't have permission &a<permission> &eto be unset."),
    PLAYER_PERMISSION_REMOVED("PLAYER.PERMISSION-REMOVED", "{prefix} &eSuccessfully removed &a<permission> &epermission from &a<player>'s &edata."),
    SERVER_LOADING_KICK("SERVER-LOADING-KICK", "&cServer is currently loading up, please wait."),

    GRANT_RANK_GRANTED_PERM("GRANT.RANK-GRANTED-PERM", "{prefix} &eYou have &bpermanently &egranted &a<rank> &erank to &a<user>&e."),
    GRANT_RANK_GRANTED_TEMP("GRANT.RANK-GRANTED-TEMP", "{prefix} &eYou have &egranted &a<rank> &erank to &a<user> &efor &b<time>&e."),

    GRANT_RANK_GRANTED_PERM_TARGET("GRANT.RANK-GRANTED-PERM-PLAYER", "{prefix} &eYou have been &bpermanently &egranted &a<rank> &erank."),
    GRANT_RANK_GRANTED_TEMP_TARGET("GRANT.RANK-GRANTED-TEMP-PLAYER", "{prefix} &eYou have been &egranted &a<rank> &erank for &b<time>&e."),

    REPORT_DOESNT_HAVE("REPORT-DOESNT-HAVE", "{prefix} &b<player> &edoesn't have any reports recorded."),
    REPORT_FORMAT("REPORT-FORMAT", "&b[R] &6<player> &3reported &6<target> &3for &b<reason>&3."),
    REPORT_COOLDOWN("REPORT-COOLDOWN", "{prefix} &3You're on a &breport &3cooldown for another &b<seconds> seconds&3."),
    REPORT_TO_PLAYER("REPORT-TO-PLAYER", "{prefix} &aYour report has been successfulyl sent to all staff. We will take care about it."),
    REPORT_CANT_REPORT_YOURSELF("REPORT-CANT-REPORT-YOURSELF", "{prefix} &aYou're are not allowed to report yourself."),

    HELPOP_TO_PLAYER("HELPOP-TO-PLAYER", "{prefix} &aYour helpop has been successfully sent to all staff."),
    HELPOP_FORMAT("HELPOP-FORMAT", "&2[H] &a<player> &3requsted staff help for &b<reason>&3."),
    HELPOP_COOLDOWN("HELPOP-COOLDOWN", "{prefix} &3You're on a &bhelpop &3cooldown for another &b<seconds> seconds&3."),


    ALREADY_HAVE_GAMEMODE("GAMEMODE.ALREADY-HAVE", "{prefix} &bYour gamemode is already &3<gamemode>&b."),
    GAMEMODE_UPDATED("GAMEMODE.UPDATED", "{prefix} &bYour gamemode has been updated to &3<gamemode>&b"),
    ALREADY_HAVE_GAMEMODE_OTHER("GAMEMODE.ALREADY-HAVE-OTHER", "{prefix} &3<player>'s &bgamemode is already set to &3<gamemode>&b."),
    GAMEMODE_UPDATED_OTHER("GAMEMODE.UPDATED-OTHER-SENDER", "{prefix} &3<player>'s &bgamemode has been updated to &3<gamemode>&b"),
    GAMEMODE_UPDATED_OTHER_TARGET("GAMEMODE.UPDATED-OTHER-TARGET", "{prefix} &bYour gamemode has been updated to &3<gamemode> &bby &3<sender>&b."),

    WRONG_GAMEMODE("GAMEMODE.WRONG", "{prefix} &bThat's not a valid gamemode."),
    GOD_MODE_ENABLED("GOD-MODE.ENABLED", "{prefix} &bGod Mode is now &aenabled&b."),
    GOD_MODE_DISABLED("GOD-MODE.DISABLED", "{prefix} &bGod Mode is now &cdisabled&b."),

    STAFF_MESSAGES_CONNECT("STAFF-MESSAGES.CONNECT", "&b[S] &b<player> &3connected to &b<server>&3."),
    STAFF_MESSAGES_SWITCH("STAFF-MESSAGES.SWITCH", "&b[S] &b<player> &3joined &b<server> &3from &b<from>&3."),
    STAFF_MESSAGES_DISCONNECT("STAFF-MESSAGES.DISCONNECT", "&b[S] &b<player> &3disconnected from &b<server>&3."),

    STAFF_CHAT_ENABLED("STAFF-CHAT.ENABLED", "{prefix} &bStaff chat has been &aenabled&b."),
    STAFF_CHAT_DISABLED("STAFF-CHAT.DISABLED", "{prefix} &bStaff chat has been &cdisabled&b."),
    STAFF_CHAT_FORMAT("STAFF-CHAT.FORMAT", "&b[SC] &3[<server>] &3<player>&7: &f<message>"),

    ADMIN_CHAT_ENABLED("ADMIN-CHAT.ENABLED", "{prefix} &bAdmin chat has been &aenabled&b."),
    ADMIN_CHAT_DISABLED("ADMIN-CHAT.DISABLED", "{prefix} &bAdmin chat has been &cdisabled&b."),
    ADMIN_CHAT_FORMAT("ADMIN-CHAT.FORMAT", "&c[AC] &3[<server>] &3<player>&7: &c<message>"),

    TAGS_NO_ACCESS("TAGS.NO-ACCESS", "{prefix} &bYou don't have permission to use tags&7. &bPurchase rank &3@<store> &bto grant access&3!"),
    TAGS_DONT_HAVE_APPLIED("TAGS.DONT-HAVE-APPLIED", "{prefix} &bYou don't have any tag applied!"),
    TAGS_ALREADY_HAVE_APPLIED("TAGS.ALREADY-HAVE-APPLIED", "{prefix} &bYour tag is already set to &3<tag>&b!"),
    TAGS_TAG_APPLIED("TAGS.TAG-APPLIED", "{prefix} &bYou have applied &3<tag> &btag to yourself."),
    TAGS_TAG_REMOVE("TAGS.TAG-REMOVED", "{prefix} &bYou have removed your tag."),
    TAGS_COLOR_APPLIED("TAGS.COLOR-CHANGED", "{prefix} &bYou have set your tag color to <color>&b."),

    NAME_COLOR_NO_ACCESS("NAME-COLOR.NO-ACCESS", "{prefix} &bYou don't have permission to change your name color&7. &bPurchase rank &3@<store> &bto grant access&3!"),
    NAME_COLOR_COLOR_CHANGED("NAME-COLOR.CHANGED", "{prefix} &bYou have changed your name color to &a<color>&b!"),
    NAME_COLOR_COLOR_RESET("NAME-COLOR.RESET", "{prefix} &bYou have reseted your name color to &a<color>&b!"),

    CHAT_COLOR_NO_ACCESS("CHAT-COLOR.NO-ACCESS", "{prefix} &bYou don't have permission to change your chat color&7. &bPurchase rank &3@<store> &bto grant access&3!"),
    CHAT_COLOR_COLOR_CHANGED("CHAT-COLOR.CHANGED", "{prefix} &bYou have changed your chat color to &a<color>&b!"),
    CHAT_COLOR_COLOR_RESET("CHAT-COLOR.RESET", "{prefix} &bYou have reseted your chat color to &a<color>&b!"),

    MESSAGE_FORMAT_TO_SENDER("MESSAGE-FORMAT.TO-SENDER", "&7(&3To &b<target>&7): &f<message>"),
    MESSAGE_FORMAT_TO_TARGET("MESSAGE-FORMAT.TO-TARGET", "&7(&3From &b<sender>&7): &f<message>"),

    MESSAGES_HAVE_MESSAGE_TOGGLED_OFF("MESSAGES.HAVE-MESSAGES-TOGGLED-OFF", "{prefix} &cYou have your messages turned &c&lOFF&c."),
    MESSAGES_HAVE_MESSAGE_TOGGLED_OFF_TARGET("MESSAGES.HAVE-MESSAGES-TOGGLED-OFF-TARGET", "{prefix} &c&l<target> &chas their messages turned &c&lOFF&c."),
    MESSAGES_CANT_SEND_YOURSELF("MESSAGES.CANT-SEND-YOURSELF", "{prefix} &cYou can't message yourself."),
    MESSAGE_INGORING_TARGET("MESSAGES.IGNORING-TARGET", "{prefix} &3<target> &bis currently ignoring you and you may not send message at the moment."),
    MESSAGE_INGORING_PLAYER("MESSAGES.IGNORING-SENDER", "{prefix} &bYou are currently ignoring &3<target> &band can't send message at the moment."),
    MESSAGES_TOGGLED_ON("MESSAGES.TOGGLED-ON", "{prefix} &bYou have now your private messages &3enabled&b."),
    MESSAGES_TOGGLED_OFF("MESSAGES.TOGGLED-OFF", "{prefix} &bYou have now your private messages &3disabled&b."),
    MESSAGES_IGNORE_LIST_EMPTY("MESSAGES.IGNORE.LIST-EMPTY", "{prefix} &bYou're not currently ignoring anyone."),
    MESSAGES_IGNORE_ALREADY_IGNORING("MESSAGES.IGNORE.ALREADY-IGNORING", "{prefix} &bYou are already ignoring &3<player>&b."),
    MESSAGES_IGNORE_NOT_IGNORING("MESSAGES.IGNORE.NOT-IGNORING", "{prefix} &bYou are currently not ignoring &3<player>&b."),
    MESSAGES_IGNORE_IGNORED("MESSAGES.IGNORE.IGNORED", "{prefix} &bYou have &asuccessfully &badded &3<player> &bto your ignore list."),
    MESSAGES_IGNORE_UNIGNORED("MESSAGES.IGNORE.UN-IGNORED", "{prefix} &bYou have &asuccessfully &bremoved &3<player> &bfrom your ignore list."),
    MESSAGES_TOGGLED_ON_SOUNDS("MESSAGES.TOGGLED-ON-SOUNDS", "{prefix} &bYou have now your private messages sounds &3enabled&b."),
    MESSAGES_TOGGLED_OFF_SOUNDS("MESSAGES.TOGGLED-OFF-SOUNDS", "{prefix} &bYou have now your private messages sounds &3disabled&b."),

    SERVER_MANAGER_SERVER_DONT_EXISTS("SERVER-MANAGER.INVALID-SERVER", "{prefix} &bThats not a valid server&7. &bServers&7: &3<servers>"),
    SERVER_MANAGER_COMMAND_PERFORMED_ALL("SERVER-MANAGER.COMMAND-PERFORMED-ALL", "{prefix} &bYou have runned console command on every server&7. &7(&3<command>&7)"),
    SERVER_MANAGER_COMMAND_PERFORMED_SERVER("SERVER-MANAGER.COMMAND-PERFORMED-SERVER", "{prefix} &bYou have runned console command on &3<server> &bserver&7. &7(&3<command>&7)"),
    SERVER_MANAGER_SERVER_LIST("SERVER-MANAGER.SERVER-LIST", "{prefix} &bCurrently available servers&7: &3<servers>"),
    CAME_ONLINE("SERVER-MANAGER.SERVER-ONLINE", "{prefix} &7[&4ServerManager&7] &b<server> &3has just came &aonline &band will be joinable in &b5 seconds&3!"),
    WENT_OFFLINE("SERVER-MANAGER.SERVER-OFFLINE", "{prefix} &7[&4ServerManager&7] &b<server> &3has just went &coffline &3and is no longer joinable&3!"),

    FREEZE_TARGET("FREEZE.TARGET", "{prefix} &bYou have been &3frozen &bby &2{player}&b."),
    FREEZE_PLAYER("FREEZE.PLAYER", "{prefix} &bYou have successfully &3freeze &2{target}&b."),
    UN_FREEZE_TARGET("UNFREEZE.TARGET", "{prefix} &bYou have been &3unfrozen &bby &2{player}&b."),
    UN_FREEZE_PLAYER("UNFREEZE.PLAYER", "{prefix} &bYou have successfully &3unfreeze &2{target}&b."),
    LEFT_FROZEN("LEFT-FROZEN", "&b[S] &3[<server>] &a<name> &3left while frozen."),

    /*FILTER_TO_PLAYER("FILTER.TO-PLAYER", "{prefix} &cYour message contains blocked world(s)."),
    FILTER_TO_STAFF("FILTER.TO-STAFF", "&7[&4&lFiltered&7] {format}"),*/

    PANIC_COMMAND_COOLDOWN("PANIC.COMMAND-COOLDOWN", "{prefix} &bYou're on a panic command cooldown for another &3<time>&b!"),
    PANIC_COMMAND_ALREADY_IN_PANIC("PANIC.ALREADY-IN-PANIC", "{prefix} &bYou're already in panic mode. Your panic will expire in &3<time>&b!"),
    PANIC_COMMAND_USED("PANIC.USED", "{prefix} &bYou have used your panic, now you're frozen for &3<time>&b. &cStaff has been alerted!"),
    PANIC_STAFF_ALERT("PANIC.STAFF", "&7[&4&lPANIC&7] &c[<server>] &4&l<name> &bjust used panic ability by typing &c/panic&b."),
    UNPANIC_NOT_IN_PANIC("PANIC.UNPANIC.NOT-IN-PANIC", "{prefix} &3<player> &bis not currently in panic."),
    UNPANIC_UNPANICED_SENDER("PANIC.UNPANIC.TO-SENDER", "{prefix} &3<player> &bis no longer in panic."),
    UNPANIC_UNPANICED_TARGET("PANIC.UNPANIC.TO-TARGET", "{prefix} &bYour panic has been removed by &3<sender>&b."),

    BROADCAST_FORMAT("BROADCAST-FORMAT", "&7[&bBroadcast&7] &3<message>"),
    ALERT_FORMAT("ALERT-FORMAT", "&7[&4Alert&7] &3<message>"),

    INVENTORY_CLEAR_SELF("INVENTORY-CLEAR.SELF", "{prefix} &bYou have cleared your inventory. &7(&3<total> total items&7)"),
    INVENTORY_CLEAR_OTHER_SENDER("INVENTORY-CLEAR.OTHER-SENDER", "{prefix} &bYou have cleared &3<target>'s &binventory. &7(&3<total> total items&7)"),
    INVENTORY_CLEAR_OTHER_TARGET("INVENTORY-CLEAR.OTHER-TARGET", "{prefix} &bYour inventory has been cleared by &3<player>&b. &7(&3<total> total items&7)"),

    PING_SELF("PING.SELF", "{prefix} &bYour ping&7: &3<ping> ms"),
    PING_OTHER("PING.OTHER", "{prefix} &3<target>'s &bping&7: &3<ping> ms"),

    HEAL_OTHER("HEAL", "{prefix} &3<target> &bhas been successfully healed."),
    FEED_OTHER("FEED", "{prefix} &3<target> &bhas been successfully fed."),

    SKULL_INV_FULL("SKULL.INV-FULL", "{prefix} &bYour inventory is currently full."),
    SKULL_GIVEN("SKULL.GIVEN", "{prefix} &bYou have received &3<name>'s &bskull."),
    SKULL_CLICK("SKULL.CLICK", "{prefix} &bYou've clicked &3<name>'s &bhead."),

    MORE_ITEM_NULL("MORE.INVALID-ITEM", "{prefix} &bYou can't be holding air."),
    MORE_SUCCESS("MORE.SUCCESS", "{prefix} &bSuccessfully set hand item amount to it's max amount&b."),

    TELELPORT_TO_TARGET("TELEPORT.TO-TARGET", "{prefix} &bYou've been teleported to &3<target>&b."),
    TELEPORT_INVALID_COORD("TELEPORT.INVALID-COORDS", "{prefix} &bYou have entered invalid coordinates."),
    TELELPORT_TO_COORDS("TELEPORT.TO-COORDS", "{prefix} &bYou've been teleported to &3x:<x>&7, &3y:<y>&7, &3z:<z>&b."),
    TELEPORT_HERE_TO_SENDER("TELEPORT.HERE.SENDER", "{prefix} &bYou have teleported &3<target> &bto yourself."),
    TELEPORT_HERE_TO_TARGET("TELEPORT.HERE.TARGET", "{prefix} &3<player> &bteleported you to their location."),
    TELEPORT_WORLD_INVALID("TELEPORT.WORLD.INVALID-WORLD", "{prefix} &bYou have entered invalid world."),
    TELEPORT_WORLD_ALREADY_IN_WORLD("TELEPORT.WORLD.ALREADY-IN-WORLD", "{prefix} &bYou are already in that world."),
    TELEPORT_WORLD_SUCESS("TELEPORT.WORLD.SUCESS", "{prefix} &bYou've been teleported to &3<world> &bworld."),

    RENAME_ITEM_NULL("RENAME.INVALID-ITEM", "{prefix} &bYou can't be holding  air."),
    RENAME_SUCCESS("RENAME.RENAMED", "{prefix} &bItem has been renamed to &3<name>&b."),

    LORE_ITEM_NULL("LORE.INVALID-ITEM", "{prefix} &bYou can't be holding  air."),
    LORE_SUCCESS("LORE.ADDED", "{prefix} &bYou have added new lore to your item."),

    LORE_REMOVE_ITEM_NULL("LORE-REMOVE.INVALID-ITEM", "{prefix} &bYou can't be holding  air."),
    LORE_REMOVE_FAIL("LORE-REMOVE.FAIL", "{prefix} &bIt seems that your item doesn't have that lore."),
    LORE_REMOVE_SUCCESS("LORE-REMOVE.REMOVED", "{prefix} &bYou have removed lore with numbder &3<number> &bfrom item."),

    FILTER_CANT_SEND("FILTER.CANT-SEND", "{prefix} &cYour message contains blocked word(s) and can't be  sent!"),
    FILTER_STAFF_ALERT("FILTER.STAFF-ALERT", "&7[&cFiltered&7] &3[<server>] &b<player>&7: &e<message>"),
    FILTER_STAFF_ALERT_PRIVATE_MESASGE("FILTER.STAFF-ALERT-PRIVATE", "&7[&cFiltered&7] &b<sender> &7-> &b<target>&7: &e<message>"),

    CHAT_MUTED("CHAT.MUTED", "{prefix} &bGlobal chat has been &3disabled &bby &3&l<player>&b."),
    CHAT_UN_MUTED("CHAT.UN-MUTED", "{prefix} &bGlobal chat has been &3enabled &bby &3&l<player>&b."),
    CHAT_SLOWED("CHAT.SLOW", "{prefix} &bGlobal chat has been &3delayed &bto &3<seconds> seconds &bby &3&l<player>&b."),
    CHAT_ON_DELAY("CHAT.ON-DELAY", "{prefix} &bYou're on a chat delay for another &3<seconds> second(s)&b."),
    CHAT_ALREADY_MUTED("CHAT.ALREADY-MUTED", "{prefix} &bChat is already muted."),
    CHAT_ALREADY_UN_MUTED("CHAT.ALREADY-UN-MUTED", "{prefix} &bChat is already unmuted."),
    CHAT_MUTED_PLAYER("CHAT.CANT-SEND", "{prefix} &bGlobal chat is currently &3disabled&b."),
    CHAT_CLEAR("CHAT.CLEARED", "{prefix} &bGlobal chat have been cleared by &3<player>&b."),

    SUDO_USED_COMMAND("SUDO.COMMAND", "{prefix} &bYou have made &3<player> &bto perform &3<command> &bcommand."),
    SUDO_USED_CHAT("SUDO.CHAT", "{prefix} &bYou have made &3<player> &bto type &3<message> &bin chat."),

    FLY_ENABLED("FLY.ENABLED", "{prefix} &bFlight mode for &3<player> &bhas been &aenabled&b."),
    FLY_DISABLED("FLY.DISABLED", "{prefix} &bFlight mode for &3<player> &bhas been &cdisabled&b."),
    FLY_NO_PERMISSION_OTHER("FLY.NO-PERMISSION-OTHER", "{prefix} &cYou don't have permission to toggle fly for other players!"),

    DISCORD("MEDIA.DISCORD", "{prefix} &bOur discord server: &3<discord>"),
    TEAMSPEAK("MEDIA.TEAMSPEAK", "{prefix} &bOur teamspeak server: &3<teamspeak>"),
    TWITTER("MEDIA.TWITTER", "{prefix} &bOur twitter: &3<twitter>"),
    STORE("MEDIA.STORE", "{prefix} &bOur store: &3<store>"),

    STAFF_ROLLBACK_WIPING("STAFF-ROLLBACK.WIPING", "{prefix} &aWiping all &2<type>&a, please wait..."),
    STAFF_ROLLBACK_DONT_HAVE_HISTORY("STAFF-ROLLBACK.NO-HISTORY", "{prefix} &aThat player doesn't have &2<type> &aperformed."),
    STAFF_ROLLBACK_WIPED("STAFF-ROLLBACK.WIPED", "{prefix} &aSuccessfully wiped &2<amount> <type> &afrom &2<name> &adatabase. &7(&a<active> Active&7, &a<expired> Expired&7)"),

    VANISH_VANISHED("VANISH.VANISHED", "&b[S] &3You have just &aenabled &3vanish mode."),
    VANISH_UN_VANISHED("VANISH.UN-VANISHED", "&b[S] &3You have just &cdisabled &3vanish mode."),

    RANDOM_TELEPORT_SUCCESS("STAFF-MODE.RANDOM-TELEPORT.SUCCESS", "{prefix} &bYou have been randomly teleported to &3<target>&b!"),
    RANDOM_TELEPORT_FAIL("STAFF-MODE.RANDOM-TELEPORT.FAIL", "{prefix} &bThere is no enough non-staff players to find."),
    TELEPORTED_TO_PLAYER("STAFF-MODE.TELEPORTED-TO-PLAYER", "{prefix} &bYou have been teleported to &3<target>&b!"),
    NO_ONLINE_PLAYERS("STAFF-MODE.NO-ONLINE-PLAYERS", "{prefix} &bThere is no online non-staff players."),
    STAFF_MODE_ENABLED("STAFF-MODE.ENABLED", "{prefix} &bYou have successfully &aenabled &byour staff mode."),
    STAFF_MODE_DISABLED("STAFF-MODE.DISABLED", "{prefix} &bYou have successfully &cdisabled &byour staff mode."),

    TOP_ALREADY_AT_TOP("TOP.ALREADY-AT-TOP", "{prefix} &bYou're already at or above the highest block at your location."),
    TOP_TELEPORTED("TOP.TELEPORTED", "{prefix} &bYou have been teleported to the &3highest &blocation at your position."),

    MASSAY_SUCCESS("MASSAY-SUCCESS", "{prefix} &bYou have successfully made everyone to type &3<message>"),

    SPEED_LIMITED("SPEED.LIMIT", "{prefix} &bSpeed limit can't be under 1 or above 10"),
    SPEED_WALK_SET("SPEED.WALK-SET", "{prefix} &bYou have set your &3walk speed &bto &3<amount>&b."),
    SPEED_FLY_SET("SPEED.FLY-SET", "{prefix} &bYou have set your &3fly speed &bto &3<amount>&b."),

    REPAIR_ITEM_NULL("REPAIR.ITEM-NULL", "{prefix} &bYou can't repair air or non repairable item, please put something repairable in your hand!"),
    REPAIR_ALREADY_REPAIRED("REPAIR.ITEM-ALREADY-REPAIRED", "{prefix} &bItem in your hand is already repaired."),
    REPAIR_ITEM_REPAIRED("REPAIR.ITEM-REPAIRED", "{prefix} &bYou have repaired item in your hand."),
    REPAIR_ARMOR_REPAIRED("REPAIR.ARMOR-REPAIRED", "{prefix} &bYou have repaired your armor."),
    REPAIR_REPAIRED("REPAIR.ALL-REPAIRED", "{prefix} &bYou have repaired all of your items."),

    JOIN_SPAWN_SET("JOIN-SPAWN-SET", "{prefix} &aYou have updated join location for Aqua core."),
    JOIN_SPAWN_NOTE("JOIN-SPAWN-NOTE", "{prefix} &cPlease note that the join teleport in configuration is disabled, enable it if you want it to take effect."),

    PLAYTIME_SELF("PLAYTIME.SELF", "{prefix} &eYour playtime is &b<playtime> &eon this server."),
    PLAYTIME_OTHER("PLAYTIME.OTHER", "{prefix} &b<player> &eplaytime is &b<playtime> &eon this server."),

    BLACKLISTED_PERMISSIONS_ALREADY_ADDED("BLACKLISTED-PERMISSIONS.ALREADY-ADDED", "{prefix} &bThat permission is already blacklisted."),
    BLACKLISTED_PERMISSIONS_ADDED("BLACKLISTED-PERMISSIONS.ADDED", "{prefix} &bYou have added &3<permission> &bto blacklisted permissions."),
    BLACKLISTED_PERMISSIONS_DONT_EXISTS("BLACKLISTED-PERMISSIONS.DONT-EXISTS", "{prefix} &bThat permission is not currently blacklisted."),
    BLACKLISTED_PERMISSIONS_REMOVED("BLACKLISTED-PERMISSIONS.REMOVED", "{prefix} &bYou have removed &3<permission> &bfrom blacklisted permissions."),

    BAN_EVADING("BAN-EVADING-ALERT",  "{prefix} &3<player> &bjoined &3<server> &band might be ban evading&b! &7(&bAlts&7: &a<alts>&7)"),
    CHAT_MENTION("CHAT-MENTION", "{0} {prefix} &b<player> &3has just mention you in a chat! {0} "),

    COINS_MESSAGE("COINS.MESSAGE", "{prefix} &eYou currently have &b<coins> &ecoins&7. &eYou can purchase &b<amount> &eranks!"),
    COINS_DONT_HAVE_ENOUGH("COINS.PURCHASE-NOT-ENOUGH", "{prefix} &eYou don't have enough coins to purchase any of purchasable ranks!"),
    COINS_NO_RANKS_TO_PURCHASE("COINS.NO-RANK-TO-PURCHASE", "{prefix} &eThere is no ranks that can be currently purchased!"),
    COINS_SET("COINS.SET", "{prefix} &eYou have successfully set &b<player>'s &ecoins to &b<amount>&e."),
    COINS_RANK_PURCHASED("COINS.RANK-PURCHASED", "{prefix} &eYou have purchased &b<rank> &erank for &b30 days. &eNow you have &b<coins> coins&e."),

    JOINED_VANISHED("JOINED-VANISHED", "{prefix} &bYou have joined vanished with your vanish priority at &3<priority> %&b."),

    NOTE_ADDED("NOTE.ADDED", "{prefix} &bNote with id &3#<id> &bhas been added to &3<player>&b."),
    NOTE_INVALID_ID("NOTE.INVALID-ID", "{prefix} &cThat's not a valid note id. Use /note <player> to check ids."),
    NOTE_REMOVED("NOTE.REMOVED", "{prefix} &bNote with id &3#<id> &bhas been removed from &3<player>&b."),
    NOTE_DONT_HAVE("NOTE.DONT-HAVE", "{prefix} &bThat player has no notes saved."),

    STAFF_AUTH_DONT_NEED("STAFF-AUTH.DONT-NEED", "{prefix} &bYou don't need to authenticate yourself!"),
    STAFF_AUTH_WRONT_PASS("STAFF-AUTH.AUTH-WRONG", "{prefix} &bYou've entered wrong password."),
    STAFF_AUTH_AUTHENTICATED("STAFF-AUTH.AUTHENTICATED", "{prefix} &bYou have successfully authenticated yourself."),
    STAFF_AUTH_REGISTER_WRONG_REPEAT("STAFF-AUTH.REGISTER.WRONG-PASS-REPEAT", "{prefix} &cYour passwords do not match, please try again!"),
    STAFF_AUTH_RESET("STAFF-AUTH.RESET", "{prefix} &bStaff Auth for &3<player> &bhas been reseted!"),
    STAFF_AUTH_RESET_ERROR("STAFF-AUTH.RESET-CANT", "{prefix} &cThat player is not currently authenticated!"),

    BACK_CANT_FIND("BACK.CANT-FIND", "{prefix} &cWe couldn't find your back location."),
    BACK_TELEPORTED("BACK.TELEPORTED", "{prefix} &bYou've been teleported to your last location."),

    PLAYER_DAY_SET("DAY-SET", "{prefix} &aTime updated to day."),
    PLAYER_NIGHT_SET("NIGHT-SET", "{prefix} &aTime updated to night."),


    END("", "");

    @Getter
    private final String path;
    @Getter
    private final String value;
    @Getter
    private final List<String> listValue;

    private final ConfigFile configFile = AquaCore.INSTANCE.getLanguage();

    Language(String path, String value) {
        this.path = path;
        this.value = value;
        this.listValue = new ArrayList<>(Collections.singletonList(value));
    }

    public String toString() {
        Replacement replacement = new Replacement(Color.translate(configFile.getString(this.path)));
        replacement.add("{prefix} ", configFile.getString("PREFIX"));
        return replacement.toString().replace("{0}", "\n");
    }
}
