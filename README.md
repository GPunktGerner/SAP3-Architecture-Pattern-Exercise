# Chat App

## Version 1 – Server-Client-Architektur

Der Chat-Client sendet und empfängt Nachrichten vom Server. Der Server verarbeitet die eingehenden Nachrichten und stellt sie über einen Socket dem Client zur Verfügung.

**Vorteile**

- Erweiterbar: Es können weitere Clients hinzugefügt werden (z. B. Web-Version, App-Version).
- Nachrichten werden nur den Nutzern angezeigt, für die sie bestimmt sind.

**Nachteile**

- Höhere Belastung des Backends.

---

## Version 2 – Event-Driven-Architektur (nur Konzept)

Der Chat-Client und das Backend kommunizieren nicht direkt miteinander. Stattdessen veröffentlicht der Client Daten an einen Message Broker. Der Server verarbeitet die eingehenden Nachrichten und veröffentlicht eine bearbeitete Version erneut im Message Broker. Die Clients können die Nachrichten abonnieren.

**Vorteile**

- Erweiterbar: Weitere Clients können leicht integriert werden (z. B. Web-Version, App-Version).

**Nachteile**

- Alle Nutzer erhalten alle Nachrichten, unabhängig von der Zielgruppe.
