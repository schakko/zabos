ZABOS
=====
ZABOS ist eine webbasierte Applikation zur SMS-Alarmierung von Behörden und Organisationen.
ZABOS unterstützt Siemens MC35-Modems zum Empfang von eingehenden SMSen für Rückmeldungen und nutzt monitord als Auswerter für ZVEI-, POCSAG- oder FMS-Nachrichten.

Features
--------
* manuelle Auslösung von Alarmen via Weboberfläche
* manuelle Auslösung von Alarmen via SMS
* automatische Auslösung von Alarmen via ZVEI (Fünftonfolgeruf). POCSAG und FMS ist noch nicht implementiert.
* Rückmeldungen via SMS werden nahezu unverzögert dargestellt
* Freidefinierbare Aliase für Rückmelde-SMSen (Status: ja, nein, später)
* Nachalarmierung von Schleifen zum Einsatz von MANVs o.ä. im Klinikumsbereich falls Sollstärke für Funktionsträger nicht erreicht ist
* Reportfunktionalität: automatische Erzeugung von PDFs mit Statistiken
* Automatischer Druck der PDFs durch Interceptoren/lpr möglich
* Zuweisen von Aufgaben von alarmierten Personen. Aufgagben können z.B. auf einen großen Bildschirm dargestellt werden, so dass bei Eintreffen der alarmierten Mitarbeiter sofort die Aufgabe ersichtlich ist
* fein abgestuftes Rollen-/Rechte-Konzept
* Abbildung der Organisationsstrukturen über Organisationen, Organisationseinheiten/Standorte und Schleifen
* Personen können ihre Abwesenheitszeiten pflegen
* Konnektoren für SMS-Gateways wie SMS 77 oder SMS-One; weitere können einfach erstellt werden
* Konnektor für Alarmierung per E-Mail
* Verarbeitung von eingehenden SMSen durch MC35-Modems (getestet u.a. mit Trueport IOLAN)
* Loadbalancing für Verarbeitung der eingehenden SMSen

Installation
------------
ZABOS nutzt als Datenbank PostgreSQL 8.2. Das Schema befindet sich unter database/create_pg.sql.
TODO: auf die korrekten JAR-Dateien verweisen.

monitord
--------
Die Abnahme von FMS-, POCSAG- und ZVEI-Nachrichten erfolgt über monitord, einen freien Funkauswerter.
monitord muss dazu mit dem libactivemq-Plugin betrieben werden. Die Nachrichten werden monitord in eine bestehende ActiveMQ-Queue gepusht und dann von ZABOS bzw. JMS verarbeitet.

Lizenz
------
ZABOS - Zusatzalarmierung für Behörden und Organisationen mit Sicherheitsaufgaben
Copyright (C) 2012  EDV Consulting Wohlers GmbH

This program is free software: you can redistribute it and/or modify
it under the terms of the GNU General Public License as published by
the Free Software Foundation, either version 3 of the License, or
(at your option) any later version.

This program is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU General Public License for more details.

Die Lizensierung gilt für alle vorliegenden Quellcodeteile innerhalb dieses Repositories auch wenn deren Header nicht explizit mit der GPLv3 markiert ist.

































































