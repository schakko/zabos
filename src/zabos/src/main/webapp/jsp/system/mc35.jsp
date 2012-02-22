<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="zabos" uri="http://ecw.de/taglibs/zabos"%>
<%@ page language="java" contentType="text/html; charset=utf-8"
	pageEncoding="UTF-8"%>

<div id="mc35"
	title="Konfigurieren Sie hier die GSM-Modems des Systems. Vorsicht: Falsche Einstellungen können zu einem Nichtfunktionieren des Systems führen."
	class="setting" style="display: none"><c:if
	test="${sessionScope.user.accessControlList.systemKonfigurationAendernErlaubt}">
	<table class="popup_base">
		<tr class="list_head">
			<td class="h_spacer"></td>
			<td>GSM-Modems bearbeiten:</td>
		</tr>
		<tr class="list_head">
			<td class="spacer"></td>
			<td>
			<form method="post"
				action="<zabos:url url="controller/system/?submit=true&amp;tab=mc35"/>"
				name="frmSelectModem" id="frmSelectModem"><select
				title="Klicken Sie hier, um ein neues Modem einzurichten oder ein vorhandenes zu bearbeiten."
				id="ModemId" name="ModemId" class="helpEvent input"
				onchange="document.forms.frmSelectModem.submit()">
				<option value="0" <c:if test="${empty objModem}">selected</c:if>>&lt;Neues
				GSM-Modem&gt;</option>
				<c:forEach items="${arrModemAvailable.data}" var="data">
					<option value="<c:out value="${data.baseId}"/>"
						<c:if test="${data.baseId == objModem.baseId}">selected</c:if>><c:out
						value="${data.rufnummer}" /></option>
				</c:forEach>
			</select></form>
			</td>
		</tr>
		<tr>
			<td></td>
			<td>
			<table class="popup_inner">
				<tr>
					<td class="text_info">W&auml;hlen Sie hier ein Modem aus,
					deren Daten Sie bearbeiten wollen, oder legen sie ein neues Modem
					an.</td>
				</tr>
				<tr class="v_spacer">
					<td></td>
				</tr>
			</table>
			</td>
		</tr>
	</table>

	<!-- Bereich: Ende -->
	<form name="frmEditModem" id="frmEditModem" action=""
		onKeyUp=
	Aenderung(this.id);
