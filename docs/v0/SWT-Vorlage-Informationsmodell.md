`

||Stand|
|-|-|
|Informationsmodell/ (Datenmodell und<br />Datenzugriff)|23.01.2026|

|Kunde||Projektbezeichnug|
|-|-|-|
|<Name der Kunden>|-|SafetySpot|

|Autoren|||
|-|-|-|
|Milian Hinz, Noel Neuhoff, linus Nagel|-||

Der hier gewählte Begriff des 'Informationsmodelles' beschreibt das Modell der Datenhaltung und des Datenzugriffs.  
Das Dokument gibt Antwort auf die Fragen:

* Welche Informationen müssen persistent gespeichert werden?
* Welche Zugriffe auf die Informationen müssen ermöglicht werden?
* Welche Informationen werden auf welchem Rechner abgelegt/benutzt?

Das Informationsmodell beschreibt, **welche Daten SafetySpot überhaupt braucht**, **wo** diese Daten liegen (Server vs. App) und **wie** darauf zugegriffen wird (z. B. Schüler spielt → Fortschritt wird gespeichert).

### **Warum es ein Backend braucht**

SafetySpot ist nicht nur eine Offline-Lernapp, sondern soll:

* Inhalte (Szenarien) **zentral verwalten** und an viele Schüler verteilen,
* **Fortschritt, Punkte und Leaderboards** zuverlässig speichern,
* Lehrkräften einen Admin-Bereich geben (Klassen, Nutzer, Statistiken, eigene Szenarien).

Deshalb liegt die „Wahrheit“ der Daten **auf dem Backend-Server**. Die App lädt Inhalte herunter und sendet Ergebnisse wieder zurück.

### **Was gespeichert werden muss (persistent)**

Damit alles funktioniert, werden Daten in vier großen Blöcken persistent benötigt:

1. **Organisation \& Rollen**
* Schulen, Klassen und Benutzer (Schüler/Lehrer/Admin)
* Login-Daten (als Hash, nie Klartext)
* Zuordnung: welcher Schüler gehört zu welcher Klasse/Schule
2. **Lerninhalte**
* Kategorien (Chemie, Sport, Technik …)
* Szenarien (Titel, Beschreibung, Schwierigkeit, Status/Version)
* Aufgaben/Items innerhalb eines Szenarios (Fragen, Antwortoptionen, richtige Lösung, Feedback, Punkte)
3. **Fortschritt \& Gamification**
* pro Schüler: was wurde gestartet/abgeschlossen, Bestscore, Anzahl Versuche
* Punkte/Level/Streak
* Leaderboards (meist berechnet aus Punkten; optional gespeichert, wenn man Performance braucht)
4. **Admin/Lizenz**
* Lizenzstatus der Schule, evtl. Freischaltcodes
* einfache Logs/Audit für Lehreraktionen (Passwort-Reset, Szenario veröffentlicht …)

### **Welche Zugriffe nötig sind (Datenzugriff)**

* **Schüler**: login, Inhalte laden, spielen, Antworten senden, Punkte/Ranking ansehen, eigenen Fortschritt ansehen
* **Lehrer**: Klassen/Schüler verwalten (inkl. Passwort reset), Szenarien erstellen und veröffentlichen, Statistiken einsehen
* **Schule/Admin** (kann auch Lehrrolle sein): Lizenz/Organisation verwalten

### **Wo liegt was? (Server vs App)**

* **Backend (Server)** speichert dauerhaft: Benutzer, Klassen, Szenarien, Fortschritt, Punkte, (optional) Versuche/Statistiken, Lizenzdaten.
* **Android-App** speichert nur das, was sie für Nutzung braucht:

  * Login-Token sicher,
  * Cache für Szenarien (damit es schnell lädt / optional offline),
  * „pending attempts“ falls offline gespielt wird (später synchronisieren).

Hinweise zum Dokument

||Zielgruppe:|Das Informationsmodell richtet sich an: den Datenbankentwickler an den Anbieter der Schnittstelle zur Datenhaltung. an die Nutzer der Schnittstelle zur Datenhaltung Welche Daten müssen gespeichert werden?  Welche Zugriffe sind zu ermöglichen.|
|-|-|-|
||Zweck|Nach den Vorgaben aus diesem Dokument kann die Datenbank aufgebaut werden und können die Zugriffsfunktionen implementiert werden. Allerdings werden in diesem Dokument noch nicht alle Details festgelegt (mehr das 'was' als das 'wie'). Weitere Entwurfsentscheidungen haben zu folgen. (Fehlermodel, Integritätsbedingungen, physikalischer Entwurf)|
||Zeitpunkt von Beginn und Fertigstellung|Das Dokument entwickelt sich mit der Zeit. Beginnen kann man mit einem „Initialen Informationsmodell. Später reift das Informationsmodell zu einem Architekturdokument.|
||Form|UML-Klassendiagramm,  Beschreibung der relevanten Klassen und Attribute|



