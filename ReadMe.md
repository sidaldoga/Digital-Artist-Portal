# Projectwürfe

## 1. Digital Kunstler Portal

**Hauptaufgabe:** Bei dieser Plattform handelt es sich um eine Abonnementplattform, auf der Künstler ihre physischen Merchandise Pakete (Postkarten, Aufkleber usw.) verwalten und Abonnenten sich für eine begrenzte Anzahl von Veranstaltungen anmelden können.

**Zielgruppe:** Die Zielgruppe besteht aus digitalen Künstlern, die physische Produkte versenden, und Kunstliebhabern, die diese abonnieren möchten.

**Motivation:** Mein Ziel ist es, Künstlern über eine zentrale digitale Plattform ein regelmäßiges Einkommensmodell und eine begrenzte Mittelzuweisung zu ermöglichen.

## Geplante Funktionalitäten

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

## Netzwerkarchitektur

Die Anwendung basiert auf einem **Client Server Modell**. Ein zentraler
`Server` lauscht auf TCP Port `8080` und startet pro eingehender
Verbindung einen eigenen `ClientHandler` Thread, sodass mehrere Clients
gleichzeitig bedient werden.

**Zuverlässiger Transport.** Die Kommunikation erfolgt über
**TCP Sockets**, die eine geordnete und verlustfreie Übertragung
garantieren  eine Voraussetzung für konsistente Datenbankoperationen.

**Request/Response Muster.** Jeder Austausch nutzt ein typisiertes
Nachrichtenpaar: Der Client sendet ein `Request`Objekt (`Command`Enum
+ Nutzdaten), der Server antwortet mit einem `Response`Objekt
  (`Status`Enum + Nutzdaten). Das Protokoll ist dadurch explizit und
  selbstbeschreibend.

**DTO Schicht.** Es werden **keine** Modellobjekte (`User`, `Artist`,
`Purchase`,...) direkt über das Netzwerk serialisiert. Jedes wird über
symmetrische `toDTO`/`fromDTO`Methoden auf ein passendes **Data
Transfer Object** (`UserDTO`, `ArtistDTO`,...) abgebildet. Das entkoppelt
den Netzwerkvertrag vom internen Modell und verhindert das Durchsickern
sensibler Persistenzdetails (z. B. PasswortHashes).

**Deadlock Vermeidung.** Ein `ObjectInputStream` blockiert beim
Erzeugen, bis er den Header des gegenüberliegenden
`ObjectOutputStream` gelesen hat. Würden beide Seiten zuerst ihren
Eingabestrom öffnen, käme es zum Deadlock. Die Hilfsklasse
`ServerConnection` erzwingt die korrekte Reihenfolge   den
`ObjectOutputStream` zuerst öffnen und per `flush()` leeren, danach den
`ObjectInputStream`; der `ClientHandler` verfährt spiegelbildlich. Als
`AutoCloseable` schließt `ServerConnection` die Verbindung zudem sauber
mit einem `BYE`Befehl.

## Nebenläufigkeit & Synchronisation

Da jeder Client in einem eigenen Server Thread läuft, müssen
gleichzeitige Schreibzugriffe auf die gemeinsame SQLiteDatenbank
serialisiert werden. Dafür dient ein globaler `ReentrantLock`, der über
`DatabaseLock.WRITE_LOCK` bereitgestellt wird.

Der `ClientHandler` ordnet jeden Befehl über die Menge `WRITE_COMMANDS`
ein (alle `ADD_*`, `UPDATE_*`, `REMOVE_*`, `REGISTER`, `ADD_PURCHASE`
usw.). Vor einem Schreibbefehl wird der Lock angefordert und in einem
`finally`-Block wieder freigegeben. Jeder Schreibbefehl wird so als
unteilbarer **kritischer Abschnitt** ausgeführt:

      if (isWrite) DatabaseLock.WRITE_LOCK.lock();
      try {}
      finally { if (isWrite) DatabaseLock.WRITE_LOCK.unlock(); }

