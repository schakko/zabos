<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="zabos" uri="http://ecw.de/taglibs/zabos"%>
<%@ page language="java" contentType="text/html; charset=utf-8"
    pageEncoding="UTF-8"%>
      <div class="settings" title="Folgende Schleifen sind dieser Organisationseinheit zugeordnet." id="schleifen" style="display:none">
		<c:if test="${sessionScope.user.accessControlList.organisationseinheitAendernErlaubt}">
		<c:if test="${!empty objOrganisationseinheit}">

		<table class="popup_base">
		  <tr class="list_head">
		    <td class="h_spacer"></td>
		    <td>Zugeh&ouml;rige Schleifen</td>
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
		        <td class="inner_option_large text_bold">Schleifenname</td>
		        <td class="text_bold">Beschreibung
		        </td>
		      </tr>
		    </table>
			
			
			<div class="scroll"><table class="popup_inner_scroll">
				  <c:forEach items="${arrSchleifenAvailable.data}" var="data">
					<c:if test="${data.geloescht == false}">			
					  <tr>
						<td class="text inner_option_large"><a title="Klicken Sie hier um die Einstellungen der Schleife '<c:out value="${data.name} (${data.kuerzel})"/>' einzusehen oder zu bearbeiten." class="helpEvent" href="<zabos:url url="controller/schleife/"/>?SchleifeId=<c:out value="${data.baseId}"/>"><c:out value="${data.kuerzel}"/></a>
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
				  
				  <c:if test="${arrSchleifenAvailable.size == 0}">		    
					  <tr>
						<td colspan="2" class="text">Es sind keine Schleifen vorhanden</td>
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
							<c:if test="${sessionScope.user.accessControlList.schleifeAnlegenLoeschenErlaubt}">
								<div align="left">
									<input title="Um eine neue Schleife für diese Organisationseinheit anzulegen, klicken Sie auf auf 'Neue Schleife'." name="inputNeueSchleife" class="helpEvent button" type="submit" value="Neue Schleife" onclick="window.location.href='<zabos:url url="controller/schleife/"/>?OrganisationseinheitId=${objOrganisationseinheit.baseId}&amp;SchleifeId=0'">
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