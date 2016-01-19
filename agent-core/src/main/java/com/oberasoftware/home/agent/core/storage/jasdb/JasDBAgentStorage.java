package com.oberasoftware.home.agent.core.storage.jasdb;

import com.oberasoftware.home.agent.core.storage.AgentStorage;
import com.oberasoftware.home.api.exceptions.RuntimeHomeAutomationException;
import nl.renarj.jasdb.api.DBSession;
import nl.renarj.jasdb.api.SimpleEntity;
import nl.renarj.jasdb.api.model.EntityBag;
import nl.renarj.jasdb.api.query.QueryBuilder;
import nl.renarj.jasdb.api.query.QueryResult;
import nl.renarj.jasdb.core.exceptions.JasDBException;
import nl.renarj.jasdb.core.exceptions.JasDBStorageException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * @author Renze de Vries
 */
@Component
public class JasDBAgentStorage implements AgentStorage {
    private static final Logger LOG = LoggerFactory.getLogger(JasDBAgentStorage.class);

    private static final String DATA_BAG = "agent";

    @Autowired
    private JasDBSessionFactory jasDBSessionFactory;

    @Override
    public String getValue(String key) {
        return getValue(key, null);
    }

    @Override
    public String getValue(String key, String defaultValue) {
        SimpleEntity entity = createOrGetAgentConfig();
        if(entity.hasProperty(key)) {
            return entity.getValue(key);
        } else {
            return defaultValue;
        }
    }

    @Override
    public boolean containsValue(String key) {
        return createOrGetAgentConfig().hasProperty(key);
    }

    @Override
    public void putValue(String key, String value) {
        SimpleEntity entity = createOrGetAgentConfig();
        entity.addProperty(key, value);

        try {
            persist(entity);
        } catch (JasDBStorageException e) {
            throw new RuntimeHomeAutomationException("Unable to store agent configuration value", e);
        }
    }

    private SimpleEntity createOrGetAgentConfig() {
        try {
            EntityBag bag = getBag();
            QueryResult result = bag.find(QueryBuilder.createBuilder().field("configurationItem").value("agent")).execute();
            if(result.hasNext()) {
                SimpleEntity entity = result.next();
                LOG.debug("Found existing agent configuration: {}", entity);
                return entity;
            } else {
                SimpleEntity entity = new SimpleEntity();
                entity.addProperty("configurationItem", "agent");
                LOG.debug("Creating new agent configuration: {}", entity);
                return persist(bag, entity);
            }
        } catch(JasDBException e) {
            throw new RuntimeHomeAutomationException("Unable to get agent configuration", e);
        }

    }

    private SimpleEntity persist(EntityBag bag, SimpleEntity entity) throws JasDBStorageException {
        return bag.persist(entity);
    }

    private SimpleEntity persist(SimpleEntity entity) throws JasDBStorageException {
        return persist(getBag(), entity);
    }

    private EntityBag getBag() throws JasDBStorageException {
        DBSession session = jasDBSessionFactory.createSession();
        return session.createOrGetBag(DATA_BAG);
    }
}