Dies garantiert **Atomarität** auch für mehrstufige
Lese Ändere Schreibe Abläufe und sichert damit die **Datenintegrität**
bei simultanem Zugriff. Lesebefehle werden nicht gesperrt und bleiben
dadurch performant. Ergänzend kapselt `DatabaseLock` die Methoden
`write(Runnable)` und `write(Supplier<T>)` zum Schutz einzelner Blöcke.

Die kritische gemeinsame Ressource ist ein einzelner Datenbankdatensatz
(z. B. der Lagerbestand `stock` eines physischen Produkts), auf den
mehrere Client Threads gleichzeitig schreibend zugreifen können. Ohne
Synchronisation entsteht eine Race Condition, da sich nebenläufige
Lese Ändere Schreibe Abläufe verschränken und so einen verlorenen
Schreibvorgang (Lost Update) bzw. einen Überverkauf erzeugen können. Der
`DatabaseLock` (ein globaler `ReentrantLock`) serialisiert alle
Schreibzugriffe zu atomaren kritischen Abschnitten, wodurch der
`RaceConditionTest` reproduzierbar zeigt, dass der finale Zustand des
Datensatzes stets konsistent bleibt.

## Race Condition Test

`RaceConditionTest` provoziert eine Schreib Schreib Wettlaufsituation:
Zwei Threads (`Client-A`, `Client-B`) öffnen je eine eigene Verbindung
und senden gleichzeitig 25 `UPDATE_USER` Anfragen auf **denselben**
Datensatz (Benutzer-ID 1).

