import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.channel.middleman.MessageChannel;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

public class Messenger extends ListenerAdapter {

    public void onMessageReceived(MessageReceivedEvent event) {
        if (event.getAuthor().isBot()) return;

        Message message = event.getMessage();
        MessageChannel channel = event.getChannel();
        String content = message.getContentRaw();
        String msg = "";

        // dad joke
        if (content.toLowerCase().contains("im ") || content.toLowerCase().contains("i'm" ) || content.toLowerCase().contains("i’m" )) {
            if (content.toLowerCase().contains("im ")) {
                msg = "hi " + content.substring(content.toLowerCase().indexOf("im ") + 3);
            }
            else {
                msg = "hi " + content.substring(content.toLowerCase().indexOf("i'm ") + 4);
            }

            channel.sendMessage(msg).queue(); // Important to call .queue() on the RestAction returned by sendMessage(...)
        }

        // funny
        if(content.contains(System.getenv("trigger")) && event.getGuild().getId().equals(System.getenv("funnyServer"))) {
            channel.sendMessage("<@" + System.getenv("targetID") + ">").queue();
        }
    }
}
