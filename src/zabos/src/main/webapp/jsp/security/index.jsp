<%@ page language="java" contentType="text/html; charset=utf-8"
	pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="/WEB-INF/tld/c.tld"%>
<%@ taglib prefix="zabos" uri="http://ecw.de/taglibs/zabos"%>
<jsp:include page="../include/_header.jsp" flush="true" />

<center>
<table style="width: 850px;">
	<tr>
		<td>
		<div id="Zentrierung" style="width: 850px;" class="Zentrierung">
		<table style="width: 850px;">
			<tr>
				<td scope="row"><img src="<zabos:url url="images/head.jpg"/>"
					width="850" height="55" alt=""></td>
			</tr>
		</table>
		<table>
			<tr>
				<td>
				<table class="base">
					<tr>
						<td
							style="width:9px; background-image:url(<zabos:url url="images/line_left.jpg"/>)">
						</td>
						<td>
						<table style="width: 841px" class="base">
							<tr>
								<td class="h_spacer"></td>
								<td style="width: 655px;"></td>
								<td style="width: 166px;"></td>
							</tr>
							<tr>
								<td align="center"></td>
								<td align="left" valign="top">
								<table style="width: 637px; vertical-align: top">
									<tr>
										<td><c:if test="${!empty errors}">
											<table>
												<tr class="v_spacer">
													<td></td>
												</tr>
												<tr>
													<td><c:forEach items="${errors.messages}" var="data">
														<div class="text_bold">Fehler: <c:out
															value="${data.text}" /></div>
													</c:forEach></td>
												</tr>
												<tr class="v_spacer">
													<td></td>
												</tr>
											</table>
										</c:if>

										<div id="messageNoJS" class="text_bold">Fehler:
										Javascript ist nicht aktiviert.<br>
										<br>
										<span class="text"> Für diese Anwendung wird Javascript
										ben&ouml;tigt. Aktivieren Sie dies in den Einstellungen Ihres
										Browsers und laden Sie diese Seite neu um fortzufahren. </span></div>

										<div id="formLogin" style="display: none">
										<form name="login" method="post"
											action="<zabos:url url="controller/security/?do=doLogin&amp;submit=true" />">
										<input type="hidden" name="forwardPage"
											value="<c:if test="${!empty requestScope.forwardPage}"><c:out value="${requestScope.forwardPage}" /></c:if>" />

										<div class="text_bold">System-Anmeldung<br>
										<br>
										</div>

										<table>
											<tr>
												<td class="text" colspan="2">Bitte Benutzername und
												Passwort eingeben:</td>
											</tr>
											<tr class="v_spacer">
												<td></td>
											</tr>
											<tr>
												<td class="text">Benutzername:</td>
												<td><input onfocus="this.focus();" class="input"
													type="text" name="textUsername"
													value="<c:out value="${param.textUsername}"/>" /></td>
											</tr>
											<tr>
												<td class="text">Passwort:</td>
												<td><input class="input" type="password"
													name="textPassword"
													value="<c:out value="${param.textPassword}" />" /></td>
											</tr>
											<tr class="v_spacer">
												<td></td>
											</tr>
											<tr>
												<td></td>
												<td><input type="submit" value="Anmelden"
													class="button" /></td>
											</tr>
										</table>

										</form>
										</div>

										</td>
									</tr>
								</table>
								<td style="width: 166px;" align="left" valign="top"></td>
								<td
									style="background-image:url(<zabos:url url="images/line_right.jpg"/>); width:10px;">
								</td>
							</tr>
						</table>
						</td>
					</tr>
				</table>

				<table style="width: 850px; height: 18px;">
					<tr>
						<td>
						<table style="width: 850px;">
							<tr>
								<td
									style="height:18px; width:18px; background-image:url(<zabos:url url="images/bottom_left.jpg"/>)"></td>
								<td
									style="width:813px; background-image:url(<zabos:url url="images/line_bottom.jpg"/>)"></td>
								<td
									style="width:19px; background-image:url(<zabos:url url="images/bottom_right.jpg"/>)"></td>
							</tr>
						</table>
						</td>
					</tr>
				</table>

				<table style="width: 850px; background-color: #FFFFFF;">
					<tr>
						<td>
						<div id="footer" align="center">
						<table style="width: 840px;">
							<tr>
								<td style="width: 33%"></td>
								<td style="width: 33%"><img
									alt="EDV Consulting Wohlers GmbH"
									src="<zabos:url url="images/ecwlogo_min_text.gif"/>"
									width="234" height="9"></td>
								<td style="width: 33%"><span id="zabos_info">ZABOS
								${zabos_version} (Build ${zabos_builddate}) </span></td>
							</tr>
						</table>
						</div>
						</td>
					</tr>
				</table>
				</td>
			</tr>
		</table>
		</div>
		</td>
	</tr>
</table>
</center>
<script type="text/javascript" language="JavaScript">
	checkJS();
	checkBrowser();
	document.login.textUsername.focus();
	//document.getElementsByName('textUsername')[0].focus(); 
</script>
</body>
</html>