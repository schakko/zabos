<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="zabos" uri="http://ecw.de/taglibs/zabos"%>
<%@ page language="java" contentType="text/html; charset=utf-8"
    pageEncoding="UTF-8"%>
      <div id="object" title="Hier können Sie Namen und Beschreibung der Organisationseinheit festlegen." class="setting" style="display:none">
		<form name="frmEditOrganisationseinheit" id="frmEditOrganisationseinheit" action="" onKeyUp="Aenderung(this.id)" method="post">
		<input type="hidden" name="OrganisationseinheitId" value="<c:out value="${objOrganisationseinheit.baseId}" default="0"/>" />
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
		        <td><input title="Geben Sie hier den Namen der Organisationseinheit an." name="textName" type="text" class="helpEvent input" value="<c:out value="${objOrganisationseinheit.name}" />" size="30" <c:if test="${!sessionScope.user.accessControlList.organisationseinheitAendernErlaubt}">disabled</c:if> >
		        </td>
		      </tr>
		      <tr>
		        <td class="inner_option text">Beschreibung</td>
		        <td><input title="Geben Sie hier eine Beschreibung der Organisationseinheit ein." name="textBeschreibung" type="text" class="helpEvent input" size="30" value="<c:out value="${objOrganisationseinheit.beschreibung}" />" <c:if test="${!sessionScope.user.accessControlList.organisationseinheitAendernErlaubt}">disabled</c:if> >
		        </td>
		      </tr>
		      <c:if test="${!empty objOrganisationseinheit}">
		      <!-- tr>
		        <td class="inner_option text"></td>
		        <td><input name="submitSchleifenProbetermine" type="submit" class="button" id="submitSchleifenProbetermine" value="Probetermine..."></td>
		      </tr -->
		      </c:if>
		      <tr class="v_spacer">
		        <td></td>
		        <td></td>
		      </tr>
		    </table></td>
		  </tr>
		</table>
		</form>
		<!--  SPACER -->
		<!-- BEDIENELEMENTE -->
		<table class="popup_inner" id="controls">
		  <tr class="list_head">
		    <td>
				<table class="popup_base">
					<tr>
						<td class="h_spacer"></td>
						<td>
							<c:if test="${sessionScope.user.accessControlList.organisationseinheitAendernErlaubt}">
								<div align="left" id="controlButtons">
									<input title="Klicken Sie zum Übernehmen der Einstellungen auf 'Speichern'." name="inputSave" type="submit" class="helpEvent button" value="Speichern" onclick="document.forms.frmEditOrganisationseinheit.action='<zabos:url url="controller/organisationseinheit/?do=doUpdateOrganisationseinheit&amp;submit=true"/>';document.forms.frmEditOrganisationseinheit.submit()">
								</div>
							</c:if>
						</td>
						<td>
							<c:if test="${sessionScope.user.accessControlList.organisationseinheitAendernErlaubt}">						
								<div align="center" style="margin-left:38px;">
									<input title="Wollen Sie die Änderungen verwerfen klicken Sie auf 'Abbrechen'." name="inputCancel" type="reset" class="helpEvent button" onclick="document.forms.frmEditOrganisationseinheit.reset()"  value="Zurücksetzen">
								</div>
							</c:if>
						</td>
						<c:if test="${!empty objOrganisationseinheit}">
							<td>
								<c:if test="${sessionScope.user.accessControlList.organisationseinheitAnlegenLoeschenErlaubt}">													
									<div align="right" style="margin-left:38px;"><input name="inputDelete" title="Wollen Sie die Organisationseinheit löschen, klicken Sie auf 'Entfernen.'" type="submit" class="helpEvent button" value="Entfernen" onclick="document.forms.frmEditOrganisationseinheit.action='<zabos:url url="controller/organisationseinheit/?do=doDeleteOrganisationseinheit&amp;submit=true"/>';document.forms.frmEditOrganisationseinheit.submit()">
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
		<!--  BESCHREIBUNG - BEDIENELEMENTE -->
		<table class="popup_inner">
		  <tr>
		    <td>
				<table class="popup_base">
				  <tr class="v_spacer">
					<td class="h_spacer"></td>
					<td></td>
					<td></td>
				  </tr>
				  <tr>
					<td></td>
					<td class="text_info">Klicken Sie zum &Uuml;bernehmen der Einstellungen auf &quot;Speichern&quot;. <br>
					  Wollen Sie die &Auml;nderungen verwerfen, klicken Sie auf &quot;Abbrechen&quot;.</td>
					<td></td>
				  </tr>
				  <tr class="v_spacer">
					<td></td>
					<td></td>
					<td></td>
				  </tr>
				</table>
			</td>
		  </tr>
		</table>
	</div>