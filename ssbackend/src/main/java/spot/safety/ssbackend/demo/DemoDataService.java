package spot.safety.ssbackend.demo;

import lombok.*;
import net.datafaker.Faker;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import spot.safety.ssbackend.dto.image.CreateImageRequest;
import spot.safety.ssbackend.dto.user.CreateUserRequest;
import spot.safety.ssbackend.enums.LicenseStatus;
import spot.safety.ssbackend.enums.Role;
import spot.safety.ssbackend.image.ImageService;
import spot.safety.ssbackend.image.ImageDataService;
import spot.safety.ssbackend.dto.image.ImageResponse;
import org.springframework.core.io.ClassPathResource;
import spot.safety.ssbackend.model.TagValue;
import spot.safety.ssbackend.school.School;
import spot.safety.ssbackend.school.SchoolClass;
import spot.safety.ssbackend.school.SchoolClassRepository;
import spot.safety.ssbackend.school.SchoolService;
import spot.safety.ssbackend.user.User;
import spot.safety.ssbackend.user.UserPrincipal;
import spot.safety.ssbackend.user.UserRepository;
import spot.safety.ssbackend.user.UserService;

import java.time.ZoneOffset;
import java.util.List;
import java.util.stream.IntStream;

@Service
@RequiredArgsConstructor
public class DemoDataService {

    private final UserRepository userRepo;
    private final UserService userSvc;
    private final SchoolService schoolSvc;
    private final SchoolClassRepository schoolClassRepo;
    private final PasswordEncoder passwordEncoder;
    private final ImageService imageService;
    private final ImageDataService imageDataService;

