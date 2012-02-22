<%@ page language="java" contentType="text/html; charset=utf-8"
	pageEncoding="UTF-8"%>
<%@ taglib prefix="zabos" uri="http://ecw.de/taglibs/zabos"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<jsp:include page="../include/_header.jsp" flush="false" />

<body onload="ZeitAnzeigen();">
<div id="confirmAlarmShadow" class="confirmAlarmShadow"></div>
<div id="confirmAlarm" class="confirmAlarm">
<center>
<table class="confirmTable">
	<tr class="table_head">
		<th class="confirmTableHead">ZABOS Sicherheitsabfrage</th>
	</tr>
	<tr class="v_spacer">
		<td></td>
	</tr>
	<tr>
		<td class="text_bold">
		<center>Soll der Alarm wirklich gesendet werden?</center>
		</td>
	</tr>
	<tr class="v_spacer">
		<td></td>
	</tr>
	<tr>
		<td>
		<center>
		<table style="margin-top: 20px;">
			<tr>
				<td><input class="button" onclick="confirmAlarm();"
					type="button" name="AlarmAusloesen" value="Ja - Alarm senden">
				</td>
				<td style="width: 40px;"></td>
				<td><input class="button" onclick="confirmAlarmClose();"
					type="submit" name="AlarmAbbrechen" value="Nein - Abbrechen">
				</td>
			</tr>
		</table>
		</center>
		</td>
	</tr>
</table>
</center>
</div>

