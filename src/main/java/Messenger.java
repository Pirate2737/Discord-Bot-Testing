import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.channel.ChannelType;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.entities.emoji.Emoji;
import net.dv8tion.jda.api.events.interaction.component.StringSelectInteractionEvent;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.exceptions.HierarchyException;
import net.dv8tion.jda.api.exceptions.InsufficientPermissionException;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.components.selections.StringSelectMenu;

import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

public class Messenger extends ListenerAdapter {

    public void onMessageReceived(MessageReceivedEvent event) {
        if (event.getAuthor().isBot()) return; // ignores bots

        int rng = (int) (Math.random() * 100);
        Message message = event.getMessage();
        String content = message.getContentRaw();
        String contentLowerCase = message.getContentRaw().toLowerCase();
        ArrayList<String> words = messageContentToArrayList(contentLowerCase);
        
        if (event.isFromType(ChannelType.PRIVATE)) {
            DirectMessenger.mailman(event, message, content);
        }

        // amogus in channel
        if (contentLowerCase.equals("!amogusify")) {
            message.delete().queue();

            message.reply("i love amogus. which channels?").addActionRow(
                    StringSelectMenu.create("amogus-channels")
                            .addOption("This Channel", "this", Emoji.fromUnicode("U+1F93C"))
                            .addOption("All Channels", "all", Emoji.fromUnicode("U+1F479"))
                            .addOption("Delete This", "null", Emoji.fromUnicode("U+274C"))
                            .build())
                    .queue();
        }

        // dad joke NEW
        if (words.size() > 1 && words.subList(0, words.size()-1).contains("im")) {
            message.getChannel().sendTyping().queue();

            if (rng > 50) {
                message.reply(dadReply(words)).queueAfter(1, TimeUnit.SECONDS);
            }
        }

        // response to its own name
        if (content.contains("<@" + DiscordBot.getJDA().getSelfUser().getIdLong() + ">") || contentLowerCase.contains(DiscordBot.getJDA().getSelfUser().getName().toLowerCase())) {
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

                try {
                    message.reply("smh").queue();
                    event.getGuild().timeoutFor(message.getAuthor(), 37, TimeUnit.SECONDS).queue();
                }
                catch (InsufficientPermissionException | HierarchyException e) {
                    message.reply("smh (give me perms to timeout u)").queue();
                    DirectMessenger.sendMessageToUser(Long.parseLong(System.getenv("ownerID")), "Permission Issue in Server " + event.getGuild());
                }
            }
       }
    }

    public static ArrayList<String> messageContentToArrayList(String content) {
        ArrayList<String> words = new ArrayList<>();
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

    public String dadReply(ArrayList<String> words) {
        StringBuilder msg = new StringBuilder("hi ");

        // reassembler -- break into new method?
        for (String word : words.subList(words.indexOf("im")+1, words.size())) {
            if (word.equalsIgnoreCase("and") && words.indexOf("and") > 1) {
                break;
            }

            msg.append(word).append(" ");
        }

        return msg.toString();
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

    public void amogusify(TextChannel channel) {
        String[] lettersToSend = new String[] {"a", "m", "o", "n", "g", "** **", "u", "s"};
        for (String letter : lettersToSend) {
            channel.sendMessage(letter).queue();
        }
    }

    // Dropdown Menu Handler
    public void onStringSelectInteraction(StringSelectInteractionEvent event) {
        if (event.getComponentId().equals("amogus-channels")) {
            event.reply("👨‍🎓").setEphemeral(true).queue();
            event.getMessage().delete().queue();

            switch (event.getValues().getFirst()) {
                case "all" -> {
                    for (TextChannel channel : event.getGuild().getTextChannels()) {
                        amogusify(channel);
                    }
                }
                case "this" -> amogusify(event.getChannel().asTextChannel());
            }
        }
    }
}