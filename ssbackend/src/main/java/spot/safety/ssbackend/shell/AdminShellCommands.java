package spot.safety.ssbackend.shell;

import org.springframework.context.annotation.Profile;
import org.springframework.shell.core.command.annotation.Command;
import org.springframework.shell.core.command.annotation.Option;
import org.springframework.stereotype.Component;
import spot.safety.ssbackend.demo.DemoDataService;

@Component
@Profile("!prod")
public class AdminShellCommands {
    private final DemoDataService demoDataService;

    public AdminShellCommands(DemoDataService demoDataService) {
        this.demoDataService = demoDataService;
    }

    @Command(name = "seed-data", description = "Fills the database with demo data.")
    public String seedData(
            //@Option(longName = "count", defaultValue = "10", description = "The number of entities to seed")
            //int count
    ) {
        try {
            demoDataService.seedDatabase();
            return "Success: Successfully planted demo data in the database!";
        } catch (Exception e) {
            return "Error: Failed to seed data: " + e.getMessage();
        }
    }
}