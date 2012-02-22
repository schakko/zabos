<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="zabos" uri="http://ecw.de/taglibs/zabos"%>
<%@ page language="java" contentType="text/html; charset=utf-8"
    pageEncoding="UTF-8"%>
    
	<script language="JavaScript" type="text/javascript">
	  var currentBranchOpened,branchPrefix = "TREE_";
	  
	  /**
	   * oeffnet einen Zweig im Tree
	   */
	  function expandBranch(branchId) {
	    var branch;
	    branch = document.getElementById(branchId);
	
	    if (branch != null) {
	      branch.style.display = "block";
	    }
	  }
	  
	  /**
	   * Schliesst einen Zweig im Tree 
	   */
	  function collapseBranch(branchId) {
	    var branch;
	    branch = document.getElementById(branchId);
	
	    if (branch != null) {
	      branch.style.display = "none";
	    }
	  }
	  
	  /**
	   * Liefert true bzw. false zurueck wenn der Branch geoeffnet ist
	   */
	  function isOpened(branchId) {
	    var branch;
	    branch = document.getElementById(branchId);
	
	    if (branch != null) {
	      if (branch.style.display == "none") {
	        return false;
	      } else {
	        return true;
	      }
	    }
	    
	    return false;
	  }
	  
	  /**
	   * oeffnet den Zweig
	   */
	  function openBranch(branchId) {
	    var arrBranches, currentOpeningBranchId;
	    arrBranches = getPureBranch(branchId).split("_");
	
	    // Hauptzweig oeffnen
	    for (var i = 0, m = arrBranches.length; i < m; i++) {
	      currentOpeningBranchId = "";
	      // Branch-ID aufbauen
	      for (var j = 0, n = arrBranches.length; j < n; j++) {
	        if (j > i) {
	          currentOpeningBranchId += "0";
	        } else {
	          currentOpeningBranchId += arrBranches[j];
	        }
	         
	        // Wenn nicht letztes Element ein _ hinten drann
	        if ((j + 1) != arrBranches.length) {
	          currentOpeningBranchId += "_";
	        }
	      }
	      expandBranch(branchPrefix+currentOpeningBranchId);
	    }
	
	  }
	  
	  /**
	   * Liefert den Namen des Zweigs zurueck, der uebergeben wurde, OHNE den TREE_-Prefix
	   * @param string branchId Name des Zweiges
	   * @return string Name des Zweiges
	   */
	  function getPureBranch(branchId) {
	    branchId = branchId.replace(branchPrefix,"");
	
	    return branchId;
	  }
	  
	  /**
	   * Schliesst den ggw. Zweig
	   */
	  function closeCurrentBranch(branchIdToOpen) {
	    var arrCurrentBranches, currentClosingBranchId;
	    
	    // Ein Zweig ist offen
	    if (currentBranchOpened != null) {
	      arrCurrentBranches = getPureBranch(currentBranchOpened).split("_");
	
	      // Die einzelnen Zweige schliessen
	      for (var i = (arrCurrentBranches.length - 1), m = 0; i > m; i--) {
			currentClosingBranchId = "";
			
			// Jetzt die jeweiligen Branch-Ids bilden
	        for (var j = 0, n = arrCurrentBranches.length; j < n; j++) {
	          if (j >= i) {
	            currentClosingBranchId += "0";
	          } else {
	            currentClosingBranchId += arrCurrentBranches[j];
	          }
	          
	          // Wenn nicht letztes Element ein _ hinten drann
	          if ((j + 1) != arrCurrentBranches.length) {
	            currentClosingBranchId += "_";
	          }
	        }
	        
	        collapseBranch(branchPrefix+currentClosingBranchId);
	      }
	    }
	  }
	  
	  /**
	   * Wird aufgerufen, wenn ein Link im Tree geklickt wurde
	   * @param String url URL, die eigentlich geoeffnet werden soll
	   * @param String branch_id ID des Branchs, der dem Zweig zugrunde liegt
	   */
	  function handleNavigationLink(url, branchId) {
	
	    this.location.href = url;    
	    closeCurrentBranch(branchId);  
	//  openBranch(branchId);
	    currentBranchOpened = branchId;
	
	
	  }
	</script>
	
	<div id="tree" class="tree">
		<div class="logout"><a class="logout" href="#" onClick="logout();">Abmelden</a></div>
		<div id="system" class="system">
			<a href="#" onclick="handleNavigationLink('<zabos:url url="controller/system"/>','TREE_0_0_0')">System</a>
			<br>
	
			<c:if test="${sessionScope.user.accessControlList.personAnlegenLoeschenErlaubt}">
				<div class="newentry">
					<a href="<zabos:url url="controller/person/?PersonId=0"/>">Neue Person</a>
				</div>
			</c:if>
			<br>
		</div>
	
		<div class="tree_level_1" id="TREE_0_0_0_0">
			<c:forEach items="${requestScope.navigationTree.subTree}" var="o" varStatus="oStatus">
		    	<a href="#" style="background-image:url(
			    	<c:choose>
			    		<c:when test="${oStatus.last == true}">
							<zabos:url url='images/tree.gif'/>		
				    	</c:when>
				    	<c:otherwise>
							<c:choose>    	
					    		<c:when test="${(sessionScope.user.ctxO.baseId.longValue == o.id)}">
									<zabos:url url='images/tree.gif'/>
								</c:when>
								<c:otherwise>
						    		<zabos:url url='images/tree_open.gif'/>
						    	</c:otherwise>
					    	</c:choose>
				    	</c:otherwise>
			    	</c:choose>
	    		);<c:if test="${(sessionScope.user.ctxO.baseId.longValue == o.id) && (!empty requestScope.OrganisationId)}">font-weight:bold;color:#900;</c:if>" onclick="handleNavigationLink('<zabos:url url="controller/organisation/?OrganisationId=${o.id}"/>','TREE_${o.id}_0_0')">
	    				<img alt="o" class="tree_image" src="<zabos:url url="images/ico_o.gif"/>">${o.name}
				</a>
	    		<br>
	    		
					<div class="tree_level_2" id="TREE_${o.id}_0_0" style="display:none">
				    	<c:forEach items="${o.subTree}" var="oe" varStatus="oeStatus">
				        	<a href="#" style="background-image:url(       
						    	<c:choose>
						    		<c:when test="${oeStatus.last == true}">
										<zabos:url url='images/tree.gif'/>		
								    </c:when>
								    <c:otherwise>
										<c:choose>    	
									    	<c:when test="${(sessionScope.user.ctxOE.baseId.longValue == oe.id)}">
												<zabos:url url='images/tree.gif'/>
											</c:when>
											<c:otherwise>
											    <zabos:url url='images/tree_open.gif'/>
									    	</c:otherwise>
									    </c:choose>
								    </c:otherwise>
							    </c:choose>
			       			 );<c:if test="${(sessionScope.user.ctxOE.baseId.longValue == oe.id) && (!empty requestScope.OrganisationseinheitId)}">font-weight:bold;color:#900;</c:if>" onclick="handleNavigationLink('<zabos:url url="controller/organisationseinheit/?OrganisationseinheitId=${oe.id}"/>','TREE_${o.id}_${oe.id}_0')"><img alt="e" class="tree_image" src="<zabos:url url="images/ico_e.gif"/>">${oe.name}</a>
			       			 <br>

						        <div class="tree_level_3" id="TREE_${o.id}_${oe.id}_0" style="display:none">
									<c:forEach items="${oe.subTree}" var="s" varStatus="sStatus">
						            	<a href="#" style="background-image:url(
									    	<c:choose>
									    		<c:when test="${sStatus.last == true}">
													<zabos:url url='images/tree.gif'/>		
											    </c:when>
											    <c:otherwise>
													<zabos:url url='images/tree_open.gif'/>
											    </c:otherwise>
										    </c:choose>
						    	        );<c:if test="${(sessionScope.user.ctxSchleife.baseId.longValue == s.id) && (!empty requestScope.SchleifeId)}">font-weight:bold;color:#900;</c:if>" onclick="handleNavigationLink('<zabos:url url="controller/schleife/?SchleifeId=${s.id}"/>','TREE_${o.id}_${oe.id}_${s.id}')"><img alt="s" class="tree_image" src="<zabos:url url="images/ico_s.gif"/>">${s.name}</a>
						        	    <br>
									</c:forEach>
									
									<c:if test="${oe.sizeSubTree == 0}">
										<span class="text noentry">
											Keine Schleife vorhanden
										</span>
										<br>
									</c:if>
									<c:if test="${sessionScope.user.accessControlList.schleifeAnlegenLoeschenErlaubt}">
						  				<div class="newentry">
						  					<a class="newentry" href="<zabos:url url="controller/schleife/?SchleifeId=0"/>">
						  						Neue Schleife
											</a>
										</div>
									</c:if>
									
   								</div>
			</c:forEach>
	
			<c:if test="${o.sizeSubTree == 0}"><span class="text noentry">Keine Einheit vorhanden</span><br></c:if>
			<c:if test="${sessionScope.user.accessControlList.organisationseinheitAnlegenLoeschenErlaubt}">
				<div class="newentry">
					<a href="<zabos:url url="controller/organisationseinheit/?OrganisationseinheitId=0"/>">Neue Organisationseinheit</a>
				</div>
			</c:if>
		</div>
		</c:forEach>
	
		<c:if test="${sessionScope.user.accessControlList.organisationAnlegenLoeschenErlaubt}">
	
		<div class="newentry"><a href="<zabos:url url="controller/organisation/?OrganisationId=0"/>">Neue Organisation</a></div>
			</c:if>  
		</div>
			
	<c:if test="${sessionScope.user.accessControlList.statistikAnzeigenErlaubt}">
		<div class="system">
			<a href="<zabos:url url="controller/statistik/" />">Berichte</a>
		</div>
	</c:if>
	<div id="myOptions" class="system">
		<a href="<zabos:url url="controller/person/" />?JSESSIONID=<%= session.getId() %>&amp;PersonId=${user.person.baseId}">
			Meine Einstellungen
		</a>
	</div>
	</div>

<script language="JavaScript" type="text/javascript">
	// Kontext laden 
	openBranch("TREE_<c:out value="${sessionScope.user.ctxO.baseId}" default="0"/>_<c:out value="${sessionScope.user.ctxOE.baseId}" default="0"/>_<c:out value="${sessionScope.user.ctxSchleife.baseId}" default="0"/>");
</script>