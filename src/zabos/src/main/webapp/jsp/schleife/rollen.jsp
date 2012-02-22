<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="zabos" uri="http://ecw.de/taglibs/zabos"%>
<%@ page language="java" contentType="text/html; charset=utf-8"
    pageEncoding="utf-8"%>
<jsp:useBean id="beanPersonenDelta" class="de.ecw.zabos.frontend.beans.DeltaBean" type="de.ecw.zabos.frontend.beans.DeltaBean" scope="page"/>
  <div class="settings" title="Wählen Sie hier für diese Schleife die Rolle aus, die sie einer Person zuteilen wollen." id="rollen" style="display:none">
	  <c:if test="${sessionScope.user.accessControlList.personenRollenZuweisenErlaubt}">
		<table class="popup_base">
		  <tr class="list_head">
		    <td class="h_spacer"></td>
		    <td class="dropdown">
		      <form method="post" action="<zabos:url url="controller/schleife/?tab=rollen"/>" name="frmSelectRollen" id="frmSelectRollen">
		        <select title="Klicken Sie hier, um eine Rolle auszuwählen und dieser Schleife Personen in dieser Rolle zuzuteilen." id="RolleId" name="RolleId" class="helpEvent input" onchange="document.forms.frmSelectRollen.submit()">
		          <option value="0" <c:if test="${!empty objRolle}">selected</c:if>>&lt;Rolle ausw&auml;hlen&gt;</option>
		          <c:forEach items="${arrKompatibleRollenAvailable.data}" var="data">
		            <c:if test="${data.geloescht == false}">
		              <option value="<c:out value="${data.baseId}"/>" <c:if test="${data.baseId == objRolle.baseId}">selected</c:if>><c:out value="${data.name}"/></option>
		        	</c:if>
		          </c:forEach>
		        </select>
		      </form>
		    </td>
		  </tr>
		</table>
		<c:if test="${!empty objRolle}">
		<form name="frmEditRollen" id="frmEditRollen" action="" onKeyUp="Aenderung(this.id)" method="post">
		<input type="hidden" name="RolleId" value="<c:out value="${objRolle.baseId}"/>" />
		<table class="popup_base">
		  <tr>
		    <td class="h_spacer"></td>
		  </tr>
		  <tr class="v_spacer">
		    <td></td>
		    <td></td>
		  </tr>
		  <tr>
		    <th></th>
		    <td>
		      <table class="inner">
		        <tr>
		          <th class="text_bold">Zugeordnete Personen</th>
		          <th width="184"></th>
		          <th class="text_bold">Verf&uuml;gbare Personen</th>
		        </tr>
		        <tr>
		          <td valign="top">
		            <select name="selectPersonenAssigned" size="29" onDblClick="javascript:moveSelectedEntries('selectPersonenAssigned','selectPersonenAvailable');" multiple id="selectPersonenAssigned">
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
					<div id="inputNeuePerson"></div>		            
		          </td>
		          <td valign="top">
		          <div align="center"> <br>
		            <table border="0" align="center" cellpadding="0" cellspacing="0">
		              <tr><td height="60"></td></tr><tr>
		                <th height="70"><input class="button" type="button" name="btnAdd2" id="btnAdd22" value="< Hinzuf&uuml;gen" onclick="javascript:moveSelectedEntries('selectPersonenAvailable','selectPersonenAssigned'); return false" /></th>
		              </tr>
		              <tr>
		                <th><input class="button" type="button" name="btnDel2" id="btnDel22" value="Entfernen >" onclick="javascript:moveSelectedEntries('selectPersonenAssigned','selectPersonenAvailable'); return false" /></th>
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
				 <select name="selectOrganisationen" id="selectOrganisationen" onchange="out_findOrganisationseinheitenInOrganisation();out_findPersonenInKontext();"></select>
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
				 <select name="selectOrganisationseinheiten" id="selectOrganisationseinheiten" onchange="out_findPersonenInKontext();"></select>
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
		            <select  name="selectPersonenAvailable" onDblClick="javascript:moveSelectedEntries('selectPersonenAvailable','selectPersonenAssigned');" size="20" multiple id="selectPersonenAvailable">
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
		            			Personen suchen:
		            			<span id="indicatorSP" style="visibility: hidden;" >
			            			<img alt="#" src="<zabos:url url="images/indicator.gif"/>">
		            			</span>
	            			</td>
            			</tr>
            		</table>
            		
		            <table class="inner">
            			<tr>
            				<td>
            					<input type="text" class="input_long" name="inputSearch" id="inputSearch" value="" onkeyup="hideObject('indicatorSP',false);resetList('selectOrganisationen');resetList('selectOrganisationseinheiten');window.setTimeout('out_findPersonenByPattern(in_schleifenEditRollen)',1000)" />		
		            		</td>
	            		</tr>
            		</table>            		
		            
		            
		            
		          </td>
		        </tr>
		    </table>
		    </td>
		  </tr>
		</table>
