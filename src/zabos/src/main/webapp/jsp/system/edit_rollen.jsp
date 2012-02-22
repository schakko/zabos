<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="zabos" uri="http://ecw.de/taglibs/zabos"%>
<%@ page language="java" contentType="text/html; charset=utf-8"
	pageEncoding="UTF-8"%>
<jsp:useBean id="beanRollenDelta"
	class="de.ecw.zabos.frontend.beans.DeltaBean"
	type="de.ecw.zabos.frontend.beans.DeltaBean" scope="page" />

<div class="settings" id="edit_rollen" style="display: none"
	title="Hier können Sie neue Rollen anlegen oder die Einstellungen von vorhandene Rollen ändern.">
<c:if
	test="${sessionScope.user.accessControlList.rollenAendernErlaubt || sessionScope.user.accessControlList.rollenAnlegenLoeschenErlaubt}">
	<table class="popup_base">
		<tr class="list_head">
			<td class="h_spacer"></td>
			<td>Rollen:</td>
		</tr>
		<tr class="list_head">
			<td class="spacer"></td>
			<td>
			<form method="post"
				action="<zabos:url url="controller/system/?tab=edit_rollen"/>"
				name="frmSelectRollen_edit" id="frmSelectRollen_edit"><select
				title="Wählen Sie hier eine vorhandene Rolle aus um sich deren Einstellungen anzeigen zu lassen oder diese zu ändern. Wählen Sie 'Neue Rolle' um eine neue Rolle anzulegen."
				id="RolleId1" name="RolleId" class="helpEvent input"
				onchange="document.forms.frmSelectRollen_edit.submit()">
				<option value="0" <c:if test="${empty objRolle}">selected</c:if>>&lt;Neue
				Rolle&gt;</option>
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
		<tr>
			<th></th>
			<td>
			<table class="popup_inner">
				<tr>
					<td class="text_info">Alle hier angelegte Rollen sind
					systemweit verf&uuml;gbar.</td>
				</tr>
			</table>
			</td>
		</tr>
	</table>

	<form name="frmEditRolle" id="frmEditRolle" action=""
		onKeyUp="Aenderung(this.id)" method="post">
	<table>
		<tr>
			<td>
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
							<td class="inner_option text">Name</td>
							<td><input type="hidden" name="RolleId"
								value="<c:out value="${objRolle.baseId}" default="0"/>" /> <input
								title="Geben Sie hier einen eindeutigen und sinnvollen Namen für die Rolle ein."
								id="textName" name="textName" type="text"
								class="helpEvent input"
								value="<c:out value="${objRolle.name}" default="${param.textBeschreibung}"/>"
								size="30"></td>
						</tr>
						<tr>
							<td class="inner_option text">Beschreibung</td>
							<td><input
								title="Geben Sie hier eine erklärende Beschreibung für die Rolle ein."
								name="textBeschreibung" id="textBeschreibung" type="text"
								class="helpEvent input" size="30"
								value="<c:out value="${objRolle.beschreibung}" default="${param.textBeschreibung}"/>">
							</td>
						</tr>
						<tr class="v_spacer">
							<td></td>
							<td></td>
						</tr>
					</table>
					</td>
				</tr>
			</table>

			<table class="popup_base">
				<tr class="v_spacer">
					<td></td>
					<td></td>
				</tr>
				<tr>
					<th></th>
					<td>
					<table class="inner">
						<tr>
							<td class="h_spacer"></td>
							<td class="text_bold">Gew&auml;hlte Rechte</td>
							<td width="184"></td>
							<td class="text_bold">Vorhandene Rechte</td>
						</tr>
						<tr>
							<td colspan="2" valign="top"><br />
							<select
								title="Alle in dieser Liste anzeigten Rechte sind in der Rolle verfügbar."
								class="helpEvent"
								ondblclick="javascript:moveSelectedEntries('selectRechteAssigned','selectRechteAvailable');"
								name="selectRechteAssigned" id="selectRechteAssigned" size="20"
								multiple>
								<c:if test="${!empty arrRechteAssigned}">
									<c:forEach items="${arrRechteAssigned.data}" var="data">
										<c:set target="${beanRollenDelta}" property="entry"
											value="${data.baseId}" />
										<option value="<c:out value="${data.baseId}"/>"><c:out
											value="${data.name}" /></option>
									</c:forEach>
								</c:if>
							</select></td>
							<td valign="top">

							<div align="center"><br>
							<table border="0" align="center" cellpadding="0" cellspacing="0">
								<tr>
									<td height="60"></td>
								</tr>
								<tr>
									<th height="70"><input
										title="Klicken Sie hier um die markierten verfügbaren Rechte der Rolle hinzuzufügen."
										class="helpEvent button" type="button" name="btnAdd2"
										id="btnAdd22" value="< Hinzuf&uuml;gen"
										onclick="javascript:moveSelectedEntries('selectRechteAvailable','selectRechteAssigned'); return false" />
									</th>
								</tr>
								<tr>
									<th><input
										title="Klicken Sie hier um die markierten zugeordneten Rechte aus der Rolle zu entfernen."
										class="helpEvent button" type="button" name="btnDel2"
										id="btnDel22" value="Entfernen >"
										onclick="javascript:moveSelectedEntries('selectRechteAssigned','selectRechteAvailable'); return false" />
									</th>
								</tr>
							</table>
							</div>

							</td>
							<td valign="top"><br>
							<!-- select name="selectFilter" class="input">
			            							<option selected>Filter - alle zeigen</option>
				            					</select --> <select
								title="Alle in dieser Liste anzeigten Rechte sind global verfügbar, aber dieser Rolle nicht zugeteilt."
								class="helpEvent"
								ondblclick="javascript:moveSelectedEntries('selectRechteAvailable','selectRechteAssigned');"
								name="selectRechteAvailable" size="20"
								id="selectRechteAvailable" multiple>
								<c:if test="${!empty arrRechteAvailable}">
									<c:forEach items="${arrRechteAvailable.data}" var="data">
										<c:set target="${beanRollenDelta}" property="test"
											value="${data.baseId}" />
										<c:if test="${beanRollenDelta.result == false}">
											<option value="<c:out value="${data.rechtId}"/>"><c:out
												value="${data.name}" /></option>
										</c:if>
									</c:forEach>
								</c:if>
							</select> <!-- suchmaske: <input type="text" name="selectPersonenSearch" autocomplete="off"><br> -->

							<div id="selectPersonenSearchPopulate"
								style="display: none; border: 1px solid black; background-color: white; position: relative;"></div>
							</td>
							<td valign="top"></td>
						</tr>
					</table>
					</td>
				</tr>
			</table>

			<table class="popup_base">
				<tr class="v_spacer">
					<td></td>
					<td></td>
				</tr>
			</table>
			</td>
		</tr>
	</table>
	</form>

	<!-- BEDIENELEMENTE -->
	<table class="popup_inner" id="controls">
		<tr class="list_head">
			<td>
			<table class="popup_base">
				<tr>
					<td class="h_spacer"></td>
					<td>
					<div align="left" id="controlButtons"><input
						title="Klicken Sie hier um alle gemachten Änderungen zu speichern."
						id="inputSave" name="inputSave" type="submit"
						class="helpEvent button" value="Speichern"
						onclick="selectAllEntries('selectRechteAssigned');document.forms.frmEditRolle.action='<zabos:url url="controller/system/?do=doUpdateRolle&amp;submit=true&amp;tab=edit_rollen"/>';document.forms.frmEditRolle.submit()">
					</div>
					</td>
					<td><!-- div align="center">
									<input name="inputCancel" onclick="document.forms.frmEditRolle.reset()" type="submit" class="button" value="Abbrechen">
								</div --></td>
					<c:if test="${!empty objRolle}">
						<td>
						<div align="right"><input name="inputDelete" type="submit"
							class="button" value="Entfernen"
							onclick="document.forms.frmEditRolle.action='<zabos:url url="controller/system/?do=doDeleteRolle&amp;submit=true&amp;tab=edit_rollen"/>';document.forms.frmEditRolle.submit()">
						</div>
						</td>
					</c:if>
					<td class="h_spacer"></td>
				</tr>
			</table>
			</td>
		</tr>
	</table>
</c:if></div>