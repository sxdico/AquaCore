package me.activated.core.commands.rank;

import me.activated.core.api.rank.RankData;
import me.activated.core.enums.RankType;
import me.activated.core.menus.rank.RankImportMenu;
import me.activated.core.menus.rank.RankListMenu;
import me.activated.core.enums.Language;
import me.activated.core.utilities.Utilities;
import me.activated.core.utilities.chat.Color;
import me.activated.core.utilities.chat.Replacement;
import me.activated.core.utilities.command.BaseCommand;
import me.activated.core.utilities.command.Command;
import me.activated.core.utilities.command.CommandArgs;
import me.activated.core.utilities.general.StringUtils;
import me.activated.core.utilities.general.Tasks;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Comparator;

public class RankCommand extends BaseCommand {

    @Command(name = "rank", permission = "Aqua.command.rank", aliases = {"ranks"}, inGameOnly = false)
    public void onCommand(CommandArgs command) {
        Tasks.runAsync(plugin, () -> {
            CommandSender player = command.getSender();
            String[] args = command.getArgs();

            if (args.length == 0) {
                this.sendUsage(player, 1);
                return;
            }
            if (args.length == 1) {
                if (args[0].equalsIgnoreCase("list")) {
                    if (player instanceof Player) {
                        if (plugin.getCoreConfig().getBoolean("rank-list-use-gui")) {
                            new RankListMenu().open((Player) player);
                            return;
                        }
                    }
                    player.sendMessage(Color.translate("&7&m---------------------------------------------"));
                    plugin.getRankManagement().getRanks().stream().sorted(Comparator.comparingInt(RankData::getWeight).reversed()).forEach(rankData -> {
                        plugin.getCoreConfig().getStringList("rank-list-format").forEach(message -> {
                            Replacement replacement = new Replacement(message);
                            replacement.add("<color>", rankData.getColor());
                            replacement.add("<name>", rankData.getName());
                            replacement.add("<prefix>", !rankData.getPrefix().equals("") ? rankData.getPrefix() : "None");
                            replacement.add("<suffix>", !rankData.getSuffix().equals("") ? rankData.getSuffix() : "None");
                            replacement.add("<permissions>", rankData.getPermissions().size() == 0 ? "None" :
                                    StringUtils.getStringFromList(rankData.getPermissions()));
                            replacement.add("<inheritance>", rankData.getInheritance().size() == 0 ? "None" :
                                    StringUtils.getStringFromList(rankData.getInheritance()));
                            replacement.add("<weight>", rankData.getWeight());

                            player.sendMessage(replacement.toString());
                        });
                    });
                    player.sendMessage(Color.translate("&7&m---------------------------------------------"));
                    return;
                }
                if (args[0].equalsIgnoreCase("import")) {
                    if (!(player instanceof Player)) {
                        player.sendMessage(Color.translate("&cYou can't open GUI."));
                        return;
                    }
                    new RankImportMenu().open((Player) player);
                    return;
                }
                if (args[0].equalsIgnoreCase("getall")) {
                    player.sendMessage(Color.translate("&aPlease wait..."));
                    plugin.getRankManagement().loadRanks();
                    plugin.getRankManagement().saveRanksToConfig();
                    player.sendMessage(Color.translate(Language.PREFIX.toString() + "&aAll ranks have been saved to &branks.yml&a, you can now backup them!"));
                    return;
                }
                this.sendUsage(player, 1);
                return;
            }
            if (args.length == 2) {
                if (args[0].equalsIgnoreCase("help")) {
                    if (!Utilities.isNumberInteger(args[1])) {
                        player.sendMessage(Language.USE_NUMBERS.toString());
                        return;
                    }
                    int page = Integer.parseInt(args[1]);
                    if (page != 1 && page != 2 && page != 3) {
                        player.sendMessage(Color.translate(Language.PREFIX + "&cInvalid page."));
                        return;
                    }
                    this.sendUsage(player, page);
                    return;
                }
                if (args[0].equalsIgnoreCase("create")) {
                    RankData rankData = plugin.getRankManagement().getRank(args[1]);

                    if (rankData != null) {
                        player.sendMessage(Language.RANK_ALREADY_EXISTS.toString()
                                .replace("<rank>", rankData.getName()));
                        return;
                    }
                    plugin.getRankManagement().getRanks().add(new RankData(args[1]));
                    player.sendMessage(Language.RANK_CREATED.toString()
                            .replace("<rank>", args[1]));
                    plugin.getRankManagement().getRank(args[1]).save();
                    plugin.getRankManagement().requestRankUpdate();
                    return;
                }
                if (args[0].equalsIgnoreCase("delete")) {
                    RankData rankData = plugin.getRankManagement().getRank(args[1]);

                    if (rankData == null) {
                        player.sendMessage(Language.RANK_NOT_EXISTS.toString()
                                .replace("<rank>", args[1]));
                        return;
                    }
                    player.sendMessage(Language.RANK_DELETED.toString()
                            .replace("<rank>", rankData.getName()));
                    plugin.getRankManagement().deleteRank(rankData);
                    plugin.getRankManagement().requestRankUpdate();
                    return;
                }
                if (args[0].equalsIgnoreCase("setdefault")) {
                    RankData rankData = plugin.getRankManagement().getRank(args[1]);

                    if (rankData == null) {
                        player.sendMessage(Language.RANK_NOT_EXISTS.toString()
                                .replace("<rank>", args[1]));
                        return;
                    }
                    player.sendMessage(Language.RANK_DEFAULT_SET.toString()
                            .replace("<rank>", rankData.getName()));

                    RankData current = plugin.getRankManagement().getDefaultRank();
                    if (current != null) {
                        current.setDefaultRank(false);
                        current.save();
                    }
                    rankData.setDefaultRank(true);
                    rankData.save();
                    plugin.getRankManagement().requestRankUpdate();
                    Tasks.runLater(plugin, plugin.getRankManagement()::requestPermissionsUpdate, 5L);
                    return;
                }
                if (args[0].equalsIgnoreCase("listpermissions") || args[0].equalsIgnoreCase("listperm")) {
                    RankData rankData = plugin.getRankManagement().getRank(args[1]);

                    if (rankData == null) {
                        player.sendMessage(Language.RANK_NOT_EXISTS.toString()
                                .replace("<rank>", args[1]));
                        return;
                    }
                    if (rankData.getPermissions().size() == 0) {
                        player.sendMessage(Language.RANK_HAS_NO_PERMISSIONS.toString()
                                .replace("<rank>", rankData.getName()));
                        return;
                    }
                    player.sendMessage(Language.RANK_PERMISSIONS.toString()
                            .replace("<rank>", rankData.getName())
                            .replace("<permissions>", StringUtils.getStringFromList(rankData.getPermissions()))
                            .replace("<total>", String.valueOf(rankData.getPermissions().size())));
                    return;
                }
                if (args[0].equalsIgnoreCase("listbpermissions") || args[0].equalsIgnoreCase("listbperm")) {
                    RankData rankData = plugin.getRankManagement().getRank(args[1]);

                    if (rankData == null) {
                        player.sendMessage(Language.RANK_NOT_EXISTS.toString()
                                .replace("<rank>", args[1]));
                        return;
                    }
                    if (rankData.getBungeePermissions().size() == 0) {
                        player.sendMessage(Language.RANK_HAS_NO_PERMISSIONS.toString()
                                .replace("<rank>", rankData.getName()));
                        return;
                    }
                    player.sendMessage(Language.RANK_PERMISSIONS.toString()
                            .replace("<rank>", rankData.getName())
                            .replace("<permissions>", StringUtils.getStringFromList(rankData.getBungeePermissions()))
                            .replace("<total>", String.valueOf(rankData.getBungeePermissions().size())));
                    return;
                }
                this.sendUsage(player, 1);
                return;
            }
            if (args[0].equalsIgnoreCase("setprefix")) {
                RankData rankData = plugin.getRankManagement().getRank(args[1]);

                if (rankData == null) {
                    player.sendMessage(Language.RANK_NOT_EXISTS.toString()
                            .replace("<rank>", args[1]));
                    return;
                }
                String prefix = Color.translate(StringUtils.buildMessage(args, 2));
                rankData.setPrefix(prefix);

                player.sendMessage(Language.RANK_PREFIX_SET.toString()
                        .replace("<rank>", args[1])
                        .replace("<prefix>", Color.translate(prefix)));

                rankData.save();
                plugin.getRankManagement().requestRankUpdate();
                return;
            }
            if (args[0].equalsIgnoreCase("setsuffix")) {
                RankData rankData = plugin.getRankManagement().getRank(args[1]);

                if (rankData == null) {
                    player.sendMessage(Language.RANK_NOT_EXISTS.toString()
                            .replace("<rank>", args[1]));
                    return;
                }
                String suffix = Color.translate(StringUtils.buildMessage(args, 2));
                rankData.setSuffix(suffix);

                player.sendMessage(Language.RANK_SUFFIX_SET.toString()
                        .replace("<rank>", args[1])
                        .replace("<suffix>", Color.translate(suffix)));

                rankData.save();
                plugin.getRankManagement().requestRankUpdate();
                return;
            }
            if (args.length == 3) {
                if (args[0].equalsIgnoreCase("setbold")) {
                    RankData rankData = plugin.getRankManagement().getRank(args[1]);

                    if (rankData == null) {
                        player.sendMessage(Language.RANK_NOT_EXISTS.toString()
                                .replace("<rank>", args[1]));
                        return;
                    }
                    if (args[2].equalsIgnoreCase("true") && rankData.isBold()) {
                        player.sendMessage(Language.RANK_ALREADY_BOLD.toString());
                        return;
                    }
                    if (args[2].equalsIgnoreCase("false") && !rankData.isBold()) {
                        player.sendMessage(Language.RANK_NOT_BOLD.toString());
                        return;
                    }
                    if (args[2].equalsIgnoreCase("true")) {
                        rankData.setBold(true);
                        player.sendMessage(Language.RANK_BOLD_SET.toString()
                                .replace("<rank>", rankData.getName()));

                        rankData.save();
                        plugin.getRankManagement().requestRankUpdate();
                        Tasks.runLater(plugin, plugin.getRankManagement()::requestPermissionsUpdate, 5L);
                        return;
                    }
                    if (args[2].equalsIgnoreCase("false")) {
                        rankData.setBold(false);
                        player.sendMessage(Language.RANK_BOLD_UN_SET.toString()
                                .replace("<rank>", rankData.getName()));

                        rankData.save();
                        plugin.getRankManagement().requestRankUpdate();
                        Tasks.runLater(plugin, plugin.getRankManagement()::requestPermissionsUpdate, 5L);
                        return;
                    }
                    player.sendMessage(Color.translate("&cCorrect usage: /rank setbold <true|false>"));
                    return;
                }
                if (args[0].equalsIgnoreCase("setitalic")) {
                    RankData rankData = plugin.getRankManagement().getRank(args[1]);

                    if (rankData == null) {
                        player.sendMessage(Language.RANK_NOT_EXISTS.toString()
                                .replace("<rank>", args[1]));
                        return;
                    }
                    if (args[2].equalsIgnoreCase("true") && rankData.isItalic()) {
                        player.sendMessage(Language.RANK_ALREADY_ITALIC.toString());
                        return;
                    }
                    if (args[2].equalsIgnoreCase("false") && !rankData.isItalic()) {
                        player.sendMessage(Language.RANK_NOT_ITALIC.toString());
                        return;
                    }
                    if (args[2].equalsIgnoreCase("true")) {
                        rankData.setItalic(true);
                        player.sendMessage(Language.RANK_ITALIC_SET.toString()
                                .replace("<rank>", rankData.getName()));

                        rankData.save();
                        plugin.getRankManagement().requestRankUpdate();
                        Tasks.runLater(plugin, plugin.getRankManagement()::requestPermissionsUpdate, 5L);
                        return;
                    }
                    if (args[2].equalsIgnoreCase("false")) {
                        rankData.setItalic(false);
                        player.sendMessage(Language.RANK_ITALIC_UN_SET.toString()
                                .replace("<rank>", rankData.getName()));

                        rankData.save();
                        plugin.getRankManagement().requestRankUpdate();
                        Tasks.runLater(plugin, plugin.getRankManagement()::requestPermissionsUpdate, 5L);
                        return;
                    }
                    player.sendMessage(Color.translate("&cCorrect usage: /rank setbold <true|false>"));
                    return;
                }
                if (args[0].equalsIgnoreCase("setpurchasable")) {
                    RankData rankData = plugin.getRankManagement().getRank(args[1]);

                    if (rankData == null) {
                        player.sendMessage(Language.RANK_NOT_EXISTS.toString()
                                .replace("<rank>", args[1]));
                        return;
                    }
                    if (args[2].equalsIgnoreCase("true") && rankData.isPurchasable()) {
                        player.sendMessage(Language.RANK_ALREADY_PURCHASABLE.toString());
                        return;
                    }
                    if (args[2].equalsIgnoreCase("false") && !rankData.isPurchasable()) {
                        player.sendMessage(Language.RANK_NOT_PURCHASABLE.toString());
                        return;
                    }
                    if (args[2].equalsIgnoreCase("true")) {
                        rankData.setPurchasable(true);
                        player.sendMessage(Language.RANK_PURCHASABLE_SET.toString()
                                .replace("<rank>", rankData.getName()));

                        rankData.save();
                        plugin.getRankManagement().requestRankUpdate();
                        Tasks.runLater(plugin, plugin.getRankManagement()::requestPermissionsUpdate, 5L);
                        return;
                    }
                    if (args[2].equalsIgnoreCase("false")) {
                        rankData.setPurchasable(false);
                        player.sendMessage(Language.RANK_PURCHASABLE_UN_SET.toString()
                                .replace("<rank>", rankData.getName()));

                        rankData.save();
                        plugin.getRankManagement().requestRankUpdate();
                        Tasks.runLater(plugin, plugin.getRankManagement()::requestPermissionsUpdate, 5L);
                        return;
                    }
                    player.sendMessage(Color.translate("&cCorrect usage: /rank setpurchasable <true|false>"));
                    return;
                }
                if (args[0].equalsIgnoreCase("setbungee")) {
                    RankData rankData = plugin.getRankManagement().getRank(args[1]);

                    if (rankData == null) {
                        player.sendMessage(Language.RANK_NOT_EXISTS.toString()
                                .replace("<rank>", args[1]));
                        return;
                    }
                    if (args[2].equalsIgnoreCase("true") && rankData.isBungee()) {
                        player.sendMessage(Language.RANK_ALREADY_BUNGEE.toString());
                        return;
                    }
                    if (args[2].equalsIgnoreCase("false") && !rankData.isBungee()) {
                        player.sendMessage(Language.RANK_NOT_BUNGEE.toString());
                        return;
                    }
                    if (args[2].equalsIgnoreCase("true")) {
                        rankData.setBungee(true);
                        player.sendMessage(Language.RANK_BUNGEE_SET.toString()
                                .replace("<rank>", rankData.getName()));

                        rankData.save();
                        plugin.getRankManagement().requestRankUpdate();
                        Tasks.runLater(plugin, plugin.getRankManagement()::requestPermissionsUpdate, 5L);
                        return;
                    }
                    if (args[2].equalsIgnoreCase("false")) {
                        if (!(player instanceof Player)) {
                            player.sendMessage(Color.translate("&cThis argument is executable by player only!"));
                            return;
                        }
                        plugin.getServerManagement().getGlobalPlayers().forEach(globalPlayer -> {
                            if (globalPlayer.getRankName().equalsIgnoreCase(rankData.getName())) {
                                rankData.getBungeePermissions().forEach(perm -> {
                                    plugin.getRankManagement().sendPermissionToBungee((Player) player, globalPlayer.getName(), perm, false);
                                });
                            }
                        });
                        rankData.setBungee(false);
                        player.sendMessage(Language.RANK_BUNGEE_UN_SET.toString()
                                .replace("<rank>", rankData.getName()));

                        rankData.save();
                        plugin.getRankManagement().requestRankUpdate();
                        Tasks.runLater(plugin, plugin.getRankManagement()::requestPermissionsUpdate, 5L);
                        return;
                    }
                    player.sendMessage(Color.translate("&cCorrect usage: /rank setpurchasable <true|false>"));
                    return;
                }
                if (args[0].equalsIgnoreCase("setcolor")) {
                    RankData rankData = plugin.getRankManagement().getRank(args[1]);

                    if (rankData == null) {
                        player.sendMessage(Language.RANK_NOT_EXISTS.toString()
                                .replace("<rank>", args[1]));
                        return;
                    }
                    ChatColor color;
                    try {
                        color = ChatColor.valueOf(args[2]);
                    } catch (Exception e) {
                        player.sendMessage(Language.RANK_INVALID_COLOR.toString().replace("<color>", args[2]));
                        return;
                    }
                    rankData.setColor(color);

                    player.sendMessage(Language.RANK_COLOR_SET.toString()
                            .replace("<rank>", args[1])
                            .replace("<color>", color + args[2]));

                    rankData.save();
                    plugin.getRankManagement().requestRankUpdate();
                    return;
                }
                if (args[0].equalsIgnoreCase("setchatcolor")) {
                    RankData rankData = plugin.getRankManagement().getRank(args[1]);

                    if (rankData == null) {
                        player.sendMessage(Language.RANK_NOT_EXISTS.toString()
                                .replace("<rank>", args[1]));
                        return;
                    }
                    ChatColor color;
                    try {
                        color = ChatColor.valueOf(args[2]);
                    } catch (Exception e) {
                        player.sendMessage(Language.RANK_INVALID_CHAT_COLOR.toString().replace("<color>", args[2]));
                        return;
                    }
                    rankData.setChatColor(color);

                    player.sendMessage(Language.RANK_CHAT_COLOR_SET.toString()
                            .replace("<rank>", args[1])
                            .replace("<color>", color + args[2]));

                    rankData.save();
                    plugin.getRankManagement().requestRankUpdate();
                    return;
                }
                if (args[0].equalsIgnoreCase("setweight")) {
                    RankData rankData = plugin.getRankManagement().getRank(args[1]);

                    if (rankData == null) {
                        player.sendMessage(Language.RANK_NOT_EXISTS.toString()
                                .replace("<rank>", args[1]));
                        return;
                    }
                    if (!Utilities.isNumberInteger(args[2])) {
                        player.sendMessage(Language.RANK_INVALID_WEIGHT.toString().replace("<weight>", args[2]));
                        return;
                    }
                    rankData.setWeight(Integer.parseInt(args[2]));

                    player.sendMessage(Language.RANK_WEIGHT_SET.toString()
                            .replace("<rank>", args[1])
                            .replace("<weight>", String.valueOf(rankData.getWeight())));

                    rankData.save();
                    plugin.getRankManagement().requestRankUpdate();
                    return;
                }
                if (args[0].equalsIgnoreCase("settype")) {
                    RankData rankData = plugin.getRankManagement().getRank(args[1]);

                    if (rankData == null) {
                        player.sendMessage(Language.RANK_NOT_EXISTS.toString()
                                .replace("<rank>", args[1]));
                        return;
                    }
                    RankType type;
                    try {
                        type = RankType.valueOf(args[2].toUpperCase());
                    } catch (Exception e) {
                        player.sendMessage(Language.RANK_INVALID_TYPE.toString().replace("<types>", RankType.toMessage()));
                        return;
                    }
                    rankData.setRankType(type);

                    player.sendMessage(Language.RANK_TYPE_SET.toString()
                            .replace("<rank>", args[1])
                            .replace("<type>", StringUtils.convertFirstUpperCase(args[2])));

                    rankData.save();
                    plugin.getRankManagement().requestRankUpdate();
                    return;
                }
                if (args[0].equalsIgnoreCase("setcoinscost")) {
                    RankData rankData = plugin.getRankManagement().getRank(args[1]);

                    if (rankData == null) {
                        player.sendMessage(Language.RANK_NOT_EXISTS.toString()
                                .replace("<rank>", args[1]));
                        return;
                    }
                    if (!Utilities.isNumberInteger(args[2])) {
                        player.sendMessage(Language.USE_NUMBERS.toString());
                        return;
                    }
                    rankData.setCoinsCost(Integer.parseInt(args[2]));

                    player.sendMessage(Language.RANK_COINS_COST_SET.toString()
                            .replace("<rank>", args[1])
                            .replace("<amount>", String.valueOf(rankData.getCoinsCost())));

                    rankData.save();
                    plugin.getRankManagement().requestRankUpdate();
                    return;
                }
                if (args[0].equalsIgnoreCase("addpermission") || args[0].equalsIgnoreCase("addperm")) {
                    RankData rankData = plugin.getRankManagement().getRank(args[1]);
                    String permission = args[2];

                    if (rankData == null) {
                        player.sendMessage(Language.RANK_NOT_EXISTS.toString()
                                .replace("<rank>", args[1]));
                        return;
                    }
                    if (rankData.hasPermission(permission)) {
                        player.sendMessage(Language.RANK_ALREADY_HAVE_PERMISSION.toString()
                                .replace("<rank>", rankData.getName())
                                .replace("<permission>", permission));
                        return;
                    }
                    rankData.getPermissions().add(permission);
                    player.sendMessage(Language.RANK_PERMISSION_SET.toString()
                            .replace("<rank>", rankData.getName())
                            .replace("<permission>", permission));

                    rankData.save();
                    plugin.getRankManagement().requestRankUpdate();
                    Tasks.runLater(plugin, plugin.getRankManagement()::requestPermissionsUpdate, 5L);
                    return;
                }
                if (args[0].equalsIgnoreCase("removepermission") || args[0].equalsIgnoreCase("removeperm")) {
                    RankData rankData = plugin.getRankManagement().getRank(args[1]);
                    String permission = args[2];

                    if (rankData == null) {
                        player.sendMessage(Language.RANK_NOT_EXISTS.toString()
                                .replace("<rank>", args[1]));
                        return;
                    }
                    if (!rankData.hasPermission(permission)) {
                        player.sendMessage(Language.RANK_NOT_HAVE_PERMISSION.toString()
                                .replace("<rank>", rankData.getName())
                                .replace("<permission>", permission));
                        return;
                    }
                    rankData.getPermissions().remove(permission);
                    player.sendMessage(Language.RANK_PERMISSION_REMOVE.toString()
                            .replace("<rank>", rankData.getName())
                            .replace("<permission>", permission));

                    rankData.save();
                    plugin.getRankManagement().requestRankUpdate();
                    Tasks.runLater(plugin, plugin.getRankManagement()::requestPermissionsUpdate, 5L);
                    return;
                }


                if (args[0].equalsIgnoreCase("addbungeepermission") || args[0].equalsIgnoreCase("addbungeeperm")) {
                    if (!(player instanceof Player)) {
                        player.sendMessage(Color.translate("&cThis argument is executable by player only!"));
                        return;
                    }
                    RankData rankData = plugin.getRankManagement().getRank(args[1]);
                    String permission = args[2];

                    if (rankData == null) {
                        player.sendMessage(Language.RANK_NOT_EXISTS.toString()
                                .replace("<rank>", args[1]));
                        return;
                    }
                    if (rankData.hasBungeePermission(permission)) {
                        player.sendMessage(Language.RANK_ALREADY_HAVE_PERMISSION.toString()
                                .replace("<rank>", rankData.getName())
                                .replace("<permission>", permission));
                        return;
                    }
                    rankData.getBungeePermissions().add(permission);
                    player.sendMessage(Language.RANK_PERMISSION_SET.toString()
                            .replace("<rank>", rankData.getName())
                            .replace("<permission>", permission));

                    rankData.save();
                    plugin.getRankManagement().requestRankUpdate();
                    Tasks.runLater(plugin, plugin.getRankManagement()::requestPermissionsUpdate, 5L);
                    return;
                }
                if (args[0].equalsIgnoreCase("removebungeepermission") || args[0].equalsIgnoreCase("removebungeeperm")) {
                    if (!(player instanceof Player)) {
                        player.sendMessage(Color.translate("&cThis argument is executable by player only!"));
                        return;
                    }

                    RankData rankData = plugin.getRankManagement().getRank(args[1]);
                    String permission = args[2];

                    if (rankData == null) {
                        player.sendMessage(Language.RANK_NOT_EXISTS.toString()
                                .replace("<rank>", args[1]));
                        return;
                    }
                    if (!rankData.hasBungeePermission(permission)) {
                        player.sendMessage(Language.RANK_NOT_HAVE_PERMISSION.toString()
                                .replace("<rank>", rankData.getName())
                                .replace("<permission>", permission));
                        return;
                    }

                    plugin.getServerManagement().getGlobalPlayers().forEach(globalPlayer -> {
                        if (globalPlayer.getRankName().equalsIgnoreCase(rankData.getName())) {
                            rankData.getBungeePermissions().forEach(perm -> {
                                plugin.getRankManagement().sendPermissionToBungee((Player) player, globalPlayer.getName(), perm, false);
                            });
                        }
                    });

                    rankData.getBungeePermissions().remove(permission);
                    player.sendMessage(Language.RANK_PERMISSION_REMOVE.toString()
                            .replace("<rank>", rankData.getName())
                            .replace("<permission>", permission));

                    rankData.save();
                    plugin.getRankManagement().requestRankUpdate();
                    Tasks.runLater(plugin, plugin.getRankManagement()::requestPermissionsUpdate, 5L);
                    return;
                }


                if (args[0].equalsIgnoreCase("addinheritance") || args[0].equalsIgnoreCase("addinher")) {
                    RankData rankData = plugin.getRankManagement().getRank(args[1]);
                    String inheritance = args[2];

                    if (rankData == null) {
                        player.sendMessage(Language.RANK_NOT_EXISTS.toString()
                                .replace("<rank>", args[1]));
                        return;
                    }
                    if (plugin.getRankManagement().getRank(inheritance) == null) {
                        player.sendMessage(Language.RANK_NOT_EXISTS.toString()
                                .replace("<rank>", args[2]));
                        return;
                    }
                    if (rankData.hasInheritance(inheritance)) {
                        player.sendMessage(Language.RANK_ALREADY_HAVE_INHERITANCE.toString()
                                .replace("<rank>", rankData.getName())
                                .replace("<inheritance>", inheritance));
                        return;
                    }
                    rankData.getInheritance().add(inheritance);
                    player.sendMessage(Language.RANK_INHERITANCE_SET.toString()
                            .replace("<rank>", rankData.getName())
                            .replace("<inheritance>", inheritance));

                    rankData.save();
                    plugin.getRankManagement().requestRankUpdate();
                    Tasks.runLater(plugin, plugin.getRankManagement()::requestPermissionsUpdate, 5L);
                    return;
                }
                if (args[0].equalsIgnoreCase("removeinheritance") || args[0].equalsIgnoreCase("removeinher")) {
                    RankData rankData = plugin.getRankManagement().getRank(args[1]);
                    String inheritance = args[2];

                    if (rankData == null) {
                        player.sendMessage(Language.RANK_NOT_EXISTS.toString()
                                .replace("<rank>", args[1]));
                        return;
                    }
                    if (plugin.getRankManagement().getRank(inheritance) == null) {
                        player.sendMessage(Language.RANK_NOT_EXISTS.toString()
                                .replace("<rank>", args[2]));
                        return;
                    }
                    if (!rankData.hasInheritance(inheritance)) {
                        player.sendMessage(Language.RANK_NOT_HAVE_INHERITANCE.toString()
                                .replace("<rank>", rankData.getName())
                                .replace("<inheritance>", inheritance));
                        return;
                    }
                    rankData.getInheritance().remove(inheritance);
                    player.sendMessage(Language.RANK_INHERITANCE_REMOVE.toString()
                            .replace("<rank>", rankData.getName())
                            .replace("<inheritance>", inheritance));

                    rankData.save();
                    plugin.getRankManagement().requestRankUpdate();
                    Tasks.runLater(plugin, plugin.getRankManagement()::requestPermissionsUpdate, 5L);
                    return;
                }
                this.sendUsage(player, 1);
                return;
            }
            this.sendUsage(player, 1);
        });
    }