<!-- BEDIENELEMENTE -->
		<table class="popup_inner">
		<tr class="v_spacer"><td></td></tr>
		  <tr class="list_head">
		    <td>
				<table class="popup_base">
					<tr>
						<td class="h_spacer"></td>
							<c:if test="${!empty objRolle}">
								<td>
									<div align="left">
											<input name="inputSave" id='inputSave' type="button" class="button" value="Speichern" onclick="getObject('inputSave').setAttribute('disabled', 'true');selectAllEntries('selectPersonenAssigned');document.forms.frmEditRollen.action='<zabos:url url="controller/schleife/?do=doUpdateRollenKontext&amp;tab=rollen&amp;submit=true&amp;selectOrganisationId=${requestScope.selectOrganisationId}&amp;selectOrganisationseinheitId=${requestScope.selectOrganisationseinheitId}"/>';document.forms.frmEditRollen.submit()">
									</div>
								</td>
							</c:if>
						<td width="100%">
						</td>
					</tr>
				</table>
			</td>
		  </tr>
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
		</c:if>
		
		<table class="popup_base">
			<tr class="v_spacer"><td colspan="2"></td></tr>
			<tr class="list_head">
				<td class="h_spacer"></td>
				<td>Personen und deren Rollen innerhalb dieser Schleife (direkt)</td>
			</tr>
			<tr class="v_spacer">
				<td></td>
				<td></td>
			</tr>
			<tr>
				<td></td>
				<td>
					<table class="popup_inner">
					  <tr>
						<td class="inner_option_large text_bold">Person</td>
						<td class="text_bold">Rollen in dieser Schleife
						</td>
				      </tr>
				    </table>
				    <!-- div id="deleteInfo" style="">Hinweis: Klicken Sie auf das Mülleimer-Symbol um die Person in dieser Rolle aus dieser Einheit zu entfernen</div -->
					<div class="scroll">
					  <table class="popup_inner_scroll">				    
							<c:forEach items="${arrPersonenMitRollen.data}" var="data">					  
							  <tr>
								<td class="inner_option_large text"> <a title="Klicken Sie hier um die Einstellungen von '${data.person.displayName}' zu bearbeiten." class="helpEvent" href="<zabos:url url="controller/person/" />?PersonId=${data.person.baseId.longValue}">${data.person.displayName}</a></td>
								<td class="text">          
										<c:forEach items="${data.rollen}" var="rollen" varStatus="statusVar">
											<span class="helpEvent" title="${data.person.displayName} ist dieser Einheit in der Rolle '${rollen.name}' zugeteilt.">${rollen.name}</span>
											<c:if test="${sessionScope.user.accessControlList.personAendernErlaubt == true}">
											<a href="<zabos:url url="controller/schleife/" />?PersonId=${data.person.baseId.longValue}&amp;RolleId=${rollen.baseId.longValue}&amp;SchleifeId=${sessionScope.user.ctxSchleife.baseId.longValue}&amp;tab=rollen&amp;do=doRemoveRolleFromPerson&amp;submit=true">
												<img title="Klicken Sie auf das Mülleimer-Symbol um die Rollenzuordnung '${rollen.name}' dieser Person innerhalb von 'System' zu entfernen." id="trash${rollen.name}${data.person.baseId.longValue}" onMouseOver="showHelp('trash${rollen.name}${data.person.baseId.longValue}',true);" onMouseOut="showHelp('rollen',false);" src="<zabos:url url='images/btn_trash.gif'/>"></a>
											</c:if>
											<c:if test="${statusVar.last == false}"> ,</c:if>
										</c:forEach>
								</td>
						      </tr>
							  </c:forEach>
					  </table>
					</div>
			  </td>				
			</tr>
			<tr class="v_spacer">
				<td></td>
				<td></td>
			</tr>
		</table>
		
		<table class="popup_base">
			<tr class="v_spacer"><td colspan="2"></td></tr>
			<tr class="list_head">
				<td class="h_spacer"></td>
				<td>Personen mit geerbten Rollen innerhalb dieser Schleife</td>
			</tr>
			<tr class="v_spacer">
				<td></td>
				<td></td>
			</tr>
			<tr>
				<td></td>
				<td>
					<table class="popup_inner">
					  <tr>
						<td class="inner_option_large text_bold">Person</td>
						<td class="text_bold">geerbte Rollen in dieser Schleife
						</td>
				      </tr>
				    </table>
					<div class="scroll">
					  <table class="popup_inner_scroll">				    
							<c:forEach items="${arrPersonenMitRollenVererbt.data}" var="data">					  
							  <tr>
								<td class="inner_option_large text">${data.person.displayName}</td>
								<td class="text">          
										<c:forEach items="${data.rollen}" var="rollen" varStatus="statusVar">
											${rollen.name}<c:if test="${statusVar.last == false}">,</c:if>
										</c:forEach>
								</td>
						      </tr>
							  </c:forEach>
					  </table>
					</div>
			  </td>				
			</tr>
			<tr class="v_spacer">
				<td></td>
				<td></td>
			</tr>
		</table>		
		<table class="popup_inner">
		  <tr class="list_head">
		    <td>
				<table class="popup_base">
					<tr>
						<td class="h_spacer"></td>
						<td>
							<c:if test="${sessionScope.user.accessControlList.personAnlegenLoeschenErlaubt}">
								<div align="left">								
								</div>
							</c:if>	
						</td>
						<td width="100%"></td>	
						<td class="h_spacer"></td>
					</tr>
				</table>
			</td>
		  </tr>
		</table>	
	  </c:if>	
	</div>