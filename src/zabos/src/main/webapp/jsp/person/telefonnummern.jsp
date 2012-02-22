<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="zabos" uri="http://ecw.de/taglibs/zabos"%>
<%@ page language="java" contentType="text/html; charset=utf-8"
    pageEncoding="UTF-8"%>
      <div id="telefonnummern" title="Wählen Sie hier ein Nummer aus, deren Daten Sie bearbeiten wollen, oder legen sie eine neue Nummer an." class="setting" style="display:none">
		<c:if test="${sessionScope.user.accessControlList.personAendernErlaubt || (sessionScope.user.accessControlList.eigeneTelefoneAendernErlaubt && (objPerson.personId.longValue == sessionScope.user.person.personId.longValue))}">
		<form method="post" action="<zabos:url url="controller/person/?tab=telefonnummern&amp;PersonId="/>${objPerson.baseId}" name="frmSelectTelefon" id="frmSelectTelefon">
			<table class="popup_base">
			  <tr class="list_head">
			    <td class="h_spacer"></td>
			    <td class="dropdown">Telefone dieser Person:
			      <select title="Wählen Sie hier aus, ob sie eine vorhandenes Telefon bearbeiten oder eine neues anlegen wollen." id="TelefonId" name="TelefonId" class="helpEvent input" onchange="document.forms.frmSelectTelefon.submit()">
			      <option value="0" <c:if test="${empty objTelefon}">selected</c:if>>&lt;Neues Telefon&gt;</option>
			      <c:forEach items="${arrTelefoneAvailable.data}" var="data">
			        <c:if test="${data.geloescht == false}">
			          <option value="<c:out value="${data.baseId}"/>" <c:if test="${data.baseId == objTelefon.baseId}">selected</c:if>><c:out value="${data.nummer}"/></option>
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
		<form name="frmEditTelefon" id="frmEditTelefon" action="" onKeyUp="Aenderung(this.id)" method="post">
		<input type="hidden" name="TelefonId" value="<c:out value="${objTelefon.baseId}" default="0" />" />
		<table class="popup_base">
		  <tr class="list_head">
		    <td class="h_spacer"></td>
		    <td>Telefon:</td>
		  </tr>
		  <tr class="v_spacer">
				<td></td>
				<td></td>
			</tr>
		    <tr>
		      <td></td>
		      <td><table class="popup_inner">
		          <tr>
		            <td class="text inner_option">Nummer</td>
		            <td><input name="textNummer" type="text" title="Geben Sie hier die Mobilfunknummer ein für den Empfang und dem Versand der SMS-Nachrichten ein." class="helpEvent input" value="<c:out value="${objTelefon.nummer}" default="0049"/>" size="30">
		            </td>
		          </tr>
		          <tr>
		            <td class="text inner_option">&nbsp;</td>
		            <td class="text"><input class="helpEvent" title="Markieren Sie diese Checkbox, damit die angegebene Telefonnummer von dem System benutzt wird." type="checkbox" name="isAktiv" value="checkbox" <c:if test="${(objTelefon.aktiv) || (empty objTelefon)}">checked</c:if>>
		            Ist aktiv</td>
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
							<c:if test="${sessionScope.user.accessControlList.personAendernErlaubt || (sessionScope.user.accessControlList.eigeneTelefoneAendernErlaubt && (objPerson.personId.longValue == sessionScope.user.person.personId.longValue))}">						
								<div align="left" id="controlButtons">
									<input title="Klicken Sie auf 'Speichern' um Ihre Änderungen zu speichern" name="inputSave" type="submit" class="helpEvent button" value="Speichern" onclick="document.forms.frmEditTelefon.action='<zabos:url url="controller/person/"/>?submit=true&amp;do=doUpdateTelefon&amp;tab=telefonnummern&amp;PersonId=${objPerson.baseId}';document.forms.frmEditTelefon.submit()">
								</div>
							</c:if>
						</td>
						<td>
							<c:if test="${sessionScope.user.accessControlList.personAendernErlaubt || (sessionScope.user.accessControlList.eigeneTelefoneAendernErlaubt && (objPerson.personId.longValue == sessionScope.user.person.personId.longValue))}">						
								<div align="center">
									<input title="Klicken Sie auf 'Zurücksetzen' um Ihre Änderungen rückgängig zu machen." name="inputCancel" type="submit" onclick="document.forms.frmEditTelefon.reset()" class="helpEvent button" value="Zurücksetzen">
								</div>
							</c:if>
						</td>
							<c:if test="${!empty objTelefon}">
								<td>
									<c:if test="${sessionScope.user.accessControlList.personAnlegenLoeschenErlaubt || (sessionScope.user.accessControlList.eigeneTelefoneAendernErlaubt && (objPerson.personId.longValue == sessionScope.user.person.personId.longValue))}">								
										<div align="right">
											<input title="Klicken Sie auf 'Entfernen' um das gewählte Telefon zu löschen" name="inputDelete" type="submit" class="helpEvent button" value="Entfernen" onclick="document.forms.frmEditTelefon.action='<zabos:url url="controller/person/"/>?submit=true&amp;do=doDeleteTelefon&amp;tab=telefonnummern&amp;PersonId=${objPerson.baseId}';document.forms.frmEditTelefon.submit()">
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