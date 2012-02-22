<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="zabos" uri="http://ecw.de/taglibs/zabos"%>
<%@ page import="de.ecw.zabos.frontend.objects.fassade.klinikum.*"%>
<%@ page import="de.ecw.zabos.frontend.beans.*"%>
<%@ page import="java.util.*"%>
<%@ page language="java" contentType="text/html; charset=utf-8"
	pageEncoding="UTF-8"%>
<div class="settings"
	title="Hier werden alle Schleifen und deren Funktionsträger/Bereiche und deren Auslastung angezeigt"
	id="bereichsauslastung" style="display: none">
<ul>
	<li>Die erste Zahl bedeutet, wie viele Personen der Schleife
	direkt oder indirekt zugeordnet sind</li>
	<li>Die zweite Zahl gibt an, wie viele Personen in dieser Schleife
	wirklich auf Handy alarmiert werden. Diese berechnen sich aus der
	Anzahl der gültigen Handynummer.</li>
	<li>Die dritte Zahl entspricht der Sollst&auml;rke des jeweiligen
	Bereichs</li>
</ul>
<ul>
	<%List<Object> schleifen = ((DataBean) request
                            .getAttribute("arrSchleifenFassade")).getData();

            for (int i = 0, m = schleifen.size(); i < m; i++)
            {
                SchleifenFassade sf = (SchleifenFassade) schleifen.get(i);
                out
                                .println("<li><span  style=\'font-weight:bold;height:20px;padding-bottom:5px;\'>"
                                                + sf.getSchleifeVO().getName()
                                                + "</span><ul>");

                BereichInSchleifeMitPersonenFassade[] bismpfContainer = sf
                                .getBereichInSchleifeMitPersonenFassade();

                // HashMap aufbauen
                Map<String, String> mapFunktionstraegerZuBereiche = new HashMap<String, String>();

                for (int j = 0, n = bismpfContainer.length; j < n; j++)
                {
                    BereichInSchleifeMitPersonenFassade bismpf = bismpfContainer[j];
                    String id = bismpf.getFunktionstraeger().getBeschreibung();
                    String bereiche = "";

                    if (mapFunktionstraegerZuBereiche.containsKey(id))
                    {
                        bereiche = mapFunktionstraegerZuBereiche.get(id);
                    }

                    int alarmierteMitHandy = bismpf
                                    .getPersonenMitAktivemHandy().length;
                    int sollstaerke = bismpf.getBereichInSchleife()
                                    .getSollstaerke();

                    String cssStyle = "style=\"color: ";
                    if (alarmierteMitHandy < sollstaerke)
                    {
                        cssStyle += "red";
                    }
                    else
                    {
                        cssStyle += "green";
                    }
                    cssStyle += ";\"";

                    bereiche += "<li style=\'height:16px;margin-top:5px;\'><span title='Bereichsname'>"
                                    + bismpf.getBereich().getName()
                                    + "</span> - <span title='Zugeordnete Personen'>"
                                    + bismpf.getPersonen().length
                                    + "</span> / <span title='Personen mit Handy' "
                                    + cssStyle
                                    + ">"
                                    + alarmierteMitHandy
                                    + "</span> / "
                                    + "<span title='Sollst&auml;rke'"
                                    + cssStyle
                                    + ">"
                                    + sollstaerke
                                    + "</span></li>";

                    mapFunktionstraegerZuBereiche.put(id, bereiche);
                }

                Iterator<String> it = mapFunktionstraegerZuBereiche.keySet()
                                .iterator();

                while (it.hasNext())
                {
                    String funktionstraeger = it.next();
                    out.println("<ul>"
                                    + funktionstraeger
                                    + "<ul>"
                                    + mapFunktionstraegerZuBereiche
                                                    .get(funktionstraeger)
                                    + "</ul></ul>");
                }

                out.println("</ul></li>");
            }%>
</ul>


</div>