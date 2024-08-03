import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.emoji.Emoji;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

import java.util.ArrayList;

public class DirectMessenger extends Messenger {
    public static void mailman(MessageReceivedEvent event, Message message, String content) {
        ArrayList<String> words = messageContentToArrayList(content);

        if (message.getAuthor().getIdLong() == Long.parseLong(System.getenv("ownerID"))) {
            if (words.getFirst().equals("dm") && content.charAt(0) == '!') {
                try {
                    Long userID = Long.parseLong(words.get(1));
                    String messageToSend = "<@" + message.getAuthor().getIdLong() + ">: " + content.substring(23);

                    DiscordBot.sendMessageToUser(userID, messageToSend);
                    message.addReaction(Emoji.fromUnicode("U+1F44D")).queue();
                } catch (IndexOutOfBoundsException | NumberFormatException e) {
                    event.getChannel().sendMessage("Missing arguments! Ex: `!dm [userID] [message]`").queue();
                } catch (UnsupportedOperationException e) {
                    event.getChannel().sendMessage(("!dm " + message.getAuthor().getIdLong() + " " + content.substring(23) + " 🙄")).queue();
                } catch (NullPointerException e) {
                    event.getChannel().sendMessage("Hmm.. I can't seem to find that user").queue();
                }

            }
        }
        else {
            String messageToSend = "<@" + message.getAuthor().getIdLong() + ">: " + content;
            DiscordBot.sendMessageToUser(Long.parseLong(System.getenv("ownerID")), messageToSend);
        }
    }
}
