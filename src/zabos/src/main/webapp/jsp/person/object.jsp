<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="zabos" uri="http://ecw.de/taglibs/zabos"%>
<%@ page language="java" contentType="text/html; charset=utf-8"
	pageEncoding="UTF-8"%>
<div id="object" title="Hier können Sie die Personendaten einstellen."
	class="setting" style="display: none">
	<c:choose>
		<c:when test="${sessionScope.user.accessControlList.personAendernErlaubt || (sessionScope.user.accessControlList.eigenePersonAendernErlaubt && (objPerson.personId.longValue == sessionScope.user.person.personId.longValue)) || (sessionScope.user.accessControlList.personAnlegenLoeschenErlaubt && (!objPerson))}">
			<c:set var="isPersonAenderbar" value="" scope="request" />
		</c:when>
		<c:otherwise>
			<c:set var="isPersonAenderbar" value="disabled" scope="request" />
		</c:otherwise>
	</c:choose>
	<c:choose>
		<c:when test="${sessionScope.user.accessControlList.personAnlegenLoeschenErlaubt && (!empty objPerson) && (objPerson.baseId != sessionScope.user.person.baseId)}">
			<c:set var="isPersonLoeschbar" value="true" scope="request" />
		</c:when>
		<c:otherwise>
			<c:set var="isPersonLoeschbar" value="false" scope="request" />
		</c:otherwise>
	</c:choose>
	<c:if
	test="${(empty isPersonAenderbar) || isPersonLoeschbar}">
	<form name="frmEditPerson" id="frmEditPerson" action=""
		onKeyUp="Aenderung(this.id)" method="post" accept-charset="UTF-8">
	<input type="hidden" name="PersonId"
		value="<c:out value="${objPerson.baseId}" default="0"/>" /> <c:if
		test="${!empty objKontextMitRolle}">
		<input type="hidden" name="bAssignPersonToRolle" value="1">
		<input type="hidden" name="intRolleId"
			value="${objKontextMitRolle.rolleId}" />
		<input type="hidden" name="intKontextId"
			value="${objKontextMitRolle.kontextId}" />
		<input type="hidden" name="intKontextType"
			value="${objKontextMitRolle.kontextType.id}" />
	</c:if>
	<table class="popup_base">
		<tr class="list_head">
			<td class="h_spacer"></td>
			<td>Personendaten:</td>
		</tr>
		<tr class="v_spacer">
			<td></td>
			<td></td>
		</tr>
		<c:if test="${objKontextMitRolle.kontextType.id > 0}">
			<tr>
				<td></td>
				<td>Die Person wird im Kontext
				${objKontextMitRolle.kontextType.name} <c:if
					test="${objKontextMitRolle.kontextType.id != 1}">"${objKontextMitRolle.kontextName}"</c:if>
				mit der Rolle ${objKontextMitRolle.rollenName} hinzugefügt.</td>
			</tr>
			<tr class="v_spacer">
				<td></td>
				<td></td>
			</tr>
		</c:if>
		<tr>
			<td></td>
			<td>
			<table class="popup_inner" id="tablePerson">
				<tr>
					<td class="text inner_option_large">Benutzer erstellt von</td>
					<td><c:if test="${objPersonErstelltVon != null}"><c:out value="${objPersonErstelltVon.displayName}" /></c:if></td>
				</tr>
				<tr>
					<td class="text inner_option_large">Vorname</td>
					<td><input title="Geben Sie hier den Vornamen der Person an."
						name="textVorname" type="text" class="helpEvent input"
						value="<c:out value="${objPerson.vorname}" default="${param.textVorname}"/>"
						size="30" ${isPersonAenderbar}></td>
				</tr>
				<tr>
					<td class="text inner_option_large">Nachname</td>
					<td><input title="Geben Sie hier den Nachnamen der Person an."
						name="textNachname" type="text" class="helpEvent input"
						value="<c:out value="${objPerson.nachname}" default="${param.textNachname}"/>"
						size="30"></td>
				</tr>
				<tr>
					<td class="text inner_option_large">Benutzername</td>
					<td class="text"><input
						title="Geben Sie hier einen eindeutigen Benutzernamen der Person an. Dieser wird verwendet um sich an das System anzumelden."
						id="textName" name="textName" type="text" class="helpEvent input"
						value="<c:out value="${objPerson.name}" default="${param.textName}"/>"
						size="30" ${isPersonAenderbar}></td>
				</tr>
				<tr>
					<td class="text inner_option_large">E-Mail</td>
					<td class="text"><input
						title="Geben Sie hier, soweit vorhanden, eine gültige E-Mail-Adresse der Person an."
						name="textEmail" id="textEmail" type="text"
						class="helpEvent input"
						value="<c:out value="${objPerson.email}" default="${param.textEmail}"/>"
						size="30" ${isPersonAenderbar}></td>
				</tr>
				<tr>
					<td class="text inner_option_large">Pin</td>
					<td class="text"><input name="textPin"
						title="Sofern gewünscht geben Sie hier eine PIN für die Person an. Diese wird zum Aulösen von Alarmen per SMS benötigt."
						id="textPin" type="text" class="helpEvent input"
						value="<c:out value="${objPerson.pin.pin}" default="${param.textPin}"/>"
						size="30" maxlength="8" ${isPersonAenderbar}></td>
				</tr>
				<tr>
					<td class="text inner_option_large">Beschreibung</td>
					<td class="text"><input
						title="Geben Sie hier einen Beschreibung für die Person an."
						name="textBeschreibung" id="textBeschreibung" type="text"
						class="helpEvent input"
						value="<c:out value="${objPerson.beschreibung}" default="${param.textBeschreibung}"/>"
						size="30" ${isPersonAenderbar}></td>
				</tr>
				<tr>
					<td class="text inner_option_large"></td>
					<td class="text"></td>
				</tr>
				<tr>
					<td class="text inner_option_large">Login in Web-Interface
					erlaubt</td>
					<td class="text"><input
						title="Markieren Sie die Checkbox, wenn die Person sich über das Internet in das System einloggen darf."
						name="isAbleToLoginInWebInterface"
						onClick="passwordUncheck('isAbleToLoginInWebInterface', 'textPassword','textPasswordC', 'PasswordConfirm', 'divSave');"
						id="isAbleToLoginInWebInterface" type="checkbox"
						class="helpEvent input" value="1"
						<c:if test="${!empty requestScope.isAbleToLoginInWebInterface}">checked</c:if>
						onchange="javascript:toggleActivationOnElement('isAbleToLoginInWebInterface','textPassword',false);toggleActivationOnElement('isAbleToLoginInWebInterface','textPasswordC',false)" 
						${isPersonAenderbar}/></td>
				</tr>
				<tr>
					<td class="text inner_option_large"><c:if
						test="${!empty objPerson}">Neues </c:if>Passwort f&uuml;r
					Web-Interface</td>
					<td class="text"><input
						title="Geben Sie hier ein Password ein. Dieses wird für den Zugriff über das Internet zusammen mit dem Benutzernamen benötigt."
						name="textPassword" id="textPassword"
						onkeyup="javascript:passwordConfirm('textPassword', 'textPasswordC','PasswordConfirm', 'divSave');"
						onblur="javascript:passwordConfirm('textPassword','textPasswordC','PasswordConfirm','divSave');" 
						type="password"
						class="helpEvent input" value="" size="30"
						<c:if test="${empty requestScope.isAbleToLoginInWebInterface || (!empty isPersonAenderbar)}">disabled</c:if>></td>
				</tr>
				<tr>
					<td class="text inner_option_large">Passwort wiederholen</td>
					<td class="text"><input name="textPasswordC"
						title="Wiederholen Sie hier das oben angegebene Paswort."
						id="textPasswordC"
						onkeyup="javascript:passwordConfirm('textPassword', 'textPasswordC','PasswordConfirm', 'divSave');"
						onblur="javascript:passwordConfirm('textPassword','textPasswordC','PasswordConfirm','divSave')" 
						type="password"
						class="helpEvent input" value="" size="30"
						<c:if test="${empty requestScope.isAbleToLoginInWebInterface || (!empty isPersonAenderbar)}">disabled</c:if>>
					</td>
				</tr>
				<tr>
					<td class="text inner_option_large"></td>
					<td class="text red">
					<div id="PasswordConfirm"></div>
					</td>
				</tr>
				<tr>
					<td class="text inner_option_large">Zuständige
					Organisationseinheit für Abrechnung der SMS</td>
					<td class="text"><select
						title="Geben Sie hier an, über welche Kostenstelle versendete SMS, die an diese Person gesendet wurden, abgerechnet werden."
						id="OEKostenstelleId" name="textOEKostenstelleId"
						class="helpEvent input"
						<c:if test="${!sessionScope.user.accessControlList.oeKostenstelleFestlegenErlaubt}">disabled</c:if>>
						<option value="0" <c:if test="${empty objPerson && empty param.OrganisationsEinheitId}">selected</c:if>>&lt;System&gt;</option>
						<c:forEach items="${arrOrganisationsEinheitenAvailable.data}" var="data">
							<c:if test="${data.geloescht == false}">
								<option value="<c:out value="${data.baseId}"/>"
									<c:choose>
							          <c:when test="${!empty objPerson.OEKostenstelle}">
							        	<c:if test="${data.baseId.longValue == objPerson.OEKostenstelle.longValue}">selected</c:if>
							          </c:when>
							          <c:otherwise>
							            <c:if test="${(data.baseId.longValue == param.textOEKostenstelleId) || (data.baseId.longValue == param.OrganisationsEinheitId)}">selected</c:if>
							          </c:otherwise>
				        			</c:choose>>
								<c:out value="${data.name}" /></option>
							</c:if>
						</c:forEach>
					</select></td>
				</tr>
				<tr>
					<td class="text inner_option_large">Funktionsträger</td>
					<td class="text"><select
						${isPersonAenderbar}
						title="Geben Sie hier an, welche Funktion die Person in der Wehr ausübt."
						id="FunktionstraegerId" name="textFunktionstraegerId"
						class="helpEvent input">
						<c:forEach items="${arrFunktionstraegerAvailable.data}" var="data">
							<option value="<c:out value="${data.baseId}"/>"
								<c:choose>
				        <c:when test="${!empty objPerson.funktionstraegerId}">
				      	  <c:if test="${data.baseId.longValue == objPerson.funktionstraegerId.longValue}">selected</c:if>
				        </c:when>
				        <c:otherwise>
				          <c:if test="${data.baseId.longValue == 1}">selected</c:if>
				        </c:otherwise>
				      </c:choose>>
							<c:out value="${data.beschreibung}" /></option>
						</c:forEach>
					</select></td>
				</tr>
				<tr>
					<td class="text inner_option_large">Bereich</td>
					<td class="text"><select
						${isPersonAenderbar}
						title="Geben Sie hier an, zu welchem Bereich die Person gehört."
						id="BereichId" name="textBereichId" class="helpEvent input">
						<c:forEach items="${arrBereicheAvailable.data}" var="data">
							<option value="<c:out value="${data.baseId}"/>"
								<c:choose>
				        <c:when test="${!empty objPerson.bereichId}">
				      	  <c:if test="${data.baseId.longValue == objPerson.bereichId.longValue}">selected</c:if>
				        </c:when>
				        <c:otherwise>
				          <c:if test="${data.baseId.longValue == 1}">selected</c:if>
				        </c:otherwise>
				      </c:choose>>
							<c:out value="${data.name}" /></option>
						</c:forEach>
					</select></td>
				</tr>
				<tr class="v_spacer">
					<td></td>
					<td></td>
				</tr>
				<tr id="Telefonnummer1">
					<td class="text inner_option_large" style="vertical-align: top">Telefonnummer</td>
					<td class="text baselink">
					<c:if test="${empty isPersonAenderbar}">
						<c:choose>
							<c:when test="${empty objPerson}">
			              Alle neu angelegten Telefonnummern sind standardmäßig aktiv.<br />
								<input
									title="Geben Sier hier die Telefonnummer für den SMS-Empfang ein."
									name="arrTextTelefonNummern" type="text" class="helpEvent input"
									value="<c:out value="${objTelefon.nummer}" default="${param.arrTextTelefonNummern}"/>"
									size="30">
							</c:when>
							<c:otherwise>
								<c:forEach items="${arrTelefoneAvailable.data}" var="data">
									<c:if test="${data.geloescht == false}">
										<a
											title="Klicken Sie hier, um diese Telefonnummer zu bearbeiten."
											class="helpEvent"
											href="<zabos:url url="controller/person/?tab=telefonnummern"/>&amp;PersonId=${objPerson.baseId}&amp;TelefonId=${data.baseId}">${data.nummer}</a>
										<br>
									</c:if>
								</c:forEach>
							</c:otherwise>
						</c:choose>
					</c:if>
					</td>
				</tr>
				<tr id="IstInFolgeschleife">
					<td class="text inner_option_large" style="vertical-align: top">Verst&auml;rkungsschleife</td>
					<td class="text baselink"><input type="checkbox" value="1"
						name="cbIsInFolgeschleife"
						<c:if test="${(!empty objPerson) && (objPerson.inFolgeschleife)}">checked</c:if> ${isPersonAenderbar} /></td>
				</tr>
				<tr class="v_spacer">
					<td></td>
					<td></td>
				</tr>
				<tr>
					<td></td>
					<td><c:if test="${!empty objPerson && (empty isPersonAenderbar)}">
						<input
							title="Klicken Sie hier, um alle Einheiten anzuzeigen, in denen die gewählte Person in einer Rolle zugeteilt ist."
							name="btnHierachie " class="helpEvent button" type="button"
							value="Vererbung zeigen"
							onClick="window.location.href='/zabos/controller/hierarchie/?tab=person_rolle&amp;PersonId=${objPerson.baseId}'">
					</c:if></td>
				</tr>
			</table>
			</td>
		</tr>
	</table>
	</form>
	<!--  SPACER -->
	<c:if test="${empty objPerson}">
		<table class="popup_base">
			<tr>
				<td class="h_spacer"></td>
				<td>
				<table id="tableAddTelefonnummer" class="popup_inner">
					<tr>
						<td></td>
						<td class="inner_option_large"></td>
						<td class="text"><input type="button"
							title="Klicken Sie hier um zusätzliche Telefonnummern einzugeben."
							class="helpEvent button"
							onClick="addTelefonnummer();" 
							name="addTelefonnummer" value="Weitere Nummer..."></td>
					</tr>
				</table>
				</td>
			</tr>
		</table>
	</c:if>
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
					<td><c:if
						test="${empty isPersonAenderbar}">
						<div align="left" id="divSave"><input name="inputSave"
							title="Klicken Sie auf 'Speichern' um die Änderungen zu speichern."
							type="submit" class="helpEvent button" value="Speichern"
							onclick="document.forms.frmEditPerson.action='<zabos:url url="controller/person/"/>?do=doUpdatePerson&amp;submit=true<c:if test="${!empty param.RolleId}">&amp;RolleId=${param.RolleId}</c:if><c:if test="${!empty param.OrganisationId}">&amp;OrganisationId=${param.OrganisationId}</c:if><c:if test="${!empty param.OrganisationsEinheitId}">&amp;OrganisationsEinheitId=${param.OrganisationsEinheitId}</c:if><c:if test="${!empty param.SchleifeId}">&amp;SchleifeId=${param.SchleifeId}</c:if>';document.forms.frmEditPerson.submit()">
						</div>
					</c:if></td>
					<td><c:if
						test="${empty isPersonAenderbar}">
						<div align="center" style="margin-left:38px;"><input name="inputCancel" type="submit"
							onclick="document.forms.frmEditPerson.reset();"
							class="button"
							value="Zurücksetzen"></div>
					</c:if></td>
					<td></td>
						<td>
							<c:if test="${isPersonLoeschbar}">
								<div align="right" style="margin-left:40px;"><input
									title="Klicken Sie auf 'Speichern' um die Personzu komplett zu löschen."
									name="inputDelete" type="submit" class="helpEvent button"
									value="Entfernen" onClick="confirmDelete();"> 
									<script type="text/javascript" language="JavaScript">
										function confirmDelete() {
											if (confirm("Wollen Sie die Person wirklich aus dem System löschen?")) {
												document.forms.frmEditPerson.action = "<zabos:url url="controller/person/"/>?do=doDeletePerson&submit=true";
												document.forms.frmEditPerson.submit();
												alert('Die Person wurde erfolgreich aus dem System entfernt');
											} 
										}
									</script>
								</div>
							</c:if>
						</td>
					<td class="h_spacer"></td>
				</tr>
			</table>
			</td>
		</tr>
	</table>
</c:if></div>