method="post"><input type="hidden"
		name="ModemId" value="<c:out value="${objModem.baseId}" default="0"/>" />

	<table class="popup_base">
		<tr class="list_head">
			<td class="h_spacer"></td>
			<td>Modem:</td>
		</tr>
		<tr class="v_spacer">
			<td colspan="2"></td>
		</tr>
		<tr>
			<td></td>
			<td>
			<table class="popup_inner">
				<tr>
					<td class="text inner_option_large">Rufnummer des Modems</td>
					<td><input
						title="Geben Sie hier die Rufnummer des Modems ein."
						name="textRufnummer" type="text" class="helpEvent input"
						value="<c:out value="${objModem.rufnummer}" default="0049"/>"
						size="30"></td>
				</tr>
				<tr>
					<td class="text inner_option_large">PIN</td>
					<td><input
						title="Geben Sie hier die PIN-Nummer Ihrer SIM-Karte ein."
						name="textPin1" type="text" class="helpEvent input"
						value="<c:out value="${objModem.pin1}" default="${param.textPin1}"/>"
						size="4" maxlength="4"></td>
				</tr>
				<tr>
					<td></td>
					<td>Achtung: Wird die PIN dreimal falsch eingegeben,
					deaktiviert sich die eingelegte SIM-Karte!</td>
				</tr>
				<tr>
					<td colspan="2">&nbsp;</td>
				</tr>

				<c:if
					test="${sessionScope.user.accessControlList.comPortFestlegenErlaubt}">
					<tr>
						<td class="text inner_option_large">COM-Port</td>
						<td><select
							title="Wählen Sie hier die COM-Schnittstelle aus, an der das Modem angeschlossen ist."
							class="helpEvent" name="textComPort">
							<c:forEach items="${arrAvailableComPorts.data}" var="data"
								varStatus="statusCom">
								<c:if test="${objKonfiguration.com5Ton != statusCom.index}">
									<option value="${statusCom.index}"
										<c:if test="${objModem.comPort == statusCom.index}">selected</c:if>>${data.name}</option>
								</c:if>
							</c:forEach>
						</select></td>
					</tr>
				</c:if>
				<tr>
					<td class="text">&nbsp;</td>
					<td class="text"><input class="helpEvent"
						title="Aktivieren Sie diese Checkbox, wenn das Modem dediziert zum Auslösen von Alarmen benutzt werden soll."
						type="checkbox" name="isAlarmModem" value="checkbox"
						<c:if test="${objModem.alarmModem}">checked</c:if>> Wird
					dediziert zum Auslösen von Alarmen benutzt.</td>
				</tr>
				<tr>
					<td class="text inner_option_large">Ist Modem online</td>
					<td><c:choose>
						<c:when test="${isModemOnline}"><font color="green">Ja</font></c:when>
						<c:when test="${!isModemOnline}"><font color="red">Nein</font></c:when>
					</c:choose></td>
				</tr>
				<tr class="v_spacer">
					<td colspan="2"></td>
				</tr>
				<tr>
					<td colspan="2">Achtung: Jedes Modem, dass Sie dediziert als Alarm-Modem verwenden, wird bei den gesendeten SMSen <strong>nicht</strong> als Absender benutzt!<br />
					Ein dediziertes Alarm-Modem kann definiert werden, um Alarme priorisiert per SMS auszul&ouml;sen.<br />
					R&uuml;ckmeldungen und Ausl&ouml;sungen k&ouml;nnen trotzdem &uuml;ber alle Modems erhalten werden.<br />
					<strong>Bitte
					beachten Sie: Sobald der COM-Port eines Modems geändert wird,
					werden laufende Alarme abgebrochen!</strong></td>
				</tr>
				<tr class="v_spacer">
					<td></td>
					<td></td>
				</tr>
			</table>
			</td>
		</tr>
	</table>
	</form>

	<!--  SPACER -->
	<table>
		<tr class="v_spacer">
			<td></td>
		</tr>
	</table>

	<!-- BEDIENELEMENTE -->
	<table class="popup_inner" id="controls">
		<tr class="list_head">
			<td>
			<table class="popup_base">
				<tr>
					<td class="h_spacer"></td>
					<td>
					<div align="left" id="controlButtons"><input
						title="Klicken Sie auf 'Speichern' um die Änderungen zu speichern."
						name="inputSave" type="submit" class="helpEvent button"
						value="Speichern"
						onclick="document.forms.frmEditModem.action='<zabos:url url="controller/system/?do=doUpdateModem&amp;submit=true&amp;tab=mc35"/>';document.forms.frmEditModem.submit()">
					</div>
					</td>
					<td>
					<div align="right"><input name="inputCancel"
						title="Klicken Sie auf 'Zurücksetzen' um die Änderungen zu verwerfen."
						onclick=
	document.forms.frmEditModem.reset();
type="submit"
						class="helpEvent button" value="Zurücksetzen"></div>
					</td>
					<c:if test="${!empty objModem}">
						<td>
						<div align="right"><input
							title="Klicken Sie auf 'Entfernen' um das Modem aus dem System zu entfernen."
							name="inputDelete" type="submit" class="helpEvent button"
							value="Entfernen"
							onclick="document.forms.frmEditModem.action='<zabos:url url="controller/system/?do=doDeleteModem&amp;submit=true&amp;tab=mc35"/>';document.forms.frmEditModem.submit()">
						</div>
						</td>
					</c:if>
					<td class="h_spacer"></td>
				</tr>
			</table>
			</td>
		</tr>
	</table>
</c:if></div>