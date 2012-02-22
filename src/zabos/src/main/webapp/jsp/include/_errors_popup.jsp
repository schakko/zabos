<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="zabos" uri="http://ecw.de/taglibs/zabos"%>
<div class="notice" id="errors"
	style="display:<c:choose><c:when test="${!empty errors.messages}">block</c:when><c:otherwise>none</c:otherwise></c:choose>">
<c:if test="${!empty errors.messages}">
    Es sind Fehler aufgetreten:
	<ul>
		<c:forEach items="${errors.messages}" var="data">
			<li><c:out value="${data.text}" /></li>
		</c:forEach>
	</ul>
</c:if></div>