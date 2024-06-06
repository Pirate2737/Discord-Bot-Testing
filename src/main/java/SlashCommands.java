import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

public class SlashCommands extends ListenerAdapter {
    public void onSlashCommandInteraction (SlashCommandInteractionEvent event) {
        switch (event.getName()) {
            case "say":
                say(event, event.getOption("content").getAsString());
                break;
            case "setstatus":
                if(!event.getUser().getId().equals(System.getenv("ownerID"))) {
                    event.reply("fine").queue();
                    //event.reply("nuh uh 👎").queue();
                    //break;
                }
                DiscordBot.setStatus(event.getOption("type").getAsString(), event.getOption("content").getAsString());
                event.reply("👍").setEphemeral(true).queue();
                break;
            default:
                event.reply("I can't handle that command right now :(").setEphemeral(true).queue();
                System.out.println("ERROR!! Event name: " + event.getName());
        }
    }

    public void say (SlashCommandInteractionEvent event, String content) {
        event.reply(content).queue();
    }

}
