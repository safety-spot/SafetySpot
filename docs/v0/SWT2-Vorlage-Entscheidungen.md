

|  | Stand: 23.01.2026 |
| :---- | :---- |
| Entscheidungen |  |

|  |  | Projektbezeichnug: SafetySpot |
| :---- | :---- | :---- |
|  | \- |  |

LISTE DER ENTSCHEIDUNGEN

| Für jede Entscheidung ist die nachstehende Tabelle zu kopieren und auszufüllen\! |
| :---- |

Entscheidung 1:

| Benennung der Entscheidung | Was soll entschieden werden? Entwicklungsumgebung: Android Studio |
| :---- | :---- |
| Beschreibung | Beispiel: Für die gesamte Entwicklung soll eine einheitliche Entwicklungsumgebung verwendet werden. Dazu einigt sich das Team auf Android Studio. |
| Stellenwert  | Welchen Stellenwert hat die Entscheidung? Die Entscheidung gilt für das gesamte Projekt. |
| Datum: | Wann wurde die Entscheidung getroffen? 22.01.2026 |
| Entscheider, Entscheidungskreis: | Wer hat die Entscheidung getroffen? Das gesamte Team, bestehend aus Noel Neuhoff, Milian Hinz und Linus Nagel |
|   |  |
| Alternativen | Was sind die Alternativen? IntelliJ IDEA Android SDK Qt Creator Apache Cordova Abgrenzung: Wie wurden die Alternativen identifiziert? Wie wurden von vornherein Alternativen ausgeschlossen? Alternativen haben sich aus Online-Recherche ergeben. |
| Entscheidungs- nkriterien | Was sind die Entscheidungskriterien? Produktivität Funktionsumfang Handhabung Flexibilität Kosten Einarbeitung Bei Android Studio Insbesondere: Android Studio is an integrated development environment (IDE) for Android applications that provides support to the extensive framework and testing tools. Android Studio provides support to NDK and C++ and also provides built-in support to the Google Cloud. Android Studio also supports popular programming languages like Java and Kotlin which make it an obvious choice for Android app development. |
| Bewertung der Alternativen | Was spricht für oder gegen die einzelne Alternative bzgl. der einzelnen Kriterien? \<Argumente für/gegen die einzelnen Alternativen\> Quelle: https://www.geeksforgeeks.org/blogs/android-studio-alternatives/ 1\. IntelliJ IDEA IntelliJ IDEA is an IDE that was developed by JetBrains and is written in Java, Groovy, and Kotlin. It helps in building the IntelliJ IDEA community from the source code and also produces high-quality code.     To learn more, refer to this article: IntelliJ IDEA Key Features     IntelliJ helps in detecting grammatical mistakes made by users.     It is used to search keywords from databases.     For various programming languages, it provides support for syntax highlighting. Advantages     A set of inspections are provided which are built-in static code analysis tools.     It helps to find bugs and errors in the code. Disadvantage     The paid subscription is costly.     The code corrections are sometimes useless for the developers.     Pricing \- kostenlos, da wir eine Education License vom ITC haben. 2\. Android SDK Android SDK is an API library and the tools that are used for testing and debugging Android applications. It is a development kit that includes a collection of software development tools and libraries.     To learn more, refer to this article: Android SDK Key Features     Some sample code, docs, libraries, and many more things are provided to create apps.     Some selective tools are provided in Android SDK to build the Android apps.     It supports the IOT and mobile devices. Advantage     Android SDK integration with other apps is easy.     It provides tools and resources to the developers. Disadvantage     Some security issues are there in the Android platform of Android SDK.     Pricing- Users can use the free version of Android SDK. 3\. Qt Creator Qt Creator is an IDE for programmers that is used to create multiple applications and eases GUI application modifications or designs. It is the best tool for creating Qt applications. Key Features     It supports code formatting and syntax highlighting for different programming languages.     The rest of the word is completed while we are typing and it also has great debugging facilities. Advantage     Software applications can be created in mobile and desktops.     It supports platforms like macOS, windows, and Linux.     It is easy to integrate Qt Creator with the Qt libraries. Disadvantage     Users experience some problems with the mobile versions.         Pricing- The paid plans of commercial license start from $459/month. 4\. Apache Cordova Apache Cordova is used for building mobile applications with the help of HTML, CSS, and Javascript. It is an open-source mobile application development framework that was created by Nitobi.     To learn more, refer to this article: Apache Cordova Key Features     The command line interface is provided in Cordova.     A set of core components is provided in all mobile apps. Advantages     It is used with different libraries and frameworks.     It consists of five platform supports and there are different native plugins available. Disadvantages     Cross-browser compatibility creates major issues.     Some plugins consist of some compatibility issues.     Pricing- It is open source therefore it is free to use.  |
| Entscheidung | Welche Entscheidung wurde getroffen? Die gesamte Entwicklung erfolgt mit Android Studio Objektiv, subjektiv   . ... und transparent/nachvollziehbar |
| Begründung | \<Argumente, die zur Entscheidung ‚Eclipse‘ führen\> |
| Aspekte zur Umsetzung | Beispiel: ??? |

