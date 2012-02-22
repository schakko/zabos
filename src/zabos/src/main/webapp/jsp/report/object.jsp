<%@ page language="java" contentType="text/html; charset=utf-8"
    pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="zabos" uri="http://ecw.de/taglibs/zabos"%>
<jsp:useBean id="beanPersonenDelta" class="de.ecw.zabos.frontend.beans.DeltaBean" type="de.ecw.zabos.frontend.beans.DeltaBean" scope="page"/>
	<script language="javascript" type="text/javascript">
		// Alle 10 Sekunden die Abfrage nach den Personen machen
		var intInterval = 10000;
		var timer = window.setInterval("out_checkAlarmReport()", intInterval);

		/** 
		* Alarm-Report abfragen
		*/
		function out_checkAlarmReport() {
			// Daten setzen
			var objData = {'AlarmId':${objStatistik.alarm.alarmId}};
	
			// Abfrage starten
			zabosAjaxRequest('GetAlarmReport',objData,in_checkAlarmReport);
		}
	 
		out_checkAlarmReport();
	</script>
		
		
<div id="confirmAlarmShadow" class="confirmAlarmShadow">
</div>
<div id="confirmAlarm" class="confirmAlarm">
	<center>
	<table class="confirmTable"> 
		<tr class="table_head">
			<th class="confirmTableHead">
				ZABOS Sicherheitsabfrage
			</th>
		</tr>
		<tr class="v_spacer">
			<td>
			</td>
		</tr>
		<tr>
			<td class="text_bold">
				<center>Soll der Alarm wirklich entwarnt werden?</center>
			</td>
		</tr>
		<tr class="v_spacer">
			<td>
			</td>
		</tr>
		<tr>
			<td>
				<center>
					<table style="margin-top: 20px;">
						<tr>
							<td>
								<input class="button"  onclick="confirmEntwarnung();" type="button" name="AlarmEntwarnen" value="Ja - Entwarnen">
							</td>
							<td style="width: 40px;">
							</td>
							<td>
								<input class="button"  onclick="confirmAlarmClose();" type="submit" name="AlarmAbbrechen" value="Nein - Abbrechen">
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
		    <td><table class="popup_base_large">
		      <tr class="head_normal">
		        <th>
		        Alarmierungen ID 
			       <script language="javascript" type="text/javascript">
				        var hexid = ${objStatistik.alarm.reihenfolge};
				        hexid = hexid.toString(16);	        
				       document.write(hexid);
			        </script>
		          <!-- SystemID ${objStatistik.alarm.alarmId} --> - <zabos:formatts format="time" timeStamp="${objStatistik.alarm.alarmZeit.timeStamp}"/>h <zabos:formatts format="date" timeStamp="${objStatistik.alarm.alarmZeit.timeStamp}"/> </th>
		        </tr>
		    </table></td>
		  </tr>
		</table>
		
		<form method="post" action="<zabos:url url="controller/report/?tab=object&amp;submit=true&amp;do=doStopAlarm" />" name="frmEntwarnen" id="frmEntwarnen">
		  <input type="hidden" name="AlarmId" value="${objStatistik.alarm.alarmId}" />
			<table class="popup_base_large">
			  <tr class="v_spacer">
			    <td class="h_spacer"></td>
			    <td></td>
			    <td></td>
			    <td></td>
			  </tr>
			  <tr class="list_head">
			    <td class="h_spacer"></td>
			    <td class="text_bold inner_option">Ausl&ouml;ser:</td>
			    <td class="text inner_input">
			      <c:choose>
			        <c:when test="${objStatistik.fuenfTonAusloesung == false}">${objStatistik.person.displayName}</c:when>
			        <c:otherwise>5-Ton-Folgeruf</c:otherwise>
			      </c:choose>
			   </td>
			   <td>
		        	<c:if test="${objStatistik.alarm.aktiv == true}">
						<input class="button"  onclick="confirmEntwarnungOpen()" type="button" name="AlarmEntwarnen" style="height:30px; width:180px" value="ALARM ENTWARNEN">
					</c:if>
			   </td>
			  </tr>
			  <tr class="list_head">
			    <td></td>
			    <td class="text_bold">Ausl&ouml;seart:</td>
			    <td class="text">
			    <c:choose>
			      <c:when test="${objStatistik.smsAusloesung}">SMS</c:when>
			      <c:when test="${objStatistik.fuenfTonAusloesung}">5-Ton-Folgeruf</c:when>
			      <c:when test="${objStatistik.webAusloesung}">Web-Terminal</c:when>
			      <c:otherwise>Unbekannt</c:otherwise>
			    </c:choose>
			    </td>
			    <td></td>
			  </tr>
			  <tr class="list_head">
			    <td></td>
			    <td class="text_bold">GPS-Koordinaten</td>
			    <td class="text">
			    <c:choose>
			      <c:when test="${!empty objStatistik.alarm.gpsKoordinate}">${objStatistik.alarm.gpsKoordinate}</c:when>
			      <c:otherwise>Unbekannt</c:otherwise>
			    </c:choose>
			    </td>
			    <td></td>
			  </tr>
			  <tr class="list_head">
			    <td></td>
			    <td class="text_bold">Ausl&ouml;sungstext</td>
			    <td class="text">
			    <c:choose>
			      <c:when test="${objStatistik.fuenfTonAusloesung == false}">${objStatistik.alarm.kommentar}</c:when>
			      <c:otherwise>--- F&uuml;nfton ---</c:otherwise>
			    </c:choose>
			    </td>
			    <td></td>
			  </tr>
			  <tr class="list_head">
			    <td></td>
			    <td class="text_bold">Alarm ist aktiv: </td>
			    <td class="text">
			      <span class="" id="alarm_aktiv">
			        <c:choose>
			          <c:when test="${objStatistik.alarm.aktiv == true}">
			            Ja
			          </c:when>
			          <c:otherwise>
			            Nein
			          </c:otherwise>
			        </c:choose>
			      </span>
			    </td>
			    <td></td>
			  </tr>
			  <tr class="list_head">
			    <td></td>
			    <td class="text_bold">Status: </td>
			    <td class="text">
			    	Ja: 
		      		<span class="green" id="antworten_ja">
		      		  <span class="text">-</span>
	      			</span>&nbsp;
			    	Nein: 
		      		<span class="red" id="antworten_nein">
		      		  <span class="text">-</span>
	      			</span>&nbsp;
	      			
			    	Sp&auml;ter: 
		      		<span class="blue" id="antworten_spaeter">
		      		  <span class="text">-</span>
	      			</span>&nbsp;
	      			
			    	Unbekannt: 
		      		<span class="grey" id="antworten_unbekannt">
		      		  <span class="text">-</span>
		      		</span>			
			    </td>
			    <td>
			    </td>
			  </tr>
			  <tr class="v_spacer">
			  <td colspan="4"></td>
			  </tr>
			</table>
		</form>
		<div id="divContent"></div>

		<table>
		  <tr class="v_spacer">
		    <td></td>
		  </tr>
		</table>
		
		<table class="popup_base_large">
		  <tr class="list_head">
		    <td>
				<table>
				  <tr>
				    <td class="h_spacer"></td>
				    <td><input name="Submit" type="reset" class="button" onClick="javascript:window.history.back()" value="Zur&uuml;ck"></td>
				    <td class="h_spacer"></td>
				    <td><input name="Submit" type="reset" class="button" onClick="javascript:window.close()" value="Schlie&szlig;en"></td>
				  </tr>
				</table>
			</td>
		  </tr>
		</table>
