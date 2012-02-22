<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="zabos" uri="http://ecw.de/taglibs/zabos"%>
<%@ page language="java" contentType="text/html; charset=utf-8"
	pageEncoding="UTF-8"%>
<div id="object"
	title="Hier können Sie grundlegende Einstellungen der Schleife festlegen."
	class="setting" style="display: none">
<form name="frmEditSchleife" id="frmEditSchleife" action=""
	onKeyUp=
	Aenderung(this.id);
method="post"><input type="hidden"
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
				<td class="inner_option_large text">Schleifenname</td>
				<td><input title="Geben Sie hier den Namen der Schleife an."
					name="textName" type="text" class="helpEvent input"
					value="<c:out value="${objSchleife.name}" default="${param.textName}"/>"
					size="30"
					<c:if test="${!sessionScope.user.accessControlList.schleifeAendernErlaubt}">disabled</c:if>>
				</td>
			</tr>
			<tr>
				<td class="inner_option text">K&uuml;rzel</td>
				<td><input title="Geben Sie hier das Kürzel der Schleife an."
					name="textKuerzel" type="text" class="helpEvent input"
					value="<c:out value="${objSchleife.kuerzel}" default="${param.textKuerzel}"/>"
					size="30" maxlength="5"
					<c:if test="${!sessionScope.user.accessControlList.schleifeAendernErlaubt}">disabled</c:if>>
				</td>
			</tr>
			<tr>
				<td class="inner_option text">F&uuml;nfton</td>
				<td><input name="textFuenfton"
					title="Geben Sie hier die 5-Ton-Sequenz der Schleife an."
					type="text" class="helpEvent input"
					value="<c:out value="${objSchleife.fuenfton}" default="${param.textFuenfton}"/>"
					size="30" maxlength="5"
					<c:if test="${!sessionScope.user.accessControlList.schleifeAendernErlaubt}">disabled</c:if>>
				</td>
			</tr>
			<tr>
				<td class="inner_option text">SMS-Statusreport bei
				5-Tonausl&ouml;sung</td>
				<td><input
					title="Geben Sie hier an, ob ein Statusreport bei 5-Ton-Auslösung gesendet werden soll."
					name="cbStatusreportFuenfton" type="checkbox"
					class="helpEvent input" value="1"
					<c:if test="${objSchleife.statusreportFuenfton || param.cbStatusreportFuenfton}">checked</c:if>
					<c:if test="${!sessionScope.user.accessControlList.schleifeAendernErlaubt}">disabled</c:if> />
				</td>
			</tr>
			<tr>
				<td class="inner_option text">Ist abrechenbar</td>
				<td><input
					title="Geben Sie hier an, ob die Schleife abrechenbar ist"
					name="cbAbrechenbar" type="checkbox" class="helpEvent input"
					value="1"
					<c:if test="${objSchleife.abrechenbar || param.cbAbrechenbar || empty objSchleife}">checked</c:if>
					<c:if test="${!sessionScope.user.accessControlList.schleifeAbrechnungFestlegenErlaubt}">disabled</c:if> />
				</td>
			</tr>
			<tr>
				<td class="inner_option text">Beschreibung</td>
				<td><input name="textBeschreibung"
					title="Geben Sie hier eine Beschreibung der Schleife an."
					type="text" class="helpEvent input" size="30"
					value="<c:out value="${objSchleife.beschreibung}" default="${param.textBeschreibung}"/>"
					<c:if test="${!sessionScope.user.accessControlList.schleifeAendernErlaubt}">disabled</c:if>>
				</td>
			</tr>
			<c:if test="${isDruckerKuerzelAktiv}">
			<tr>
				<td class="inner_option text">Drucker-K&uuml;rzel</td>
				<td><input name="textDruckerKuerzel"
					title="Geben Sie hier ein Druckerkürzel, das der Schleife zugeordnet ist. Wenn der Report auf mehreren Druckern gedruckt werden soll, benutzen Sie ';' oder ',' als Trennzeichen."
					type="text" class="helpEvent input" size="15" maxlength="15"
					value="<c:out value="${objSchleife.druckerKuerzel}" default="${param.textDruckerKuerzel}"/>"
					<c:if test="${!sessionScope.user.accessControlList.schleifeAendernErlaubt}">disabled</c:if>>
				</td>
			</tr>
			</c:if>
			<c:if test="${isRueckmeldeIntervalAktiv}">
			<tr>
				<td class="inner_option text">Rückmeldeintervall in Sekunden</td>
				<td><input name="textRueckmeldeintervall"
					title="Geben Sie hier das Rückmeldeintervall der Schleife in Sekunden an."
					type="text" class="helpEvent input" size="7" maxlength="7"
					value="<c:out value="${objSchleife.rueckmeldeintervall}" default="${param.textRueckmeldeintervall}"/>"
					<c:if test="${!sessionScope.user.accessControlList.schleifeAendernErlaubt}">disabled</c:if>>
				</td>
			</tr>
			</c:if>
			<c:if test="${isFolgeschleifeAktiv}">
			<tr>
				<td class="inner_option text">Folgeschleife</td>
				<td><select name="selectFolgeschleife"
					<c:if test="${!sessionScope.user.accessControlList.schleifeAendernErlaubt}">disabled</c:if>>
					<option value="0"
						<c:if test="${(empty objSchleife) || ((!empty objSchleife) && (objSchleife.folgeschleifeId == null))}">selected</c:if>>Keine Folgeschleife definiert</option>
					<c:forEach items="${arrMoeglicheFolgeschleifen.data}" var="data">
						<c:if
							test="${(empty objSchleife) || ((!empty objSchleife) && (objSchleife.schleifeId.longValue != data.schleifeId.longValue))}">
							<option value="<c:out value="${data.schleifeId}"/>" <c:if test="${(!empty objSchleife) && (!empty objSchleife.folgeschleifeId) && (objSchleife.folgeschleifeId.longValue == data.schleifeId.longValue)}">selected</c:if>> <c:out
								value="${data.displayName}" /></option>
						</c:if>
					</c:forEach>
				</select></td>
			</tr>
			</c:if>
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
				<td><c:if
					test="${sessionScope.user.accessControlList.schleifeAendernErlaubt}">
					<div align="left" id="controlButtons"><input name="inputSave"
						title="Klicken Sie zum Übernehmen der Einstellungen auf 'Speichern'."
						type="submit" class="helpEvent button" value="Speichern"
						onclick="document.forms.frmEditSchleife.action='<zabos:url url="controller/schleife/?do=doUpdateSchleife&amp;submit=true"/>';document.forms.frmEditSchleife.submit()">
					</div>
				</c:if></td>
				<td><c:if
					test="${sessionScope.user.accessControlList.schleifeAendernErlaubt}">
					<div align="center" style="margin-left:38px;"><input name="inputCancel"
						title="Wollen Sie die Änderungen verwerfen klicken Sie auf 'Abbrechen'."
						type="submit" onclick=
	document.forms.frmEditSchleife.reset();
class="helpEvent button" value="Zurücksetzen"></div>
				</c:if></td>

				<c:if test="${!empty objSchleife}">
					<td><c:if
						test="${sessionScope.user.accessControlList.schleifeAnlegenLoeschenErlaubt}">
						<div align="right" style="margin-left:38px;"><input name="inputDelete"
							title="Wollen Sie die Schleife löschen, klicken Sie auf 'Entfernen.'"
							type="submit" class="helpEvent button" value="Entfernen"
							onclick="document.forms.frmEditSchleife.action='<zabos:url url="controller/schleife/?do=doDeleteSchleife&amp;submit=true"/>';document.forms.frmEditSchleife.submit()">
						</div>
					</c:if></td>
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
</div>