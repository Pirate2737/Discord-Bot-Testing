import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.entities.emoji.Emoji;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

import java.util.ArrayList;

public class DirectMessenger extends Messenger {

    private static long setUser = 0;

    // handles all the message forwarding
    public static void mailman(MessageReceivedEvent event, Message message, String content) {
        ArrayList<String> words = messageContentToArrayList(content);

        // other user's msgs get forwarded to me
        if (message.getAuthor().getIdLong() != Long.parseLong(System.getenv("ownerID"))) {
            String messageToSend = "<@" + message.getAuthor().getIdLong() + ">: " + content;
            if (!message.getAttachments().isEmpty()) {
                StringBuilder attatchments = new StringBuilder();
                for (Message.Attachment imgs : message.getAttachments()) {
                    attatchments.append(imgs.getUrl()).append("\n");
                }

                messageToSend += "\n\n**__Attatchments__**" + attatchments;
            }

            sendMessageToUser(Long.parseLong(System.getenv("ownerID")), messageToSend);
            return;
        }

        // command to directly dm someone
        if (words.getFirst().equals("dm") && content.charAt(0) == '!') {
            try {
                long userID = Long.parseLong(words.get(1));
                if (!hasAccessToUser(userID)) {
                    event.getChannel().sendMessage("❌ Hmm.. I can't seem to find that user").queue();
                    return;
                }

                String messageToSend = "<@" + message.getAuthor().getIdLong() + ">: " + content.substring(23);

                if (userID != DiscordBot.getJDA().getSelfUser().getIdLong()) {
                    if (!message.getAttachments().isEmpty()) {
                        StringBuilder attatchments = new StringBuilder();
                        for (Message.Attachment imgs : message.getAttachments()) {
                            attatchments.append(imgs.getUrl()).append("\n");
                        }

                        messageToSend += "\n\n**__Attatchments__**" + attatchments;
                    }
                    sendMessageToUser(userID, messageToSend);
                    message.addReaction(Emoji.fromUnicode("U+1F44D")).queue();
                }
                else {
                    event.getChannel().sendMessage(("!dm " + message.getAuthor().getIdLong() + " " + content.substring(23) + " 🙄")).queue();
                }

            } catch (IndexOutOfBoundsException | NumberFormatException e) {
                event.getChannel().sendMessage("❌ Missing arguments! Ex: `!dm [userID] [message]`").queue();
            }
            return;
        }

        // set dm command
        else if (words.getFirst().equals("setdm") && content.charAt(0) == '!') {
            try {
                setUser = Long.parseLong(words.get(1));
                if (!hasAccessToUser(setUser)) {
                    setUser = 0;
                    event.getChannel().sendMessage("❌ Hmm.. I can't seem to find that user").queue();

                } else {
                    event.getChannel().sendMessage("> ✅ Set dm to <@" + setUser + ">!").queue();
                }

            } catch (NumberFormatException e) {
                setUser = 0;
                event.getChannel().sendMessage("❌ Missing arguments! Ex: `!setdm [userID]`").queue();
            } catch (IndexOutOfBoundsException e) {
                setUser = 0;
                event.getChannel().sendMessage("✅ Set user cleared").queue();
            }

            return;
        }

        // if there is a user set, then message is forwarded
        if (setUser != 0) {
            String messageToSend = content;
            if (!message.getAttachments().isEmpty()) {
                StringBuilder attatchments = new StringBuilder();
                for (Message.Attachment imgs : message.getAttachments()) {
                    attatchments.append(imgs.getUrl()).append("\n");
                }

                messageToSend += "\n\n**__Attatchments__**\n" + attatchments;
            }

            sendMessageToUser(setUser, messageToSend);
        }

    }

    // checks if the bot has a user in cache and is accessible
    public static boolean hasAccessToUser(long userID) {
        JDA jda = DiscordBot.getJDA();

        if (userID == jda.getSelfUser().getIdLong()) return false; // ignores itself
        jda.retrieveUserById(userID).queue();
        User user = jda.getUserById(userID);

        try {
            assert user != null;
            user.openPrivateChannel().queue();
        }
        catch (NullPointerException e) {
            return false;
        }
        return true;
    }

    public static void sendMessageToUser(long userID, String message) {
        if (!hasAccessToUser(userID)) return;
        User user = DiscordBot.getJDA().getUserById(userID);

        assert user != null;
        user.openPrivateChannel().queue(privateChannel -> privateChannel.sendMessage(message).queue(
                success -> System.out.print(""),
                failure -> System.err.println("Failed to send message: " + failure.getMessage())
        ));
    }
}