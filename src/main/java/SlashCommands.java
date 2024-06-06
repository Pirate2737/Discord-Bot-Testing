import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

public class SlashCommands extends ListenerAdapter {
    public void onSlashCommandInteraction (SlashCommandInteractionEvent event) {
        switch (event.getName()) {
            case "Say":
                say(event, event.getOption("content").getAsString());
                break;
            case "Set Status":
                DiscordBot.setStatus(event.getOption("content").getAsString());
                break;
        }
    }

    public void say (SlashCommandInteractionEvent event, String content) {
        event.reply(content).queue();
    }

}
