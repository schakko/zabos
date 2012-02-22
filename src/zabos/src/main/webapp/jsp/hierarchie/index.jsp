<%@ page language="java" contentType="text/html; charset=utf-8"
	pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="zabos" uri="http://ecw.de/taglibs/zabos"%>
<jsp:include page="../include/_header_popup.jsp" flush="true" />
<div id="titel" class="titel">System > <em>Hierarchie von
${objPerson.displayName}</em></div>
<div id="main" class="main">
<div id="submenu_layer" class="submenu_layer">
<table class="popup_base">
	<tr>
		<th class="head_normal" id="tab_person_rolle"><a href="#"
			onclick=showTab('person_rolle');
>Rollen der Person
		${objPerson.displayName}</a></th>
	</tr>
</table>
</div>
<div id="action_layer" class="action_layer">
<div id="notice" class="notice"></div>
<jsp:include page="../include/_errors.jsp" flush="true" /> <jsp:include
	page="person_rolle.jsp" flush="true" /></div>
</div>

<script language="JavaScript" type="text/javascript">
	showTab('<c:out value="${requestScope.tab}" default="person_rolle"/>');
</script>
<jsp:include page="../include/_footer_popup.jsp" flush="true" />