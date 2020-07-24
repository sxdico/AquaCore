package me.activated.core.data.other.systems;

import lombok.Getter;
import lombok.Setter;
import me.activated.core.utilities.chat.Replacement;
import me.activated.core.enums.Language;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

@Getter
@Setter
public class MessageSystem {

    private boolean messagesToggled = true;
    private UUID lastMessage;
    private Set<String> ignoreList = new HashSet<>();
    private boolean soundsEnabled = true;
    private boolean globalChat = true;
    private boolean chatMention = true;

    public static String getFormat(String sender, String target, String senderName, String targetName, String message, boolean isSender) {
        if (isSender) {
            Replacement replacement = new Replacement(Language.MESSAGE_FORMAT_TO_SENDER.toString());
            replacement.add("<target>", target);
            replacement.add("<target_name>", targetName);
            replacement.add("<message>", message);
            return replacement.toString();
        } else {
            Replacement replacement = new Replacement(Language.MESSAGE_FORMAT_TO_TARGET.toString());
            replacement.add("<sender>", sender);
            replacement.add("<sender_name>", senderName);
            replacement.add("<message>", message);
            return replacement.toString();
        }
    }

    public boolean isIgnoring(String name) {
        for (String nameAttribute : this.getIgnoreList()) {
            if (nameAttribute.equalsIgnoreCase(name)) return true;
        }
        return false;
    }
}
