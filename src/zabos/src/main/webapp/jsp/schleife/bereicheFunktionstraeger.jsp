<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="zabos" uri="http://ecw.de/taglibs/zabos"%>
<%@ page language="java" contentType="text/html; charset=utf-8"
	pageEncoding="UTF-8"%>
<div id="bereicheFunktionstraeger"
	title="Hier können Sie die Bereiche und Funktionsträger der Schleife festlegen."
	class="setting" style="display: none">
	<c:if test="${sessionScope.user.accessControlList.schleifeAendernErlaubt}">          			
	
	<!--  Blindcontainer für das Anfügen neuer Zuweisungen -->
	<div id="bereicheFunktionstraegerNeu" style="visibility:hidden;">
		<input type="hidden" name="arrBereichInSchleifeAvailable[]" value="0" />
		<div style="padding-bottom:5px;">
			<span class="inner_option text">
				<select name="selectBereich[]">
					<c:forEach items="${arrBereicheAvailable.data}" var="bereich">
						<option value="${bereich.baseId}" >${bereich.name}</option>
					</c:forEach>
				</select>	
			</span>
			<span class="inner_option text">
				<select name="selectFolgeschleife[]">
					<c:forEach items="${arrFunktionstraegerAvailable.data}" var="funktionstraeger">
						<option value="${funktionstraeger.baseId.longValue}" >${funktionstraeger.beschreibung} (${funktionstraeger.kuerzel})</option>
					</c:forEach>
				</select>
			</span>
			<span class="inner_option_short text">
				<input type="text" name="textSollstaerke[]" value="10" size="3" maxlength="5">
			</span>
			<span class="text">
				<input type="button" value="Entfernen" style="height:20xp;font-size:12px;" onClick="removeZuweisung(this);">
			</span>
		</div>
	</div>
	<!--  /Blindcontainer für das Anfügen neuer Zuweisungen -->
	<form name="frmEditBereichFunktionstraeger" id="frmEditBereichFunktionstraeger" 
		action=""
		onKeyUp=Aenderung(this.id);	method="post"><input type="hidden"
		name="SchleifeId"
		value="<c:out value="${objSchleife.baseId}" default="0"/>" />
	<table class="popup_base">
		<tr class="list_head">
			<td class="h_spacer"></td>
			<td>Einstellungen:</td>
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
					<th class="inner_option text_bold">Bereich</th>
					<th class="inner_option text_bold">Funktionsträger</th>
					<th class="inner_option_short text_bold">Soll</th>
					<th class="text_bold"></th>
				</tr>
			</table>
			
			<div id="bereicheContainer" style="min-height:50px;overflow:auto;">
				<c:if test="${empty arrBereichInSchleifeAvailable.data}">
					<div id="keineZuweisung" style="padding-bottom:10px;">
						Keine Zuweisung gefunden
					</div>
				</c:if>
				
				<c:forEach items="${arrBereichInSchleifeAvailable.data}" var="fassade">
				<div>
					<div style="padding-bottom:5px;">
						<input type="hidden" name="arrBereichInSchleifeAvailable[]" value="<c:out value="${fassade.bereichInSchleife.baseId.longValue}" />" />
						<span class="inner_option text">
							<select name="selectBereich[]">
								<c:forEach items="${arrBereicheAvailable.data}" var="bereich">
									<option value="${bereich.baseId.longValue}" <c:if test="${bereich.baseId.longValue == fassade.bereich.bereichId.longValue}">selected</c:if>>${bereich.name}</option>
								</c:forEach>
							</select>	
						</span>
						<span class="inner_option text">
							<select name="selectFolgeschleife[]">
								<c:forEach items="${arrFunktionstraegerAvailable.data}" var="funktionstraeger">
									<option value="${funktionstraeger.baseId.longValue}" <c:if test="${funktionstraeger.baseId.longValue == fassade.funktionstraeger.funktionstraegerId.longValue}">selected</c:if>>${funktionstraeger.beschreibung} (${funktionstraeger.kuerzel})</option>
								</c:forEach>
							</select>
						</span>
						<span class="inner_option_short text">
							<input type="text" name="textSollstaerke[]" value="${fassade.bereichInSchleife.sollstaerke}" size="3" maxlength="5">
						</span>
						<span class="text">
							<input type="button" value="Entfernen" style="height:20xp;font-size:12px;" onClick="removeZuweisung(this);">
						</span>
					</div>	
				</div>
				</c:forEach>
			</div>
			<div class="inner_option text">
				<input type="button" value="Neue Zuweisung" onClick="addZuweisung();"/>
			</div>	
			</td>
		</tr>
	</table>
	</form>
	
	<table class="popup_base">
			<tr class="v_spacer"><td colspan="2"></td></tr>
			<tr class="list_head">
				<td class="h_spacer"></td>
				<td>Personen, die in dieser Schleife alarmiert werden</td>
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
						<td class="inner_option_large text_bold">Funktionsträger - Bereich</td>
						<td class="text_bold">Personen
						</td>
				      </tr>
				    </table>
					
					<div class="scroll">
					  <table class="popup_inner_scroll">				    
						<%--
		  					Alle Personen anzeigen, die in der Schleife alarmiert werden
						--%>
						<c:forEach items="${arrBereichInSchleifeMitPersonenAvailable.data}" var="fassade">					  
							<c:if test="${!empty fassade.personen}">
							  <tr>
								<td class="inner_option_large text"> 
									<%--
								  		Wenn einer Bereich-Funktionsträger-Kombination keine Person zugeordnet ist, diese nicht anzeigen
									--%>
									${fassade.funktionstraeger.beschreibung} - ${fassade.bereich.name}
								</td>
								<td class="text">          
									<%--
			  							Alle Personen der Kombination anzeigen
									--%>
									<c:forEach items="${fassade.personen}" var="person">
										<div style="height:20px;vertical-align:center;" title="Damit eine Person alarmiert werden kann, muss Ihre zugeteilte Kombination aus Funktionsträger und Bereich auch hier in der Schleife eingetragen sein.">${person.displayName} </div>
									</c:forEach>
								</td>
						      </tr>
					     	</c:if>
						 </c:forEach>
					  </table>
					</div>
			  </td>				
			</tr>
			<tr class="v_spacer">
				<td></td>
				<td></td>
			</tr>
		</table>
	
	<!--  SPACER -->
	<table>
		<tr class="v_spacer">
			<td></td>
		</tr>
	</table>
	<!-- BEDIENELEMENTE -->
	<table class="popup_inner">
		<tr class="list_head">
			<td>
			<table class="popup_base">
				<tr>
					<td class="h_spacer"></td>
					<td><c:if
						test="${sessionScope.user.accessControlList.schleifeAendernErlaubt}">
						<div align="left"><input name="inputSave"
							title="Klicken Sie zum Übernehmen der Einstellungen auf 'Speichern'."
							type="submit" class="helpEvent button" value="Speichern"
							onclick="document.forms.frmEditBereichFunktionstraeger.action='<zabos:url url="controller/schleife/?do=doUpdateBereichFunktionstraeger&amp;submit=true&amp;tab=bereicheFunktionstraeger"/>';document.forms.frmEditBereichFunktionstraeger.submit()">
						</div>
					</c:if></td>
					<td><!-- <c:if
						test="${sessionScope.user.accessControlList.schleifeAendernErlaubt}">
						<div align="center"><input name="inputCancel"
							title="Wollen Sie die Änderungen verwerfen klicken Sie auf 'Abbrechen'."
							type="submit" onclick=
		document.forms.frmEditBereichFunktionstraeger.reset();
	class="helpEvent button" value="Zurücksetzen"></div>
					</c:if>--> </td>
	
					<c:if test="${!empty objSchleife}">
						<td><!-- <c:if
							test="${sessionScope.user.accessControlList.schleifeAnlegenLoeschenErlaubt}">
							<div align="right"><input name="inputDelete"
								title="Wollen Sie die Schleife löschen, klicken Sie auf 'Entfernen.'"
								type="submit" class="helpEvent button" value="Entfernen"
								onclick="document.forms.frmEditBereichFunktionstraeger.action='<zabos:url url="controller/schleife/?do=doUpdateBereichFunktionstraeger&amp;submit=true"/>';document.forms.frmEditBereichFunktionstraeger.submit()">
							</div>
						</c:if> --></td>
					</c:if>
					<td class="h_spacer"></td>
				</tr>
			</table>
			</td>
		</tr>
	</table>
	<!--  BESCHREIBUNG - BEDIENELEMENTE -->
	<table class="popup_inner">
		<tr>
			<td>
			<table class="popup_base">
				<tr class="v_spacer">
					<td class="h_spacer"></td>
					<td></td>
					<td></td>
				</tr>
				<tr>
					<td></td>
					<td class="text_info">Klicken Sie zum &Uuml;bernehmen der
					Einstellungen auf &quot;Speichern&quot;. <br>
					Wollen Sie die &Auml;nderungen verwerfen, klicken Sie auf
					&quot;Abbrechen&quot;.</td>
					<td></td>
				</tr>
				<tr class="v_spacer">
					<td></td>
					<td></td>
					<td></td>
				</tr>
			</table>
			</td>
		</tr>
	</table>
	</c:if>
</div>