<center>
<table style="width: 850px;">
	<tr>
		<td>
		<div id="Zentrierung" style="width: 850px;" class="Zentrierung">
		<div id="Uhr" class="Uhr">Fehler (Uhr)</div>

		<form accept-charset="utf-8" method="post"
			action="<zabos:url url="controller/alarmierung/?submit=true&amp;do=doAlarm" />"
			name="frmAlarm" id="frmAlarm">
		<table style="width: 850px;">
			<tr>
				<td scope="row"><img alt="Logo"
					src="<zabos:url url="images/head.jpg"/>" width="850" height="55"></td>
			</tr>
		</table>
		<table>
			<tr>
				<td>
				<table class="base">
					<tr>
						<td
							style="width:9px; background-image:url(<zabos:url url="images/line_left.jpg"/>)"></td>
						<td>
						<table style="width: 831px" class="base">
							<tr class="v_spacer">
								<td class="h_spacer"></td>
								<td style="width: 655px;"></td>
								<td style="width: 166px;"></td>
							</tr>
							<tr>
								<td align="center"></td>
								<td align="left" valign="top">
								<table style="width: 637px; vertical-align: top">
									<tr>
										<td>
										<table style="width: 637px">
											<tr>
												<td
													style="height:23px; background-image:url(<zabos:url url="images/kopfzeile.gif"/>)">
												<span class="text_white" style="padding-left: 10px;">Alarmauslösung</span>
												</td>
											</tr>
										</table>

										<c:if
											test="${!sessionScope.user.accessControlList.alarmAusloesenErlaubt}">
											<table class="light" style="width: 637px; height: 50px;">
												<tr>
													<td class="h_spacer"></td>
													<td class="text">Sie sind nicht berechtigt, einen
													Alarm auszulösen.</td>
												</tr>
											</table>
										</c:if>

										<div class="light">
										<table
											style="width:637px; height:30px;<c:if test="${!sessionScope.user.accessControlList.alarmAusloesenErlaubt}">visibility:hidden</c:if>"
											class="light">
											<tr>
												<td
													style="width:1px; background-image:url(<zabos:url url="images/form_line.jpg"/>)"></td>
												<td style="width: 635px; height: 30px">
												<table>
													<tr class="v_spacer">
														<td></td>
													</tr>
												</table>
												<table class="inner" style="height: 419px;">
													<tr>
														<td class="h_spacer"></td>
													</tr>
													<tr class="v_spacer">
														<td></td>
													</tr>
													<tr>
														<td></td>
														<td valign="top">
														<table class="inner">
															<tr>
																<td style="padding-right: 10px;" class="text"><jsp:include
																	page="../include/_errors.jsp" flush="false" /> <c:if
																	test="${!empty istAusgeloest}">
																	<table class="error">
																		<tr>
																			<td class="error text_bold">Ein Alarm wurde
																			ausgelöst:</td>
																		</tr>
																		<c:forEach items="${arrSchleifenAusgeloest}"
																			var="schleife">
																			<tr>
																				<td class="error_h text">Schleife
																				${schleife.kuerzel} ausgelöst!</td>
																			</tr>
																		</c:forEach>
																	</table>
																	<table>
																		<tr class="v_spacer">
																			<td></td>
																		</tr>
																	</table>
																</c:if></td>
															</tr>
														</table>
														<table class="inner">
															<tr>
																<td class="text_bold" style="width: 200px;">Gew&auml;hlte
																Schleifen</td>
																<td></td>
																<td class="text_bold" style="width: 200px;">Vorhandene
																Schleifen</td>
																<td class="h_spacer"></td>
															</tr>
															<tr class="v_spacer">
																<td colspan="4"></td>
															</tr>
															<tr>
																<td class="text" style="vertical-align: top"><select
																	name="selectSchleifenSendTo" id="selectSchleifenSendTo"
																	size="22"
																	ondblclick="javascript:moveSelectedEntries('selectSchleifenSendTo','selectSchleifenAvailable');"
																	multiple>
																</select></td>
																<td>
																<table class="inner_button">
																	<tr>
																		<td><input class="button" type="button"
																			name="add" id="add" value="< Hinzuf&uuml;gen"
																			onClick="javascript:moveSelectedEntries('selectSchleifenAvailable','selectSchleifenSendTo'); return false">
																		</td>
																	</tr>
																	<tr class="v_spacer">
																		<td></td>
																	</tr>
																	<tr>
																		<td><input class="button" type="button"
																			name="del" value="Entfernen >"
																			onClick="javascript:removeSchleifenFromKontext(); return false">
																		</td>
																	</tr>
																</table>
																</td>
																<td class="text" style="vertical-align: top">
																<table>
																	<tr>
																		<td>

																		<table class="inner">
																			<tr class="list_head">
																				<td class="h_spacer_5"></td>
																				<td class="text">In Organisation:</td>
																			</tr>
																		</table>

																		<table class="inner">
																			<tr>
																				<td><select name="selectOrganisationen"
																					id="selectOrganisationen"
																					onchange="out_findOrganisationseinheitenInOrganisation();out_findSchleifenInKontext();">
																				</select></td>
																			</tr>
																		</table>

																		</td>
																	</tr>
																	<tr>
																		<td>
																		<table class="inner">
																			<tr class="v_spacer">
																				<td colspan="2"></td>
																			</tr>
																			<tr class="list_head">
																				<td class="h_spacer_5"></td>
																				<td class="text">In Einheit:</td>
																			</tr>
																		</table>

																		<table class="inner">
																			<tr>
																				<td><select name="selectOrganisationseinheiten"
																					id="selectOrganisationseinheiten"
																					onchange="out_findSchleifenInKontext();">
																				</select></td>
																			</tr>
																			<tr class="v_spacer">
																				<td></td>
																			</tr>
																		</table>
																		<table class="inner">

																			<tr class="list_head">
																				<td class="h_spacer_5"></td>
																				<td class="text_bold">Schleifen:</td>
																			</tr>
																		</table>
																		</td>
																	</tr>
																	<tr>
																		<td><select name="selectSchleifenAvailable"
																			id="selectSchleifenAvailable" size="13"
																			ondblclick="javascript:moveSelectedEntries('selectSchleifenAvailable','selectSchleifenSendTo');"
																			multiple></select></td>
																	</tr>
																</table>
																</td>
																<td></td>
															</tr>
															<tr>
																<td></td>
																<td></td>
																<td class="text">

																<table class="inner">
																	<tr class="v_spacer">
																		<td colspan="2"></td>
																	</tr>
																	<tr class="list_head">
																		<td class="h_spacer"></td>
																		<td class="text">In allen Schleifen suchen: <span
																			id="indicator" style="visibility: hidden;"> <img
																			alt="#" src="<zabos:url url="images/indicator.gif"/>">
																		</span></td>
																	</tr>
																</table>

																<table class="inner">
																	<tr>
																		<td><input name="inputSearch" id="inputSearch"
																			class="input_long" type="text"
																			onKeyUp="javascript:hideObject('indicator',false);resetList('selectOrganisationen');resetList('selectOrganisationseinheiten');window.setTimeout('out_findSchleifenByPatternMitAusloeseberechtigung(in_findSchleifenByPatternMitAusloeseberechtigung)',1000)">
																		</td>
																	</tr>
																</table>

																</td>
																<td></td>
															</tr>
															<tr class="v_spacer">
																<td></td>
																<td></td>
																<td></td>
																<td></td>
															</tr>
														</table>
														</td>
													</tr>
												</table>
												<table class="inner">
													<tr>
														<td class="h_spacer"></td>
														<td></td>
													</tr>
													<tr class="v_spacer">
														<td></td>
														<td></td>
													</tr>
													<tr>
														<td></td>
														<td>
														<table>
															<tr>
																<td class="text_bold">Optionaler Alarmtext: <span
																	style="text-align: right"><input maxlength="${maxLengthNachricht}"
																	class="input" name="textNachricht" id="textNachricht"
																	type="text" value="" style="width: 390px;"></span> <span
																	class="text_info">${maxLengthNachricht} Zeichen </span><br />
																	<c:if test="${isGpsEnabled}">
																Optionale GPS-Koordinaten: <span
																	style="text-align: right"><input maxlength="18"
																	class="input" name="textGpsKoordinate"
																	id="textGpsKoordinate" type="text" value=""
																	style="width: 200px;"></span> <span class="text_info">
																18 Zeichen </span>
																</c:if></td>
															</tr>
															<tr class="v_spacer">
																<td></td>
															</tr>
														</table>
														</td>
													</tr>
												</table>

												<table class="inner">
													<tr>
														<td class="h_spacer"></td>
														<c:if
															test="${sessionScope.user.accessControlList.alarmAusloesenErlaubt}">
															<td class="text" style="width: 100%;"><input
																class="button" onclick="confirmAlarmOpen();"
																type="button" name="AlarmAusloesen"
																style="height: 30px; width: 180px" value="ALARM SENDEN">
															</td>
															<td valign="bottom">
															<div align="right"><input name="inputCancel"
																type="button" class="button"
																onclick="resetAlarmierung();" value="Zurücksetzen">
															</div>
															</td>
														</c:if>
														<td class="h_spacer"></td>
													</tr>
												</table>
												</td>
												<td
													style="width:1px; background-image:url(<zabos:url url="images/form_line.jpg"/>)"></td>
											</tr>
										</table>
										</div>
										<table style="width: 637px;">
											<tr style="height: 10px;">
												<td>
												<table style="width: 637px;">
													<tr>
														<td class="h_spacer"
															style="background-image:url(<zabos:url url="images/form_bottom_left.jpg"/>);"></td>
														<td
															style="width:100%; height:10px; background-image:url(<zabos:url url="images/form_line_bottom.jpg"/>);"></td>
														<td class="h_spacer"
															style="background-image:url(<zabos:url url="images/form_bottom_right.jpg"/>);"></td>
													</tr>
												</table>
												</td>
											</tr>
										</table>
										</td>
									</tr>
								</table>

								<td style="width: 166px;" align="left" valign="top">

								<table class="base">
									<c:choose>

										<c:when
											test="${sessionScope.user.accessControlList.alarmhistorieSehenErlaubt}">
											<tr>
												<td>
												<table style="width: 157px;" class="base">
													<tr>
														<td class="text_white"
															style="width:157px; height:23px; background-image:url(<zabos:url url="images/iframe_top.gif"/>)">
														<span style="padding-left: 30px;">Alarmierungen</span></td>
													</tr>
												</table>
												</td>
											</tr>
											<tr>
												<td>
												<div id="iframe"
													style="border-width: 1px; padding-left: 5px; padding-top: 5px; border-style: solid; border-color: #d0c5c5; height: 437px; width: 150px; overflow: auto; background-color: #f7f6f6;">
												</div>
												</td>
											</tr>
											<tr>
												<td>
												<table style="width: 157px;" class="base">
													<tr>
														<td
															style="width:157px; height:30px; background-image:url(<zabos:url url="images/iframe_bottom.jpg"/>)">
														<div align="center"><c:if
															test="${sessionScope.user.accessControlList.alarmhistorieSehenErlaubt}">
															<input name="button" type="button" class="button"
																value="Alarm-Historie"
																onclick="popup('<zabos:url url="controller/report/?tab=history" />&amp;JSESSIONID=<%=session.getId()%>','Historie','700','720')" />
														</c:if></div>
														</td>
													</tr>
												</table>
												</td>
											</tr>
										</c:when>

										<c:otherwise>
											<div id="iframe" style="display: none"></div>
										</c:otherwise>

									</c:choose>
								</table>
								<div id="Verwaltung" class="Verwaltung"><input
									name="btnVerwaltung" type="button" class="button"
									value="System-Verwaltung"
									onclick="popup('<zabos:url url="controller/system/" />?JSESSIONID=<%=session.getId()%>','Verwaltung',900,700)"><br>
								<input name="btnMeineEinstellungen" type="button" class="button"
									value="Meine Einstellungen"
									onclick="popup('<zabos:url url="controller/person/" />?JSESSIONID=<%=session.getId()%>&amp;PersonId=${user.person.baseId}','Verwaltung',1024,700)">
								</div>
								</td>
							</tr>
						</table>
						</td>
						<td
							style="background-image:url(<zabos:url url="images/line_right.jpg"/>); width:10px;"></td>
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
						<td style="width: 33%"><img alt="EDV Consulting Wohlers GmbH"
							src="<zabos:url url="images/ecwlogo_min_text.gif"/>" width="234"
							height="9"></td>
						<td style="width: 33%"><span id="zabos_info">ZABOS
						${zabos_version} (Build ${zabos_builddate}) </span></td>
					</tr>
				</table>
				</div>


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
					// Schleifen finden => SelectBox mit den auslösbaren Schleifen sollte beim Start ALLE Schleifen enthalten
				out_findSchleifenInKontext();
				
				//out_findSchleifenByPatternMitAusloeseberechtigung(in_findSchleifenByPatternMitAusloeseberechtigung);
									
				<c:if test="${sessionScope.user.accessControlList.alarmhistorieSehenErlaubt}">		
						// Alle 10 Sekunden die Abfrage nach den Personen machen
					var intInterval = 20000; // Milisekunden
					var timer = window.setInterval("out_findLetzteAlarmierungenMitBerechtigung(in_findLetzteAlarmierungenMitBerechtigung)", intInterval);			
						// Alarmierungen finden
				out_findLetzteAlarmierungenMitBerechtigung(in_findLetzteAlarmierungenMitBerechtigung);
				</c:if>				
				
				self.name = "ZABOSfront";
			</script>

		<div class="session"><c:choose>
			<c:when test="${sessionScope.user.loggedIn}">
				  Angemeldet als: <a href="#"
					onclick="return popup('<zabos:url url="controller/person/" />?JSESSIONID=<%=session.getId()%>&amp;PersonId=${user.person.baseId}','Verwaltung',1024,700)">${user.person.displayName}</a>
			</c:when>
			<c:otherwise>Nicht angemeldet</c:otherwise>
		</c:choose> - <a
			href="<zabos:url url="controller/security/?do=doLogout&amp;submit=true"/>"
			onclick="popupClose();">Abmelden</a></div>

		</div>
		</td>
	</tr>
</table>
</center>

<jsp:include page="../include/_footer.jsp" flush="true" />