<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="zabos" uri="http://ecw.de/taglibs/zabos"%>
<%@ page language="java" contentType="text/html; charset=utf-8"
	pageEncoding="UTF-8"%>

<div class="settings" id="rollen" style="display: none;"
	title="Wählen Sie hier für 'System' die Rolle aus, die sie einer Person zuteilen wollen.">
<c:if
	test="${sessionScope.user.accessControlList.personenRollenZuweisenErlaubt}">

	<form method="post"
		action="<zabos:url url="controller/system/?tab=rollen"/>"
		name="frmSelectRollen" id="frmSelectRollen">
	<table class="popup_base">
		<tr class="list_head">
			<td class="h_spacer"></td>
			<td class="dropdown"><select
				title="Klicken Sie hier, um eine Rolle auszuwählen und der Einheit 'System' Personen in dieser Rolle zuzuteilen."
				id="RolleId" name="RolleId" class="helpEvent input"
				onchange="document.forms.frmSelectRollen.submit()">
				<option value="0" <c:if test="${!empty objRolle}">selected</c:if>>&lt;Rolle
				ausw&auml;hlen&gt;</option>
				<c:forEach items="${arrKompatibleRollenAvailable.data}" var="data">
					<c:if test="${data.geloescht == false}">
						<option value="<c:out value="${data.baseId}"/>"
							<c:if test="${data.baseId == objRolle.baseId}">selected</c:if>><c:out
							value="${data.name}" /></option>
					</c:if>
				</c:forEach>
			</select></td>
		</tr>
	</table>
	</form>

	<jsp:include page="../include/_assignRollen.jsp" />

	<c:if test="${!empty objRolle}">
		<table class="popup_inner">
			<tr class="list_head">
				<td>
				<table class="popup_base">
					<tr>
						<td class="h_spacer"></td>
						<c:if test="${!empty objRolle}">
							<td>
							<div align="left">
							<input
									title="Klicken Sie hier um alle gemachten Änderungen zu speichern."
									id="inputSave" name="inputSave" type="submit"
									class="helpEvent button" value="Speichern"
									onclick="getObject('inputSave').setAttribute('disabled', 'true');selectAllEntries('selectPersonenAssigned');document.forms.frmEditRollen.action='<zabos:url url="controller/system/?do=doUpdateRollenKontext&amp;tab=rollen&amp;submit=true"/>';document.forms.frmEditRollen.submit()">
							</div>
							</td>
							<td width="100%"></td>
						</c:if>
					</tr>
				</table>
				</td>
			</tr>
		</table>
	</c:if>

	<table class="popup_base">
		<tr class="v_spacer">
			<td></td>
			<td></td>
		</tr>
		<tr class="list_head">
			<td class="h_spacer"></td>
			<td id="personenOb"
				title="Hier sehen Sie eine Liste mit bereits eingetragenen Personen mit der zugeteilten Rolle für 'System'."
				class="helpEvent">Personen und deren Rollen innerhalb von
			&quot;System&quot;</td>
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
					<td class="inner_option_large text_bold">Person</td>
					<td class="text_bold">Rollen in System</td>
				</tr>
			</table>
			<div class="scroll">
			<table class="popup_inner_scroll">
				<c:forEach items="${arrPersonenMitRollen.data}" var="data">
					<tr>
						<td class="inner_option_large text"><a
							title="Klicken Sie hier um die Einstellungen von '${data.person.displayName}' zu bearbeiten."
							class="helpEvent"
							href="<zabos:url url="controller/person/" />?PersonId=${data.person.baseId.longValue}">${data.person.displayName}</a></td>
						<td class="text"><c:forEach items="${data.rollen}"
							var="rollen" varStatus="statusVar">
							<span class="helpEvent"
								title="${data.person.displayName} ist dieser Einheit in der Rolle '${rollen.name}' zugeteilt.">
							${rollen.name} </span>
							<c:if
								test="${sessionScope.user.accessControlList.personAendernErlaubt == true}">
								<a
									href="<zabos:url url="controller/system/" />?PersonId=${data.person.baseId.longValue}&amp;RolleId=${rollen.baseId.longValue}&amp;SchleifeId=${sessionScope.user.ctxSchleife.baseId.longValue}&amp;tab=rollen&amp;do=doRemoveRolleFromPerson&amp;submit=true">
								<img alt="Person aus Rolle entfernen"
									title="Klicken Sie auf das Mülleimer-Symbol um die Rollenzuordnung '${rollen.name}' dieser Person innerhalb von 'System' zu entfernen."
									class="helpEvent" src="<zabos:url url='images/btn_trash.gif'/>"></a>
							</c:if>
							<c:if test="${statusVar.last == false}">,</c:if>
						</c:forEach></td>
					</tr>
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

	<table class="popup_inner">
		<tr class="list_head">
			<td>
			<table class="popup_base">
				<tr>
					<td class="h_spacer"></td>
					<td><c:if
						test="${sessionScope.user.accessControlList.personAnlegenLoeschenErlaubt}">
						<div align="left"><input id="inputNeuePerson"
							title="Klicken Sie hier, um eine neue Person anzulegen"
							name="inputNeuePerson" class="helpEvent button" type="submit"
							value="Neue Person"
							onclick="window.location.href='<zabos:url url="controller/person/"/>?PersonId=0'">
						</div>
					</c:if></td>
					<td width="100%"></td>
					<td class="h_spacer"></td>
				</tr>
			</table>
			</td>
		</tr>
	</table>
</c:if></div>