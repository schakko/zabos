<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="zabos" uri="http://ecw.de/taglibs/zabos"%>
<%@ page language="java" contentType="text/html; charset=utf-8"
    pageEncoding="UTF-8"%>
      <div id="object" title="Hier können Sie Namen und Beschreibung der Organisation festlegen." class="setting" style="display:none">
		<form name="frmEditOrganisation" id="frmEditOrganisation" action="" onKeyUp="Aenderung(this.id)" method="post">
		<input type="hidden" name="OrganisationId" value="<c:out value="${objOrganisation.baseId}" default="0"/>" />
		<table class="popup_base">
		  <tr class="list_head">
		    <td class="h_spacer"></td>
		    <td>Einstellungen:</td>
		  </tr>
		  <tr class="v_spacer">
		    <td></td>
		    <td></td>
		  </tr>
		  <tr>
		    <td></td>
		    <td><table class="popup_inner">
		      <tr>
		        <td class="inner_option text">Name</td>
		        <td>
		        	<input name="textName" type="text" title="Geben Sie hier den Namen der Organisation an." class="helpEvent input" value="<c:out value="${objOrganisation.name}" default="${param.textName}"/>" size="30" <c:if test="${!sessionScope.user.accessControlList.organisationAendernErlaubt}">disabled</c:if> >
		        </td>
		      </tr>
		      <tr>
		        <td class="inner_option text">Beschreibung</td>
		        <td><input name="textBeschreibung" title="Geben Sie hier eine Beschreibung der Organisation ein." type="text" class="helpEvent input" size="30" value="<c:out value="${objOrganisation.beschreibung}" default="${param.textBeschreibung}"/>" <c:if test="${!sessionScope.user.accessControlList.organisationAendernErlaubt}">disabled</c:if>>
		        </td>
		      </tr>
		      <tr class="v_spacer">
		        <td></td>
		        <td></td>
		      </tr>
		    </table></td>
		  </tr>
		</table>
		</form>
		<!--  SPACER -->
		<table>
		  <tr class="v_spacer">
		    <td></td>
		  </tr>
		</table>
		<!-- BEDIENELEMENTE -->
		<table class="popup_inner" id="controls">
		  <tr class="list_head">
		    <td>
				<table class="popup_base">
					<tr>
						<td class="h_spacer"></td>
						<td>					
							<c:if test="${sessionScope.user.accessControlList.organisationAendernErlaubt}">
								<div align="left" id="controlButtons">
									<input title="Klicken Sie zum Übernehmen der Einstellungen auf 'Speichern'." name="inputSave" type="submit" class="helpEvent button" value="Speichern" onclick="document.forms.frmEditOrganisation.action='<zabos:url url="controller/organisation/?do=doUpdateOrganisation&amp;submit=true"/>';document.forms.frmEditOrganisation.submit()">
								</div>
							</c:if>
						</td>
						<td>
							<c:if test="${sessionScope.user.accessControlList.organisationAendernErlaubt}">				
								<div align="center" style="margin-left:38px;">														
									<input name="inputCancel" title="Wollen Sie die Änderungen verwerfen klicken Sie auf 'Abbrechen'." type="button" onclick="document.forms.frmEditOrganisation.reset()" class="helpEvent button" value="Zurücksetzen">
								</div>
							</c:if>								
						</td>
						
						<c:if test="${!empty objOrganisation}">
						<td>
							<c:if test="${sessionScope.user.accessControlList.organisationAnlegenLoeschenErlaubt}">
								<div align="right" style="margin-left:38px;">
									<input name="inputDelete" type="submit" title="Wollen Sie die Organisationseinheit löschen, klicken Sie auf 'Entfernen.'" class="helpEvent button" value="Entfernen" onclick="document.forms.frmEditOrganisation.action='<zabos:url url="controller/organisation/?do=doDeleteOrganisation&amp;submit=true" />';document.forms.frmEditOrganisation.submit()">
								</div>
							</c:if>
						</td>
						</c:if>
						
						<td class="h_spacer"></td>
					</tr>
				</table>
			</td>
		  </tr>
		</table>
      </div>