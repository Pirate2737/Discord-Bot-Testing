import net.dv8tion.jda.api.events.interaction.command.MessageContextInteractionEvent;
import net.dv8tion.jda.api.events.interaction.command.UserContextInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

public class ContextMenu extends ListenerAdapter {

    public void onUserContextInteraction(UserContextInteractionEvent event) {
        if (event.getName().equals("Get user avatar")) {
            event.reply("Avatar: " + event.getTarget().getEffectiveAvatarUrl()).setEphemeral(true).queue();
        }
    }

    public void onMessageContextInteraction(MessageContextInteractionEvent event) {
        if (event.getName().equals("Count words")) {
            event.reply("> " + event.getTarget().getJumpUrl() + "\nWords: " + event.getTarget().getContentRaw().split("\\s+").length).setEphemeral(true).queue();
        }
    }
}
