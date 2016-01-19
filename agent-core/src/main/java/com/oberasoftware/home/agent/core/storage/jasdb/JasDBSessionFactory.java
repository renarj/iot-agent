package com.oberasoftware.home.agent.core.storage.jasdb;

import nl.renarj.jasdb.LocalDBSession;
import nl.renarj.jasdb.api.DBSession;
import nl.renarj.jasdb.core.exceptions.JasDBStorageException;
import nl.renarj.jasdb.rest.client.RestDBSession;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import static nl.renarj.core.utilities.StringUtils.stringNotEmpty;
import static org.slf4j.LoggerFactory.getLogger;

/**
 * @author renarj
 */
@Component
public class JasDBSessionFactory {
    private static final Logger LOG = getLogger(JasDBSessionFactory.class);

    @Value("${jasdb.mode}")
    private String jasdbMode;

    @Value("${jasdb.wipe.startup:false}")
    private boolean wipeStartup;

    @Value("${jasdb.host:}")
    private String jasdbHost;

    @Value("${jasdb.post:7050}")
    private int jasdbPort;

    @Value("${jasdb.instance:default}")
    private String jasdbInstance;


    public DBSession createSession() throws JasDBStorageException {
        DBSession session;
        if(stringNotEmpty(jasdbMode) && jasdbMode.equals("rest")) {
            LOG.debug("Creating JasDB REST session to host: {} port: {} instance: {}", jasdbHost, jasdbPort, jasdbInstance);
            session = new RestDBSession(jasdbInstance, jasdbHost, jasdbPort);
        } else {
            LOG.debug("Creating JasDB Local session to instance: {}", jasdbInstance);
            session = new LocalDBSession(jasdbInstance);
        }

        return session;
    }
}
