<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="zabos" uri="http://ecw.de/taglibs/zabos"%>
<%@ page language="java" contentType="text/html; charset=utf-8"
	pageEncoding="UTF-8"%>

<div id="aliase"
	title="Die Aliase werden zur Erkennung der Antworten benutzt. Diese lassen sich beliebig anpassen."
	class="setting" style="display: none">
<form name="frmEditAliase" id="frmEditAliase"
	action="<zabos:url url="controller/system/?do=doUpdateAliase&amp;submit=true&amp;tab=aliase"/>"
	method="post">
<table class="popup_base">
	<tr class="list_head">
		<td class="h_spacer"></td>
		<td>Vorhandene Aliase</td>
	</tr>
	<tr>
		<th></th>
		<td>

		<table class="popup_inner">
			<tr class="v_spacer">
				<td colspan="2"></td>
			</tr>
			<tr>
				<td class="inner_option text_bold" style="vertical-align: top;">Aliase
				"Ja"</td>
				<td class="text"><c:forEach items="${arrAliasJaAvailable.data}"
					var="data">
					<input class="helpEvent"
						title="Markieren Sie diese Checkbox und klicken Sie auf die Schaltfläche 'Gewählte entfernen' um den Eintrag '<c:out value="${data.alias}"/>' zu löschen"
						type="checkbox" name="arrToDelete[]"
						value="<c:out value="${data.baseId}"/>" />
					<c:out value="${data.alias}" />
					<br />
				</c:forEach></td>
			</tr>
			<tr class="v_spacer">
				<td></td>
			</tr>
			<tr>
				<td class="text_bold" style="vertical-align: top;">Aliase
				"Nein"</td>
				<td class="text"><c:forEach
					items="${arrAliasNeinAvailable.data}" var="data">
					<input class="helpEvent"
						title="Markieren Sie diese Checkbox und klicken Sie auf die Schaltfläche 'Gewählte entfernen' um den Eintrag '<c:out value="${data.alias}"/>' zu löschen"
						type="checkbox" name="arrToDelete[]"
						value="<c:out value="${data.baseId}"/>" />
					<c:out value="${data.alias}" />
					<br />
				</c:forEach></td>
			</tr>
			<tr class="v_spacer">
				<td></td>
			</tr>
			<tr>
				<td class="text_bold" style="vertical-align: top;">Aliase
				"Sp&auml;ter"</td>
				<td class="text"><c:forEach
					items="${arrAliasSpaeterAvailable.data}" var="data">
					<input class="helpEvent"
						title="Markieren Sie diese Checkbox und klicken Sie auf die Schaltfläche 'Gewählte entfernen' um den Eintrag '<c:out value="${data.alias}"/>' zu löschen"
						type="checkbox" name="arrToDelete[]"
						value="<c:out value="${data.baseId}"/>" />
					<c:out value="${data.alias}" />
					<br />
				</c:forEach></td>
			</tr>
			<tr class="v_spacer">
				<td></td>
			</tr>
			<tr>
				<td></td>
				<td><input
					title="Klicken Sie auf diese Schaltfläche um alle markierten Einträge zu löschen."
					class="button" type="submit" value="Gew&auml;hlte entfernen" /></td>
			</tr>
			<tr class="v_spacer">
				<td></td>
			</tr>
		</table>
		</td>
	</tr>
</table>
</form>

<form name="frmEditAliaseNew" id="frmEditAliaseNew"
	action="<zabos:url url="controller/system/?do=doUpdateAliase&amp;submit=true&amp;tab=aliase"/>"
	method="post">
<table class="popup_base">
	<tr class="list_head">
		<td class="h_spacer"></td>
		<td>Neuen Alias anlegen:</td>
	</tr>
	<tr>
		<td></td>
		<td>
		<table class="popup_inner">
			<tr class="v_spacer">
				<td></td>
			</tr>
			<tr>
				<td class="inner_option">Alias</td>
				<td><input title="Geben Sie hier einen neuen Alias ein"
					class="helpEvent input_long" type="text" name="textAlias"
					value="<c:out value="${param.textAlias}"/>" /></td>
			</tr>
			<tr>
				<td>Typ</td>
				<td><select
					title="Geben Sie hier an, für welchen Antworttyp der Alias verwendet werden soll."
					class="helpEvent" name="selectType">
					<option value="0">Nein</option>
					<option value="1">Ja</option>
					<option value="2">Sp&auml;ter</option>
				</select></td>
			</tr>
			<tr class="v_spacer">
				<td colspan="2"></td>
			</tr>
			<tr>
				<td></td>
				<td><input
					title="Klicken Sie auf 'Alias anlegen' um den neuen Alias zu erstellen."
					class="helpEvent button" type="submit" value="Alias anlegen" /></td>
			</tr>
		</table>
		</td>
	</tr>
</table>
</form>
</div>