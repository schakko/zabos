<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="zabos" uri="http://ecw.de/taglibs/zabos"%>
<%@ page language="java" contentType="text/html; charset=utf-8"
    pageEncoding="UTF-8"%>
    
	  <script type="text/javascript">
	  /**
	  *	Dialog zum Bestätigen des Löschens
	  */
	  	function confirmDeleteFunktionstraeger() {
	  		var answer = confirm("Wenn Sie einen Funktionsträger löschen werden alle zugeordneten Benutzer ihre Zuweisung verlieren. Bereiche mit der Funktionsträger/Schleifen-Kombination werden entfernt.")
	  		if (answer){
	  			document.forms.frmEditFunktionstraeger.action='<zabos:url url="controller/system/"/>?submit=true&do=doDeleteFunktionstraeger&tab=funktionstraeger&FunktionstraegerId=${objFunktionstraeger.baseId}';
		  		document.forms.frmEditFunktionstraeger.submit();
		  	}
	  	}
	  </script>
	  
      <div id="funktionstraeger" title="Konfigurieren Sie hier die vorhandenen Funktionsträger" class="setting" style="display:none">
		<c:if test="${sessionScope.user.accessControlList.funktionstraegerFestlegenErlaubt}">
		<form method="post" action="<zabos:url url="controller/system/?tab=funktionstraeger" />" name="frmSelectFunktionstraeger" id="frmSelectFunktionstraeger">
			<table class="popup_base">
			  <tr class="list_head">
			    <td class="h_spacer"></td>
			    <td class="dropdown">Vorhandene Funktionsträger:
			      <select title="Wählen Sie hier aus, ob sie einen vorhandenen Funktionstraeger bearbeiten oder einen neuen anlegen wollen." id="FunktionstraegerId" name="FunktionstraegerId" class="helpEvent input" onchange="document.forms.frmSelectFunktionstraeger.submit()">
			      <option value="0" <c:if test="${empty objFunktionstraeger}">selected</c:if>>&lt;Neuer Funktionsträger&gt;</option>
			      <c:forEach items="${arrFunktionstraegerAvailable.data}" var="data">
			        <c:if test="${data.geloescht == false}">
			          <option value="<c:out value="${data.baseId}"/>" <c:if test="${data.baseId == objFunktionstraeger.baseId}">selected</c:if>><c:out value="${data.kuerzel}"/> - <c:out value="${data.beschreibung}"/></option>
			        </c:if>
			      </c:forEach>
			      </select>
	
			    </td>
			  </tr>
			  <tr class="v_spacer">
			    <td></td>
			    <td></td>
			  </tr>
			</table>
        </form>
		<!-- Bereich: Ende -->
		<form name="frmEditFunktionstraeger" id="frmEditFunktionstraeger" action="" onKeyUp="Aenderung(this.id)" method="post">
		<input type="hidden" name="textFunktionstraegerId" value="<c:out value="${objFunktionstraeger.baseId}" default="0" />" />
		<table class="popup_base">
		  <tr class="list_head">
		    <td class="h_spacer"></td>
		    <td>Funktionsträger:</td>
		  </tr>
		  <tr class="v_spacer">
				<td></td>
				<td></td>
			</tr>
		    <tr>
		      <td></td>
		      <td><table class="popup_inner">
		          <tr>
		            <td class="text inner_option">Kürzel</td>
		            <td><input name="textKuerzel" type="text" title="Geben Sie den Namen des Funktionsträgers ein." class="helpEvent input" value="<c:out value="${objFunktionstraeger.kuerzel}" />" size="30">
		            </td>
		          </tr>
		          <tr>
		            <td class="text inner_option">Beschreibung</td>
		            <td class="text"><!--  input class="helpEvent" title="Markieren Sie diese Checkbox, damit die angegebene Telefonnummer von dem System benutzt wird." type="checkbox" name="isAktiv" value="checkbox" <c:if test="${(objTelefon.aktiv) || (empty objTelefon)}">checked</c:if>>
		            Ist aktiv --> 
		            <input name="textBeschreibung" type="text" title="Geben Sie hier eine Beschreibung für den Funktionsträger ein." class="helpEvent input" value="<c:out value="${objFunktionstraeger.beschreibung}" />" size="30">
		            </td>
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
							<c:if test="${sessionScope.user.accessControlList.funktionstraegerFestlegenErlaubt}">						
								<div align="left" id="controlButtons">
									<input title="Klicken Sie auf 'Speichern' um Ihre Änderungen zu speichern" name="inputSave" type="submit" class="helpEvent button" value="Speichern" onclick="document.forms.frmEditFunktionstraeger.action='<zabos:url url="controller/system/"/>?submit=true&amp;do=doUpdateFunktionstraeger&amp;tab=funktionstraeger&amp;FunktionstraegerId=${objFunktionstraeger.baseId}';document.forms.frmEditFunktionstraeger.submit()">
								</div>
							</c:if>
						</td>
						<td>
							<c:if test="${sessionScope.user.accessControlList.funktionstraegerFestlegenErlaubt}">						
								<div align="center">
									<input title="Klicken Sie auf 'Zurücksetzen' um Ihre Änderungen rückgängig zu machen." name="inputCancel" type="submit" onclick="document.forms.frmEditFunktionstraeger.reset()" class="helpEvent button" value="Zurücksetzen">
								</div>
							</c:if>
						</td>
							<c:if test="${!empty objFunktionstraeger}">
								<td>
									<c:if test="${sessionScope.user.accessControlList.funktionstraegerFestlegenErlaubt}">								
										<div align="right">
											<input title="Klicken Sie auf 'Entfernen' um den gewählten Funktionsträger zu löschen" name="inputDelete" type="submit" class="helpEvent button" value="Entfernen" onclick="confirmDeleteFunktionstraeger();">
										</div>
									</c:if>
								</td>
							</c:if>
						<td class="h_spacer"></td>
					</tr>
				</table>
			</td>
		  </tr>
		</table>	
		</c:if>
	  </div>