<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="zabos" uri="http://ecw.de/taglibs/zabos"%>
<%@ page language="java" contentType="text/html; charset=utf-8"
    pageEncoding="UTF-8"%>
<div id="person_rolle" class="setting" style="display:none">

	<table class="popup_base">
		<tr class="list_head">
			<td class="h_spacer"></td>
			<td>Rechte und deren Vererbung</td>
		</tr>
	</table>
		<div class="tree_level_1" style="font-size:12px;">
		System <br>
			<div class="tree_level_2" style="display:block">
				<c:forEach items="${requestScope.hierarchyRollenTree.dataBean.data}" var="rollen_system">
					Rolle: ${rollen_system.rolle.name}
					<c:choose>
						<c:when test="${rollen_system.istVererbt == false}">
							(nativ)
						</c:when>
						<c:otherwise>
							(geerbt)
						</c:otherwise>
					</c:choose>
				</c:forEach>
			</div>	
		</div>

		
<div class="tree_level_1" style="font-size:12px;">
	<c:forEach items="${requestScope.hierarchyRollenTree.subTree}" var="o" varStatus="oStatus">
		<a href="<zabos:url url="controller/organisation/?OrganisationId=${o.id}"/>&amp;tab=rollen">
			<img alt="o" class="tree_image" src="<zabos:url url="images/ico_o.gif"/>"> ${o.name}<br/>
		</a>
		<div class="tree_level_2" style="display:block">
			<c:forEach items="${o.dataBean.data}" var="rollen_organisation">
				Rolle: ${rollen_organisation.rolle.name}
				<c:choose>
					<c:when test="${rollen_organisation.istVererbt == false}">
					(nativ)
					</c:when>
					<c:otherwise>
						(geerbt)
					</c:otherwise>
				</c:choose>
				<br>
			</c:forEach>
		</div>
		
		<div class="tree_level_2" style="display:block">
			<c:forEach items="${o.subTree}" var="oe" varStatus="oeStatus">
				<a href="<zabos:url url="controller/organisationseinheit/?OrganisationseinheitId=${oe.id}"/>&amp;tab=rollen">			
					<img alt="oe" class="tree_image" src="<zabos:url url="images/ico_e.gif"/>"> <span class="text_bold">${oe.name}</span>
				</a>
				<div class="tree_level_3" style="display:block">
					<c:forEach items="${oe.dataBean.data}" var="rollen_organisationseinheit">
						Rolle: ${rollen_organisationseinheit.rolle.name}
						 <c:choose>
						 	<c:when test="${rollen_organisationseinheit.istVererbt == false}">
						 	(nativ)
					 		</c:when>
					 		<c:otherwise>
					 			(geerbt)
				 			</c:otherwise>
		 				</c:choose>
 						<br>
					</c:forEach>
				</div>
		
				<div class="tree_level_3" style="display:block">

					<c:forEach items="${oe.subTree}" var="s" varStatus="sStatus">
						<a href="<zabos:url url="controller/schleife/?SchleifeId=${s.id}"/>&amp;OrgansationId=${o.id}&amp;OrgansationseinheitId=${oe.id}&amp;tab=rollen" >
							<img alt="s" class="tree_image" src="<zabos:url url="images/ico_s.gif"/>"><span class="text_bold">${s.name}</span>
						</a>
						<div class="tree_level_3" style="display:block">
							<c:forEach items="${s.dataBean.data}" var="rollen_schleife">
								Rolle: ${rollen_schleife.rolle.name}
								<c:choose>
									<c:when test="${rollen_schleife.istVererbt == false}">
										(nativ)
									</c:when>
									<c:otherwise>
										(geerbt)
									</c:otherwise>
								</c:choose>
								<br>
							</c:forEach>
		    			</div>
					</c:forEach>
				</div>
			</c:forEach>
		</div>
	</c:forEach>
</div>
</div>