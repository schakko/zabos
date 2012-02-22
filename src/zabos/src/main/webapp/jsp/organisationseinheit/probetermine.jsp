<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="zabos" uri="http://ecw.de/taglibs/zabos"%>
<%@ page language="java" contentType="text/html; charset=utf-8"
    pageEncoding="UTF-8"%>
<% 
	long aktuelleZeit = new java.util.Date().getTime();  
	pageContext.setAttribute("aktuelleZeit",""+aktuelleZeit);
%>
      <div id="probetermine" title="Sie können hier Probetermine für Ihre Einheit erstellen. Während dieser Termine werden Alarme über 5-Ton nicht weitergeleitet." class="setting" style="display:none">
		<script language="javascript" type="text/javascript">
		  function setSchedulerContent() {
		  	var layerToShow = "", obj, objToUse;
		  	layerToShow = "div_"+document.forms["frmAddScheduledDates"].idType.options[document.forms["frmAddScheduledDates"].idType.selectedIndex].value;
		  	objToUse = document.getElementById(layerToShow);
		
		  	if (null != objToUse) {
		      document.getElementById("divContent").innerHTML = objToUse.innerHTML;
		      document.getElementById("divSubmit").style.display = "block";
		    } else {
		      document.getElementById("divSubmit").style.display = "none";
		      document.getElementById("divContent").innerHTML = "";
		    }
		    setHelpByClass();
		  }
		</script>
		<form method="post" action="<zabos:url url="controller/organisationseinheit/?tab=probetermine&amp;do=doAddProbetermine&amp;submit=true"/>" id="frmAddScheduledDates" name="frmAddScheduledDates">

<c:if test="${sessionScope.user.accessControlList.probealarmAdministrierenErlaubt}">

		<table class="popup_base">
		  <tr class="list_head">
		    <td class="h_spacer"></td>
		    <td class="dropdown">Probetermine erstellen:
		   	<select name="idType" title="Klicken Sie hier, um einen neuen Probetermin anzulegen. Bestimmen Sie durch Auswahl eines Types die Art des Alarms." class="helpEvent" id="idType" onchange="setSchedulerContent()">
		   	  <option value="">Bitte ausw&auml;hlen</option> 
		      <option value="1">Einmalig</option>
		      <option value="2">T&auml;glich</option>
		      <option value="3">W&ouml;chentlich</option>
		      <option value="4">Monatlich</option>
		    </select>
		    </td>
		  </tr>
		  <tr class="v_spacer">
		    <td colspan="2"></td>
		  </tr>
		</table>
		
