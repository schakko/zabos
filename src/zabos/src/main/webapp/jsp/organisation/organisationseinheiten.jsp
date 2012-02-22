<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="zabos" uri="http://ecw.de/taglibs/zabos"%>
<%@ page language="java" contentType="text/html; charset=utf-8"
    pageEncoding="UTF-8"%>
      <div class="settings" title="Folgende Organisationseinheiten sind dieser Organisation zugeordnet." id="organisationseinheiten" style="display:none">
		<c:if test="${sessionScope.user.accessControlList.organisationAendernErlaubt}">
		<c:if test="${!empty objOrganisation}">

		<table class="popup_base">
		  <tr class="list_head">
		    <td class="h_spacer"></td>
		    <td>Zugeh&ouml;rige Organisationseinheiten</td>
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
		        <td class="inner_option_large text_bold">Einheitenname</td>
		        <td class="text_bold">Beschreibung
		        </td>
		      </tr>
		    </table>
						
			<div class="scroll"><table class="popup_inner_scroll">
				  <c:forEach items="${arrOrganisationsEinheitenAvailable.data}" var="data">
					<c:if test="${data.geloescht == false}">			
					  <tr>
						<td class="text inner_option_large"><a title="Klicken Sie hier um die Einstellungen der Organisationseinheit '<c:out value="${data.name}"/>' einzusehen oder zu bearbeiten." class="helpEvent" href="<zabos:url url="controller/organisationseinheit/"/>?OrganisationseinheitId=<c:out value="${data.baseId}"/>"><c:out value="${data.name}"/></a>
						</td>
						<td class="text">
							<c:choose>
								<c:when test="${!empty data.beschreibung}">
									<c:out value="${data.beschreibung}"/>
								</c:when>
								<c:otherwise>
									Keine Beschreibung vohanden
								</c:otherwise>
							</c:choose>
						</td>
					  </tr>
					</c:if>
				  </c:forEach>			
				  
				  <c:if test="${arrOrganisationsEinheitenAvailable.size == 0}">		    
					  <tr>
						<td colspan="2" class="text">Es sind keine zugeh&ouml;rigen Einheiten vorhanden</td>
					  </tr>
			      </c:if>
		      <tr>
		        <td colspan="2"></td>
		      </tr>
		      <tr class="v_spacer">
		        <td></td>
		        <td></td>
		      </tr>
		    </table></div></td>
		  </tr>
		</table>
		<table class="popup_inner">
		  <tr class="list_head">
		    <td>
				<table class="popup_base">
					<tr>
						<td class="h_spacer"></td>
						<td>
							<c:if test="${sessionScope.user.accessControlList.organisationseinheitAnlegenLoeschenErlaubt}">
								<div align="left">
									<input title="Um eine neue Organisationseinheit für diese Organisation anzulegen, klicken Sie auf auf 'Neue Einheit'." name="inputNeueSchleife" class="helpEvent button" type="submit" value="Neue Einheit" onclick="window.location.href='<zabos:url url="controller/organisationseinheit/"/>?OrganisationId=${objOrganisation.baseId}&amp;OrganisationseinheitId=0'">
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
	</c:if>
  </div>