    private void sendUsage(CommandSender player, int page) {
        if (page == 1) this.sendFirstPage(player);
        if (page == 2) this.sendSecondPage(player);
        if (page == 3) this.sendThirdPage(player);
    }

    private void sendFirstPage(CommandSender player) {
        player.sendMessage(Color.translate("&7&m---------------------------------------------"));
        player.sendMessage(Color.translate("&e&lRanks Help &7- &6Page &7[&b1/3&7] &7- &b/rank help <page>"));
        player.sendMessage(" ");
        player.sendMessage(Color.translate("&e/rank create <name> &8- &7create rank."));
        player.sendMessage(Color.translate("&e/rank delete <name> &8- &7delete rank."));
        player.sendMessage(Color.translate("&e/rank setbold <rank> <true|false> &8- &7set rank to bold"));
        player.sendMessage(Color.translate("&e/rank setitalic <rank> <true|false> &8- &7set rank to italic"));
        player.sendMessage(Color.translate("&e/rank setpurchasable <rank> <true|false> &8- &7set rank to be purchasable"));
        player.sendMessage(Color.translate("&e/rank setdefault <name> &8- &7set rank to default one."));
        player.sendMessage(Color.translate("&e/rank setprefix <name> <prefix> &8- &7set rank's prefix."));
        player.sendMessage(Color.translate("&e/rank setsuffix <name> <suffix> &8- &7set rank's suffix."));
        player.sendMessage(Color.translate("&e/rank list &8- &7see available ranks " + (plugin.getCoreConfig().getBoolean("rank-list-use-gui") ? "&b(AquaMenu)" : "") + "&7."));
        player.sendMessage(Color.translate("&7&m---------------------------------------------"));
    }

