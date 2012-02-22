<%@ page language="java" contentType="text/html; charset=utf-8"
	pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="zabos" uri="http://ecw.de/taglibs/zabos"%>

<jsp:include page="../include/_header_popup.jsp" flush="true" />

<div id="titel" class="titel"><span class="text"><em>System</em></span>
</div>

<div id="main" class="main">
<div id="submenu_layer" class="submenu_layer">
<c:if
			test="${sessionScope.user.accessControlList.statistikAnzeigenErlaubt}">
			<table class="popup_base">
	<tr>
		<th class="head_normal" style="width: 20%;" id="tab_nicht_zugeordnet">
			<strong> <a href="#" onclick="showTab('nicht_zugeordnet'); showHelp('nicht_zugeordnet', false);">Nicht
			zug. Personen</a> </strong>
		</th>
		<th class="head_normal" style="width: 20%;" id="tab_bereichsauslastung">
		<strong> <a href="#" onclick="showTab('bereichsauslastung'); showHelp('bereichsauslastung', false);">Auslastung</a> </strong>
			</th>
		<th class="head_normal" style="width: 20%;" id="tab_ohne_handynummer">
				<strong> <a href="#" onclick="showTab('ohne_handynummer'); showHelp('ohne_handynummer', false);">Ohne Handynummer</a> </strong>
		</th>
		<th class="head_normal" style="width: 20%;" id="tab_dummy1"></th>
	</tr>
</table>


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
<!--  / HELP-LAYER --> 
<jsp:include page="nicht_zugeordnet.jsp" flush="true" />
<jsp:include page="bereichsauslastung.jsp" flush="true" />
<jsp:include page="ohne_handynummer.jsp" flush="true" />
<script language="JavaScript" type="text/javascript">
	showTab('<c:out value="${requestScope.tab}" default="nicht_zugeordnet"/>');

	/* Hilfetext laden */
	showHelp('nicht_zugeordnet', false);
</script>
</c:if> 
</div>
<%@ include file="../include/_footer_popup.jsp"%>