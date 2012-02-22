<%@ page language="java" contentType="text/html; charset=utf-8"
    pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="zabos" uri="http://ecw.de/taglibs/zabos"%>

		<table class="popup_inner_large">
		  <tr class="head_normal">
		    <td>
		    	<table class="popup_base_large">
		        	<tr class="head_normal">
			          <th>Alarmierungshistorie</th>
			        </tr>
			    </table>
		    </td>
		  </tr>
		</table>

		<table class="popup_base_large">
		  <tr>
		    <td>
		      <table class="popup_inner_large">
			      <tr class="v_spacer">
				      <td colspan="6">
				      </td>
			      </tr>
			      <tr class="list_head">
			        <td colspan="6" class="text">
				        <center>
				        <form method="post" action="<zabos:url url="controller/report/?tab=history&amp;do=doSetHistorySettings&amp;submit=true"/>" name="frmSelectDatum" id="frmSelectDatum">

					        <input id="textDatum" type="text" name="textDatum" value="${requestScope.textDatum}" size="10" maxlength="10"/> 
					        <select name="selectDatumRahmen" onchange="submit()">
					          <option value="day_1"<c:if test="${param.selectDatumRahmen == 'day_1'}"> selected</c:if>>1 Tag</option>
					          <option value="day_2"<c:if test="${param.selectDatumRahmen == 'day_2'}"> selected</c:if>>2 Tage</option>
					          <option value="day_3"<c:if test="${param.selectDatumRahmen == 'day_3'}"> selected</c:if>>3 Tage</option>
					          <option value="day_4"<c:if test="${param.selectDatumRahmen == 'day_4'}"> selected</c:if>>4 Tage</option>
					          <option value="day_5"<c:if test="${param.selectDatumRahmen == 'day_5'}"> selected</c:if>>5 Tage</option>
					          <option value="day_6"<c:if test="${param.selectDatumRahmen == 'day_6'}"> selected</c:if>>6 Tage</option>
					          <option value="week_1"<c:if test="${param.selectDatumRahmen == 'week_1'}"> selected</c:if>>1 Woche</option>
					          <option value="week_2"<c:if test="${param.selectDatumRahmen == 'week_2'}"> selected</c:if>>2 Wochen</option>
					          <option value="week_3"<c:if test="${param.selectDatumRahmen == 'week_3'}"> selected</c:if>>3 Wochen</option>
					          <option value="week_4"<c:if test="${param.selectDatumRahmen == 'week_4'}"> selected</c:if>>4 Wochen</option>
					          <option value="month_1"<c:if test="${param.selectDatumRahmen == 'month_1'}"> selected</c:if>>1 Monat</option>
					          <option value="month_2"<c:if test="${param.selectDatumRahmen == 'month_2'}"> selected</c:if>>2 Monate</option>
					          <option value="month_3"<c:if test="${param.selectDatumRahmen == 'month_3'}"> selected</c:if>>3 Monate</option>
					        </select>
					        <input onClick="checkDate()" type="button" value="OK" />&nbsp;&nbsp;&nbsp;					        
					        <input type="button" name="btnStepPast" value="&lt;&lt;" onclick="document.forms.frmSelectDatum.action+='&amp;selectStep=past';document.forms.frmSelectDatum.submit()" />
					        <input type="button" name="btnStepFuture" value="&gt;&gt;" onclick="checkDateFuture()" />
							<br>
					        <zabos:formatts format="date" timeStamp="${tsDatumStart}"/>, <zabos:formatts format="time" timeStamp="${tsDatumStart}"/>h  bis <zabos:formatts format="date" timeStamp="${tsDatumEnd}"/>, <zabos:formatts format="time" timeStamp="${tsDatumEnd}"/>h
					      </form>
				        </center>
			        </td>
		        </tr>
				<tr class="v_spacer"><td colspan="6"></td></tr>
		      <tr class="list_head">
		        <td class="h_spacer"></td>
		        <td style="width:50px;" class="text_bold">ID</td>
		        <td style="width:110px;" class="text_bold">Zeit</td>
		        <td style="width:180px;" class="text_bold">Auslöser</td>
		        <td class="text_bold">Schleifen</td>
		        <td style="width:110px;" class="text_bold">Rückmeldung</td>
		      </tr>
		      <tr class="v_spacer">
		        <td></td>
		        <td></td>
		        <td></td>
		        <td></td>
		        <td></td>
		        <td></td>
		      </tr>
		      <c:forEach items="${arrAlarmeStatistiken}" var="data" varStatus="iteratorStatus">
		      <tr valign="top" class="alarmlist_<c:choose><c:when test="${iteratorStatus.count%2 == 0}">odd</c:when><c:otherwise>even</c:otherwise></c:choose>_row">
		      <td></td>
		        <td class="text_bold reportid">
		        <a href="<zabos:url url="controller/report/?tab=object&amp;AlarmId=${data.alarm.alarmId}"/>">
				        <SCRIPT type="text/javascript">
					        var hexid = ${data.alarm.reihenfolge};
					        hexid = hexid.toString(16);	        
					        document.write(hexid);
				        </SCRIPT>
					</a></td>
		        <td class="text_bold">
		        	<zabos:formatts format="time" timeStamp="${data.alarm.alarmZeit.timeStamp}"/><br>
					<span class="text"><zabos:formatts format="date" timeStamp="${data.alarm.alarmZeit.timeStamp}"/></span></td>
		        <td class="text">
		          <c:choose>
		            <c:when test="${data.fuenfTonAusloesung == false}">${data.person.displayName}</c:when>
		            <c:otherwise>Durch 5-Ton-Folgeruf</c:otherwise>
		          </c:choose>
		        </td>
		        <td class="text">
		        <c:forEach items="${data.schleifenStatistik}" var="schleifen">
		          ${schleifen.schleife.displayName}
		        </c:forEach>
		        </td>
		        <td class="text">
			        Ja: <span class="green">${data.rueckmeldeStatistik.numJa}</span><br/>
				    Nein: <span class="red">${data.rueckmeldeStatistik.numNein}</span><br />
				    Sp&auml;ter: <span class="blue">${data.rueckmeldeStatistik.numSpaeter}</span><br />
				    Unbekannt: <span class="grey">${data.rueckmeldeStatistik.numUnbekannt}</span><br />
		        </td>
		      </tr>
		      </c:forEach>
		    </table>
		  </tr>
		</table>