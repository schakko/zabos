<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="zabos" uri="http://ecw.de/taglibs/zabos"%>
<%@ page language="java" contentType="text/html; charset=utf-8"
	pageEncoding="UTF-8"%>

<div class="settings"
	title="Hier werden sämtliche im System vorhandenen Personen angezeigt, die keine aktive Handynummer besitzen. Klicken Sie auf eine Person, um sich deren Einstellungen anzeigen zu lassen."
	id="ohne_handynummer" style="display: none">
<table class="popup_base">
	<tr class="list_head">
		<td class="h_spacer"></td>
		<td class="helpEvent"
			title="Hier sehen Sie alle Personen, die keine aktive Handynumemr besitzen">Personen,
		die keine aktive Handynummer besitzen</td>
	</tr>
	<tr class="v_spacer">
		<td></td>
		<td></td>
	</tr>
	<tr>
		<td></td>
		<td>
		<table>
			<tr>
				<td class="inner_option_large text_bold">Person</td>
				<td class="text_bold">Status</td>
			</tr>
			<c:if test="${!empty arrPersonenOhneHandyNummer}">
				<c:forEach items="${arrPersonenOhneHandyNummer.data}" var="data">
					<tr>
						<td class="inner_option_large"><a
							href="<zabos:url url='controller/person/?PersonId=${data.personId}' />">${data.displayName}</a>
						</td>
						<td>Keine Handynummer</td>
					</tr>
				</c:forEach>
			</c:if>
		</table>
		</td>
	</tr>
</table>
</div>