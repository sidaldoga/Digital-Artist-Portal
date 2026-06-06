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

## Modul und Klassenübersicht
| Klasse                 | Aufgabe                                                                                                                                                                                           |
|:-----------------------|:--------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------|
| **Artist**             | Sie erbt von der Klasse „User“. Sie repräsentiert Künstlerprofildaten und die vom Künstler angebotenen Produkte/Abonnementpakete.                                                                 |
| **Subscriber**         | Sie erbt von der Klasse „User“. Sie enthält die für die Zustellung physischer Produkte notwendigen Versandadressinformationen und Abonnentendaten.                                                |
| **ArtProduct**         | Es handelt sich um eine abstrakte Oberklasse (Basisklasse), die die gemeinsamen Attribute (ID, Titel, Preis) aller Produkte enthält.                                                              |
| **Subscription**       | Dies ist die Geschäftslogikschicht, die die Beziehung zwischen einem Subscriber und einem Artist herstellt und das Duplikat-Abonnement-System verwaltet.                                          |
| **User**               | Es handelt sich um die Basisklasse für alle Benutzer im System. Sie enthält grundlegende Mitgliedsinformationen wie Name, E-Mail-Adresse und Passwort.                                            |
| **PhysicalProduct**    | Sie erbt von der Klasse ArtProduct. Sie repräsentiert physische Verpackungen wie Postkarten, Aufkleber und Leinwände mit Attributen wie Gewicht, Größe, Stil, Produkttyp (Enum) und Lagerbestand. |
| **DigitalProduct**     | Es erbt von der Klasse ArtProduct. Es repräsentiert digitale Kunstwerke mit Attributen wie Download-Link, Dateigröße, Dateiformat, Kunststil und digitalem Produkttyp.                            |
| **NetworkManager**     | Architektur Skeleton für die Netzwerkkommunikation. Enthält `startServer()` / `stopServer()` sowie `handleSubscriptionRequest()`, das jedoch aktuell leer ist keine Thread Logik implementiert.   |
| **UserDAO**            | Kapselt CRUD-Operationen für Benutzer, inkl. Authentifizierungs-Logik.                                                                                                                            |
| **ArtistDAO**          | Verwaltet die spezialisierten Daten und Details für Künstler.                                                                                                                                     |
| **SubscriberDAO**      | Verwaltet die spezifischen Daten für Subscriber und deren Profile.                                                                                                                                |
| **SubscriptionDAO**    | Realisiert relationale Abfragen für Abonnements und sorgt für Duplikatsvermeidung.                                                                                                                |
| **ArtProductDAO**      | Basis-CRUD für alle Produkttypen und deren relationale Zuweisungen.                                                                                                                               |
| **PhysicalProductDAO** | Verwaltet spezifische Details für physische Waren und Lagerbestände.                                                                                                                              |
| **DigitalProductDAO**  | Verwaltet spezifische Daten für digitale Downloads und Links.                                                                                                                                     |
| **AddressDAO**         | Verwaltet die CRUD-Operationen für die Adressdaten der Subscriber.                                                                                                                                |
| **DimensionsDAO**      | Kapselt die Datenbankzugriffe für physische Produktabmessungen.                                                                                                                                   |
| **DatabaseConnection** | Verwaltet den Verbindungsaufbau zur lokalen SQLite-Datenbankdatei.                                                                                                                                |
| **ArtStyle**           | Enum für Kunststile: `REALISM`, `MINIMALISM`, `SURREALISM`, `FANTASY`, `CYBERPUNK`, `WHIMSICAL`.                                                                                                  |
| **ProductType**        | Enum für physische Produkttypen: `CANVAS`, `PAPER`, `POSTCARD`, `STICKER`.                                                                                                                        |
| **MediumType**         | Enum für verwendete Materialien: `OIL_PAINTING`, `CHARCHOAL`, `WATERCOLOR`, `PASTEL`, `PENCIL`.                                                                                                   |
| **DigProductType**     | Enum für digitale Produkttypen: `POSTER`, `POSTCARD`, `STICKER`, `COMMISSIONED`, `WALLPAPER`.                                                                                                     |
| **PasswordHasher**     | Utility-Klasse für SHA-256 Passwort-Hashing und Verifikation.                                                                                                                                     |

## Datenstruktur

Die Anwendung verwendet eine lokale SQLite Datenbank, um Projektdaten dauerhaft zu speichern. Tabellenstrukturen, Schlüssel und relationale Links sind wie folgt strukturiert:

### 1. Existierende Tabellen und gespeicherte Informationen

* **`users`** (Zentrale Tabelle für alle Benutzer)
  * `id`: Primary Key (INTEGER, AUTOINCREMENT)
  * `fname`: Vorname (TEXT, NOT NULL)
  * `lname`: Nachname (TEXT, NOT NULL)
  * `username`: Eindeutiger Benutzername für Authentifizierung (TEXT, UNIQUE, NOT NULL)
  * `password_hash`: Passwort (TEXT, NOT NULL)
  * `email`: E-Mail-Adresse (TEXT, NOT NULL)
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
  
* **`digital_products`** ()
  * `product_id`:Primary Key & Foreign Key (INTEGER, verweist auf `art_products(id)`)
  * `style`: Kunststil des digitalen Werks (TEXT)
  * `download_url`: Sicherer Link zum Herunterladen (TEXT)
  * `file_size_mb`: Dateigröße in Megabyte (REAL)
  * `file_extension`:Dateiformat  (TEXT)
  * `dig_product_type`: Produkttyp wie 'POSTER', 'POSTCARD'  (TEXT)
  
* **`physical_products`** ()
  * `product_id`: Primary Key & Foreign Key (INTEGER, verweist auf `art_products(id)`)
  * `style`: Stil des physischen Werks (TEXT)
  * `dimensions`: Dimensions (TEXT)
  * `weight`: Gewicht des Produkts für den Versand (REAL)
  * `stock`: Aktueller Lagerbestand (INTEGER)
  * `medium`: Verwendetes Material wie 'PASTEL', 'OIL_PAINTING', 'CHARCOAL' (TEXT) 
  * `product_type`: Produkttyp als Enum wie 'POSTCARD', 'STICKER', 'CANVAS', 'PAPER' (TEXT)

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