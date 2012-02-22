/**
 * verfuegbare Organisationen abfragen
 */
function out_findOrganisationen() {
	var objData = {};
	zabosAjaxRequest("FindOrganisationen", objData, in_findOrganisationen);
}

/**
 * Eingehende Antworten vom AJAX-Server
 * 
 * @param objData
 *            Daten in der JSON-Syntax
 */
function in_findOrganisationen(objData) {
	// SelectBox mit den Organisationen updaten
	resetSelectList('selectOrganisationen');
	for ( var i = 0, m = objData.arrOrganisationen.length; i < m; i++) {
		addElementToSelectList('selectOrganisationen', new Option(
				objData.arrOrganisationen[i].szName,
				objData.arrOrganisationen[i].iId));
	}

	// if (objData.arrOrganisationen.length == 0) {
	addElementToSelectList('selectOrganisationen', new Option("Alle zeigen",
			"0", false, true));
	// }
	resetList('selectOrganisationen'); // Letzten Eintrag selektieren
}

/**
 * verfuegbare Organisationseinheiten abfragen
 */
function out_findOrganisationseinheitenInOrganisation() {
	var objData = {
		'OrganisationId' : getValueOfSelectList('selectOrganisationen')
	};
	zabosAjaxRequest("FindOrganisationseinheitenInOrganisation", objData,
			in_findOrganisationseinheitenInOrganisation);
}

/**
 * Eingehende Antworten vom AJAX-Server
 * 
 * @param objData
 *            Daten in der JSON-Syntax
 */
function in_findOrganisationseinheitenInOrganisation(objData) {
	resetSelectList('selectOrganisationseinheiten');
	for ( var i = 0, m = objData.arrOrganisationseinheiten.length; i < m; i++) {
		addElementToSelectList('selectOrganisationseinheiten', new Option(
				objData.arrOrganisationseinheiten[i].szName,
				objData.arrOrganisationseinheiten[i].iId));
	}

	// if (objData.arrOrganisationseinheiten.length == 0) {
	addElementToSelectList('selectOrganisationseinheiten', new Option(
			"Alle zeigen", "0", false, true));
	// }
	resetList('selectOrganisationseinheiten'); // Letzten Eintrag selektieren
}

/**
 * Entfernt die ausgewaehlte Schleife(n) aus der "Zugewiesen"-Box ueberprueft
 * dabei, ob die Schleife auf der rechten Seite wieder erscheinen muss oder
 * nicht (ob der Kontext System/O/OE stimmt) Ablauf: Zuerst Elemente in die
 * "Verfuegbar"-Liste verschieben Danach den Kontext der Liste neu laden.
 */
function removeSchleifenFromKontext() {
	moveSelectedEntries('selectSchleifenSendTo', 'selectSchleifenAvailable');
	// Wenn in System, dann braucht die Methode nicht aufgerufen werden
	if (getValueOfSelectList('selectOrganisationen') != 0) {
		out_findSchleifenInKontext();
	}
}

/**
 * Anfrage an den AJAX-Server, welche Schleifen sich im ggw. Kontext befinden
 */
function out_findSchleifenInKontext() {
	var objData = {}, methodName = "";

	// Schleifen Systemweit finden, wenn keine O und keine OE gesetzt ist
	if ((getValueOfSelectList('selectOrganisationen') == 0)
			&& (getValueOfSelectList('selectOrganisationseinheiten') == 0)) {
		methodName = "FindSchleifenInSystemMitAusloeseberechtigung";
	} else {
		// Schleifen in einer Organisation suchen
		if (getValueOfSelectList('selectOrganisationseinheiten') == 0) {
			objData = {
				"OrganisationId" : getValueOfSelectList('selectOrganisationen')
			};
			methodName = "FindSchleifenInOrganisationMitAusloeseberechtigung";
		} else {
			objData = {
				"OrganisationseinheitId" : getValueOfSelectList('selectOrganisationseinheiten')
			};
			methodName = "FindSchleifenInOrganisationseinheitMitAusloeseberechtigung";
		}
	}
	zabosAjaxRequest(methodName, objData, in_findSchleifenInKontext);
	// 2006-05-11 MBI: Sobald als Methode nurnoch ..InSystem.. benutzt wird,
	// funktioniert es auch mit FF

}

/**
 * Eingehende Antworten vom AJAX-Server
 * 
 * @param objData
 *            Daten in der JSON-Syntax
 * @json-param array arrSchleifen int iId ID der Schleife string szName Name der
 *             Schleife Ablauf: Wenn Schleife auf der "Zu Ausloesen-Seite":
 *             Naechste Schleife
 */
function in_findSchleifenInKontext(objData) {
	resetSelectList('selectSchleifenAvailable');

	for ( var i = 0, m = objData.arrSchleifen.length; i < m; i++) {
		if (isValueElementInList('selectSchleifenSendTo',
				objData.arrSchleifen[i].iId) == false) {
			addElementToSelectList('selectSchleifenAvailable', new Option(
					objData.arrSchleifen[i].szDisplayName,
					objData.arrSchleifen[i].iId));
		}
	}
}

/**
 * Anfrage an den AJAX-Server, welche Personen sich im ggw. Kontext befinden
 */
function out_findPersonenInKontext() {
	var objData = {}, methodName = "";

	// Schleifen Systemweit finden, wenn keine O und keine OE gesetzt ist
	if ((getValueOfSelectList('selectOrganisationen') == 0)
			&& (getValueOfSelectList('selectOrganisationseinheiten') == 0)) {
		methodName = "FindPersonenInSystem";
	} else {
		// Schleifen in einer Organisation suchen
		if (getValueOfSelectList('selectOrganisationseinheiten') == 0) {
			objData = {
				"OrganisationId" : getValueOfSelectList('selectOrganisationen')
			};
			methodName = "FindPersonenInOrganisation";
		} else {
			objData = {
				"OrganisationseinheitId" : getValueOfSelectList('selectOrganisationseinheiten')
			};
			methodName = "FindPersonenInOrganisationseinheit";
		}
	}

	zabosAjaxRequest(methodName, objData, in_findPersonenInKontext);
}

/**
 * Eingehende Antworten vom AJAX-Server
 * 
 * @param objData
 *            Daten in der JSON-Syntax
 * @json-param array arrPersonen int iId ID der Person string szDisplayName Name
 *             der Person (Nachname, Vorname (Benutzername))
 */
function in_findPersonenInKontext(objData) {
	resetSelectList('selectPersonenAvailable');
	for ( var i = 0, m = objData.arrPersonen.length; i < m; i++) {
		if (isValueElementInList('selectPersonenAssigned',
				objData.arrPersonen[i].iId) == false) {
			addElementToSelectList('selectPersonenAvailable', new Option(
					objData.arrPersonen[i].szDisplayName,
					objData.arrPersonen[i].iId));
		}
	}
}
