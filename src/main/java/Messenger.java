import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.channel.ChannelType;
import net.dv8tion.jda.api.entities.emoji.Emoji;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.exceptions.InsufficientPermissionException;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

public class Messenger extends ListenerAdapter {

    public void onMessageReceived(MessageReceivedEvent event) {
        if (event.getAuthor().isBot()) return; // ignores bots

        int rng = (int) (Math.random() * 100);
        Message message = event.getMessage();
        String content = message.getContentRaw();
        String contentLowerCase = message.getContentRaw().toLowerCase();
        ArrayList<String> words = messageContentToArrayList(content);

        // dad joke
        if (contentLowerCase.contains("im ") || contentLowerCase.contains("i'm ") || contentLowerCase.contains("i’m ")) {
            message.getChannel().sendTyping().queue();

            if (rng > 50) {
                message.reply(dadReply(content)).queueAfter(1, TimeUnit.SECONDS);
            }
        }

        // response to its own name
        if (content.contains("<@" + System.getenv("botID") + ">") || contentLowerCase.contains(System.getenv("botName"))) {
            // event.getGuild().getEmojis().get((int) (Math.random()*(event.getGuild().getEmojis().size())))
            message.reply(nameResponse()).queue();
        }

        // amogus emoji reaction
        if (contentLowerCase.contains("among")) {
            message.addReaction(Emoji.fromFormatted("<:imposter:792647479589994516>")).queue();
        }

        // responders in funny server
        if (event.getChannelType().isGuild() && event.getGuild().getId().equals(System.getenv("funnyServer"))) {

            // target ping
            if (contentLowerCase.contains(System.getenv("trigger"))) {
                message.reply("<@" + System.getenv("targetID") + "> wakey wakey").queue();
            }

            // timeout user for "tweakin"
            if (contentLowerCase.contains("tweakin")) {
                message.reply("smh").queue();

                try {
                    event.getGuild().timeoutFor(message.getAuthor(), 37, TimeUnit.SECONDS).queue();
                }
                catch (InsufficientPermissionException e) {
                    DiscordBot.sendMessageToUser(Long.parseLong(System.getenv("ownerID")), "Permission Issue in Server " + event.getGuild());
                }
            }
       }

        if (event.isFromType(ChannelType.PRIVATE)) {
            DirectMessenger.mailman(event, message, content);
        }
    }

    public static ArrayList<String> messageContentToArrayList(String content) {
        ArrayList<String> words = new ArrayList<String>();
        StringBuilder temp = new StringBuilder();

        for (int i=0; i<content.length(); i++) {
            char currentLetter = content.charAt(i);

            // only allows letters and nums
            if (Character.isAlphabetic(currentLetter) || Character.isDigit(currentLetter)) {
                temp.append(currentLetter);
            }

            // exits on final letter
            if (i == content.length()-1) {
                words.add(temp.toString());
                break;
            }

            // checks if whitespace
            if (Character.isWhitespace(currentLetter)) {
                words.add(temp.toString());
                temp = new StringBuilder();
            }
        }

        return words;
    }

    public String dadReply(String content) {
        String msg;

        // checks for various versions of "im"
        if (content.toLowerCase().contains("im ")) {
            msg = "hi " + content.substring(content.toLowerCase().indexOf("im ") + 3);
        }
        else if (content.toLowerCase().contains("i'm ")) {
            msg = "hi " + content.substring(content.toLowerCase().indexOf("i'm ") + 4);
        }
        else {
            msg = "hi " + content.substring(content.toLowerCase().indexOf("i’m ") + 4);
        }

        // reciprocates if caps
        if (content.toUpperCase().equals(content)) {
            msg = "HI " + msg.substring(3);
        }

        // prevents reversal, ex; User: "im im dumb" \ Bot: "hi im dumb"
        if (msg.toLowerCase().contains("im") || msg.toLowerCase().contains("i'm") || msg.toLowerCase().contains("i’m")) {
            return "dont u dare put words in my mouth.";
        }

        return msg;
    }

    public String nameResponse() {
        String[] responses = new String[] {
                "ay, im walkin' over here",
                "whaddup boss",
                "sup",
                "go away",
                "stop im on roblox rn",
                "ill be right there, press alt+f4 to see a cool trick in the meantime",
                "ready to hop on amogus?",
                "not rn, my cat is eating my dog",
                "🥱"
        };

        int num = (int)(Math.random()*responses.length);
        return responses[num];
    }
}
