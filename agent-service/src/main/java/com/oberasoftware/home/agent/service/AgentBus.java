package com.oberasoftware.home.agent.service;

import com.oberasoftware.base.event.Event;
import com.oberasoftware.base.event.EventHandler;
import com.oberasoftware.base.event.impl.LocalEventBus;
import com.oberasoftware.home.agent.core.storage.AgentStorage;
import com.oberasoftware.home.api.AutomationBus;
import com.oberasoftware.home.core.mqtt.MQTTTopicEventBus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.UUID;

/**
 * @author Renze de Vries
 */
@Component
public class AgentBus implements AutomationBus {
    private static final Logger LOG = LoggerFactory.getLogger(AgentBus.class);

    private static final String CONTROLLER_ID_KEY = "controllerId";

    @Autowired
    private AgentStorage agentStorage;

    @Autowired
    private LocalEventBus localEventBus;

    @Autowired
    private MQTTTopicEventBus mqttEventBus;

    @Override
    public String getControllerId() {
        if(!agentStorage.containsValue(CONTROLLER_ID_KEY)) {
            String controllerId = UUID.randomUUID().toString();
            LOG.info("Generated a new unique agent controller id: {}", controllerId);
            agentStorage.putValue(CONTROLLER_ID_KEY, controllerId);
        }

        return agentStorage.getValue(CONTROLLER_ID_KEY);
    }

    @Override
    public void publish(Event event) {

    }

    @Override
    public void registerHandler(EventHandler eventHandler) {

    }
}
