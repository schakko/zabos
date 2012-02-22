<%@ page language="java" contentType="text/html; charset=utf-8"
	pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="zabos" uri="http://ecw.de/taglibs/zabos"%>

<jsp:include page="../include/_header_popup.jsp" flush="true" />

<div id="titel" class="titel"><span class="text"><em>System</em></span>
</div>

<div id="main" class="main">
<div id="submenu_layer" class="submenu_layer">
<table class="popup_base">
	<tr>
		<th class="head_normal" style="width: 20%;" id="tab_personen">
		<c:if test='${(!empty sessionScope.user.optionen["personenSucheAktivieren"]) && (sessionScope.user.optionen["personenSucheAktivieren"] == "1")}'>
		<strong>
		<a href="#" onclick="showTab('personen');showHelp('personen',false)">Alle
		Personen</a> </strong>
		</c:if></th>
		<th class="head_normal" style="width: 20%;" id="tab_rollen"><c:if
			test="${sessionScope.user.accessControlList.personenRollenZuweisenErlaubt}">
			<strong> <a href="#"
				onclick="showTab('rollen');showHelp('rollen',false)">Personen</a> </strong>
		</c:if></th>
		<th class="head_normal" style="width: 20%;" id="tab_edit_rollen">
		<c:if
			test="${sessionScope.user.accessControlList.rollenAendernErlaubt || sessionScope.user.accessControlList.rollenAnlegenLoeschenErlaubt}">
			<strong> <a href="#"
				onclick="showTab('edit_rollen');showHelp('edit_rollen',false)">
			Rollen</a> </strong>
		</c:if></th>
		<th class="head_normal" style="width: 20%;" id="tab_organisationen">
		<c:if
			test="${sessionScope.user.accessControlList.systemKonfigurationAendernErlaubt}">
			<strong> <a href="#"
				onclick="showTab('organisationen');showHelp('organisationen',false)">Organisationen</a>
			</strong>
		</c:if></th>
		<th class="head_normal" style="width: 20%;" id="tab_funktionstraeger"><c:if
			test="${sessionScope.user.accessControlList.funktionstraegerFestlegenErlaubt}">
			<strong> <a href="#"
				onclick="showTab('funktionstraeger');showHelp('funktionstraeger',false)">Funktionsträger</a> </strong>
		</c:if></th>
	</tr>
</table>

<table class="popup_base">
	<tr>
		<th class="head_normal" style="width: 20%;" id="tab_konfiguration">
		<c:if
			test="${sessionScope.user.accessControlList.systemKonfigurationAendernErlaubt}">
			<strong> <a href="#"
				onclick="showTab('konfiguration');showHelp('konfiguration',false)">
			Konfiguration</a> </strong>
		</c:if></th>
		<th class="head_normal" style="width: 20%;" id="tab_mc35"><c:if
			test="${sessionScope.user.accessControlList.systemKonfigurationAendernErlaubt}">
			<strong> <a href="#"
				onclick="showTab('mc35');showHelp('mc35',false)"> GSM-Modems</a> </strong>
		</c:if></th>
		<th class="head_normal" style="width: 20%;" id="tab_aliase"><c:if
			test="${sessionScope.user.accessControlList.systemKonfigurationAendernErlaubt}">
			<strong> <a href="#"
				onclick="showTab('aliase');showHelp('aliase',false)"> Aliase</a> </strong>
		</c:if></th>
		<th class="head_normal" style="width: 20%;" id="tab_lizenz"><c:if
			test="${sessionScope.user.accessControlList.lizenzEinsehenErlaubt}">
			<strong> <a href="#"
				onclick="showTab('lizenz');showHelp('lizenz',false)"> Lizenz</a> </strong>
		</c:if></th>
		<th class="head_normal" style="width: 20%;" id="tab_bereiche"><c:if
			test="${sessionScope.user.accessControlList.bereicheFestlegenErlaubt}">
			<strong> <a href="#"
				onclick="showTab('bereiche');showHelp('bereiche',false)">Bereiche</a> </strong>
		</c:if></th>
	</tr>
</table>
<table class="popup_base">
	<tr>
		<th class="head_normal" style="width: 20%;" id="tab_import"><c:if
			test="${sessionScope.user.accessControlList.systemKonfigurationAendernErlaubt}">
			<strong> <a href="#"
				onclick="showTab('import');showHelp('import',false)">Import</a> </strong>
		</c:if>
		</th>
		<th class="head_normal" style="width: 20%;" id="tab_adummy"></th>
		<th class="head_normal" style="width: 20%;" id="tab_aaliase"></th>
		<th class="head_normal" style="width: 20%;" id="tab_alizenz"></th>
		<th class="head_normal" style="width: 20%;" id="tab_aimport"></th>
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
		<div id="helpLayer" class="helpLayer">Die Hilfe wird geladen...
		</div>
		</td>
	</tr>
</table>

<c:if
	test="${sessionScope.user.accessControlList.systemKonfigurationAendernErlaubt}">
	<jsp:include page="konfiguration.jsp" flush="true" />
	<jsp:include page="mc35.jsp" flush="true" />
	<jsp:include page="aliase.jsp" flush="true" />
	<jsp:include page="import.jsp" flush="true" />
	<jsp:include page="organisationen.jsp" flush="true" />
</c:if> 


<c:if
	test="${sessionScope.user.accessControlList.personenRollenZuweisenErlaubt}">
	<jsp:include page="rollen.jsp" flush="true" /> 
</c:if>

<c:if test="${sessionScope.user.accessControlList.rollenAendernErlaubt || sessionScope.user.accessControlList.rollenAnlegenLoeschenErlaubt}">
	<jsp:include page="edit_rollen.jsp" flush="true" /> 
</c:if>

<c:if test="${sessionScope.user.accessControlList.lizenzEinsehenErlaubt}">
	<jsp:include page="lizenz.jsp" flush="true" />
</c:if>

<c:if test="${sessionScope.user.accessControlList.funktionstraegerFestlegenErlaubt}">
	<jsp:include page="funktionstraeger.jsp" flush="true" />
</c:if>
<c:if test="${sessionScope.user.accessControlList.bereicheFestlegenErlaubt}">
	<jsp:include page="bereiche.jsp" flush="true" />
</c:if>
</div>
<!--  / HELP-LAYER --> 
<c:if test='${(!empty sessionScope.user.optionen["personenSucheAktivieren"]) && (sessionScope.user.optionen["personenSucheAktivieren"] == "1")}'>
	<jsp:include page="personen.jsp" flush="true" /> 
	<script language="JavaScript" type="text/javascript">
		showTab('<c:out value="${requestScope.tab}" default="personen"/>');
				
		/* Hilfetext laden */
		showHelp('personen',false);
	</script>
</c:if>

</div>
<%@ include file="../include/_footer_popup.jsp"%>