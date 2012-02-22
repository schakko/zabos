<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="zabos" uri="http://ecw.de/taglibs/zabos"%>
<%@ page language="java" contentType="text/html; charset=utf-8"
	pageEncoding="UTF-8"%>

<div id="import"
	title="Importieren Sie hier Benutzerdaten aus einer CVS-Datei."
	class="setting" style="display: none"><c:if
	test="${sessionScope.user.accessControlList.systemKonfigurationAendernErlaubt}">
	<form name="frmImport" id="frmImport"
		action="<zabos:url url="controller/system/?do=doImport&amp;submit=true&amp;tab=import" />"
		method="post">

	<table class="popup_base">
		<tr class="list_head">
			<td class="h_spacer"></td>
			<td>Import:</td>
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
					<td class="text">Das Format der CSV-Listen ist
					'Vorname;Nachname;Handynummer'. Die Benutzernamen werden
					automatisch erzeugt.</td>
				</tr>
				<tr>
					<td><textarea class="helpEvent"
						title="Geben Sie in dieses Textfeld Ihre CVS-Daten ein"
						name="textImportCSV" rows="30" cols="70"></textarea></td>
				</tr>
			</table>
			</td>
		</tr>
	</table>
	</form>

	<table class="popup_inner">
		<tr class="list_head">
			<td>
			<table class="popup_base">
				<tr>
					<td class="h_spacer"></td>
					<td>
					<div align="left"><input name="inputSave" type="submit"
						class="helpEvent button" value="Importieren"
						title="Klicken Sie hier, um die eingebenen Daten in das System zu importieren."
						onclick="document.forms.frmImport.action='<zabos:url url="controller/system/?do=doImport&amp;submit=true&amp;tab=import" />';document.forms.frmImport.submit()"></div>
					</td>
					<td width="100%"></td>
				</tr>
			</table>
			</td>
		</tr>
	</table>
</c:if></div>