Entscheidung 2:

| Benennung der Entscheidung | Was soll entschieden werden? Auswahl der primären Programmiersprache für die Entwicklung der Android-App. Programmiersprache: Kotlin |
| :---- | :---- |
| Beschreibung | Beispiel: Um die Business-Logik und das UI der App "SafetySpot" umzusetzen, muss eine Programmiersprache gewählt werden, die von der gewählten IDE (Android Studio) unterstützt wird. |
| Stellenwert  | Welchen Stellenwert hat die Entscheidung? Hoch. Diese Entscheidung beeinflusst die gesamte Code-Basis, die Wartbarkeit und die Fehleranfälligkeit der Anwendung. Ein späterer Wechsel wäre mit enormem Aufwand verbunden. |
| Datum: | Wann wurde die Entscheidung getroffen? 23.01.2026 |
| Entscheider, Entscheidungskreis: | Wer hat die Entscheidung getroffen? Linus Nagel |
|   |  |
| Alternativen | Was sind die Alternativen? 1\. Java (Klassischer Standard für Android)   2\. C++ (Über NDK, performant aber komplex)  Abgrenzung: Alternativen wurden basierend auf der Kompatibilität mit Android Studio und der Verbreitung in der Industrie bewertet. C++ wurde aufgrund der hohen Komplexität und fehlenden Notwendigkeit für Low-Level-Performance ausgeschlossen. |
| Entscheidungs- nkriterien | Was sind die Entscheidungskriterien? \- Modernität & Zukunftsfähigkeit   \- Null-Safety (Vermeidung von Abstürzen durch NullPointerExceptions)   \- Code-Effizienz (Boilerplate-Reduzierung)   \- Lernkurve (da Vorkenntnisse gering sind)  |
| Bewertung der Alternativen | Was spricht für oder gegen die einzelne Alternative bzgl. der einzelnen Kriterien? \<Argumente für/gegen die einzelnen Alternativen\> Java:   (+) Große Community, viele Tutorials.   (+) Vorerfahrung durch Uni  (-) Verbose (viel Schreibarbeit), keine integrierte Null-Safety, veraltet im Vergleich zu modernen Standards.   Kotlin:   (++) Eingebaute Null-Safety erhöht die Stabilität der App (wichtig für Qualitätsanforderung Kategorie B/C).   (++) Weniger Code für gleiche Funktionalität im Vergleich zu Java.  |
| Entscheidung | Welche Entscheidung wurde getroffen? Die Entwicklung erfolgt in Kotlin. Objektiv, subjektiv   . ... und transparent/nachvollziehbar |
| Begründung | Kotlin ist der moderne Industriestandard für Android-Entwicklung. Das Feature der Null-Safety trägt maßgeblich dazu bei, die App robuster zu machen, was den Zielen der "Zuverlässigkeit" aus der Projektdefinition entspricht. Zudem erleichtert die prägnante Syntax das Test Driven Development (TDD), da Tests lesbarer geschrieben werden können. |
| Aspekte zur Umsetzung | Sicherstellen, dass alle Teammitglieder die Kotlin-Grundlagen beherrschen (ggf. kurzes Tutorial/Schulung). Einrichten der Code-Style-Guidelines für Kotlin in IntelliJ/Android Studio. |

Entscheidung 3:

