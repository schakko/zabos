#!/bin/bash
# statistik_zabos.sh
# Autor: Christopher Klein <christopher[dot]klein[at]ecw[dot]de>
# EDV Consulting Wohlers GmbH 2010
#
# Script zum automatischen Erzeugen der gesendeten SMS
# Das Script muss in die crontab eingetragen und sollte jeden Tag aufgerufen werden.
#
# Ablauf:
#  Erzeugung /opt/tomcat/zabos/statistik_yyyymmdd.csv
#  Aufruf pgsql > statistik_yyyymmdd.csv
# 
# Historie
# 2006-11-11 ckl 	Initialscript
# 2010-01-28 moe 	mail routine hinzugefuegt
# 2012-01-17 ckl 	Variablen angepasst
#			Mit Postgresql 8.4.1 wird das Datum anders geparst, dehalb DATe_MONTH auf +%-m geaendert

# Als Datum wird das gestrige genommen
DATE_FULL=`date -d yesterday +%Y%m%d`
DATE_DAY=`date -d yesterday +%d`
DATE_MONTH=`date -d yesterday +%-m`
DATE_YEAR=`date -d yesterday +%Y`
DIR_ZABOS="/data/zabos/statistik"
DATE_FULL_NOW=`date +%Y%m%d`

FILE_STATISTIK_SUFFIX="statistik"
FILE_STATISTIK_FULLPATH=${DIR_ZABOS}/${DATE_FULL}_${FILE_STATISTIK_SUFFIX}.csv

FILE_SCHLEIFEN_SUFFIX="schleifen"
FILE_SCHLEIFEN_FULLPATH=${DIR_ZABOS}/${DATE_FULL_NOW}_${FILE_SCHLEIFEN_SUFFIX}.csv

# Kommentar entfernen, falls E-Mail mit den Statistiken gesendet werden soll
#FILE_MAIL_RCPT=zabos-statistik@host.local

#
# SMS-Statistik
#
echo "Creating SMS statistic file for $DATE_FULL..."
# Datei loeschen, falls bereits vorhanden. ueberschreiben ist durch die Rechte nicht so sinnvoll
if [ -e $FILE_STATISTIK_FULLPATH ]; then
  rm $FILE_STATISTIK_FULLPATH
  echo "[+] existing $FILE_STATISTIK_FULLPATH removed"
fi

# Statistikdatei anlegen
touch $FILE_STATISTIK_FULLPATH
echo "[+] $FILE_STATISTIK_FULLPATH touched"

# Datei als CSV aus der Datenbank laden
echo -e "\\\encoding iso-8859-1 \n select * from v_sms_statistik where jahr='$DATE_YEAR' and monat='$DATE_MONTH'"  | su -c "psql -d zabos -t -A -F \";\"" - postgres > $FILE_STATISTIK_FULLPATH
echo "[+] SQL statement executed"

# User 'tomcat' ist Besitzer, ebenso die Gruppe
chown tomcat:tomcat $FILE_STATISTIK_FULLPATH
echo "[+] ownership changed"

# Rechte anpassen
chmod 644 $FILE_STATISTIK_FULLPATH
echo "[+] filemode changed"

#
# Schleifen-Statistik
#
echo "Creating Schleifen statistic file for $DATE_FULL..."
# Datei loeschen, falls bereits vorhanden. ueberschreiben ist durch die Rechte nicht so sinnvoll
if [ -e $FILE_SCHLEIFEN_FULLPATH ]; then
  rm $FILE_SCHLEIFEN_FULLPATH
  echo "[+] existing $FILE_SCHLEIFEN_FULLPATH removed"
fi

# Statistikdatei anlegen
touch $FILE_SCHLEIFEN_FULLPATH
echo "[+] $FILE_SCHLEIFEN_FULLPATH touched"

# Datei als CSV aus der Datenbank laden
echo -e "\\\encoding iso-8859-1 \n select * from v_schleifen_statistik"  | su -c "psql -d zabos -t -A -F \";\"" - postgres > $FILE_SCHLEIFEN_FULLPATH
echo "[+] SQL statement executed"

# User 'tomcat' ist Besitzer, ebenso die Gruppe
chown tomcat:tomcat $FILE_SCHLEIFEN_FULLPATH
echo "[+] ownership changed"

# Rechte anpassen
chmod 644 $FILE_SCHLEIFEN_FULLPATH
echo "[+] filemode changed"
# Erzeugte csv Dateien TARen, ZIPen und an FILE_MAIL_RCPT versenden
if [ -n "$FILE_MAIL_RCPT" ]; then
	act_dir=`/bin/pwd`
	cd $DIR_ZABOS
	/bin/tar -czvf "statistik_"$DATE_FULL_NOW'.tar.gz' $(find *.csv -mtime -3) >/dev/null
	echo "_" |` /usr/bin/mutt -a 'statistik_'$DATE_FULL_NOW'.tar.gz' -s "ZABOS KLBS Statistik Daten vom "$DATE_FULL_NOW $FILE_MAIL_RCPT`
	# TAR File loeschen
	rm "statistik_"$DATE_FULL_NOW'.tar.gz'
	#	 
	cd $act_dir
fi

echo "finished!"