    private void sendSecondPage(CommandSender player) {
        player.sendMessage(Color.translate("&7&m---------------------------------------------"));
        player.sendMessage(Color.translate("&e&lRanks Help &7- &6Page &7[&b2/3&7] &7- &b/rank help <page>"));
        player.sendMessage(" ");
        player.sendMessage(Color.translate("&e/rank setcolor <name> <color> &8- &7set rank's color."));
        player.sendMessage(Color.translate("&e/rank setchatcolor <name> <color> &8- &7set rank's chat color."));
        player.sendMessage(Color.translate("&e/rank setweight <name> <weight> &8- &7set rank's weight."));
        player.sendMessage(Color.translate("&e/rank setcoinscost <name> <cost> &8- &7set rank's coins cost."));
        player.sendMessage(Color.translate("&e/rank [listpermissions|listperm] <name> &8- &7list rank's permissions."));
        player.sendMessage(Color.translate("&e/rank [addpermission|addperm] <name> <permission> &8- &7set rank's permission."));
        player.sendMessage(Color.translate("&e/rank [removepermission|removeperm] <name> <permission> &8- &7remove rank's permission."));
        player.sendMessage(Color.translate("&e/rank [addinheritance|addinher] <name> <inheritance> &8- &7add rank's inheritance."));
        player.sendMessage(Color.translate("&e/rank [removeinheritance|removeinher] <name> <inheritance> &8- &7remove rank's inheritance."));
        player.sendMessage(Color.translate("&7&m---------------------------------------------"));

    }

