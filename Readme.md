ZABOS
=====
ZABOS ist eine webbasierte Applikation zur SMS-Alarmierung von Beh�rden und Organisationen.
ZABOS unterst�tzt Siemens MC35-Modems zum Empfang von eingehenden SMSen f�r R�ckmeldungen und nutzt monitord als Auswerter f�r ZVEI-, POCSAG- oder FMS-Nachrichten.

Features
--------
* manuelle Ausl�sung von Alarmen via Weboberfl�che
* manuelle Ausl�sung von Alarmen via SMS
* automatische Ausl�sung von Alarmen via ZVEI (F�nftonfolgeruf). POCSAG und FMS ist noch nicht implementiert.
* R�ckmeldungen via SMS werden nahezu unverz�gert dargestellt
* Freidefinierbare Aliase f�r R�ckmelde-SMSen (Status: ja, nein, sp�ter)
* Nachalarmierung von Schleifen zum Einsatz von MANVs o.�. im Klinikumbereich falls Sollst�rke f�r Funktionstr�ger nicht erreicht ist
* Reportfunktionalit�t: automatische Erzeugung von PDFs mit Statistiken
* Automatischer Druck der PDFs durch Interceptoren/lpr m�glich
* Zuweisen von Aufgaben von alarmierten Personen. Aufgagben k�nnen z.B. auf einen gro�en Bildschirm dargestellt werden, so dass bei Eintreffen der alarmierten Mitarbeiter sofort die Aufgabe ersichtlich ist
* fein abgestuftes Rollen-/Rechte-Konzept
* Abbildung der Organisationsstrukturen �ber Organisationen, Organisationseinheiten/Standorte und Schleifen
* Personen k�nnen ihre Abwesenheitszeiten pflegen
* Konnektoren f�r SMS-Gateways wie SMS 77 oder SMS-One; weitere k�nnen einfach erstellt werden
* Konnektor f�r Alarmierung per E-Mail
* Verarbeitung von eingehenden SMSen durch MC35-Modems (getestet u.a. mit Trueport IOLAN)
* Loadbalancing f�r Verarbeitung der eingehenden SMSen

Installation
------------
ZABOS nutzt als Datenbank PostgreSQL 8.2. Das Schema befindet sich unter database/create_pg.sql.
TODO: auf die korrekten JAR-Dateien verweisen.

monitord
--------
Die Abnahme von FMS-, POCSAG- und ZVEI-Nachrichten erfolgt �ber monitord, einen freien Funkauswerter.
monitord muss dazu mit dem libactivemq-Plugin betrieben werden. Die Nachrichten werden monitord in eine bestehende ActiveMQ-Queue gepusht und dann von ZABOS bzw. JMS verarbeitet.

Lizenz
------
ZABOS - Zusatzalarmierung f�r Beh�rden und Organisationen mit Sicherheitsaufgaben
Copyright (C) 2012  EDV Consulting Wohlers GmbH

This program is free software: you can redistribute it and/or modify
it under the terms of the GNU General Public License as published by
the Free Software Foundation, either version 3 of the License, or
(at your option) any later version.

This program is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU General Public License for more details.

Die Lizensierung �bertr�gt sich auf alle vorliegenden Quellcodeteile innerhalb dieses Repositories.
