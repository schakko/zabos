<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="zabos" uri="http://ecw.de/taglibs/zabos"%>
<%@ page language="java" contentType="text/html; charset=utf-8"
	pageEncoding="UTF-8"%>
<div class="settings"
	title="Wählen Sie hier für diese Organisationseinheit die Rolle aus, die sie einer Person zuteilen wollen."
	id="rollen" style="display: none">
<c:if test="${sessionScope.user.accessControlList.personenRollenZuweisenErlaubt}">
	<table class="popup_base">
		<tr class="list_head">
			<td class="h_spacer"></td>
			<td class="dropdown">
			<form method="post"
				action="<zabos:url url="controller/organisationseinheit/?tab=rollen"/>"
				name="frmSelectRollen" id="frmSelectRollen"><select
				title="Klicken Sie hier, um eine Rolle auszuwählen und dieser Organisationseinheit Personen in dieser Rolle zuzuteilen."
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
			</select></form>
			</td>
		</tr>
	</table>
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
								<input name="inputSave" id='inputSave' type="submit" class="button"
									value="Speichern"
									onclick="getObject('inputSave').setAttribute('disabled', 'true');selectAllEntries('selectPersonenAssigned');document.forms.frmEditRollen.action='<zabos:url url="controller/organisationseinheit/?do=doUpdateRollenKontext&amp;tab=rollen&amp;submit=true"/>';document.forms.frmEditRollen.submit()">
							</div>
							</td>
							</c:if>
						<td width="100%"></td>
					</tr>
				</table>
				</td>
			</tr>
		</table>
		<table>
			<tr class="v_spacer">
				<td></td>
				<td></td>
			</tr>
		</table>
	</c:if>

	<table class="popup_base">
		<tr class="list_head">
			<td class="h_spacer"></td>
			<td>Personen und deren Rollen innerhalb der Organisationseinheit
			(direkt)</td>
		</tr>
		<tr class="v_spacer">
			<td colspan="2"></td>
		</tr>
		<tr>
			<td></td>
			<td>
			<table class="popup_inner">
				<tr>
					<td class="inner_option_large text_bold">Person</td>
					<td class="text_bold">Rollen in dieser Organisationseinheit</td>
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
								title="${data.person.displayName} ist dieser Einheit in der Rolle '${rollen.name}' zugeteilt.">${rollen.name}</span>
							<c:if
								test="${sessionScope.user.accessControlList.personAendernErlaubt == true}">
								<a
									href="<zabos:url url="controller/organisationseinheit/" />?PersonId=${data.person.baseId.longValue}&amp;RolleId=${rollen.baseId.longValue}&amp;OrganisationseinheitId=${sessionScope.user.ctxOE.baseId.longValue}&amp;tab=rollen&amp;do=doRemoveRolleFromPerson&amp;submit=true">
								<img alt="Person aus Rolle entfernen"
									title="Klicken Sie auf das Mülleimer-Symbol um die Rollenzuordnung '${rollen.name}' dieser Person innerhalb von dieser Organisation zu entfernen."
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
	<table class="popup_base">
		<tr class="v_spacer">
			<td colspan="2"></td>
		</tr>
		<tr class="list_head">
			<td class="h_spacer"></td>
			<td>Personen mit geerbten Rollen innerhalb dieser
			Organisationseinheit</td>
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
					<td class="text_bold">Rollen in dieser Organisationseinheit</td>
				</tr>
			</table>
			<div class="scroll">
			<table class="popup_inner_scroll">
				<c:forEach items="${arrPersonenMitRollenVererbt.data}" var="data">
					<tr>
						<td class="inner_option_large text">${data.person.displayName}</td>
						<td class="text"><c:forEach items="${data.rollen}"
							var="rollen" varStatus="statusVar">
											${rollen.name}<c:if test="${statusVar.last == false}">,</c:if>
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
						<div align="left"></div>
					</c:if></td>
					<td width="100%"></td>
					<td class="h_spacer"></td>
				</tr>
			</table>
			</td>
		</tr>
	</table>
</c:if>
</div>