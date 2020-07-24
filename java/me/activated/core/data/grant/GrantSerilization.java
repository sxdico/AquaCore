package me.activated.core.data.grant;

import me.activated.core.api.rank.grant.Grant;

import java.util.ArrayList;
import java.util.List;

public class GrantSerilization {

    public static String serilizeGrants(List<Grant> grants) {
        StringBuilder builder = new StringBuilder();
        for (Grant grant : grants) {
            builder.append(serilizeGrant(grant));
            builder.append(";");
        }
        return builder.toString();
    }

    public static List<Grant> deserilizeGrants(String source) {
        if (!source.contains(";")) return new ArrayList<>();

        List<Grant> grants = new ArrayList<>();

        String[] attributes = source.split(";");
        for (String piece : attributes) {
            Grant grant = deserilizeGrant(piece);
            if (grant != null) {
                grants.add(grant);
            }
        }

        return grants;
    }

    private static String serilizeGrant(Grant grant) {
        StringBuilder builder = new StringBuilder();

        if (grant == null) return "null";

        builder.append("rankName@").append(grant.getRankName());
        builder.append(":addedAt@").append(grant.getAddedAt());
        builder.append(":duration@").append(grant.getDuration());
        builder.append(":addedBy@").append(grant.getAddedBy());
        builder.append(":reason@").append(grant.getReason());
        builder.append(":active@").append(grant.isActive());
        builder.append(":server@").append(grant.getServer());
        builder.append(":permanent@").append(grant.isPermanent());
        if (grant.getRemovedBy() != null) {
            builder.append(":removedBy@").append(grant.getRemovedBy());
            builder.append(":removedAt@").append(grant.getRemovedAt());
        }

        return builder.toString();
    }

    private static Grant deserilizeGrant(String source) {
        String rankName = "";
        String server = "Global";
        long addedAt = 1L;
        long duration = 1L;
        long removedAt = 1L;
        String removedBy = null;
        String addedBy = "";
        String reason = "";
        boolean active = false, permanent = false;

        if (source.equals("null")) return null;

        String[] attributes = source.split(":");

        for (String info : attributes) {
            String[] grantAttributes = info.split("@");
            String data = grantAttributes[0];

            if (data.equalsIgnoreCase("rankName")) {
                rankName = grantAttributes[1];
            }
            if (data.equalsIgnoreCase("server")) {
                server = grantAttributes[1];
            }
            if (data.equalsIgnoreCase("removedBy")) {
                removedBy = grantAttributes[1];
            }
            if (data.equalsIgnoreCase("removedAt")) {
                removedAt = Long.valueOf(grantAttributes[1]);
            }
            if (data.equalsIgnoreCase("addedAt")) {
                addedAt = Long.valueOf(grantAttributes[1]);
            }
            if (data.equalsIgnoreCase("duration")) {
                duration = Long.valueOf(grantAttributes[1]);
            }
            if (data.equalsIgnoreCase("addedBy")) {
                addedBy = grantAttributes[1];
            }
            if (data.equalsIgnoreCase("reason")) {
                reason = grantAttributes[1];
            }
            if (data.equalsIgnoreCase("active")) {
                active = Boolean.valueOf(grantAttributes[1]);
            }
            if (data.equalsIgnoreCase("permanent")) {
                permanent = Boolean.valueOf(grantAttributes[1]);
            }
        }

        Grant grant = new Grant();
        grant.setRankName(rankName);
        grant.setAddedAt(addedAt);
        grant.setDuration(duration);
        grant.setAddedBy(addedBy);
        grant.setReason(reason);
        grant.setServer(server);
        grant.setActive(active);
        grant.setPermanent(permanent);
        if (removedBy != null) {
            grant.setRemovedBy(removedBy);
            grant.setRemovedAt(removedAt);
        }
        return grant;
    }
}
