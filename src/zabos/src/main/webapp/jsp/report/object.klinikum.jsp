<%@ page language="java" contentType="text/html; charset=utf-8"
	pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="zabos" uri="http://ecw.de/taglibs/zabos"%>
<jsp:useBean id="beanPersonenDelta"
	class="de.ecw.zabos.frontend.beans.DeltaBean"
	type="de.ecw.zabos.frontend.beans.DeltaBean" scope="page" />

<script language="javascript" type="text/javascript">
	var ALARM_ID = "${objStatistik.alarm.alarmId}";
	
	// Alle 7 Sekunden die Abfrage nach den Personen machen
	var intInterval = 7000;
	var timer = window.setInterval("out_checkAlarmReportByBereich()", intInterval);
	
	// Erste Abfrage nach Laden der Seite ausführen
	out_checkAlarmReportByBereich();
	
	// Alle konfigurierbaren Bereiche abfragen 
	out_findFunktionstraegerMitBereichen();
</script>

<div id="confirmAlarmShadow" class="confirmAlarmShadow"></div>
<div id="notificationChange" style="display: none"><b>Aktualisierung
wurde angehalten</b>: Änderungen an Einstellungen werden vorgenommen.<br>
Schließen Sie den Konfigurationsbereich oder speichern Sie den Kommentar
um fortzufahren.</div>
<div id="confirmAlarm" class="confirmAlarm">
<center>
<table class="confirmTable">
	<tr class="table_head">
		<th class="confirmTableHead">ZABOS Sicherheitsabfrage</th>
	</tr>
	<tr class="v_spacer">
		<td></td>
	</tr>
	<tr>
		<td class="text_bold">
		<center>Soll der Alarm wirklich entwarnt werden?</center>
		</td>
	</tr>
	<tr class="v_spacer">
		<td></td>
	</tr>
	<tr>
		<td>
		<center>
		<table style="margin-top: 20px;">
			<tr>
				<td><input class="button" onclick="confirmEntwarnung();"
					type="button" name="AlarmEntwarnen" value="Ja - Entwarnen">
				</td>
				<td style="width: 40px;"></td>
				<td><input class="button" onclick="confirmAlarmClose();"
					type="submit" name="AlarmAbbrechen" value="Nein - Abbrechen">
				</td>
			</tr>
		</table>
		</center>
		</td>
	</tr>
</table>
</center>
</div>

<table class="popup_inner_large">
	<tr class="head_normal">
		<td>
		<table class="popup_base_large">
			<tr id="head_status" class="head_status">
				<th>Alarmierungen ID <script language="javascript"
					type="text/javascript">
				        var hexid = ${objStatistik.alarm.reihenfolge};
				        //hexid = hexid.toString(16);	        
				       document.write(hexid.toString(16));
			        </script> <!-- SystemID ${objStatistik.alarm.alarmId} --> - <zabos:formatts
					format="time" timeStamp="${objStatistik.alarm.alarmZeit.timeStamp}" />h
				<zabos:formatts format="date"
					timeStamp="${objStatistik.alarm.alarmZeit.timeStamp}" /></th>
			</tr>
		</table>
		</td>
	</tr>
