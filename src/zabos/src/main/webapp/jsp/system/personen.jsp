<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="zabos" uri="http://ecw.de/taglibs/zabos"%>
<%@ page language="java" contentType="text/html; charset=utf-8"
	pageEncoding="UTF-8"%>

<div class="settings"
	title="Hier werden sämtliche im System vorhandenen Personen angezeigt. Klicken Sie auf eine, um sich deren Einstellungen anzeigen zu lassen."
	id="personen" style="display: none">		
	<c:if test='${(!empty sessionScope.user.optionen["personenSucheAktivieren"]) && (sessionScope.user.optionen["personenSucheAktivieren"] == "1")}'>
	<table class="popup_base">
		<tr class="v_spacer">
			<td></td>
			<td></td>
		</tr>
		<tr>
			<td class="inner">
			<select id="selectPersonen" class="helpEvent"
				title="Klicken Sie auf eine der angezeigten Personen um sich deren Einstellungen anzuschauen."
				name="selectPersonen" size="25"
				onclick="out_findPersonById(this.options[this.selectedIndex].value,in_findPersonById)">
				<c:if test="${!empty arrPersonenAvailable}">
					<c:forEach items="${arrPersonenAvailable.data}" var="data">
						<option value="<c:out value="${data.personId}"/>"><c:out
							value="${data.displayName}" /></option>
					</c:forEach>
				</c:if>
			</select>

			<table class="inner">
				<tr class="v_spacer">
					<td colspan="2"></td>
				</tr>
				<tr class="list_head">
					<td class="h_spacer_5"></td>
					<td class="text left">Personen suchen: <span id="indicator"
						style="visibility: hidden;"> <img alt="#"
						src="<zabos:url url="images/indicator.gif"/>"> </span></td>
				</tr>
			</table>

			<table class="inner">
				<tr>
					<td><input
						title="Suchen Sie hier nach einer bestimmten Person, indem Sie deren Namen eingeben."
						type="text" class="helpEvent input_long" name="inputSearchAllPersonen"
						id="inputSearchAllPersonen" value=""
						onkeyup="hideObject('indicator',false);window.setTimeout('out_findPersonenByPattern(in_systemListPersonen,\'inputSearchAllPersonen\')',1000);" />
					</td>
				</tr>
			</table>
			</td>
			<td valign="top">
			<table class="inner">
				<tr class="list_head">
					<td class="h_spacer"></td>
					<td class="text_bold">Einstellungen:</td>
				</tr>
				<tr class="v_spacer">
					<td></td>
					<td></td>
				</tr>
				<tr>
					<td></td>
					<td>
					<div id="PersonenDetails" class="PersonenDetails">Bitte eine
					Person ausw&auml;hlen.</div>
					</td>
				</tr>
			</table>
			</td>
		</tr>
	</table>
</c:if></div>