    private void sendThirdPage(CommandSender player) {
        player.sendMessage(Color.translate("&7&m---------------------------------------------"));
        player.sendMessage(Color.translate("&e&lRanks Help &7- &6Page &7[&b3/3&7] &7- &b/rank help <page>"));
        player.sendMessage(" ");
        player.sendMessage(Color.translate("&e/rank [addbungeepermission|addbungeeperm] <name> <permission> &8- &7set rank's bungee permission."));
        player.sendMessage(Color.translate("&e/rank [removebungeepermission|removebungeeperm] <name> <permission> &8- &7remove rank's bungee permission."));
        player.sendMessage(Color.translate("&e/rank setbungee <rank> <true|false> &8- &7set rank to be bungee"));
        player.sendMessage(Color.translate("&e/rank [listbungeepermissions|listbungeeperm] <name> &8- &7list rank's bungee permissions."));
        player.sendMessage(Color.translate("&e/rank import &8- &7import ranks &b(AquaMenu)&7."));
        player.sendMessage(Color.translate("&e/rank getall &8- &7save all ranks from Mongo data base to &branks.yml&7."));
        player.sendMessage(Color.translate("&e/rank settype <name> <type> &8- &7set rank type"));
        player.sendMessage(Color.translate("&7&m---------------------------------------------"));
    }
}
