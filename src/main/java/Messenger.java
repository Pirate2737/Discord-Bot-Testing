import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.emoji.Emoji;
import net.dv8tion.jda.api.entities.emoji.RichCustomEmoji;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import java.util.ArrayList;

public class Messenger extends ListenerAdapter {

    public void onMessageReceived(MessageReceivedEvent event) {
        if (event.getAuthor().isBot() /* && !event.getAuthor().getId().equals(System.getenv("botID"))*/) return; // ignores other bots

        Message message = event.getMessage();
        String content = message.getContentRaw();

        // dad joke
        if (content.toLowerCase().contains("im ") || content.toLowerCase().contains("i'm" ) || content.toLowerCase().contains("i’m" )) {
            message.reply( dadReply(message, content) ).queue(); // Important to call .queue() on the RestAction returned by sendMessage(...)
        }

        // funny
        if (content.toLowerCase().contains(System.getenv("trigger")) && event.getGuild().getId().equals(System.getenv("funnyServer"))) {
            message.reply("<@" + System.getenv("targetID") + "> wakey wakey").queue();
        }

        // response to name
        if (content.contains(System.getenv("botID")) || content.toLowerCase().contains(System.getenv("botName"))) {
            nameResponse(message, event.getGuild().getEmojis().get((int) (Math.random()*(event.getGuild().getEmojis().size()))));
        }

        // amogus
        if (content.toLowerCase().contains("among")) {
            message.addReaction(Emoji.fromFormatted("<:imposter:792647479589994516>")).queue();
        }
    }

//    public void dadReplyNEW(Message message, String content) {
//        /*
//            Goal: recognize "im" regardless of non-letter chars before and after the word
//                  and ignore up to two non-letter chars in the middle of the word
//
//            Step 1: generate a new string based on 'content' that does not contain non-keyboard characters
//            Step 2: locate index of "im", if any            -- arrays?
//            Step 3: if found, check
//         */
//        ArrayList<String> words = new ArrayList<String>();
//        String temp = "";
//
//        for (int i=0; i<content.length(); i++) {
//            if (content.substring(i, i+1).)
//            temp += content.substring(i, i+1);
//
//            if (temp.equals(" ")) {
//                words.add(temp.substring(0, temp.length()-1));
//                temp = "";
//            }
//        }
//    }

    public String dadReply(Message message, String content) {
        String msg;
        if (content.toLowerCase().contains("im ")) {
            msg = "hi " + content.substring(content.toLowerCase().indexOf("im ") + 3);
        }
        else {
            msg = "hi " + content.substring(content.toLowerCase().indexOf("i'm ") + 4);
        }

        return msg;
    }

    public void nameResponse (Message message, RichCustomEmoji emoji) {
        String[] responses = new String[] {
                "ay, im walkin' over here",
                "whaddup boss",
                "sup",
                "go away",
                "stop im on roblox rn",
                "ill be right there, press alt+f4 to see a cool trick in the meantime",
                "ready to hop on amogus?"
        };
        int num = (int)(Math.random()*responses.length);

        message.reply(responses[num]).queue();
    }
}
