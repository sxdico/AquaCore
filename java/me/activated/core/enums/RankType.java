package me.activated.core.enums;

import me.activated.core.utilities.general.StringUtils;

import java.util.stream.Collectors;
import java.util.stream.Stream;

public enum RankType {

    DEFAULT, DONATOR, STAFF;

    public static String toMessage() {
        return StringUtils.getStringFromList(Stream.of(RankType.values()).map(RankType::toString)
                .map(String::toLowerCase).map(StringUtils::convertFirstUpperCase).collect(Collectors.toList()));
    }
}
