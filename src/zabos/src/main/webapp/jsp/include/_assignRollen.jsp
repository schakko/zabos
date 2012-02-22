<%@ page language="java" contentType="text/html; charset=utf-8"
    pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="zabos" uri="http://ecw.de/taglibs/zabos"%>
<jsp:useBean id="beanPersonenDelta" class="de.ecw.zabos.frontend.beans.DeltaBean" type="de.ecw.zabos.frontend.beans.DeltaBean" scope="page"/>
<c:if test="${!empty objRolle}">

<form name="frmEditRollen" id="frmEditRollen" action="" onKeyUp="Aenderung(this.id)" method="post">
	<input type="hidden" name="RolleId" value="<c:out value='${objRolle.baseId}'/>" />
	<table class="popup_base">
	  <tr>
	  	<td></td>
	    <td>
		<table class="popup">
			<tr>
			  <th class="text">Zugeordnete Personen</th>
			  <th width="184"></th>
			  <th class="text">Verf&uuml;gbare Personen</th>
			</tr>
			<tr>
			  <td valign="top">			  
				<select id="selectPersonenAssigned" title="Diese Liste zeigt Ihnen alle Personen an, die in der gewählten Rolle zugeordnet sind" onMouseOver="showHelp('selectPersonenAssigned', true)" onMouseOut="showHelp('rollen', false)" name="selectPersonenAssigned" size="29" multiple id="selectPersonenAssigned" onDblClick="javascript:moveSelectedEntries('selectPersonenAssigned','selectPersonenAvailable');">
				  <c:if test="${!empty arrPersonenAssigned}">
					<c:forEach items="${arrPersonenAssigned.data}" var="data">
					  <c:set target="${beanPersonenDelta}" property="entry" value="${data.baseId}" />
					  <option value="<c:out value="${data.baseId}"/>"><c:out value="${data.displayName}"/></option>       			
					</c:forEach>
							
				  </c:if>
				</select>
				<table>
					<tr class="v_spacer">
						<td>
						</td>
					</tr>
				</table>
				<div id="inputNeuePerson" onMouseOver="showHelp('inputNeuePerson',true)" onMouseOut="showHelp('rollen',false)" title="Klicken Sie hier, um eine neue Person in der gewählten Rolle und in der gewählten Einheit einzutragen"></div>
			  </td>
			  <td valign="top">
				<div align="center"> <br>
		            <table border="0" align="center" cellpadding="0" cellspacing="0">
		              <tr><td height="60"></td></tr><tr>
		                <th height="70"><input title="Klicken Sie hier, um alle markierten Personen in der gewählten Rolle zuzuordnen" onMouseOver="showHelp('btnAdd', true)" onMouseOut="showHelp('rollen', false)" class="button" type="button" name="btnAdd" id="btnAdd" value="< Hinzuf&uuml;gen" onclick="javascript:moveSelectedEntries('selectPersonenAvailable','selectPersonenAssigned'); return false" /></th>
		              </tr>
		              <tr>
		                <th><input title="Klicken Sie hier, um alle markierten Personen aus der gewählten Rolle zu entfernen" onMouseOver="showHelp('btnDel', true)" onMouseOut="showHelp('rollen', false)" class="button" type="button" name="btnDel" id="btnDel" value="Entfernen >" onclick="javascript:moveSelectedEntries('selectPersonenAssigned','selectPersonenAvailable'); return false" /></th>
		              </tr>
		            </table>
	            </div>
			  </td>
			  <td valign="top">
  				<table class="inner">
	            	<tr class="list_head">
	            		<td class="h_spacer_5"></td>
	            		<td class="text">
	            			In Organisation:
	        			</td>
	    			</tr>
	    		</table>
				 <select id="selectOrganisationen"  title="Diese Liste zeigt Ihnen alle verfügbaren Organisationen an. Wählen Sie eine Organisation aus, um sich nur deren Kameraden anzeigen zu lassen." onMouseOver="showHelp('selectOrganisationen', true)" onMouseOut="showHelp('rollen', false)" name="selectOrganisationen" id="selectOrganisationen" onchange="out_findOrganisationseinheitenInOrganisation();out_findPersonenInKontext();"></select>
				 
				 				<table class="inner">
	            	<tr class="v_spacer">
						<td colspan="2">
	        			</td>
	    			</tr>
	            	<tr class="list_head">
	            		<td class="h_spacer_5"></td>
	            		<td class="text">
	            			In Einheit:
	        			</td>
	    			</tr>
	    		</table>
				 <select id="selectOrganisationseinheiten"  title="Diese Liste zeigt Ihnen alle verfügbaren Organisationseinheiten an. Wählen Sie eine Organisationseinheit aus, um sich nur deren Kameraden anzeigen zu lassen." onMouseOver="showHelp('selectOrganisationseinheiten', true)" onMouseOut="showHelp('rollen', false)" name="selectOrganisationseinheiten" id="selectOrganisationseinheiten" onchange="out_findPersonenInKontext();"></select>

				<table class="inner">
	            	<tr class="v_spacer">
						<td colspan="2">
	        			</td>
	    			</tr>
	            	<tr class="list_head">
	            		<td class="h_spacer_5"></td>
	            		<td class="text_bold">
	            			Personen:
	        			</td>
	    			</tr>
	    		</table>	
				<select title="Diese Liste zeigt Ihnen alle verfügbaren Personen an, die Sie in der gewählten Rolle zugeordnen können" onMouseOver="showHelp('selectPersonenAvailable', true)" onMouseOut="showHelp('rollen', false)" name="selectPersonenAvailable" size="20" multiple id="selectPersonenAvailable" onDblClick="javascript:moveSelectedEntries('selectPersonenAvailable','selectPersonenAssigned');">
				  <c:if test="${!empty arrPersonenAvailable}">
					<c:forEach items="${arrPersonenAvailable.data}" var="data">
					  <c:set target="${beanPersonenDelta}" property="test" value="${data.baseId}"/>
					  <c:if test="${beanPersonenDelta.result == false}">
						<option value="<c:out value="${data.personId}"/>"><c:out value="${data.displayName}"/></option>       			
					  </c:if>
					</c:forEach>
				  </c:if>
				</select>
	
			            <table class="inner">
			            	<tr class="v_spacer">
								<td colspan="2">
		            			</td>
	            			</tr>
			            	<tr class="list_head">
			            		<td class="h_spacer"></td>
			            		<td class="text left">
			            			In allen Personen suchen:
		            			<span id="indicatorSP" style="visibility: hidden;" >
			            			<img alt="#" src="<zabos:url url="images/indicator.gif"/>">
		            			</span>
		            			</td>
	            			</tr>
	            		</table>
	            		
			            <table class="inner">
	            			<tr>
	            				<td>
	            					<input title="Geben Sie in diesem Feld den Namen einer Person ein, um diese im System zu suchen" onMouseOver="showHelp('inputSearch', true)" onMouseOut="showHelp('rollen', false)"  type="text" class="input_long" name="inputSearch" id="inputSearch" value="" onkeyup="hideObject('indicatorSP',false);resetList('selectOrganisationen');resetList('selectOrganisationseinheiten');window.setTimeout('out_findPersonenByPattern(in_schleifenEditRollen)',1000)" /><!-- in_systemListPersonen -->		
			            		</td>
		            		</tr>
	            		</table>  
	
	
			  </td>
			</tr>
			</table>
		</td>
	  </tr>
	  <tr class="v_spacer"><td colspan="2"></td></tr>
	</table>
</form>
<script type="text/javascript" language="JavaScript">
    // Nun die Select-Boxen füllen. Muss UNBEDINGT geladen werden	
	// Organisationen finden => SelectBox gefüllt mit Organisationen, Default: "Nicht filtern"
	out_findOrganisationen();
	// OEs finden => SelectBox gefüllt mit OEs, Default: "Nicht filtern"
	out_findOrganisationseinheitenInOrganisation();
	
	// Link zum Anlegen einer neuen Person im Kontext erstellen 
	out_inputNeuePerson("${objRolle.baseId}","${sessionScope.user.kontextType.id}","${sessionScope.user.ctxO.baseId.longValue}","${sessionScope.user.ctxOE.baseId.longValue}","${sessionScope.user.ctxSchleife.baseId.longValue}");
</script>
<!-- BEDIENELEMENTE -->
</c:if>
