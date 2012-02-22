<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="zabos" uri="http://ecw.de/taglibs/zabos"%>
<%@ page language="java" contentType="text/html; charset=utf-8"
	pageEncoding="UTF-8"%>

<div id="konfiguration"
	title="Ändern Sie hier die Grundeinstellungen des Systems."
	class="setting" style="display: none"><c:if
	test="${sessionScope.user.accessControlList.systemKonfigurationAendernErlaubt}">
	<form name="frmEditKonfiguration" id="frmEditKonfiguration"
		action="<zabos:url url="controller/system/?do=doUpdateSystem&amp;submit=true&amp;tab=konfiguration" />"
		method="post">

	<table class="popup_base">
		<tr class="list_head">
			<td class="h_spacer"></td>
			<td>System-Einstellungen:</td>
		</tr>
		<tr class="v_spacer">
			<td></td>
			<td></td>
		</tr>
		<tr>
			<td></td>
			<td>
			<table class="popup_inner">
				<tr>
					<td class="helpEvent inner_option_large text"
						title="Geben Sie hier an, innerhalb welcher Zeitspanne auf eine Alarmierung geantwortet werden kann.">Timeout
					Alarm</td>
					<td><input name="textAlarmTimeout" type="text"
						class="helpEvent input"
						title="Geben Sie hier an, innerhalb welcher Zeitspanne auf eine Alarmierung geantwortet werden kann."
						id="textAlarmTimeout"
						value="<c:out value="${objKonfiguration.alarmTimeout}" default="${param.textAlarmTimeout}"/>"
						size="30" maxlength="6"> Sekunden</td>
				</tr>
				<tr>
					<td class="helpEvent inner_option_large text"
						title="Geben Sie hier an, nach welcher Zeitspanne eine ausgelöste Schleife wieder alarmiert werden kann.">Timeout
					Reaktivierung</td>
					<td><input name="textReaktivierungTimeout" type="text"
						class="helpEvent input" id="textReaktivierungTimeout"
						title="Geben Sie hier an, nach welcher Zeitspanne eine ausgelöste Schleife wieder alarmiert werden kann."
						value="<c:out value="${objKonfiguration.reaktivierungTimeout}" default="${param.textReaktivierungTimeout}"/>"
						size="30" maxlength="6"> Sekunden</td>
				</tr>
				<tr>
					<td class="helpEvent inner_option_large text"
						title="Geben Sie hier an, innerhalb welcher Zeitspanne auf eine Alarmierung geantwortet werden kann.">Timeout
					SMS-Eingang</td>
					<td><input name="textSmsInTimeout" type="text"
						class="helpEvent input" id="textSmsInTimeout"
						title="Geben Sie hier an, innerhalb welcher Zeitspanne auf eine Alarmierung geantwortet werden kann."
						value="<c:out value="${objKonfiguration.smsInTimeout}" default="${param.textSmsInTimeout}"/>"
						size="30" maxlength="6"> Sekunden</td>
				</tr>
				<tr>
					<td class="helpEvent inner_option_large text"
						title="Geben Sie hier an, an welcher COM-Schnittstelle sich der 5-Ton-Empfänger befindet.">COM-Interface
					5-Ton-Empf&auml;nger</td>
					<td><select name="textCom5Ton" class="helpEvent"
						title="Geben Sie hier an, an welcher COM-Schnittstelle sich der 5-Ton-Empfänger befindet.">
						<option value=""
							<c:if test="${objKonfiguration.com5Ton == null}">selected</c:if>>Deaktivieren</option>
						<c:forEach items="${arrAvailableComPorts.data}" var="data"
							varStatus="statusCom">
							<option value="${statusCom.index}"
								<c:if test="${objKonfiguration.com5Ton == statusCom.index}">selected</c:if>>${data.name}</option>
						</c:forEach>
					</select></td>
				</tr>
				<tr>
					<td class="helpEvent inner_option_large text"
						title="Stellen Sie hier ein, wieviele Alarmierungen in der Alarm-Historie zu sehen sein sollen.">Alarmierungen
					in Alarm-Historie</td>
					<td><input
						title="Stellen Sie hier ein, wieviele Alarmierungen in der Alarm-Historie zu sehen sein sollen."
						name="textAlarmHistorieLaenge" type="text" class="helpEvent input"
						id="textAlarmHistorieLaenge"
						value="<c:out value="${objKonfiguration.alarmHistorieLaenge}" default="${param.alarmHistorieLaenge}"/>"
						size="3" maxlength="3"> Alarme</td>
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

	<table class="popup_inner" id="controls">
		<tr class="list_head">
			<td>
			<table class="popup_base">
				<tr>
					<td class="h_spacer"></td>
					<td>
					<div align="left" id="controlButtons"><input name="inputSave" type="submit"
						title="Klicken Sie auf 'Speichern' um die Änderungen zu speichern."
						class="helpEvent button" value="Speichern"
						onclick="document.forms.frmEditKonfiguration.action='<zabos:url url="controller/system/?do=doUpdateSystem&amp;submit=true&amp;tab=konfiguration" />';document.forms.frmEditKonfiguration.submit()"></div>
					</td>
					<td width="100%"></td>
				</tr>
			</table>
			</td>
		</tr>
	</table>
</c:if></div>