</table>
</div>

	<div id="master">
	
		<div id="masterFloat">
			<div id="entwarnen">	
				<c:if test="${objStatistik.alarm.aktiv == true}">
					<img id="entwarnIcon" style="cursor: pointer;" height="40" width="40" src="<zabos:url url="images/entwarnen.png"/>" alt="Report laden" onClick="confirmEntwarnungOpen()">
					<div style="padding-top:5px;" id="entwarnStatus"><a href="javascript:confirmEntwarnungOpen()">ENTWARNEN</a></div>
				</c:if>
				<c:if test="${objStatistik.alarm.aktiv == false}">
					<div id="entwarnStatus" style="padding-top:16px;">Alarm wurde beendet</div>
				</c:if>
				
			</div>
			
			<div id="containerDetails">
			
				<div id="detailsTop">
	
					<div id="topPositiv" class="detailsElement topPositiv_fail"> - </div>
					<div id="topSoll" class="detailsElement"> - </div>
					<div id="topAlarmiert" class="detailsElement"> - </div>
				</div>
				
				<div id="detailsBottom">
				
					<div id="bottomPositiv" class="detailsElement"> Positiv </div>
					<div id="bottomSoll" class="detailsElement"> Soll </div>
					<div id="bottomAlarmiert" class="detailsElement"> Alarmiert </div>
				
				</div>
			
			</div>
		</div>
	
		<div id="report">
			<a id="reportStatusIcon" href=""><img id="reportIcon" height="40" width="40" src="<zabos:url url="images/report.png"/>" alt="Report laden"></a>
			<div style="padding-top:5px;" id="reportStatus">Report laden</div>
		</div>
	</div>
	
	<div id="details">
		<div id="detailsHeader" class="groupTitle status_non" onClick="showDetails()">Details anzeigen</div>
		<div id="detailsContent" style="display:none">
		
			<div id="formContainer">
			<form method="post"
				action="<zabos:url url="controller/report/?tab=object&amp;submit=true&amp;do=doStopAlarm" />"
				name="frmEntwarnen" id="frmEntwarnen"><input type="hidden"
				name="AlarmId" value="${objStatistik.alarm.alarmId}" />
				
				<table class="popup_base_large">
					<tr>
						<td class="text_bold inner_option" style="vertical-align: middle;">Ausl&ouml;ser:</td>
						<td class="h_spacer" width="50">&nbsp;</td>
						<td class="textKlinikum inner_input">
						${objStatistik.person.displayName}</td>
					</tr>
					<tr class="list_head">
						<td class="text_bold">Ausl&ouml;seart:</td>
						<td class="h_spacer">&nbsp;</td>
						<td class="textKlinikum"><c:choose>
							<c:when test="${objStatistik.smsAusloesung}">SMS</c:when>
							<c:when test="${objStatistik.webAusloesung}">Web-Terminal</c:when>
							<c:otherwise>Unbekannt</c:otherwise>
						</c:choose></td>
					</tr>
					<tr>
						<td class="text_bold">Ausl&ouml;sungstext</td>
						<td class="h_spacer">&nbsp;</td>
						<td class="textKlinikum"><c:choose>
							<c:when test="${objStatistik.fuenfTonAusloesung == false}">${objStatistik.alarm.kommentar}</c:when>
							<c:otherwise>-</c:otherwise>
						</c:choose></td>
					</tr>
					<tr class="list_head">
						<td class="text_bold">Ist aktiv:</td>
						<td class="h_spacer">&nbsp;</td>
						<td class="textKlinikum"><span class="" id="alarm_aktiv">
						<c:choose>
							<c:when test="${objStatistik.alarm.aktiv == true}">
							            Ja
							          </c:when>
							<c:otherwise>
							            Nein
							          </c:otherwise>
						</c:choose> </span></td>
					</tr>
					<tr>
						<td class="text_bold">Zeitpunkt Nachalarmierung/Deaktivierung</td>
						<td class="h_spacer">&nbsp;</td>
						<td class="textKlinikum"><span
							id="nachalarmierungDeaktivierungZeitpunkt">unbekannt</span></td>
					</tr>
					<tr class="list_head">
						<td class="text_bold">Ist nachalarmiert:</td>
						<td class="h_spacer">&nbsp;</td>
						<td class="textKlinikum"><span class="" id="alarm_nachaktiviert">
						<c:choose>
							<c:when test="${objStatistik.alarm.nachalarmiert == true}">
							            Ja
							          </c:when>
							<c:otherwise>
							            Nein
							          </c:otherwise>
						</c:choose> </span></td>
					</tr>
					<tr>
						<td class="text_bold">Anzahl Personen mit passender Funktionstr&auml;ger/Bereichszuordnung</td>
						<td class="h_spacer">&nbsp;</td>
						<td class="textKlinikum"><span id="numAnzahlPassenderPersonen">-</span></td>
					</tr>
					<tr class="list_head">
						<td class="text_bold">Report:</td>
						<td class="h_spacer">&nbsp;</td>
						<td class="textKlinikum"><span id="linkToReport">-</span></td>
					</tr>
				</table>
			</form>
			</div>
	
		</div>
	</div>
	

<div id="divContent">

</div>

<table>
	<tr class="v_spacer">
		<td></td>
	</tr>
</table>

<div id="notifactionArea"></div>
<div id="configurationArea"></div>

<div class="group" id="groupSample" style="display: none"><!--  Vorlage für Höhenangabe --></div>
<table class="popup_base_large">
	<tr class="list_head">
		<td>
		<table>
			<tr>
				<td class="h_spacer"></td>
				<td><input name="Submit" type="reset" class="button"
					onClick="javascript:window.history.back()" value="Zur&uuml;ck"></td>
				<td class="h_spacer"></td>
				<td><input name="Submit" type="reset" class="button"
					onClick="javascript:window.close()" value="Schlie&szlig;en"></td>
			</tr>
		</table>
		</td>
	</tr>
</table>
