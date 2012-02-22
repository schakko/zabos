<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="zabos" uri="http://ecw.de/taglibs/zabos"%>
<%@ page language="java" contentType="text/html; charset=utf-8"
	pageEncoding="UTF-8"%>

<div class="settings"
	title="Folgende Organisation sind in dem System vorhanden."
	id="organisationen" style="display: none"><c:if
	test="${sessionScope.user.accessControlList.systemKonfigurationAendernErlaubt}">
	<table class="popup_base">
		<tr class="list_head">
			<td class="h_spacer"></td>
			<td>Vorhandene Organisationen</td>
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
					<td class="inner_option_large text_bold">Organisationsname</td>
					<td class="text_bold">Beschreibung</td>
				</tr>
			</table>

			<div class="scroll">
			<table class="popup_inner_scroll">
				<c:forEach items="${arrOrganisationenAvailable.data}" var="data">
					<c:if test="${data.geloescht == false}">
						<tr>
							<td class="text inner_option_large"><a class="helpEvent"
								title="Klicken Sie hier um die Einstellungen der Organisation '<c:out value="${data.name}"/>' einzusehen oder zu bearbeiten."
								href="<zabos:url url="controller/organisation/"/>?OrganisationId=<c:out value="${data.baseId}"/>"><c:out
								value="${data.name}" /></a></td>
							<td class="text"><c:choose>
								<c:when test="${!empty data.beschreibung}">
									<c:out value="${data.beschreibung}" />
								</c:when>
								<c:otherwise>
													Keine Beschreibung vohanden
												</c:otherwise>
							</c:choose></td>
						</tr>
					</c:if>
				</c:forEach>

				<c:if test="${arrOrganisationenAvailable.size == 0}">
					<tr>
						<td colspan="2" class="text">Es sind keine zugeh&ouml;rigen
						Einheiten vorhanden</td>
					</tr>
				</c:if>
				<tr>
					<td colspan="2"></td>
				</tr>
				<tr class="v_spacer">
					<td></td>
					<td></td>
				</tr>
			</table>
			</div>

			</td>
		</tr>
	</table>

	<table class="popup_inner">
		<tr class="list_head">
			<td>
			<table class="popup_base">
				<tr>
					<td class="h_spacer"></td>
					<td>
					<div align="left"><c:if
						test="${sessionScope.user.accessControlList.organisationAnlegenLoeschenErlaubt}">
						<input
							title="Klicken Sie hier um eine neue Organisation anzulegen."
							name="inputNeueOrganisation" class="helpEvent button"
							type="submit" value="Neue Organisation"
							onclick="window.location.href='<zabos:url url="controller/organisation/"/>?OrganisationId=0'" />
					</c:if></div>
					</td>
					<td width="100%"></td>
					<td class="h_spacer"></td>
				</tr>
			</table>
			</td>
		</tr>
	</table>
</c:if></div>