| Benennung der Entscheidung | Was soll entschieden werden?  Auswahl der Hosting-Plattform und Technologie für das zentrale Backend (API & Datenbank). Backend-Infrastruktur: Microsoft Azure (PaaS) |
| :---- | :---- |
| Beschreibung | Beispiel: Das Informationsmodell fordert eine zentrale Speicherung ("Wahrheit der Daten auf dem Server") für Benutzer, Klassen und Fortschritte. Es muss entschieden werden, auf welcher Infrastruktur dieser Dienst bereitgestellt wird. |
| Stellenwert  | Welchen Stellenwert hat die Entscheidung? Mittel bis Hoch. Die Wahl der Infrastruktur beeinflusst die Implementierungsgeschwindigkeit, Sicherheit und Skalierbarkeit. |
| Datum: | Wann wurde die Entscheidung getroffen? 23.01.2026 |
| Entscheider, Entscheidungskreis: | Wer hat die Entscheidung getroffen? Linus Nagel |
|   |  |
| Alternativen | Was sind die Alternativen? 1\. Google Firebase (BaaS \- Backend as a Service)  2\. Self-Hosting / Cloudflare (Eigener vServer/Raspberry Pi \+ Cloudflare Tunnel)  3\. Microsoft Azure (PaaS \- Platform as a Service) 4\. Cloudflare Workers Abgrenzung: Google Firebase ist zwar populär für Android, erfordert aber das Erlernen einer NoSQL-Struktur und proprietärer SDKs.  Self-Hosting bietet maximale Kontrolle, birgt aber hohe Risiken bzgl. Wartung (OS-Updates, Security Patches) und Ausfallsicherheit, was im Projektrahmen ("Zeitmangel") kritisch ist. |
| Entscheidungs- nkriterien | Was sind die Entscheidungskriterien? \- Vorhandene Expertise im Team (Minimierung Einarbeitungszeit)   \- Sicherheit & Compliance (Datenschutz für Schulen)   \- Wartungsaufwand (Managed vs. Self-Managed)   \- Integration (API-Fähigkeiten) |
| Bewertung der Alternativen | Was spricht für oder gegen die einzelne Alternative bzgl. der einzelnen Kriterien? \<Argumente für/gegen die einzelnen Alternativen\> Google Firebase:   (+) Einfach für Einsteiger ohne Backend-Wissen.   (-) Vendor-Lock-in, fehlende Erfahrung im Team mit NoSQL-Datenmodellierung.   Self-Hosting:   (+) Günstig.   (-) Hoher Administrationsaufwand (Security Hardening), Single Point of Failure.   Microsoft Azure:   (++) Hohe Expertise vorhanden: Teammitglied besitzt Azure-Zertifizierungen und Enterprise-Erfahrung.   (++) PaaS-Ansatz: Azure App Service und Azure SQL nehmen die Server-Verwaltung ab, bieten aber volle Flexibilität bei der Code-Gestaltung (z.B. REST API).   (++) Sicherheit: Enterprise-Grade Security Features (Entra ID / Active Directory Integration möglich).  |
| Entscheidung | Welche Entscheidung wurde getroffen? Das Backend wird auf Microsoft Azure gehostet (App Service \+ Azure SQL). |
| Begründung | Obwohl Firebase oft für Mobile-Apps genutzt wird, wiegt die bestehende Erfahrung mit Azure schwerer. Da bereits Zertifizierungen und Praxiswissen vorliegen, eliminiert diese Wahl das Risiko der Einarbeitung in neue Plattformen.   Die Architektur nutzt Azure App Service für das Hosting der Business-Logik (als Web Service) und Azure SQL Database für die strukturierte Datenhaltung. Dies garantiert eine professionelle, wartbare Umgebung ohne den Administrationsaufwand von Self-Hosting-Lösungen. |
| Aspekte zur Umsetzung | Aufsetzen einer Ressourcengruppe in Azure. Bereitstellung einer Azure SQL Instanz für die relationalen Daten (Schüler/Lehrer Relationen). Deployment der API (z.B. Spring Boot oder Ktor) auf einem Azure App Service Plan. |

# **Projektentscheidungen: SafetySpot**

**Kunde:** Schulen

**Stand:** 23.01.2026

