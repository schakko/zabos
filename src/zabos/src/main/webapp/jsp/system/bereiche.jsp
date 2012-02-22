<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="zabos" uri="http://ecw.de/taglibs/zabos"%>
<%@ page language="java" contentType="text/html; charset=utf-8"
    pageEncoding="UTF-8"%>
    
	  <script type="text/javascript">
	  /**
	  *	Dialog zum Bestätigen des Löschens
	  */
	  	function confirmDeleteBereich() {
	  		var answer = confirm("Wenn Sie einen Bereich oder Funktionsträger löschen werden alle zugeordneten Benutzer ihre Zuweisung verlieren. Bereiche mit der Funktionsträger/Schleifen-Kombination werden entfernt.")
	  		if (answer){
	  			document.forms.frmEditBereich.action='<zabos:url url="controller/system/"/>?submit=true&do=doDeleteBereich&tab=bereiche&BereichId=${objBereich.baseId}';
		  		document.forms.frmEditBereich.submit();
		  	}
	  	}
	  </script>
	  
      <div id="bereiche" title="Konfigurieren Sie hier die vorhandenen Bereiche" class="setting" style="display:none">
		<c:if test="${sessionScope.user.accessControlList.bereicheFestlegenErlaubt}">
		<form method="post" action="<zabos:url url="controller/system/?tab=bereiche" />" name="frmSelectBereich" id="frmSelectBereich">
			<table class="popup_base">
			  <tr class="list_head">
			    <td class="h_spacer"></td>
			    <td class="dropdown">Vorhandene Bereiche:
			      <select title="Wählen Sie hier aus, ob sie einen vorhandenes Bereiche bearbeiten oder einen neuen anlegen wollen." id="BereichId" name="BereichId" class="helpEvent input" onchange="document.forms.frmSelectBereich.submit()">
			      <option value="0" <c:if test="${empty objBereich}">selected</c:if>>&lt;Neuer Bereich&gt;</option>
			      <c:forEach items="${arrBereicheAvailable.data}" var="data">
			        <c:if test="${data.geloescht == false}">
			          <option value="<c:out value="${data.baseId}"/>" <c:if test="${data.baseId == objBereich.baseId}">selected</c:if>><c:out value="${data.name}"/></option>
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
		<form name="frmEditBereich" id="frmEditBereich" action="" onKeyUp="Aenderung(this.id)" method="post">
		<input type="hidden" name="textBereichId" value="<c:out value="${objBereich.baseId}" default="0" />" />
		<table class="popup_base">
		  <tr class="list_head">
		    <td class="h_spacer"></td>
		    <td>Bereich:</td>
		  </tr>
		  <tr class="v_spacer">
				<td></td>
				<td></td>
			</tr>
		    <tr>
		      <td></td>
		      <td><table class="popup_inner">
		          <tr>
		            <td class="text inner_option">Name</td>
		            <td><input name="textName" type="text" title="Geben Sie den Namen des Bereichs ein." class="helpEvent input" value="<c:out value="${objBereich.name}" />" size="30">
		            </td>
		          </tr>
		          <tr>
		            <td class="text inner_option">Beschreibung</td>
		            <td class="text"><!--  input class="helpEvent" title="Markieren Sie diese Checkbox, damit die angegebene Telefonnummer von dem System benutzt wird." type="checkbox" name="isAktiv" value="checkbox" <c:if test="${(objTelefon.aktiv) || (empty objTelefon)}">checked</c:if>>
		            Ist aktiv --> 
		            <input name="textBeschreibung" type="text" title="Geben Sie hier eine Beschreibung für den Bereich ein." class="helpEvent input" value="<c:out value="${objBereich.beschreibung}" />" size="30">
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
							<c:if test="${sessionScope.user.accessControlList.bereicheFestlegenErlaubt}">						
								<div align="left" id="controlButtons">
									<input title="Klicken Sie auf 'Speichern' um Ihre Änderungen zu speichern" name="inputSave" type="submit" class="helpEvent button" value="Speichern" onclick="document.forms.frmEditBereich.action='<zabos:url url="controller/system/"/>?submit=true&amp;do=doUpdateBereich&amp;tab=bereiche&amp;BereichId=${objBereich.baseId}';document.forms.frmEditBereich.submit()">
								</div>
							</c:if>
						</td>
						<td>
							<c:if test="${sessionScope.user.accessControlList.bereicheFestlegenErlaubt}">						
								<div align="center">
									<input title="Klicken Sie auf 'Zurücksetzen' um Ihre Änderungen rückgängig zu machen." name="inputCancel" type="submit" onclick="document.forms.frmEditBereich.reset()" class="helpEvent button" value="Zurücksetzen">
								</div>
							</c:if>
						</td>
							<c:if test="${!empty objBereich}">
								<td>
									<c:if test="${sessionScope.user.accessControlList.bereicheFestlegenErlaubt}">								
										<div align="right">
											<input title="Klicken Sie auf 'Entfernen' um den gewählten Bereich zu löschen" name="inputDelete" type="submit" class="helpEvent button" value="Entfernen" onclick="confirmDeleteBereich();">
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