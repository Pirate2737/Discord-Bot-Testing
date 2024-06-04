import net.dv8tion.jda.api.JDABuilder;
import javax.security.auth.login.LoginException;

public class DiscordBot {
    public static void main(String[] args) throws LoginException {
        JDABuilder api = JDABuilder.createDefault(System.getenv("token"));

        api.build();
    }
}
