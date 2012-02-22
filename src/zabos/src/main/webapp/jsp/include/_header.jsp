<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="zabos" uri="http://ecw.de/taglibs/zabos"%>
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<%@ page language="java" contentType="text/html; charset=utf-8"
    pageEncoding="UTF-8"%>
<html>
	<head>
		<meta http-equiv="Content-Type" content="text/html; charset=utf-8">
		<title>ZABOS</title>

		<link rel="icon" href="<zabos:url url="images/favicon.gif"/>" type="image/ico">
		<link href="<zabos:url url="styles/error.css"/>" rel="stylesheet" type="text/css">
		<link href="<zabos:url url="styles/table.css"/>" rel="stylesheet" type="text/css">
		<link href="<zabos:url url="styles/base.css"/>" rel="stylesheet" type="text/css">
		<link href="<zabos:url url="styles/index_link.css"/>" rel="stylesheet" type="text/css">
		<link href="<zabos:url url="styles/forms.css"/>" rel="stylesheet" type="text/css">
		<script type="text/javascript" language="JavaScript">
			var JSESSIONID = "<%= session.getId() %>";
			var AJAX_DISPATCHER = "<zabos:url url='ajax/' />";
		</script>
		<script src="<zabos:url url="scripts/ECW_HelperScripts.js"/>" type="text/javascript" language="JavaScript" charset="UTF-8"></script>
		<script language="JavaScript" src="<zabos:url url='scripts/ECW_AjaxClient.js'/>" type="text/javascript" charset="UTF-8"></script>	
		<script type="text/javascript" language="JavaScript" src="<zabos:url url="scripts/ajax/system.js" />" charset="UTF-8"></script>
		<script type="text/javascript" language="JavaScript" src="<zabos:url url="scripts/ajax/search.js" />" charset="UTF-8"></script>
	</head>