- **Ohne** Lock verschränken sich die Aktualisierungen, und der Datensatz
  kann inkonsistente Feldwerte aus beiden Clients enthalten
  (Lost Update bzw. „torn write").
- **Mit** aktivem `WRITE_LOCK` läuft jede Aktualisierung als kritischer
  Abschnitt. Die Zugriffe werden serialisiert, sodass das abschließende
  `GET_USER` einen in sich konsistenten Datensatz liefert   nie eine
  Mischung beider Clients.

Dasselbe Prinzip schützt den Kaufvorgang: Da `ADD_PURCHASE` ein
Schreibbefehl ist, laufen Bestandsprüfung (`stock > 0`) und
Dekrementierung gemeinsam unter dem Lock. Zwei Käufer können denselben
physischen Artikel daher nicht gleichzeitig erwerben (kein Überverkauf).

`ConcurrencyTest` ergänzt dies mit 5 Clients, die parallel `PING` und
`LOGIN` ausführen, und bestätigt die fehlerfreie Verarbeitung
gleichzeitiger Lesezugriffe.

## Modul und Klassenübersicht
| Klasse                    | Aufgabe                                                                                                                                                                                           |
|:--------------------------|:--------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------|
| **Artist**                | Sie erbt von der Klasse „User“. Sie repräsentiert Künstlerprofildaten und die vom Künstler angebotenen Produkte/Abonnementpakete.                                                                 |
| **Subscriber**            | Sie erbt von der Klasse „User“. Sie enthält die für die Zustellung physischer Produkte notwendigen Versandadressinformationen und Abonnentendaten.                                                |
| **ArtProduct**            | Es handelt sich um eine abstrakte Oberklasse (Basisklasse), die die gemeinsamen Attribute (ID, Titel, Preis) aller Produkte enthält.                                                              |
| **Subscription**          | Dies ist die Geschäftslogikschicht, die die Beziehung zwischen einem Subscriber und einem Artist herstellt und das Duplikat Abonnement System verwaltet.                                          |
| **User**                  | Es handelt sich um die Basisklasse für alle Benutzer im System. Sie enthält grundlegende Mitgliedsinformationen wie Name, E-Mail-Adresse und Passwort.                                            |
| **Address**               | Repräsentiert eine Versandadresse eines Subscribers mit Straße, Stadt, Postleitzahl und Land.                                                                                                     |
| **Dimensions**            | Wertobjekt für die Abmessungen physischer Produkte (Breite, Höhe, Tiefe).                                                                                                                         |
| **PhysicalProduct**       | Sie erbt von der Klasse ArtProduct. Sie repräsentiert physische Verpackungen wie Postkarten, Aufkleber und Leinwände mit Attributen wie Gewicht, Größe, Stil, Produkttyp (Enum) und Lagerbestand. |
| **DigitalProduct**        | Es erbt von der Klasse ArtProduct. Es repräsentiert digitale Kunstwerke mit Attributen wie Download Link, Dateigröße, Dateiformat, Kunststil und digitalem Produkttyp.                            |
| **Purchase**              | Repräsentiert einen abgeschlossenen Kaufvorgang; verknüpft Käufer und Produkt und hält das Kaufdatum.                                                                                             |
| **UserDAO**               | Kapselt CRUD Operationen für Benutzer, inkl. Authentifizierungs Logik.                                                                                                                            |
| **ArtistDAO**             | Verwaltet die spezialisierten Daten und Details für Künstler.                                                                                                                                     |
| **SubscriberDAO**         | Verwaltet die spezifischen Daten für Subscriber und deren Profile.                                                                                                                                |
| **SubscriptionDAO**       | Realisiert relationale Abfragen für Abonnements und sorgt für Duplikatsvermeidung.                                                                                                                |
| **ArtProductDAO**         | Basis CRUD für alle Produkttypen und deren relationale Zuweisungen.                                                                                                                               |
| **PhysicalProductDAO**    | Verwaltet spezifische Details für physische Waren und Lagerbestände.                                                                                                                              |
| **PurchaseDAO**           | Persistiert Kauftransaktionen; sichert die referenzielle Integrität über Fremdschlüssel (Käufer, Produkt) und liefert den generierten Schlüssel zurück.                                           |
| **DigitalProductDAO**     | Verwaltet spezifische Daten für digitale Downloads und Links.                                                                                                                                     |
| **AddressDAO**            | Verwaltet die CRUD Operationen für die Adressdaten der Subscriber.                                                                                                                                |
| **DimensionsDAO**         | Kapselt die Datenbankzugriffe für physische Produktabmessungen.                                                                                                                                   |
| **DatabaseConnection**    | Verwaltet den Verbindungsaufbau zur lokalen SQLite Datenbankdatei.                                                                                                                                |
| **DatabaseLock**          | Zentraler `ReentrantLock` zur Synchronisation aller Schreibzugriffe und zur Vermeidung von Race Conditions.                                                                                       |
| **ArtStyle**              | Enum für Kunststile: `REALISM`, `MINIMALISM`, `SURREALISM`, `FANTASY`, `CYBERPUNK`, `WHIMSICAL`.                                                                                                  |
| **ProductType**           | Enum für physische Produkttypen: `CANVAS`, `PAPER`, `POSTCARD`, `STICKER`.                                                                                                                        |
| **MediumType**            | Enum für verwendete Materialien: `OIL_PAINTING`, `CHARCHOAL`, `WATERCOLOR`, `PASTEL`, `PENCIL`.                                                                                                   |
| **DigProductType**        | Enum für digitale Produkttypen: `POSTER`, `POSTCARD`, `STICKER`, `COMMISSIONED`, `WALLPAPER`.                                                                                                     |
| **PasswordHasher**        | Utility Klasse für SHA-256 PasswortHashing und Verifikation.                                                                                                                                      |
| **ConcurrencyTest**       | Prüft die Stabilität bei gleichzeitigem Zugriff mehrerer Clients (paralleles `PING` und `LOGIN`).                                                                                                 |
| **RaceConditionTest**     | Simuliert parallele Änderungszugriffe auf denselben Datensatz, um die Wirksamkeit der Synchronisation zu validieren.                                                                              |
| **ServerIntegrationTest** | Testet den vollständigen Request Response Zyklus der zentralen Geschäftslogik (Registrierung, Login, Produkte, Kauf, Abonnement).                                                                 |
| **Client**                | Beispielhafter Einstiegspunkt; baut eine Verbindung auf, sendet eine feste Befehlssequenz und gibt die Antworten aus.                                                                             |
| **ClientHandler**         | Verarbeitet die Anfragen einer Verbindung in einem eigenen Thread, sperrt Schreibbefehle über den `WRITE_LOCK` und delegiert an die DAOs                                                          |
| **Command**               | Enum zur Definition aller unterstützten Netzwerkbefehle und Aktionen.                                                                                                                             |
| **Response/Request**      | Kapseln die Netzwerk Datenpakete (`Command`+Nutzdaten bzw. `Status`+Nutzdaten) für eine standardisierte Client Server Kommunikation.                                                              |
| **Server**                | Hauptkomponente; akzeptiert eingehende Socket Verbindungen und instanziiert pro Client einen `ClientHandler`.                                                                                     |
| **ServerConnection**      | Hilfsklasse zur Kapselung der Socket Verbindung und der Objekt Serialisierung (Streams); vermeidet Deadlocks durch korrekte Stream Reihenfolge.                                                   |
| **Status**                | Enum zur Übermittlung des Verarbeitungsergebnisses (`OK`, `ERROR`, `INVALID_ARGUMENT`, `INVALID_CREDENTIALS`, `NOT_FOUND`, `INTERNAL_ERROR`).                                                     |
| **DTO-Klassen**           | Datenübertragungsobjekte (DTOs) für den serialisierten Datenaustausch zwischen Client und Server.                                                                                                 |
| **Main**                  | Einstiegspunkt der Anwendung; initialisiert die DAOs und befüllt die Datenbank mit Beispieldaten (Seed).                                                                                          |

## Datenstruktur

Die Anwendung verwendet eine lokale SQLite Datenbank, um Projektdaten dauerhaft zu speichern. Tabellenstrukturen, Schlüssel und relationale Links sind wie folgt strukturiert:

### 1. Existierende Tabellen und gespeicherte Informationen

* **`users`** (Zentrale Tabelle für alle Benutzer)
  * `id`: Primary Key (INTEGER, AUTOINCREMENT)
  * `fname`: Vorname (TEXT, NOT NULL)
  * `lname`: Nachname (TEXT, NOT NULL)
  * `username`: Eindeutiger Benutzername für Authentifizierung (TEXT, UNIQUE, NOT NULL)
  * `password_hash`: Passwort (TEXT, NOT NULL)
  * `email`: E-Mail Adresse (TEXT, NOT NULL)
  * `role`: Benutzertyp wie 'ARTIST' oder 'SUBSCRIBER' (TEXT)
  * `address`: Abonnenten Adresse (TEXT)
  * `subscriber_point`: Punkte von Abonnenten (INTEGER)

* **`artist_details`** (Spezifische Profildaten für Künstler)
  * `user_id`: Primary Key & Foreign Key (INTEGER, verweist auf `users(id)`)
  * `is_digital`: Type des Künstlers Art (INTEGER)

* **`subscriber_details`** (Spezifische Profildaten für Abonnenten)
  * `user_id`: Primary Key & Foreign Key (INTEGER, verweist auf `users(id)`)
  * `address_text`: Versandadresse des Abonnenten als Freitext (TEXT)
  * `subscriber_point`: Punkte von Abonnenten (INTEGER)

* **`addresses`** (Verwaltung der Versandinformationen)
  * `id`: Primary Key (INTEGER, AUTOINCREMENT)
  * `street`: Straße und Hausnummer (TEXT)
  * `city`: Stadt (TEXT)
  * `zip_code`: Postleitzahl (TEXT)
  * `country`: Land (TEXT)

* **`art_products`** (Basisinformationen aller Kunstprodukte)
  * `id`: Primary Key (INTEGER, AUTOINCREMENT)
  * `name`: Titel des Werks (TEXT, NOT NULL)
  * `description`: Beschreibung (TEXT)
  * `base_price`: Basispreis (REAL, NOT NULL)
  * `artist_id`: Künstler-Referenz (TEXT, NOT NULL, verweist konzeptionell auf `users(id)`)

* **`subscriptions`** (Beziehung zwischen Abonnenten und Künstlern)
  * `id`: Primary Key (INTEGER, AUTOINCREMENT)
  * `subscriber_id`: Foreign Key (INTEGER, verweist auf `users(id)`)
  * `artist_id`: Foreign Key (INTEGER, verweist auf `users(id)`)
  * `start_date`: Beginn des Abonnements (TEXT)
  * `is_active`: Aktivitätsstatus (INTEGER)
  * *Constraint:* `UNIQUE(subscriber_id, artist_id)`  verhindert doppelte Abonnements desselben Subscribers beim selben Artist
  
* **`dimensions`** (Abmessungen für physische Produkte)
  * `id`: Primary Key (INTEGER, AUTOINCREMENT)
  * `width`: Breite des Produkts (REAL)
  * `height`: Höhe des Produkts (REAL)
  * `depth`: Tiefe des Produkts (REAL)
  
* **`digital_products`** (Spezifische Daten für digitale Produkte)
  * `product_id`: Primary Key & Foreign Key (INTEGER, verweist auf `art_products(id)`)
  * `style`: Kunststil des digitalen Werks (TEXT)
  * `download_url`: Sicherer Link zum Herunterladen (TEXT)
  * `file_size_mb`: Dateigröße in Megabyte (REAL)
  * `file_extension`: Dateiformat (TEXT)
  * `dig_product_type`: Produkttyp wie 'POSTER', 'POSTCARD' (TEXT)
  
* **`physical_products`** (Spezifische Daten für physische Produkte)
  * `product_id`: Primary Key & Foreign Key (INTEGER, verweist auf `art_products(id)`)
  * `style`: Stil des physischen Werks (TEXT)
  * `dimensions`: Dimensions (TEXT)
  * `weight`: Gewicht des Produkts für den Versand (REAL)
  * `stock`: Aktueller Lagerbestand (INTEGER)
  * `medium`: Verwendetes Material wie 'PASTEL', 'OIL_PAINTING', 'CHARCOAL' (TEXT) 
  * `product_type`: Produkttyp als Enum wie 'POSTCARD', 'STICKER', 'CANVAS', 'PAPER' (TEXT)

* **`purchases`** (Erfasste Kauftransaktionen)
  * `id`: Primary Key (INTEGER, AUTOINCREMENT)
  * `buyer_id`: Foreign Key (INTEGER, verweist auf `users(id)`)
  * `product_id`: Foreign Key (INTEGER, verweist auf `art_products(id)`)
  * `purchase_date`: Kaufdatum (TEXT)

---

### 2. Beziehungen zwischen den Tabellen (Relations)

Die folgenden Beziehungen sind im System definiert. 

* **`users` zu `art_products` (1:n)**
  * Ein Künstler (`ARTIST`) kann mehrere Kunstprodukte erstellen. Die Zuordnung erfolgt über die Spalte `artist_id` in `art_products`. 
* **`users` (als Subscriber) zu `subscriptions` (1:n)**
  * Ein Abonnent (`SUBSCRIBER`) kann mehrere Abonnements bei unterschiedlichen Künstlern abschließen. Jeder Datensatz in `subscriptions` verweist über `subscriber_id` auf genau einen Benutzer mit der Rolle 'SUBSCRIBER'.

* **`users` (als Artist) zu `subscriptions` (1:n)**
  * Ein Künstler (`ARTIST`) kann von vielen verschiedenen Abonnenten abonniert werden. Jedes Abonnement in `subscriptions` ist über `artist_id` mit dem entsprechenden Künstler verknüpft.

* **`users` zu `artist_details` (1:1)**
  *  Spezialisierung (Vererbung auf Datenbankebene). Für jeden Benutzer mit der Rolle 'ARTIST' existiert genau ein Eintrag in `artist_details`.

* **`users` zu `subscriber_details` (1:1)**
  *  Analog zur Künstlerspezialisierung enthält diese Tabelle die erweiterten Profildaten für Abonnenten (Adresstext, Bonuspunkte).

* **`art_products` zu `physical_products` (1:1)**
  *  Spezialisierung der abstrakten Basisklasse für physische Güter.
* **`art_products` zu `digital_products` (1:1)**
  *  Spezialisierung der abstrakten Basisklasse für digitale Güter. 


