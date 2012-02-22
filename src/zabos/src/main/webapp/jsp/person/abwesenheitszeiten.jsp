<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="zabos" uri="http://ecw.de/taglibs/zabos"%>
<%@ page language="java" contentType="text/html; charset=utf-8"
    pageEncoding="UTF-8"%>
      <div id="abwesenheitszeiten" class="setting" style="display:none">
		<c:if test="${sessionScope.user.accessControlList.personAendernErlaubt || (sessionScope.user.accessControlList.eigeneAbwesenheitszeitenAendernErlaubt && (objPerson.personId.longValue == sessionScope.user.person.personId.longValue))}">
		<form name="frmEditAbwesenheitszeit" id="frmEditAbwesenheitszeit" action="" onKeyUp="Aenderung(this.id)" method="post" accept-charset="UTF-8">
		<input type="hidden" name="PersonId" value="<c:out value="${objPerson.baseId}" default="0"/>" />
		<table class="popup_base">
		  <tr class="list_head">
		    <td class="h_spacer"></td>
		    <td>Abwesenheitszeiten</td>
		  </tr>
		      <tr>
		        <td class="text inner_option_large">Benutzer ist abwesend</td>
		        <td class="text"><input name="isAbwesendBis" id="isAbwesendBis" onchange="javascript:toggleActivationOnElement('isAbwesendBis','textAbwesendBisDatum',false);toggleActivationOnElement('isAbwesendBis','textAbwesendBisZeit',false);javascript:toggleActivationOnElement('isAbwesendBis','textAbwesendVonDatum',false);toggleActivationOnElement('isAbwesendBis','textAbwesendVonZeit',false);"  type="checkbox" class="input" value="1" <c:if test="${!empty requestScope.isAbwesendBis}">checked</c:if> /></td>
		      </tr>
		      <tr>
		        <td class="text inner_option_large">Abwesend von</td>
		        <td class="text">
		          <input name="textAbwesendVonDatum" id="textAbwesendVonDatum"  type="text" class="input" value="<zabos:formatts unixTime="${objPerson.abwesendVon}" format="date" defaultString="dd.mm.yyyy" />" size="11" <c:if test="${empty requestScope.isAbwesendBis}">disabled</c:if> maxlength="10" /> 
		          <input name="textAbwesendVonZeit" id="textAbwesendVonZeit"  type="text" class="input" value="<zabos:formatts unixTime="${objPerson.abwesendVon}" format="time" defaultString="HH:ii" />" size="6" <c:if test="${empty requestScope.isAbwesendBis}">disabled</c:if> maxlength="5" />       
				</td>
		      </tr>
		      <tr>
		        <td class="text inner_option_large">Abwesend bis</td>
		        <td class="text">
		          <input name="textAbwesendBisDatum" id="textAbwesendBisDatum"  type="text" class="input" value="<zabos:formatts unixTime="${objPerson.abwesendBis}" format="date" defaultString="dd.mm.yyyy" />" size="11" <c:if test="${empty requestScope.isAbwesendBis}">disabled</c:if> maxlength="10" /> 
		          <input name="textAbwesendBisZeit" id="textAbwesendBisZeit"  type="text" class="input" value="<zabos:formatts unixTime="${objPerson.abwesendBis}" format="time" defaultString="HH:ii" />" size="6" <c:if test="${empty requestScope.isAbwesendBis}">disabled</c:if> maxlength="5" />       
				</td>
		      </tr>
		</table>
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
							<c:if test="${sessionScope.user.accessControlList.personAendernErlaubt || (sessionScope.user.accessControlList.eigeneAbwesenheitszeitenAendernErlaubt && (objPerson.personId.longValue == sessionScope.user.person.personId.longValue))}">
								<div align="left" id="divSave">
									<input name="inputSave" type="submit" class="button" value="Speichern" onclick="document.forms.frmEditAbwesenheitszeit.action='<zabos:url url="controller/person/"/>?do=doUpdateAbwesenheitszeit&amp;submit=true&amp;PersonId=${objPerson.baseId}&amp;tab=abwesenheitszeiten<c:if test="${!empty param.OrganisationId}">&amp;OrganisationId=${param.OrganisationId}</c:if><c:if test="${!empty param.OrganisationsEinheitId}">&amp;OrganisationsEinheitId=${param.OrganisationsEinheitId}</c:if><c:if test="${!empty param.SchleifeId}">&amp;SchleifeId=${param.SchleifeId}</c:if>';document.forms.frmEditAbwesenheitszeit.submit()">
								</div>
							</c:if>
						</td>
						<td>
							<c:if test="${sessionScope.user.accessControlList.personAendernErlaubt || (sessionScope.user.accessControlList.eigeneAbwesenheitszeitenAendernErlaubt && (objPerson.personId.longValue == sessionScope.user.person.personId.longValue))}">
								<div align="center">
									<input name="inputCancel" type="submit" onclick="document.forms.frmEditAbwesenheitszeit.reset()" class="button" value="Zurücksetzen">
								</div>
							</c:if>
						</td>
						<td>
						<td class="h_spacer"></td>
					</tr>
				</table>
			</td>
		  </tr>
		</table>
		</form>
		</c:if>
	  </div>