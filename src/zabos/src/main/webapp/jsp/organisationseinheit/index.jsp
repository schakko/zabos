<%@ page language="java" contentType="text/html; charset=utf-8"
	pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="zabos" uri="http://ecw.de/taglibs/zabos"%>
<jsp:include page="../include/_header_popup.jsp" flush="true" />
<div id="titel" class="titel"><span class="text"> <a
	href="<zabos:url url="controller/system/"/>"> System</a> > <a
	href="<zabos:url url="controller/organisation/"/>?OrganisationId=<c:out value="${sessionScope.user.ctxO.baseId.longValue}"/>">
${sessionScope.user.ctxO.name}</a> > <c:choose>
	<c:when test="${!empty objOrganisationseinheit}">
		<em>${objOrganisationseinheit.name}</em>
	</c:when>
	<c:otherwise>
		<em>Neue Organisationseinheit erstellen</em>
	</c:otherwise>
</c:choose> </span></div>
<div id="main" class="main">
<div id="submenu_layer" class="submenu_layer"><c:if
	test="${!empty objDeletedSchleife}">

	<div class="notice" id="errors">
	<table class="error">
		<tr>
			<td class="error_h">Die Schleife ${objDeletedSchleife.name}
			wurde gelöscht.</td>
		</tr>
	</table>
	</div>

</c:if>
<table class="popup_base">
	<tr>
		<th class="head_normal" style="width: 25%;" id="tab_object"><strong>
		<a href="#" onclick="showTab('object');showHelp('object',false)">Einheit</a>
		</strong></th>
		<c:if test="${!empty objOrganisationseinheit}">
			<th class="head_normal" style="width: 25%;" id="tab_rollen"><c:if
				test="${sessionScope.user.accessControlList.personenRollenZuweisenErlaubt}">
				<strong> <a href="#"
					onclick="showTab('rollen');showHelp('rollen',false)">Personen</a> </strong>
			</c:if></th>
			<th class="head_normal" style="width: 25%;" id="tab_probetermine">
			<c:if
				test="${sessionScope.user.accessControlList.organisationseinheitAendernErlaubt}">
				<strong> <a href="#"
					onclick="showTab('probetermine');showHelp('probetermine',false)">Probetermine</a>
				</strong>
			</c:if></th>
			<th class="head_normal" style="width: 25%;" id="tab_schleifen">
			<c:if
				test="${sessionScope.user.accessControlList.organisationseinheitAendernErlaubt}">
				<strong> <a href="#"
					onclick="showTab('schleifen'); showHelp('schleifen', false);">Schleifen</a>
				</strong>
			</c:if></th>
		</c:if>
	</tr>
</table>
</div>
<div id="action_layer" class="action_layer">
<div id="notice" class="notice"></div>
<jsp:include page="../include/_errors.jsp" flush="true" /> <!--  HELP-LAYER -->
<table id="helpTable" class="popup_base">
	<tr class="list_head">
		<td class="h_spacer"></td>
		<td class="helpTd text">
		<div id="helpLayer" class="helpLayer">Die Hilfe wird geladen...</div>
		</td>
	</tr>
</table>
<!--  / HELP-LAYER --> <jsp:include page="object.jsp" flush="true" /> <c:if
	test="${!empty objOrganisationseinheit}">
	<jsp:include page="rollen.jsp" flush="true" />
	<jsp:include page="probetermine.jsp" flush="true" />
	<jsp:include page="schleifen.jsp" flush="true" />
</c:if></div>
</div>

<script language="JavaScript" type="text/javascript">
	showTab('<c:out value="${requestScope.tab}" default="object"/>');

	/* Hilfetext laden */
	showHelp('object', false);
</script>
<%@ include file="../include/_footer_popup.jsp"%>