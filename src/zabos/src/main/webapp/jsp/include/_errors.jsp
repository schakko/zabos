<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="zabos" uri="http://ecw.de/taglibs/zabos"%>
<div class="notice" id="errors"
	style="display:<c:choose><c:when test="${!empty errors && (!empty errors.messages)}">block</c:when><c:otherwise>none</c:otherwise></c:choose>">
<c:if test="${!empty errors}">
	<table class="error">
		<tr>
			<td class="error_h">Es sind Fehler aufgetreten:</td>
		</tr>
		<tr class="v_spacer">
			<td></td>
		</tr>
		<c:if test="${!empty errors.messages}">
			<c:forEach items="${errors.messages}" var="data">
				<tr>
					<td class="error"><c:out value="${data.text}" /></td>
				</tr>
			</c:forEach>
		</c:if>
	</table>
	<table>
		<tr class="v_spacer">
			<td></td>
		</tr>
	</table>
</c:if></div>
<div class="notice" id="notice"
	style="display:<c:choose><c:when test="${!empty info && (!empty info.messages)}">block</c:when><c:otherwise>none</c:otherwise></c:choose>">
<c:if test="${!empty info}">
	<table class="error">
		<tr>
			<td class="error_h">Hinweise:</td>
		</tr>
		<tr class="v_spacer">
			<td></td>
		</tr>
		<c:if test="${!empty info.messages}">
			<c:forEach items="${info.messages}" var="data">
				<tr>
					<td class="error"><c:out value="${data.text}" /></td>
				</tr>
			</c:forEach>
		</c:if>
	</table>
	<table>
		<tr class="v_spacer">
			<td></td>
		</tr>
	</table>
</c:if></div>