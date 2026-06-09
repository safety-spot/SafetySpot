# **Projektdefinition – SafetySpot**

**Stand:** 12.01.2026  
**Projekt:** SafetySpot  
**Auftraggeber:** Bildungsministerium NRW  
**Autoren:** Milian, Noel, Linus

---

## **Zweck des Dokuments** 

Diese Projektdefinition beschreibt die Ausgangssituation, Ziele und Rahmenbedingungen des Projekts SafetySpot. Sie dient als gemeinsame Grundlage zwischen Auftraggeber (Bildungsministerium NRW) und Projektteam, bevor mit der eigentlichen Umsetzung begonnen wird.

## **1 Einleitung**

Die App *SafetySpot* ist eine App, in der Schüler spielerisch und sicher den Umgang mit Gefahren lernen können. Die Auftraggeber sind Schulen, also Lehrpersonen und Schulleitungen, die ihren Schülern mehr Sicherheitsbewusstsein vermitteln möchten. Ansprechpartner wären in der Praxis vor allem Lehrkräfte und die Schulleitung.

Die Idee zum Projekt entstand aus eigenen Erfahrungen: Als Schüler hatten wir kaum Aufklärung über Risiken im Schulalltag, zum Beispiel im Chemielabor oder im Werkunterricht. Oft wurden Gefahren nur kurz erklärt und viele hörten nicht richtig zu.  
Die Motivation für das Projekt ist daher, das Bewusstsein für Gefahren zu stärken und langfristig Unfälle und Verletzungen in der Schule zu reduzieren.

## **1.1 Beschreibung der Ausgangssituation**

Aktuell findet Gefahrenaufklärung in Schulen meist nur durch kurze mündliche Erklärungen der Lehrpersonen statt. Diese sind oft oberflächlich und wenig interaktiv, weshalb viele Schüler das Thema nicht ernst nehmen oder schnell vergessen.

Die bisherige Herangehensweise ist also größtenteils manuell und nicht besonders nachhaltig. Digitale Lernangebote oder spielerische Methoden werden kaum genutzt.

Bisher wurden für dieses Projekt noch keine Vorleistungen erbracht, es handelt sich um eine neue Idee.

## **1.2 Beschreibung der Projektziele**

Ziel des Projektes ist die **Entwicklung einer mobilen Anwendung**, mit der Schüler auf einfache und spielerische Weise über Gefahren im Schulalltag lernen können.

Weitere Ziele sind:

* Ziel des Projektes ist die **Vermittlung von Wissen über Gefahrensituationen** in Schulen (z. B. Chemiesaal, Werkraum, Sportunterricht).

* Ziel des Projektes ist die **Sammlung von Erfahrungen im Umgang mit realistischen Szenarien**, ohne dass echte Risiken entstehen.

* Ziel des Projektes ist die **Motivation der Schüler durch Gamification** (z. B. Leaderboards und Punkte).

* Ziel des Projektes ist ein **Mehrwert für Schulen**, da diese Lizenzen an Eltern verkaufen können und dadurch die App für Schüler freigeschaltet wird.

Unterschiedliche Stakeholder haben unterschiedliche Ziele:

* Schüler wollen eine spannende und spielerische App.

* Lehrer wollen ein einfaches Tool zur Wissensvermittlung.

* Schulen möchten mehr Sicherheit und weniger Unfälle.

## **1.3 Rahmenbedingungen**

Für das Projekt gelten folgende Rahmenbedingungen:

* Sehr begrenzte Zeit: ca. 3 Wochen

* Extrem geringes Budget: praktisch kein Geld (ca. 500 €)

* Projektarbeit durch drei Studierende

* Es dürfen möglichst keine laufenden Kosten entstehen

---

## **2 Projektgegenstand (Beschreibung des Softwaresystems)**

SafetySpot ist eine mobile Lern-App, die speziell für Schulen entwickelt wird. Schüler sollen mithilfe der App verschiedene Gefahrensituationen kennenlernen und in einer sicheren Umgebung üben, wie man richtig reagiert. Die App ist spielerisch aufgebaut, damit Lernen mehr Spaß macht und die Motivation steigt.

Die App ist mit einem Backend-Server verbunden, über den neue Situationen heruntergeladen werden können. Gefahren sind in verschiedene Kategorien eingeteilt, zum Beispiel Chemie, Sport oder Technik. Für Lehrkräfte gibt es einen speziellen Modus, in dem sie eigene Szenarien erstellen können. Zusätzlich gibt es Spielmodi für ganze Klassen sowie Leaderboards, die den Wettbewerb fördern.

## **2.1 Beschreibung der Systemumgebung**

### **2.1.1 Organisatorische Umgebung**

Die wichtigsten Akteure sind Schüler, Lehrer und Schulen.  
 Schüler nutzen die App, um Szenarien zu lösen und Punkte zu sammeln.  
 Lehrer nutzen die App zusätzlich als Adminbereich, um Statistiken einzusehen und eigene Inhalte zu erstellen.  
 Schulen organisieren die Lizenzen und verwalten Klassen und Benutzer.

