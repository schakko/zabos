<%@ page language="java" contentType="text/html; charset=utf-8"
	pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="zabos" uri="http://ecw.de/taglibs/zabos"%>
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=utf-8">
<title>Alarm-Historie</title>
<link href="<zabos:url url='styles/table.css'/>" rel="stylesheet"
	type="text/css">
<link href="<zabos:url url='styles/popup_link.css'/>" rel="stylesheet"
	type="text/css">
<link href="<zabos:url url='styles/popup_base.css'/>" rel="stylesheet"
	type="text/css">
<link href="<zabos:url url='styles/table.css'/>" rel="stylesheet"
	type="text/css">
<link href="<zabos:url url='styles/forms.css'/>" rel="stylesheet"
	type="text/css">
<script language="JavaScript" type="text/javascript">
 	var JSESSIONID = "<%= session.getId() %>";
 	var AJAX_DISPATCHER = "<zabos:url url='ajax/' />";
</script>
<script language="JavaScript"
	src="<zabos:url url='scripts/ECW_AjaxClient.js'/>"
	type="text/javascript"></script>
<script language="JavaScript"
	src="<zabos:url url='scripts/ECW_HelperScripts.js'/>"
	type="text/javascript"></script>
<script language="JavaScript"
	src="<zabos:url url='scripts/ajax/report.js'/>"
	type="text/javascript"></script>
</head>
<body>
<div id="action_layer" class="action_layer">
<div id="notice" class="notice"></div>
<jsp:include page="../include/_errors.jsp" flush="true" /> <c:choose>
	<c:when test="${requestScope.tab eq 'object'}">
		<jsp:include page="${jspFileReportObject}" />
	</c:when>
	<c:otherwise>
		<jsp:include page="history.jsp" />
	</c:otherwise>
</c:choose></div>
</body>
</html>