    @Transactional
    public void seedDatabase() {
        var faker = new Faker();

        // school
        var school = School.builder()
                .name(faker.witcher().school())
                .licenseKey(faker.hashing().sha1())
                .licenseExpiry(faker.timeAndDate().future(365, java.util.concurrent.TimeUnit.DAYS).atOffset(ZoneOffset.UTC).toLocalDate())
                .licenseStatus(LicenseStatus.ACTIVE)
                .build();
        schoolSvc.newSchool(school);

        var schoolClass = SchoolClass.builder()
                .name(faker.name().fullName())
                .school(school)
                .build();
        schoolClassRepo.save(schoolClass);

        // admin
        var admin = userRepo.saveAndFlush(User.builder()
                .username("admin")
                .passwordHash(passwordEncoder.encode("Kennwort1!"))
                .role(Role.ADMIN)
                .school(school)
                .active(true)
                .build());

        // actor
        var actor = UserPrincipal.from(admin);

        // teacher
        userSvc.createUser(new CreateUserRequest(
                faker.witcher().character(),
                "Kennwort1!",
                Role.TEACHER,
                school.getId()
        ), actor);

        // students

        IntStream.range(0, 20)
                .forEach(i -> {
                        try {
                            userSvc.createUser(new CreateUserRequest(
                                    faker.witcher().character() + " (" + i + ")",
                                    "Kennwort1!",
                                    Role.STUDENT,
                                    school.getId()), actor);
                        } catch (Exception e) {
                            // just continue, not important that we get all users
                        }
                    }
                );


        // images
        record Scenario(String title, String description, String category, String tag, String fbCorrect,
                        String fbWrong) {
        }

        var scenarios = List.of(
                // CHEMISTRY - DANGEROUS
                new Scenario("Gefahrstoff ohne Kennzeichnung", "Auf einem Labortisch steht eine offene Flasche mit einer unbekannten Flüssigkeit. Niemand weiß, was sich darin befindet.", "CHEMISTRY", "DANGEROUS", "Richtig! Unbekannte Stoffe dürfen niemals ohne Kennzeichnung verwendet werden.", "Falsch. Unbekannte Chemikalien können gefährlich sein und dürfen nicht benutzt werden."),
                new Scenario("Experiment ohne Schutzbrille", "Ein Schüler führt ein Experiment durch, trägt aber keine Schutzbrille.", "CHEMISTRY", "DANGEROUS", "Genau! Im Chemieraum muss immer eine Schutzbrille getragen werden.", "Falsch. Chemikalien können in die Augen gelangen und schwere Verletzungen verursachen."),
                new Scenario("Wasser zur Säure", "Ein Schüler gießt Wasser direkt in konzentrierte Säure.", "CHEMISTRY", "DANGEROUS", "Richtig! Wasser darf nicht direkt zur Säure gegeben werden.", "Falsch. Dabei kann die Mischung spritzen und schwere Verätzungen verursachen."),
                new Scenario("Verschüttete Chemikalie ignorieren", "Auf dem Boden liegt eine verschüttete Chemikalie und niemand meldet den Vorfall.", "CHEMISTRY", "DANGEROUS", "Genau! Verschüttete Chemikalien müssen sofort gemeldet werden.", "Falsch. Verschüttete Stoffe können Verletzungen verursachen."),
                new Scenario("Brenner neben Alkohol", "Neben einem brennenden Bunsenbrenner steht eine offene Flasche mit Ethanol.", "CHEMISTRY", "DANGEROUS", "Richtig! Entzündliche Stoffe dürfen nie neben offenen Flammen stehen.", "Falsch. Es besteht Brand- und Explosionsgefahr."),

                // CHEMISTRY - SAFE
                new Scenario("Schutzbrille richtig aufgesetzt", "Vor dem Experiment setzen alle Schülerinnen und Schüler ihre Schutzbrillen auf.", "CHEMISTRY", "SAFE", "Genau! Schutzbrillen schützen die Augen vor Spritzern.", "Falsch. Das ist eine wichtige Sicherheitsmaßnahme."),
                new Scenario("Alle Behälter beschriftet", "Jede Flasche im Chemieraum besitzt ein gut lesbares Etikett.", "CHEMISTRY", "SAFE", "Richtig! Beschriftungen helfen dabei, Stoffe sicher zu verwenden.", "Falsch. Das ist ein sicheres Verhalten."),
                new Scenario("Schutzhandschuhe tragen", "Eine Schülerin zieht vor dem Experiment Schutzhandschuhe an.", "CHEMISTRY", "SAFE", "Genau! Handschuhe schützen die Haut vor Chemikalien.", "Falsch. Das erhöht die Sicherheit im Labor."),
                new Scenario("Sauberer Labortisch", "Nach dem Experiment räumt ein Schüler seinen Arbeitsplatz vollständig auf.", "CHEMISTRY", "SAFE", "Richtig! Ordnung reduziert Unfallrisiken.", "Falsch. Ein sauberer Arbeitsplatz ist sicher."),
                new Scenario("Unsicherheit melden", "Eine Schülerin fragt den Lehrer nach Hilfe, bevor sie einen unbekannten Stoff verwendet.", "CHEMISTRY", "SAFE", "Perfekt! Bei Unsicherheiten sollte immer nachgefragt werden.", "Falsch. Das ist die richtige Vorgehensweise."),

                // SPORTS - DANGEROUS
                new Scenario("Nasse Stelle in der Halle", "Auf dem Hallenboden befindet sich eine nasse Stelle, über die Schüler laufen.", "SPORTS", "DANGEROUS", "Genau! Auf nassen Böden besteht Rutschgefahr.", "Falsch. Dadurch können schwere Stürze entstehen."),
                new Scenario("Sofort mit dem Sprint beginnen", "Eine Gruppe startet direkt mit einem Sprinttraining ohne Aufwärmen.", "SPORTS", "DANGEROUS", "Richtig! Aufwärmen hilft Verletzungen zu vermeiden.", "Falsch. Muskeln und Gelenke sollten vorbereitet werden."),
                new Scenario("Beschädigte Turnmatte", "Eine Turnmatte ist eingerissen und wird trotzdem benutzt.", "SPORTS", "DANGEROUS", "Genau! Defekte Geräte dürfen nicht verwendet werden.", "Falsch. Beschädigte Sportgeräte können Unfälle verursachen."),
                new Scenario("Unbeaufsichtigtes Klettern", "Ein Schüler klettert ohne Aufsicht auf die Sprossenwand.", "SPORTS", "DANGEROUS", "Richtig! Klettern ohne Aufsicht kann gefährlich sein.", "Falsch. Stürze können schwere Verletzungen verursachen."),

                // SPORTS - SAFE
                new Scenario("Hallenschuhe benutzen", "Alle Schülerinnen und Schüler tragen Hallenschuhe im Sportunterricht.", "SPORTS", "SAFE", "Genau! Hallenschuhe sorgen für besseren Halt.", "Falsch. Das Verhalten ist sicher und korrekt."),
                new Scenario("Gemeinsames Aufwärmen", "Die Klasse wärmt sich vor dem Sport zehn Minuten auf.", "SPORTS", "SAFE", "Genau! Aufwärmen hilft Verletzungen zu vermeiden.", "Falsch. Das ist sicheres Verhalten."),
                new Scenario("Richtige Sportschuhe", "Alle Schülerinnen und Schüler tragen Hallenschuhe mit guter Sohle.", "SPORTS", "SAFE", "Richtig! So wird die Rutschgefahr reduziert.", "Falsch. Das ist sicher."),
                new Scenario("Schaden erkannt", "Ein Schüler bemerkt ein defektes Sportgerät und informiert sofort die Lehrkraft.", "SPORTS", "SAFE", "Genau! Schäden sollten immer gemeldet werden.", "Falsch. Das ist richtiges Verhalten."),
                new Scenario("Abstand beim Werfen", "Beim Ballwerfen achten alle auf ausreichend Abstand zu anderen.", "SPORTS", "SAFE", "Richtig! Das verhindert Zusammenstöße.", "Falsch. Das Verhalten ist sicher."),
                new Scenario("Ausreichend trinken", "Nach einer anstrengenden Übung machen die Schülerinnen und Schüler eine Trinkpause.", "SPORTS", "SAFE", "Perfekt! Regelmäßiges Trinken ist wichtig.", "Falsch. Das ist ein gesundes Verhalten."),

                // TECHNOLOGY - SAFE
                new Scenario("Gerät vor Nutzung prüfen", "Ein Schüler kontrolliert ein Gerät auf sichtbare Schäden bevor er es einschaltet.", "TECHNOLOGY", "SAFE", "Richtig! Geräte sollten immer geprüft werden.", "Falsch. Das ist eine sichere Vorgehensweise."),
                new Scenario("Lötkolben auf Ablage", "Nach dem Löten wird der heiße Lötkolben in die vorgesehene Halterung gelegt.", "TECHNOLOGY", "SAFE", "Perfekt! So wird Brandgefahr vermieden.", "Falsch. Das Verhalten ist korrekt und sicher."),
                new Scenario("Sichtprüfung vor Nutzung", "Ein Schüler überprüft Kabel und Gehäuse eines Geräts vor dem Einschalten.", "TECHNOLOGY", "SAFE", "Genau! Geräte sollten vor der Nutzung kontrolliert werden.", "Falsch. Das ist sicher."),
                new Scenario("Kabel befestigt", "Stromkabel werden ordentlich am Rand des Raumes verlegt.", "TECHNOLOGY", "SAFE", "Richtig! So entstehen keine Stolperfallen.", "Falsch. Das ist sicheres Verhalten."),
                new Scenario("Richtige Ablage", "Ein heißer Lötkolben wird direkt in die vorgesehene Halterung gelegt.", "TECHNOLOGY", "SAFE", "Genau! Das verhindert Brände und Verbrennungen.", "Falsch. Das ist die richtige Vorgehensweise."),
                new Scenario("Ordentlicher Technikplatz", "Nach dem Unterricht werden alle Werkzeuge und Kabel weggeräumt.", "TECHNOLOGY", "SAFE", "Richtig! Ordnung sorgt für Sicherheit.", "Falsch. Das Verhalten ist sicher."),
                new Scenario("Gerät vom Strom trennen", "Vor einer Reparatur wird ein Gerät vom Stromnetz getrennt.", "TECHNOLOGY", "SAFE", "Perfekt! Das schützt vor Stromschlägen.", "Falsch. Das ist sicheres Arbeiten."),

                // TECHNOLOGY - DANGEROUS
                new Scenario("Mehrfachstecker überlastet", "Mehrere leistungsstarke Geräte sind an einer einzigen Steckdosenleiste angeschlossen.", "TECHNOLOGY", "DANGEROUS", "Genau! Überlastete Steckdosen können Brände verursachen.", "Falsch. Es besteht Überhitzungs- und Brandgefahr."),
                new Scenario("Kabel quer durch den Raum", "Mehrere Stromkabel liegen ungesichert über den Boden.", "TECHNOLOGY", "DANGEROUS", "Richtig! Stolperfallen müssen vermieden werden.", "Falsch. Herumliegende Kabel sind gefährlich."),
                new Scenario("Beschädigtes Netzteil", "Ein Netzteil hat sichtbare Risse im Gehäuse und wird trotzdem verwendet.", "TECHNOLOGY", "DANGEROUS", "Genau! Beschädigte Geräte dürfen nicht benutzt werden.", "Falsch. Es besteht Stromschlaggefahr."),

                // TRAFFIC - DANGEROUS
                new Scenario("Bei Rot über die Straße", "Ein Schüler überquert die Straße, obwohl die Ampel rot zeigt.", "TRAFFIC", "DANGEROUS", "Richtig! Bei Rot darf die Straße nicht überquert werden.", "Falsch. Das erhöht das Unfallrisiko erheblich."),
                new Scenario("Blick aufs Handy", "Eine Schülerin schaut beim Überqueren einer Straße auf ihr Smartphone.", "TRAFFIC", "DANGEROUS", "Richtig! Ablenkung im Straßenverkehr kann gefährlich sein.", "Falsch. Man sollte sich auf den Verkehr konzentrieren."),
                new Scenario("Neben dem LKW stehen", "Ein Schüler wartet direkt neben einem abbiegenden LKW.", "TRAFFIC", "DANGEROUS", "Genau! Im toten Winkel kann der Fahrer Personen nicht sehen.", "Falsch. Das ist eine sehr gefährliche Situation."),

                // TRAFFIC - SAFE
                new Scenario("Zebrastreifen benutzen", "Eine Schülerin überquert die Straße an einem Zebrastreifen und schaut vorher nach links und rechts.", "TRAFFIC", "SAFE", "Genau! So verhält man sich sicher im Straßenverkehr.", "Falsch. Das Verhalten ist sicher und korrekt."),
                new Scenario("Mit Helm unterwegs", "Ein Schüler fährt mit einem korrekt sitzenden Fahrradhelm zur Schule.", "TRAFFIC", "SAFE", "Perfekt! Helme schützen bei Unfällen.", "Falsch. Das ist ein sicheres Verhalten."),
                new Scenario("Bei Grün gehen", "Eine Schülerin überquert die Straße erst, nachdem die Ampel grün zeigt.", "TRAFFIC", "SAFE", "Genau! So verhält man sich sicher im Straßenverkehr.", "Falsch. Das ist richtiges Verhalten."),
                new Scenario("Vor dem Überqueren prüfen", "Ein Schüler schaut nach links und rechts bevor er die Straße überquert.", "TRAFFIC", "SAFE", "Richtig! Aufmerksamkeit erhöht die Sicherheit.", "Falsch. Das Verhalten ist sicher."),
                new Scenario("Helm richtig befestigt", "Eine Schülerin trägt einen korrekt sitzenden Fahrradhelm.", "TRAFFIC", "SAFE", "Genau! Helme schützen bei Stürzen.", "Falsch. Das ist sicheres Verhalten."),
                new Scenario("Gut sichtbar im Dunkeln", "Ein Schüler trägt reflektierende Kleidung auf dem Schulweg.", "TRAFFIC", "SAFE", "Richtig! Sichtbarkeit erhöht die Sicherheit.", "Falsch. Das Verhalten ist sicher."),
                new Scenario("Sicher über die Straße", "Eine Schülerin nutzt einen Zebrastreifen und achtet auf den Verkehr.", "TRAFFIC", "SAFE", "Perfekt! Das ist sicheres Verhalten im Straßenverkehr.", "Falsch. Das Verhalten ist korrekt und sicher.")
        );

        for (int i = 0; i < scenarios.size(); i++) {
            var s = scenarios.get(i);
            CreateImageRequest request = new CreateImageRequest(
                    s.title(),
                    s.description(),
                    "",
                    s.category(),
                    TagValue.valueOf(s.tag()),
                    s.fbCorrect(),
                    s.fbWrong()
            );

            ImageResponse resp = imageService.createImage(request, actor);

            // For the first scenario, upload the bundled demo image from resources/demo/images
            if (i == 0) {
                try {
                    var resource = new ClassPathResource("demo/images/scenario-0.png");
                    if (resource.exists()) {
                        byte[] data = resource.getInputStream().readAllBytes();
                        imageDataService.storeBytes(resp.id(), data, "scenario-0.png", actor);
                    }
                } catch (Exception e) {
                    // non-fatal for demo seeding; log if you have logging configured
                }
            }
        }
    }
}