Im Rahmen des Projekts "SafetySpot" wurden mehrere fundamentale technische Entscheidungen getroffen, um die langfristige Entwicklungsrichtung, die Wartbarkeit und die Infrastruktur der Anwendung festzulegen. Ziel dieser Festlegungen ist es, technische Schulden frühzeitig zu vermeiden und eine solide Basis für den gesamten Software-Lebenszyklus zu schaffen. Nachfolgend sind die drei zentralen Entscheidungen bezüglich der Entwicklungsumgebung, der Programmiersprache und der Backend-Infrastruktur detailliert aufgeführt.

## **1\. Entwicklungsumgebung: Android Studio**

Am 22.01.2026 hat das gesamte Team, bestehend aus Noel Neuhoff, Milian Hinz und Linus Nagel, einstimmig beschlossen, **Android Studio** als einheitliche Entwicklungsumgebung (IDE) für das gesamte Projekt zu nutzen.

Diese Entscheidung wurde getroffen, um Produktivität, Handhabung und Flexibilität nicht nur kurzfristig zu sichern, sondern über die gesamte Projektlaufzeit zu maximieren. Android Studio bietet als offizielle IDE von Google die tiefgreifendste Integration in das Android-Ökosystem. Dazu gehören spezialisierte Werkzeuge wie der visuelle Layout-Editor, der APK-Analyzer und leistungsstarke Profiling-Tools (CPU, Speicher, Netzwerk), die in anderen Umgebungen nur schwer oder gar nicht verfügbar sind. Zudem ist die native Unterstützung der populären Sprachen Java und Kotlin sowie des Build-Systems Gradle bereits voll integriert, was die Konfiguration drastisch vereinfacht.

Im Vorfeld wurden durch umfassende Online-Recherche diverse Alternativen identifiziert und kritisch bewertet:

* **IntelliJ IDEA:** Diese IDE bildet zwar das technische Fundament von Android Studio und bietet hervorragende Features wie statische Code-Analyse und intelligentes Syntax-Highlighting. Jedoch fehlen in der Standard-Variante spezifische Android-Optimierungen. Zudem ist die vollumfängliche Ultimate-Version kostenpflichtig, was trotz vorhandener Education License unnötige Abhängigkeiten schaffen könnte.  
* **Android SDK:** Das SDK selbst ist unverzichtbar als API-Bibliothek und Werkzeugkasten (Debugger, Emulator), bietet jedoch keine grafische Benutzeroberfläche für die Code-Entwicklung. Es ist somit ein Baustein der Entwicklung, bietet aber nicht den Komfort und die Effizienz einer vollwertigen IDE.  
* **Qt Creator:** Diese Umgebung ist stark für GUI-Applikationen und plattformübergreifende C++ Entwicklung. Für eine reine Android-App weist sie jedoch teilweise Probleme mit mobilen Versionen auf, erfordert eine steile Lernkurve bei proprietären Frameworks und ist in der kommerziellen Lizenz sehr kostenintensiv.  
* **Apache Cordova:** Ermöglicht die Entwicklung mit Webtechnologien (HTML/CSS/JS). Dies wurde jedoch verworfen, da Hybrid-Apps oft Nachteile bei der Performance und dem Zugriff auf native Gerätefunktionen haben. Zudem führen Cross-Browser-Kompatibilitätsprobleme häufig zu einem schlechteren Nutzererlebnis (UX) im Vergleich zu nativen Apps.

Die Wahl fiel letztlich auf Android Studio, da es speziell auf die Anforderungen der Android-App-Entwicklung zugeschnitten ist und alle notwendigen Werkzeuge "out of the box" liefert, was Rüstzeiten minimiert.

## **2\. Primäre Programmiersprache: Kotlin**

Für die Umsetzung der Business-Logik und der Benutzeroberfläche der App wurde am 23.01.2026 durch Linus Nagel die strategische Entscheidung getroffen, **Kotlin** als primäre Programmiersprache einzusetzen.

Diese Entscheidung hat einen sehr hohen Stellenwert, da sie die Wartbarkeit, Fehleranfälligkeit und die Lesbarkeit der gesamten Code-Basis des Projekts maßgeblich beeinflusst. Kotlin wurde als moderner, von Google offiziell empfohlener Industriestandard für die Android-Entwicklung gewählt. Die Hauptargumente waren:

