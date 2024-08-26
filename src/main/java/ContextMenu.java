import net.dv8tion.jda.api.events.interaction.command.MessageContextInteractionEvent;
import net.dv8tion.jda.api.events.interaction.command.UserContextInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

public class ContextMenu extends ListenerAdapter {

    public void onUserContextInteraction(UserContextInteractionEvent event) {
        switch (event.getName()) {
            case "Get user avatar":
                event.reply(event.getTarget().getEffectiveAvatarUrl()).setEphemeral(true).queue();
                break;

            case "amogus bomb":
                String[] lettersToSend = new String[] {"a", "m", "o", "n", "g", "** **", "u", "s"};

                if (event.getTarget().getIdLong() != DiscordBot.getJDA().getSelfUser().getIdLong()) {
                    for (String letter : lettersToSend) {
                        DirectMessenger.sendMessageToUser(event.getTarget().getIdLong(), letter);
                    }
                    DirectMessenger.sendMessageToUser(event.getTarget().getIdLong(), "^ Courtesies of <@" + event.getUser().getIdLong() + ">");

                    event.reply("thumbs up").setEphemeral(true).queue();
                }
                else {
                    for (String letter : lettersToSend) {
                        DirectMessenger.sendMessageToUser(event.getUser().getIdLong(), letter);
                    }

                    event.reply("nuh uh. get amogus bombed urself bucko").setEphemeral(true).queue();
                }
                break;
        }
    }

    public void onMessageContextInteraction(MessageContextInteractionEvent event) {
        if (event.getName().equals("Count words")) {
            event.reply("> " + event.getTarget().getJumpUrl() + "\nWords: " + event.getTarget().getContentRaw().split("\\s+").length).setEphemeral(true).queue();
        }
    }
}