</c:if>
		<div id="divContent">
		</div>

		<div id="divSubmit" style="display:none">
		<table class="inner">
				<tr>
					<td>
						<input title="Klicken Sie hier um die Einstellungen zu speichern." type="submit" class="helpEvent button" value="Speichern"/>
					</td>					
				</tr>
				<tr class="v_spacer">
					<td>
					</td>					
				</tr>
			</table>
		</div>
		</form>
		
		<div id="div_1" style="display:none">
		  <table class="inner">
		    <tr>
		      <td colspan="4" class="text">am
		        <input title="Geben Sie hier das Datum des einmaligen Termins im Format Tag-Monat-Jahr an, z.B. '16.01.2009'." name="textDatum" type="text" class="helpEvent input_short" id="textEinmaligDatum" value="DD.MM.YYYY" size="10" maxlength="10">
		        von 
		        <input title="Geben Sie hier an, wann der Probealarm beginnen soll, z.B. '13:00'." name="textZeitStart" id="textZeitStart" type="text" class="helpEvent input_short" value="HH:MM" size="5" maxlength="5">
		        bis
		        <input title="Geben Sie hier an, wann der Probealarm enden soll, z.B. '13:05'." name="textZeitEnde" id="textZeitEnde" type="text" class="helpEvent input_short" value="HH:MM" size="5" maxlength="5"> Uhr
		      </td>
		    </tr>
		    <tr>
		      <td colspan="6" scope="row">&nbsp;</td>
		    </tr>
		  </table>
		</div>
		<div id="div_2" style="display:none">
			<table>
				<tr>
					<td colspan="4">Jeden: </td>
				</tr>			
				<tr class="v_spacer">
					<td colspan="4"></td>
				</tr>
			  <tr>
			    <td class="text"><input name="checkMontag" type="checkbox" id="checkMontag" value="checkMontag">
			      Montag</td>
			    <td class="text"><input name="checkDienstag" type="checkbox" id="checkDienstag" value="checkDienstag">
			      Dienstag</td>
			    <td class="text"><input name="checkMittwoch" type="checkbox" id="checkMittwoch" value="checkMittwoch">
			      Mittwoch</td>
			    <td class="text"><input name="checkDonnerstag" type="checkbox" id="checkDonnerstag" value="checkDonnerstag">
			      Donnerstag</td>
			  </tr>
			  <tr>
			    <td class="text"><input name="checkFreitag" type="checkbox" id="checkFreitag" value="checkFreitag">
			      Freitag</td>
			    <td class="text"><input name="checkSamstag" type="checkbox" id="checkSamstag" value="checkSamstag">
			      Samstag</td>
			    <td class="text"><input name="checkSonntag" type="checkbox" id="checkSTaeglichonntag" value="checkSonntag">
			      Sonntag</td>
			    <td>&nbsp;</td>
			  </tr>
				<tr class="v_spacer">
					<td colspan="4"></td>
				</tr>
			  <tr>
			    <td colspan="4" class="text">
		        von 
		        <input title="Geben Sie hier an, wann der Probealarm beginnen soll, z.B. '13:00'." class="helpEvent" name="textZeitStart" id="textZeitStart" type="text" class="input_short" value="HH:MM" size="5" maxlength="5">
		        bis
		        <input title="Geben Sie hier an, wann der Probealarm enden soll, z.B. '13:05'." class="helpEvent" name="textZeitEnde" id="textZeitEnde" type="text" class="input_short" value="HH:MM" size="5" maxlength="5"> Uhr
			    </td>
			  </tr>
			  	<tr class="v_spacer">
					<td colspan="4"></td>
				</tr>	
			  <tr>
			    <td class="text" colspan="6">
					<table class="popup_base">
						<tr class="list_head">
						    <td class="h_spacer">
						    </td>
						    <td>
					    		Dauer des Termins: 
							</td>
						</tr>
						<tr class="v_spacer">
							<td colspan="2"></td>
						</tr>	
						<tr>
							<td></td>
							<td>
								<table class="inner">
									<tr>
										<td colspan="3">
											Dieser Termin ist gültig in dem folgenden Zeitraum:
										</td>
									</tr>
									<tr class="v_spacer">
										<td colspan="3">
										</td>
									</tr>
									<tr>
										<td>
											Von Monat 
										</td>
										<td>
											<select title="Geben Sie hier an, ab wann der Probealarm gültig sein soll." class="helpEvent" id="selectMonthStart" name="selectMonthStart">
												<option value="0">Januar</option> 
												<option value="1">Februar</option>
												<option value="2">März</option>
												<option value="3">April</option>
												<option value="4">Mai</option>
												<option value="5">Juni</option>
												<option value="6">Juli</option>
												<option value="7">August</option>
												<option value="8">September</option>
												<option value="9">Oktober</option>
												<option value="10">November</option>
												<option value="11">Dezember</option>
											</select>
										</td>
										<td>
									    	<select title="Geben Sie hier an, ab wann der Probealarm gültig sein soll." class="helpEvent" id="selectYearStart" name="selectYearStart">
									        	<c:forEach items="${alYears}" var="year">
									        		<option value="${year}">${year}</option>
									        	</c:forEach>
											</select>
										</td>
									</tr>
									<tr>
							      		<td>
									      bis Monat
										</td>
										<td>
											<select title="Geben Sie hier an, bis wann der Probealarm gültig sein soll." class="helpEvent" id="selectMonthEnd" name="selectMonthEnd">
												<option value="0">Januar</option>
												<option value="1">Februar</option>
												<option value="2">März</option>
												<option value="3">April</option>
												<option value="4">Mai</option>
												<option value="5">Juni</option>
												<option value="6">Juli</option>
												<option value="7">August</option>
												<option value="8">September</option>
												<option value="9">Oktober</option>
												<option value="10">November</option>
												<option value="11">Dezember</option>
											</select>
										</td>
										<td>
											<select title="Geben Sie hier an, bis wann der Probealarm gültig sein soll." class="helpEvent" id="selectYearEnd" name="selectYearEnd">
												<c:forEach items="${alYears}" var="year">
													<option value="${year}">${year}</option>
												</c:forEach>
											</select>
										</td>
									</tr>
								</table>
							</td>
						</tr>
					</table>
			    </td>
			  </tr>
			  <tr>
			    <td colspan="6" scope="row">&nbsp;</td>
			  </tr>
			</table>
		</div>
		<div id="div_3" style="display:none">
			<table width="450">
			  <tr>
			    <td colspan="4" class="text">Jeden: 
			      <input name="checkErsten" type="checkbox" id="checkErsten" value="checkErsten">
			      1.
			      <input name="checkZweiten" type="checkbox" id="checkZweiten" value="checkZweiten"> 
			      2. 
			      <input name="checkDritten" type="checkbox" id="checkDritten" value="checkDritten">
			      3. 
			      <input name="checkVierten" type="checkbox" id="checkVierten" value="checkVierten">
			      4. 
			      <input name="checkLetzten" type="checkbox" id="checkLetzten" value="checkLetzten">
			      letzten </td>
			    </tr>
				<tr class="v_spacer">
					<td colspan="4"></td>
				</tr>
			  <tr>
			    <td class="text"><input name="checkMontag" type="checkbox" id="checkMontag" value="checkMontag">
			      Montag</td>
			    <td class="text"><input name="checkDienstag" type="checkbox" id="checkDienstag" value="checkDienstag">
			      Dienstag</td>
			    <td class="text"><input name="checkMittwoch" type="checkbox" id="checkMittwoch" value="checkMittwoch">
			      Mittwoch</td>
			    <td class="text"><input name="checkDonnerstag" type="checkbox" id="checkDonnerstag" value="checkDonnerstag">
			      Donnerstag</td>
			  </tr>			  
			  <tr>
			    <td class="text"><input name="checkFreitag" type="checkbox" id="checkFreitag" value="checkFreitag">
			      Freitag</td>
			    <td class="text"><input name="checkSamstag" type="checkbox" id="checkSamstag" value="checkSamstag">
			      Samstag</td>
			    <td class="text"><input name="checkSonntag" type="checkbox" id="checkSonntag" value="checkSonntag">
			      Sonntag</td>
			    <td>&nbsp;</td>
			  </tr>
				<tr class="v_spacer">
					<td colspan="4"></td>
				</tr>
			  <tr>
			    <td colspan="4" class="text">
		        von 
		        <input name="textZeitStart" id="textZeitStart" type="text" class="input_short" value="HH:MM" size="5" maxlength="5">
		        bis
		        <input name="textZeitEnde" id="textZeitEnde" type="text" class="input_short" value="HH:MM" size="5" maxlength="5"> Uhr
			    </td>
		    
			  </tr>
				<tr class="v_spacer">
					<td colspan="5"></td>
				</tr>
			  <tr>
			    <td class="text" colspan="6">
			      					<table class="popup_base">
						<tr class="list_head">
						    <td class="h_spacer">
						    </td>
						    <td>
					    		Dauer des Termins: 
							</td>
						</tr>
						<tr class="v_spacer">
							<td colspan="2"></td>
						</tr>	
						<tr>
							<td></td>
							<td>
								<table class="inner">
									<tr>
										<td colspan="3">
											Dieser Termin ist gültig in dem folgenden Zeitraum:
										</td>
									</tr>
									<tr class="v_spacer">
										<td colspan="3">
										</td>
									</tr>
									<tr>
										<td>
											Von Monat 
										</td>
										<td>
											<select title="Geben Sie hier an, ab wann der Probealarm gültig sein soll." class="helpEvent" id="selectMonthStart" name="selectMonthStart">
												<option value="0">Januar</option>
												<option value="1">Februar</option>
												<option value="2">März</option>
												<option value="3">April</option>
												<option value="4">Mai</option>
												<option value="5">Juni</option>
												<option value="6">Juli</option>
												<option value="7">August</option>
												<option value="8">September</option>
												<option value="9">Oktober</option>
												<option value="10">November</option>
												<option value="11">Dezember</option>
											</select>
										</td>
										<td>
									    	<select title="Geben Sie hier an, ab wann der Probealarm gültig sein soll." class="helpEvent" id="selectYearStart" name="selectYearStart">
									        	<c:forEach items="${alYears}" var="year">
									        		<option value="${year}">${year}</option>
									        	</c:forEach>
											</select>
										</td>
									</tr>
									<tr>
							      		<td>
									      bis Monat
										</td>
										<td>
											<select title="Geben Sie hier an, bis wann der Probealarm gültig sein soll." class="helpEvent" id="selectMonthEnd" name="selectMonthEnd">
												<option value="0">Januar</option>
												<option value="1">Februar</option>
												<option value="2">März</option>
												<option value="3">April</option>
												<option value="4">Mai</option>
												<option value="5">Juni</option>
												<option value="6">Juli</option>
												<option value="7">August</option>
												<option value="8">September</option>
												<option value="9">Oktober</option>
												<option value="10">November</option>
												<option value="11">Dezember</option>
											</select>
										</td>
										<td>
											<select title="Geben Sie hier an, bis wann der Probealarm gültig sein soll." class="helpEvent" id="selectYearEnd" name="selectYearEnd">
												<c:forEach items="${alYears}" var="year">
													<option value="${year}">${year}</option>
												</c:forEach>
											</select>
										</td>
									</tr>
								</table>
							</td>
						</tr>
					</table>
			    </td>
			  </tr>			  
			  <tr>
			    <td colspan="6" scope="row">&nbsp;</td>
			    </tr>
			</table>
		</div>
		<div id="div_4" style="display:none">
			<table width="450">
			  <tr>
			    <td colspan="4" class="text">am 
			      <input name="textDatum" id="textDatum" type="text" class="input_short" size="5" maxlength="2">
			      . Tag jeden Monats
		        von 
		        <input name="textZeitStart" id="textZeitStart" type="text" class="input_short" value="HH:MM" size="5" maxlength="5">
		        bis
		        <input name="textZeitEnde" id="textZeitEnde" type="text" class="input_short" value="HH:MM" size="5" maxlength="5"> Uhr
			      
			    </td>
			  </tr>
				<tr class="v_spacer">
					<td colspan="2"></td>
				</tr>
			  <tr>
			    <td class="text" colspan="6">
					<table class="popup_base">
						<tr class="list_head">
						    <td class="h_spacer">
						    </td>
						    <td>
					    		Dauer des Termins: 
							</td>
						</tr>
						<tr class="v_spacer">
							<td colspan="2"></td>
						</tr>	
						<tr>
							<td></td>
							<td>
								<table class="inner">
									<tr>
										<td colspan="3">
											Dieser Termin ist gültig in dem folgenden Zeitraum:
										</td>
									</tr>
									<tr class="v_spacer">
										<td colspan="3">
										</td>
									</tr>
									<tr>
										<td>
											Von Monat 
										</td>
										<td>
											<select title="Geben Sie hier an, ab wann der Probealarm gültig sein soll." class="helpEvent" id="selectMonthStart" name="selectMonthStart">
												<option value="0">Januar</option>
												<option value="1">Februar</option>
												<option value="2">März</option>
												<option value="3">April</option>
												<option value="4">Mai</option>
												<option value="5">Juni</option>
												<option value="6">Juli</option>
												<option value="7">August</option>
												<option value="8">September</option>
												<option value="9">Oktober</option>
												<option value="10">November</option>
												<option value="11">Dezember</option>
											</select>
										</td>
										<td>
									    	<select title="Geben Sie hier an, ab wann der Probealarm gültig sein soll." class="helpEvent" id="selectYearStart" name="selectYearStart">
									        	<c:forEach items="${alYears}" var="year">
									        		<option value="${year}">${year}</option>
									        	</c:forEach>
											</select>
										</td>
									</tr>
									<tr>
							      		<td>
									      bis Monat
										</td>
										<td>
											<select title="Geben Sie hier an, bis wann der Probealarm gültig sein soll." class="helpEvent" id="selectMonthEnd" name="selectMonthEnd">
												<option value="0">Januar</option>
												<option value="1">Februar</option>
												<option value="2">März</option>
												<option value="3">April</option>
												<option value="4">Mai</option>
												<option value="5">Juni</option>
												<option value="6">Juli</option>
												<option value="7">August</option>
												<option value="8">September</option>
												<option value="9">Oktober</option>
												<option value="10">November</option>
												<option value="11">Dezember</option>
											</select>
										</td>
										<td>
											<select title="Geben Sie hier an, bis wann der Probealarm gültig sein soll." class="helpEvent" id="selectYearEnd" name="selectYearEnd">
												<c:forEach items="${alYears}" var="year">
													<option value="${year}">${year}</option>
												</c:forEach>
											</select>
										</td>
									</tr>
								</table>
							</td>
						</tr>
					</table>
			    </td>
			  </tr>			  
			  <tr>
			    <td colspan="6" scope="row">&nbsp;</td>
			    </tr>
			</table>
		</div>
		<form method="post" id="frmProbetermine" action="<zabos:url url="controller/organisationseinheit/?do=doDeleteProbetermine&amp;submit=true&amp;tab=probetermine" />">

		<table class="popup_base">
		  <tr class="list_head">
		    <td class="h_spacer"></td>
		    <td>Eingetragene Probetermine</td>
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
		        <td class="text">
					Zu folgenden Terminen löst ZABOS beim Empfang von 5-Ton-Folgerufen der Schleifen der Organisationseinheit "${objOrganisationseinheit.name}" <span class="text_bold">keine</span> SMS-Alarme aus:
		        </td>
		      </tr>
		      <tr class="v_spacer">
		        <td></td>
		      </tr>		      
		    </table>
			<div class="scroll">
				<table class="popup_inner_scroll">
				<tr>
				<td>
				<div id="divAlteTermine" style="display:none"> 
					<table class="popup_inner_scroll">
						<tr class="list_head">
					    	<td class="h_spacer"></td>
					    	<td>Vergangene Termine</td>
						</tr>
					</table>
					<table id="TimeTableAlt" class=popup_inner_scroll>
						<tr class="v_spacer">
			        		<td colspan="5"></td>
		      			</tr>
						<c:forEach items="${arrProbetermineAvailable.data}" var="data">
				    		<c:if test="${data.start.timeStamp < aktuelleZeit}">
								<tr>
									<td class="text">
										<input type="checkbox" name="arrToDelete[]" value="${data.baseId}">
									</td>
									<td class="text">von
									</td>						
									<td class="text"> 
										<zabos:formatts sprintf="W d.m.Y H:i" timeStamp="${data.start.timeStamp}"/>
									</td>
									<td class="text">bis
									</td>							
									<td class="text">
										<zabos:formatts sprintf="W d.m.Y H:i" timeStamp="${data.ende.timeStamp}"/>
									</td>
								</tr>
							</c:if>
				  			<c:if test="${arrProbetermineAvailable.size == 0}">		    
								<tr>
									<td colspan="2" class="text_bold">Es sind keine Termine vorhanden</td>
					  			</tr>
			      			</c:if>
						</c:forEach>
						<tr>
							<td class="text">
								<input type="checkbox" name="chkMarkAllAlt" onClick="selectAllCheckbox('divAlteTermine',this);">
							</td>
							<td class="text" colspan="4"> alle markieren
							</td>
						</tr>					
						<tr class="v_spacer">
			        		<td colspan="5"></td>
		      			</tr>
					</table>
					</div>
				<div id="divZukunftsTermine"> 
					<table class="popup_inner_scroll">
						<tr class="list_head">
					    	<td class="h_spacer"></td>
					    	<td>Anstehende Termine</td>
						</tr>
					</table>
					<table id="TimeTable" class="popup_inner_scroll">
						<tr class="v_spacer">
			        		<td colspan="5"></td>
		      			</tr>
						<c:forEach items="${arrProbetermineAvailable.data}" var="data">
				    		<c:if test="${data.start.timeStamp > aktuelleZeit}">
								<tr>
									<td class="text">
										<input type="checkbox" name="arrToDelete[]" value="${data.baseId}">
									</td>
									<td class="text">von
									</td>						
									<td class="text"> 
										<zabos:formatts sprintf="W d.m.Y H:i" timeStamp="${data.start.timeStamp}"/>
									</td>
									<td class="text">bis
									</td>							
									<td class="text">
										<zabos:formatts sprintf="W d.m.Y H:i" timeStamp="${data.ende.timeStamp}"/>
									</td>
								</tr>
							</c:if>
				  			<c:if test="${arrProbetermineAvailable.size == 0}">		    
								<tr>
									<td colspan="2" class="text_bold">Es sind keine Termine vorhanden</td>
					  			</tr>
			      			</c:if>
						</c:forEach>
						<tr>
							<td class="text">
								<input type="checkbox" name="chkMarkAll" onClick="selectAllCheckbox('divZukunftsTermine',this);">
							</td>
							<td class="text" colspan="4"> alle markieren
							</td>
						</tr>
						<tr class="v_spacer">
			        		<td colspan="5"></td>
		      			</tr>
					</table>
					</div>
				  </td>
				  </tr>
				  <c:if test="${arrProbetermineAvailable.size == 0}">		    
					  <tr>
						<td colspan="2" class="text_bold">Es sind keine Termine vorhanden</td>
					  </tr>
			      </c:if>
		      <tr>
		        <td colspan="2"></td>
		      </tr>
		      <tr class="v_spacer">
		        <td></td>
		        <td></td>
		      </tr>
		    </table>
		    </div>
		    </td>
		  </tr>
		</table>
		
		<input type="submit" value="Ausgewählte Termine löschen" />
		<input id="btnAlteTermine" type="button" value="Vergangene Termine einblenden " onClick="showDivAlteTermine('divAlteTermine','btnAlteTermine','Vergangene Termine ausblenden','Vergangene Termine einblenden ')">

		<!--END-->

		</form>
	</div>
	