* **Null-Safety (Sicherheit):** Kotlin integriert das Konzept der Null-Sicherheit direkt in das Typsystem. Dies verhindert die berüchtigten Abstürze durch `NullPointerExceptions` bereits zur Kompilierzeit und erhöht die Stabilität der App signifikant. Dies ist besonders wichtig, um die hohen Qualitätsanforderungen im Schulumfeld (Kategorie B/C) zuverlässig zu erfüllen.  
* **Effizienz und Lesbarkeit:** Kotlin benötigt im Vergleich zu Java deutlich weniger Boilerplate-Code (Redundanz) für die gleiche Funktionalität. Dies führt zu kompakteren Dateien, die leichter zu verstehen und zu warten sind, was gerade in kleineren Teams die Entwicklungsgeschwindigkeit erhöht.  
* **Modernität und Interoperabilität:** Die prägnante Syntax erleichtert moderne Entwicklungsmethoden wie Test Driven Development (TDD). Zudem ist Kotlin zu 100% interoperabel mit Java, was die Nutzung existierender Bibliotheken problemlos ermöglicht.

Als Alternativen wurden **Java** und **C++** betrachtet. Java wurde aufgrund seiner Verbose-Natur (viel Schreibarbeit) und fehlender integrierter Null-Safety als veraltet für eine Neuentwicklung angesehen. C++ wurde zwar als sehr performant bewertet, aber aufgrund der hohen Komplexität und der manuellen Speicherverwaltung für den Anwendungszweck (Business-App ohne High-Performance-Grafik) als unnötiges Risiko eingestuft.

## **3\. Backend-Infrastruktur: Microsoft Azure (PaaS)**

Ebenfalls am 23.01.2026 entschied Linus Nagel über die Architektur des zentralen Backends (API & Datenbank). Es wurde festgelegt, dass das Backend auf der Cloud-Plattform **Microsoft Azure** gehostet wird, unter Nutzung der Dienste *Azure App Service* und *Azure SQL*.

Das Informationsmodell von "SafetySpot" verlangt eine zentrale, konsistente Speicherung ("Single Source of Truth") für Benutzerdaten, Klassenstrukturen und Lernfortschritte. Die Wahl der Infrastruktur ist kritisch für die Datensicherheit, die Skalierbarkeit bei steigenden Nutzerzahlen und die Geschwindigkeit der Implementierung.

Obwohl **Google Firebase** oft für mobile Apps genutzt wird und einen schnellen Einstieg ermöglicht, fiel die Wahl dagegen aus. Firebase nutzt primär NoSQL-Datenbanken, die sich weniger gut für die stark relationalen Daten eines Schulsystems (Lehrer-Schüler-Klassen-Beziehungen) eignen. Zudem sollte ein Vendor-Lock-in vermieden werden. Auch **Self-Hosting** (z.B. Raspberry Pi oder eigener vServer) wurde abgelehnt: Der hohe Administrationsaufwand für Betriebssystem-Updates, Security Patches und Backups sowie das Risiko eines "Single Point of Failure" stehen im Widerspruch zum straffen Zeitplan des Projekts.

Für Microsoft Azure sprachen vor allem folgende Punkte:

* **Expertise und Risikominimierung:** Es liegt bereits hohe, zertifizierte Expertise und Enterprise-Erfahrung im Team vor. Dies eliminiert die Einarbeitungszeit nahezu vollständig und reduziert das Risiko von Fehlkonfigurationen drastisch.  
* **PaaS-Ansatz (Platform as a Service):** Durch die Nutzung von Azure App Service und Azure SQL wird die komplette Server-Verwaltung an Microsoft ausgelagert. Das Team kann sich voll auf die Entwicklung der Business-Logik (z.B. via REST API) konzentrieren, während Updates und Skalierung automatisch erfolgen.  
* **Sicherheit und Compliance:** Die Plattform bietet Enterprise-Grade Security Features, wie z.B. die mögliche Integration von Entra ID (ehemals Active Directory), was im Kontext von schützenswerten Schuldaten einen erheblichen Vorteil darstellt.

Die Umsetzung sieht das Aufsetzen einer dedizierten Ressourcengruppe, die Bereitstellung einer SQL-Instanz für die relationalen Daten und das Deployment der API (z.B. basierend auf Spring Boot oder Ktor) auf einem skalierbaren App Service Plan vor.

