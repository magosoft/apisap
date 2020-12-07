package com.lafuente.sap.rest;

import com.lafuente.linkser.ws.ServicioLINKSER;
import com.lafuente.sap.dao.PGDatabase;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.inject.Inject;
import javax.servlet.ServletConfig;
import javax.ws.rs.ApplicationPath;
import javax.ws.rs.core.Application;
import javax.ws.rs.core.Context;
import org.apache.commons.lang3.StringUtils;

/**
 *
 * @author GUIDO CACERES PINTO
 */
@ApplicationPath("v2")
public class JAXRSApplication extends Application {
    @Inject
    private PGDatabase daoPG;

    public JAXRSApplication(@Context ServletConfig servletConfig) {          
       
    }

    @Override
    public Map<String, Object> getProperties() {
        Map<String, Object> map = new HashMap<>();
        try {
            Properties propertiesSystem = System.getProperties();
            map.put("os.name", propertiesSystem.getProperty("os.name"));
            map.put("user.name", propertiesSystem.getProperty("user.name"));
            map.put("user.timezone", propertiesSystem.getProperty("user.timezone"));
            map.put("java.specification.version", propertiesSystem.getProperty("java.specification.version"));
            map.put("jboss.home.dir", propertiesSystem.getProperty("jboss.home.dir"));
            String pathConfig = propertiesSystem.getProperty("jboss.server.config.dir");
            map.put("jboss.server.config.dir", propertiesSystem.getProperty("jboss.server.config.dir"));
            Properties properties = new Properties();
            properties.loadFromXML(new FileInputStream(pathConfig + "/api-sap/app.properties"));
            i(pathConfig + "/api-sap/app.properties");
            properties.stringPropertyNames().forEach((name) -> {
                map.put(name, properties.getProperty(name));
            });
        } catch (FileNotFoundException ex) {
            e(ex.getMessage());
            return super.getProperties();
        } catch (IOException ex) {
            e(ex.getMessage());
            return super.getProperties();
        }
        return map;
    }

    private void i(String message) {
        if (StringUtils.isEmpty(message)) {
            Logger.getLogger("API-SAP")
                    .log(Level.INFO, message);
        }
    }

    private void e(String message) {
        Logger.getLogger("API-SAP")
                .log(Level.SEVERE, StringUtils.isEmpty(message) ? "Error desconocido." : message);

    }
}
