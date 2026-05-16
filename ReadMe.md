# Projectwürfe

## 1. Digital Kunstler Portal

**Hauptaufgabe:** Bei dieser Plattform handelt es sich um eine Abonnementplattform, auf der Künstler ihre physischen Merchandise Pakete (Postkarten, Aufkleber usw.) verwalten und Abonnenten sich für eine begrenzte Anzahl von Veranstaltungen anmelden können.

**Zielgruppe:** Die Zielgruppe besteht aus digitalen Künstlern, die physische Produkte versenden, und Kunstliebhabern, die diese abonnieren möchten.

**Motivation:** Mein Ziel ist es, Künstlern über eine zentrale digitale Plattform ein regelmäßiges Einkommensmodell und eine begrenzte Mittelzuweisung zu ermöglichen.

## Geplänte Funktionalitäten

- Must Have:
    - Dauerhafte Speicherung von Künstlerprofilen, Produkttypen (Postkarten, Aufkleber usw.) und Abonnenteninformationen.
    - Abonnements bearbeiten und Produkte auflisten.
    - Es gibt zwei verschiedene Benutzertypen: Künstler (Hinzufügen von Produkten) und Abonnent (Auswählen von Paketen).
    - Die Anzahl der Abonnenten für Limited Edition-Produkte wird erfasst und die Anmeldungen werden geschlossen, sobald das Kontingent erreicht ist.
  
- Should Have:
    - Nebenläufigkeit: Verwaltung von gleichzeitig eingehenden Abonnementanfragen für eine begrenzte Anzahl verfügbarer Slots mithilfe von Threads, um Konflikte zu vermeiden.
    - GUI: Eine Benutzeroberfläche, auf der Künstler ihre Produktbilder/Produkttypen verwalten können und Abonnenten ihre Pakete auswählen können.
  
- Nice to Have:
    - Benachrichtigungen, die Abonnenten über die Veröffentlichung neuer Monatspakete informieren.
    - Eine grafische Übersicht, die das monatliche Abonnentenwachstum für Künstler zeigt.

## Modul und Klassenübersicht
| Klasse              | Aufgabe                                                                                                                                                                                           |
|:--------------------|:--------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------|
| **Artist**          | Sie erbt von der Klasse „User“. Sie repräsentiert Künstlerprofildaten und die vom Künstler angebotenen Produkte/Abonnementpakete.                                                                 |
| **Subscriber**      | Sie erbt von der Klasse „User“. Sie enthält die für die Zustellung physischer Produkte notwendigen Versandadressinformationen und Abonnentendaten.                                                |
| **ArtProduct**      | Es handelt sich um eine abstrakte Oberklasse (Basisklasse), die die gemeinsamen Attribute (ID, Titel, Preis) aller Produkte enthält.                                                              |
| **Subscription**    | Dies ist die Geschäftslogikschicht, die die Beziehung zwischen Benutzer und Art Produkt herstellt und das Kontingentsystem verwaltet.                                                             |
| **User**            | Es handelt sich um die Basisklasse für alle Benutzer im System. Sie enthält grundlegende Mitgliedsinformationen wie Name, E-Mail-Adresse und Passwort.                                            |
| **PhysicalProduct** | Sie erbt von der Klasse ArtProduct. Sie repräsentiert physische Verpackungen wie Postkarten, Aufkleber und Leinwände mit Attributen wie Gewicht, Größe, Stil, Produkttyp (Enum) und Lagerbestand. |
| **DigitalProduct**  | Es erbt von der Klasse ArtProduct. Es repräsentiert digitale Kunstwerke mit Attributen wie Download-Link, Dateigröße, Format, Auflösung und Lizenztyp.                                            |
| **DatabaseManager** | Es handelt sich um die Komponente, die für die dauerhafte Speicherung von Daten mittels SQL-Abfragen verantwortlich ist.                                                                          |
| **NetworkManager**  | Koordiniert Kundenanforderungen und sorgt für konfliktfreie Subscription.                                                                                                                         |


## 2. EventConnect

**Hauptaufgabe:** Es handelt sich um eine Plattform, auf der Nutzer Sport- oder gesellschaftliche Veranstaltungen erstellen und an diesen Veranstaltungen innerhalb einer begrenzten Anzahl von Orten teilnehmen können.

**Zielgruppe:** Es bietet einen Treffpunkt für aktive Nutzer, die in der Nähe voneinander wohnen und mit Menschen in Kontakt treten möchten, die gemeinsame Interessen teilen.

**Motivation:** Mein Ziel ist es, sicherzustellen, dass die begrenzte Kapazität populärer Veranstaltungen so bewältigt wird, dass die Datenkonsistenz unter dem Druck hoher gleichzeitiger Nachfrage nicht beeinträchtigt wird, und dabei die Prinzipien der Nebenläufigkeit anzuwenden.

## Geplänte Funktionalitäten

- Must Have:
    - Veranstaltungsdetails (Ort, Zeit, Kapazität) und Teilnehmerlisten werden dauerhaft gespeichert.
    - Die Prozesse zur gemeinsamen Nutzung von Veranstaltungen und zur Teilnahme daran werden über das Netzwerk abgewickelt.
    - Überwachung der maximal zulässigen Teilnehmerzahl bei Veranstaltungen und Schließung der Anmeldungen, sobald diese Zahl erreicht ist.

- Should Have:
    - Nebenläufigkeit: Gleichzeitige Teilnahmeanfragen konfliktfrei verwalten.
    - GUI: Eine Benutzeroberfläche, auf der Veranstaltungen über eine Stadtkarte oder eine Listenansicht angezeigt werden können, sowie eine Schaltfläche zur Teilnahme sind enthalten.

- Nice to Have:
    - Veranstaltungen nach Art (Sport, Unterhaltung usw.) oder nach Stadt/Region auflisten.

## Modul und Klassenübersicht
| Klasse                | Aufgabe                                                                                                                                         |
|:----------------------|:------------------------------------------------------------------------------------------------------------------------------------------------|
| **Event**             | Dieses Modell erfasst den Titel der Veranstaltung, den Ort, die Uhrzeit und die maximale/verfügbare Teilnehmerzahl.                             |
| **User**              | Dieser Kurs enthält die Profilinformationen der Teilnehmer und eine Liste der Veranstaltungen, für die sie angemeldet sind.                     |
| **AttendanceManager** | Diese Schicht verarbeitet Teilnahmeanfragen, steuert die Kapazität und verhindert Race Conditions aufgrund von Gleichzeitigkeit.                |
| **DatabaseManager**   | Sie sind für die Erfassung und Aktualisierung von Ereignis und Benutzerdaten in der Datenbank verantwortlich.                                   |
| **NetworkManager**    | Es verwaltet Datenpakete, indem es die Kommunikation zwischen Server und Client herstellt.                                                                |


          