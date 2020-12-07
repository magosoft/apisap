# API SAP v2

### Instalación de generar-llaves de LINKSER

Instale manualmente el JAR en un repositorio local de Maven

```bash
mvn install:install-file –Dfile=Criptografia.jar -DgroupId=criptografia -DartifactId=generar-llaves -Dversion=1.0 -Dpackaging=jar
```
Ahora, agregue la dependencia a su proyecto Maven agregando estas líneas a su archivo pom.xml:

```xml
<dependency>
    <groupId>criptografia</groupId>
    <artifactId>generar-llaves</artifactId>
    <version>1.0</version>
</dependency>
```
### Instalación de ngdbc

Instale manualmente el JAR en un repositorio local de Maven

```bash
mvn install:install-file –Dfile=ngdbc.jar -DgroupId=com.sap -DartifactId=ngdbc -Dversion=1.0 -Dpackaging=jar
```
Ahora, agregue la dependencia a su proyecto Maven agregando estas líneas a su archivo pom.xml:

```xml
<dependency>
    <groupId>com.sap</groupId>
    <artifactId>ngdbc</artifactId>
    <version>1.0</version>
</dependency>
```
### Agregando el archivo de configuración

Ubicado en `/PATH_WILDFLY/standalone/configuration/api-sap/app.properties`

```xml
<?xml version="1.0" encoding="UTF-8" standalone="no"?>
<!DOCTYPE properties SYSTEM "http://java.sun.com/dtd/properties.dtd">
<properties>
    <comment>API-SAP v2  Configuracion</comment>
    <entry key="ws.sap.url">http://glf-qas.dcloud.com.bo:8077/sap/bc/srt/rfc/sap/zfi_ws_cobranza/200/zfi_ws_cobranza/zfi_wsb_cobranza</entry>
    <entry key="ws.tigomoney.url">https://pasarelatest.tigomoney.com.bo/PasarelaServicesTP/CustomerServices?wsdl</entry>
    <entry key="jdbc.sap.url">jdbc:sap://172.16.208.2:30241/?databaseName=HQA</entry>
    <entry key="jdbc.sap.user">NOVAGEL</entry>
    <entry key="jdbc.sap.passwd">N0v4g3l2019SAP</entry>
    <entry key="env">DESARROLLO</entry>

    <entry key="bukrs.1300.confirm">El Pahuichi S.R.L.</entry>
    <entry key="bukrs.1300.key">f401408244902997aa56fb8c6e44a47f4d028a48f8524c938434c70addb6728dc1d7375575390ae2321b549cfc46cbc2e538dda4cdc15453df1b639b1dc577c5</entry>
    <entry key="bukrs.1300.encrypt">9J1OJXI6QRM39WXA8GQQOQH2</entry>    
    <entry key="ws.sap.TIGP">123456</entry>
    
    
    <entry key="bukrs.1200.confirm">Techo S.A.</entry>
    <entry key="bukrs.1200.key">f401408244902997aa56fb8c6e44a47f4d028a48f8524c938434c70addb6728dc1d7375575390ae2321b549cfc46cbc2e538dda4cdc15453df1b639b1dc577c5</entry>
    <entry key="bukrs.1200.encrypt">9J1OJXI6QRM39WXA8GQQOQH2</entry>
    <entry key="ws.sap.TIGT">123456</entry>

    <entry key="bukrs.1600.confirm">Monumental</entry>
    <entry key="bukrs.1600.key">f401408244902997aa56fb8c6e44a47f4d028a48f8524c938434c70addb6728dc1d7375575390ae2321b549cfc46cbc2e538dda4cdc15453df1b639b1dc577c5</entry>
    <entry key="bukrs.1600.encrypt">9J1OJXI6QRM39WXA8GQQOQH2</entry>
     <entry key="ws.sap.TIGM">123456</entry>

    <entry key="bukrs.2400.confirm">Casa Mia</entry>
    <entry key="bukrs.2400.key">f401408244902997aa56fb8c6e44a47f4d028a48f8524c938434c70addb6728dc1d7375575390ae2321b549cfc46cbc2e538dda4cdc15453df1b639b1dc577c5</entry>
    <entry key="bukrs.2400.encrypt">9J1OJXI6QRM39WXA8GQQOQH2</entry>
     <entry key="ws.sap.TIGC">123456</entry>

    <entry key="bukrs.1700.confirm">Parque Latinoamericano</entry>
    <entry key="bukrs.1700.key">f401408244902997aa56fb8c6e44a47f4d028a48f8524c938434c70addb6728dc1d7375575390ae2321b549cfc46cbc2e538dda4cdc15453df1b639b1dc577c5</entry>
    <entry key="bukrs.1700.encrypt">9J1OJXI6QRM39WXA8GQQOQH2</entry>
    <entry key="ws.sap.TIGL">123456</entry>
</properties>
```