import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.components.buttons.Button;

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

                DiscordBot.writeActivityToJSON(event.getOption("type").getAsString(), event.getOption("content").getAsString());
                DiscordBot.setActivity(event.getJDA().getPresence(), event.getOption("type").getAsString(), event.getOption("content").getAsString());
                event.reply("👍").setEphemeral(true).queue();

                break;

            case "joinvc":
                Music.joinVoice(event, event.getOption("vc").getAsString());
                event.reply("👍").queue();
                break;

            case "leavevc":
                if (!Music.leaveVoice(event)) {
                    event.reply("I am not in any voice channel in this sever").queue();
                }
                else {
                    event.reply("👍").queue();
                }

                break;

            default:
                event.reply("I can't handle that command right now :(").setEphemeral(true).queue();
                System.out.println("ERROR!! Event name: " + event.getName());
        }
    }

    public void say (SlashCommandInteractionEvent event, String content) {
        if(content.length() > 100) {
            event.reply("stop yappin man")
                    .addActionRow(
                            Button.danger("bypassYapLimit","Bypass Yap Limit 👎")
                    )
                    .queue();
        }

        event.reply(content).queue();
    }

    public void onButtonInteraction(ButtonInteractionEvent event) {
        if (event.getComponentId().equals("bypassYapLimit")) {
            if(event.getUser().equals(event.getMessage().getInteraction().getUser())) {
                event.getInteraction().editMessage("yikers, not available rn u rulebreaker").queue(); // send a message in the channel
                event.editButton(event.getButton().withDisabled(true)).queue();
            }
            else {
                event.reply("that is not your message to do that on es-em-aych u weirdo").setEphemeral(true).queue();
            }
        }
    }

}
