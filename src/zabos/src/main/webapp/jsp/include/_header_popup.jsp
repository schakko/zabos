<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="zabos" uri="http://ecw.de/taglibs/zabos"%>
<%@ page language="java" contentType="text/html; charset=utf-8"
    pageEncoding="UTF-8"%>
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">

<html>
	<head>
    	<title>Verwaltung</title>

    	<meta http-equiv="Content-Type" content="text/html; charset=utf-8">		

		<link href="<zabos:url url='styles/error.css'/>" rel="stylesheet" type="text/css">	
		<link href="<zabos:url url='styles/table.css'/>" rel="stylesheet" type="text/css">
		<link href="<zabos:url url='styles/popup_link.css'/>" rel="stylesheet" type="text/css">
		<link href="<zabos:url url='styles/popup_base.css'/>" rel="stylesheet" type="text/css">
		<link href="<zabos:url url='styles/table.css'/>" rel="stylesheet" type="text/css">
		<link href="<zabos:url url='styles/forms.css'/>" rel="stylesheet" type="text/css">
		<link href="<zabos:url url='styles/div/scroll-rel.css'/>" rel="stylesheet" type="text/css">

		<script language="JavaScript" type="text/javascript">
		  var JSESSIONID = "<%= session.getId() %>";
		  var AJAX_DISPATCHER = "<zabos:url url='ajax/' />";
		</script>
		<script language="JavaScript" src="<zabos:url url='scripts/effects/prototype.js'/>" type="text/javascript" charset="utf-8"></script>
		<script language="JavaScript" src="<zabos:url url='scripts/effects/scriptaculous.js'/>" type="text/javascript" charset="utf-8"></script>
		<script language="JavaScript" src="<zabos:url url='scripts/effects/effects.js'/>" type="text/javascript" charset="utf-8"></script>
		<script language="JavaScript" src="<zabos:url url='scripts/ECW_AjaxClient.js'/>" type="text/javascript" charset="utf-8"></script>
		<script language="JavaScript" src="<zabos:url url='scripts/ECW_HelperScripts.js'/>" type="text/javascript" charset="utf-8"></script>
		<script language="JavaScript" src="<zabos:url url='scripts/ajax/search.js'/>" type="text/javascript" charset="utf-8"></script>
		<script language="JavaScript" src="<zabos:url url='scripts/ajax/system.js'/>" type="text/javascript" charset="utf-8"></script>
	</head>
	
	<body class="body">
		<script language="JavaScript" type="text/javascript">
			function logout(){
				if(window.opener) 
					{ window.opener.location.href = '<zabos:url url="controller/security/?do=doLogout&amp;submit=true"/>';
					window.opener.popupClose();
				} else {
				 	window.location.href = '<zabos:url url="controller/security/?do=doLogout&amp;submit=true"/>';
				}
			}
		</script>

		<div id="session" class="session">

		</div>
		
		<div id="navigation" class="navigation">
			<a onFocus="this.blur();" onClick="invisHelp('helpTable');">
				<img alt="Hilfe" title="Hilfe" src="<zabos:url url="images/btn_info.gif"/>">
			</a>
			<a onFocus="this.blur();" href="javascript:history.back()">
				<img alt="Zurück" title="Zurück" src="<zabos:url url="images/btn_back.gif"/>">
			</a>
			<a onFocus="this.blur();" href="javascript:window.location.reload()">
				<img alt="Neu laden" title="Neu laden" src="<zabos:url url="images/btn_reload.gif"/>">
			</a>
			<a onFocus="this.blur();" href="javascript:history.forward()">
				<img alt="Vorwärts" title="Vorwärts" src="<zabos:url url="images/btn_forward.gif"/>">
			</a>
			<a onFocus="this.blur();" href="javascript:window.close()">
				<img alt="Schließen" title="Schließen" src="<zabos:url url="images/btn_close.gif"/>">
			</a>
		</div>

	    <jsp:include page="_tree.jsp" flush="true"/>

	    <div id="rights" class="rights">
		    <div id="rights_headline" class="text_info_bold rights_headline" onclick="resize(false)" onmouseover="hover(true);" onmouseout="hover(false);">
				Rechte im aktuellen Kontext<br>	
			</div>
			
			<table width="100%">
				<tr>
					<td colspan="2">
						&nbsp;
					</td>
				</tr>
				<tr>
					<td width="50%" valign="top">
						<c:if test="${sessionScope.user.accessControlList.alarmAusloesenErlaubt}">
							Alarm auslösen<br></c:if>		
						<c:if test="${sessionScope.user.accessControlList.alarmRueckmeldungsReportEmpfangenErlaubt}">
							Empfang Rückmeldungsreport<br></c:if>
						<c:if test="${sessionScope.user.accessControlList.alarmbenachrichtigungEmpfangenErlaubt}">
							Alarm empfangen<br></c:if>	
						<c:if test="${sessionScope.user.accessControlList.alarmhistorieSehenErlaubt}">
							Historie sehen<br></c:if>	
							<br>
						<c:if test="${sessionScope.user.accessControlList.systemKonfigurationAendernErlaubt}">
							System ändern<br></c:if>
						<c:if test="${sessionScope.user.accessControlList.comPortFestlegenErlaubt}">
							COM setzen<br></c:if>			
						<c:if test="${sessionScope.user.accessControlList.lizenzEinsehenErlaubt}">
							Lizenz einsehen<br></c:if>
						<c:if test="${sessionScope.user.accessControlList.probealarmAdministrierenErlaubt}">
							Probealarmadmin<br></c:if>
						<c:if test="${sessionScope.user.accessControlList.systemDeaktivierenErlaubt}">
							Sys deaktivieren<br></c:if>			
						<c:if test="${sessionScope.user.accessControlList.schleifeAbrechnungFestlegenErlaubt}">Schleifenabrechnung<br></c:if>
						<c:if test="${sessionScope.user.accessControlList.bereicheFestlegenErlaubt}">Bereiche ändern<br></c:if>
						<c:if test="${sessionScope.user.accessControlList.leitungsKommentarFestlegenErlaubt}">Leitungskommentare<br></c:if>
						
					</td>
					<td valign="top">
						<c:if test="${sessionScope.user.accessControlList.rollenAnlegenLoeschenErlaubt}">
							Rollen anlegen<br></c:if>
						<c:if test="${sessionScope.user.accessControlList.rollenAendernErlaubt}">
							Rollen ändern<br></c:if>	
						<c:if test="${sessionScope.user.accessControlList.personAnlegenLoeschenErlaubt}">
							Person anlegen/l&ouml;schen<br></c:if>
						<c:if test="${sessionScope.user.accessControlList.personAendernErlaubt}">
							Person ändern<br></c:if>	
						
						<c:if test="${sessionScope.user.accessControlList.eigenePersonAendernErlaubt}">
							Eigene Person ändern<br></c:if>
						<c:if test="${sessionScope.user.accessControlList.eigeneTelefoneAendernErlaubt}">
							Eigene Telefone ändern<br></c:if>	
						<c:if test="${sessionScope.user.accessControlList.eigeneAbwesenheitszeitenAendernErlaubt}">
							Ei. Abwesenheit ändern<br></c:if>	
						<c:if test="${sessionScope.user.accessControlList.organisationAnlegenLoeschenErlaubt}">
							Orga anlegen<br></c:if>
						<c:if test="${sessionScope.user.accessControlList.organisationAendernErlaubt}">
							Orga ändern<br></c:if>		
					
						<c:if test="${sessionScope.user.accessControlList.organisationseinheitAnlegenLoeschenErlaubt}">
							OE anlegen<br></c:if>
						<c:if test="${sessionScope.user.accessControlList.organisationseinheitAendernErlaubt}">
							OE ändern<br></c:if>	
						<c:if test="${sessionScope.user.accessControlList.schleifeAnlegenLoeschenErlaubt}">
							Schleife anlegen<br></c:if>
						<c:if test="${sessionScope.user.accessControlList.schleifeAendernErlaubt}">
							Schleife ändern<br></c:if>	
						<c:if test="${sessionScope.user.accessControlList.funktionstraegerFestlegenErlaubt}">Funktionsträger ändern<br></c:if>
						<c:if test="${sessionScope.user.accessControlList.oeKostenstelleFestlegenErlaubt}">OE-Kostenstelle ändern<br></c:if>
						<c:if test="${sessionScope.user.accessControlList.statistikAnzeigenErlaubt}">Statistik/Berichte<br></c:if>
						<c:if test="${sessionScope.user.accessControlList.personenRollenZuweisenErlaubt}">Personen Rollen zuweisen<br></c:if>
					</td>
				</tr>
			</table>
	    </div>
	    
		<script language="JavaScript" type="text/javascript">
			resize(true);
		</script>		    