Ein typischer Ablauf könnte so aussehen:  
 Die Schule kauft eine Lizenz → Lehrer erstellen Klassen → Schüler registrieren sich mit Name und Passwort → Schüler bearbeiten Aufgaben → Lehrer sehen Fortschritte im Adminbereich.

### **2.1.2 Technische Umgebung**

Die App läuft auf mobilen Geräten und benötigt eine Internetverbindung zur Kommunikation mit dem Backend-Server.  
 Der Server verwaltet Benutzerkonten, Fortschritte, Leaderboards und neue Inhalte.  
 Weitere externe Systeme (z. B. SAP) sind nicht vorgesehen.

## **2.2 Beschreibung der vorgesehenen Systemstruktur** 

SafetySpot besteht aus zwei Hauptkomponenten:

* **Mobile App (Frontend):**  
   Wird von Schülern und Lehrern genutzt. Schüler sehen Aufgaben und Spiele, Lehrer zusätzlich den Adminbereich.

* **Backend-Server:**  
   Verwaltet Benutzer, Statistiken, Leaderboards und Inhalte. Stellt neue Szenarien zum Download bereit.

Technisch geplant sind: —

* Frontend: Java, TypeScript, HTML/CSS

* Backend: Rust, C++ oder Go

* Zielplattform: Android (iOS wird ignoriert)

---

## **3 Anforderungen an das System**

### **3.1 Anforderungen an die Funktionalität (User Stories)**

* Als Schüler möchte ich Gefahrensituationen spielen, damit ich daraus lernen kann.

* Als Schüler möchte ich Punkte und Rankings sehen, damit ich motiviert bleibe.

* Als Lehrer möchte ich eigene Szenarien erstellen, damit ich den Unterricht anpassen kann.

* Als Lehrer möchte ich Statistiken sehen, damit ich den Lernfortschritt meiner Klasse beurteilen kann.

* Als Schule möchte ich Benutzer verwalten, damit Klassen organisiert bleiben.

## **3.2 Anforderungen an die Benutzerschnittstelle**

Die Benutzer sind Schüler und Lehrer an weiterführenden Schulen. Viele davon sind keine IT-Experten.  
 Die App wird ausschließlich über mobile Geräte verwendet.  
 Das Design soll simpel und übersichtlich sein, damit auch ältere Lehrkräfte gut damit zurechtkommen.

## **3.3 Anforderungen an Systemschnittstellen**

Da SafetySpot eine eigenständige Lösung ist, bestehen keine externen Systemschnittstellen zu anderen Programmen.

## **3.4 Anforderungen an die organisatorische Integration**

Lehrer müssen regelmäßig neue Inhalte erstellen oder bestehende anpassen.  
 Diese Inhalte müssen automatisch an die Schüler verteilt werden.  
 Deshalb braucht die App ein einfaches und intuitives Admin-Center.

Schüler sollen sich ohne E-Mail-Adresse registrieren können, idealerweise nur mit Name und Passwort.  
 Die Accounts sind einer Schule bzw. Klasse zugeordnet.  
 Lehrer oder Administratoren sollen Passwörter zurücksetzen und Benutzer verwalten können.

## **3.5 Produkt-Qualitätsanforderungen (Auswahl)**

**Benutzbarkeit – Kategorie A**  
 Da viele Lehrkräfte Probleme mit neuer Technik haben, muss die App besonders einfach und intuitiv sein. Wenn die Bedienung zu kompliziert ist, wird die App nicht genutzt.

**Sicherheit – Kategorie B**  
 Der Zugang muss durch Benutzername und Passwort geschützt sein.  
 Personenbezogene Daten sollen sicher übertragen werden.

**Zuverlässigkeit – Kategorie C**  
 Gelegentliche kleinere Fehler sind bei einem Prototyp akzeptabel, sollten aber selten auftreten.

**Wartbarkeit – Kategorie D**  
 Da es sich hauptsächlich um einen Prototyp handelt, spielt langfristige Wartbarkeit eine untergeordnete Rolle.

---

## **4 Angaben zur Projektdurchführung**

### **4.1 Vorgehensweise**

Die Entwicklung erfolgt grob agil. Geplante Schritte:

* Visualisierung der App mit Figma

* Planung der Architektur mit Diagrammen

* Umsetzung von Frontend und Backend

* Entwicklung nach dem Prinzip Test Driven Development

### **4.2 Entwicklungsumgebung und Werkzeuge**

* IDE: Android Studio (noch nicht sicher)

* Versionsverwaltung: GitHub

### **4.3 Anfänglich identifizierte Risiken**

* Geringe Vorkenntnisse im Umgang mit den geplanten Programmiersprachen

* Zeitmangel durch kurze Projektlaufzeit

### **4.4 Offene Punkte**

* Die konkrete Architektur des Backends ist noch nicht final geklärt.

