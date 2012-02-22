<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="zabos" uri="http://ecw.de/taglibs/zabos"%>
<%@ page language="java" contentType="text/html; charset=utf-8"
	pageEncoding="UTF-8"%>
<div id="lizenz" title="Sehen Sie hier Ihre Lizenzdaten ein."
	class="setting" style="display: none"><c:if
	test="${sessionScope.user.accessControlList.lizenzEinsehenErlaubt}">
	<table class="popup_base">
		<tr class="list_head">
			<td class="h_spacer"></td>
			<td>Lizendaten:</td>
		</tr>
		<tr>
			<td></td>
			<td>
			<table class="popup_inner">
				<tr class="v_spacer">
					<td colspan="2"></td>
				</tr>
				<tr>
					<td class="inner_option_large text_bold">Ausstellungsdatum</td>
					<td class="text"><zabos:formatts format="both"
						timeStamp="${objLicense.ausstellungsDatum.timeStamp}" /></td>
				</tr>
				<tr>
					<td class="inner_option_large text_bold">Ablaufsdatum</td>
					<td class="text"><zabos:formatts format="both"
						timeStamp="${objLicense.ablaufDatum.timeStamp}" /></td>
				</tr>
				<tr>
					<td class="inner_option_large text_bold">ZABOS-Version</td>
					<td class="text">${objLicense.version}</td>
				</tr>
				<tr>
					<td class="inner_option_large text_bold">Schleifen</td>
					<td class="text">${objLicense.curSchleifen} /
					${objLicense.maxSchleifen}</td>
				</tr>
				<tr>
					<td class="inner_option_large text_bold">Personen</td>
					<td class="text">${objLicense.curPersonen} /
					${objLicense.maxPersonen}</td>
				</tr>
				<tr>
					<td class="inner_option_large text_bold">Kundennummer</td>
					<td class="text">${objLicense.kundenNummer}</td>
				</tr>
			</table>
			</td>
		</tr>
	</table>
</c:if></div>