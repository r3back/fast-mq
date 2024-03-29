package com.qualityplus.fastmessaging;

import com.qualityplus.fastmessaging.api.sub.MessageHandlerRegistry;
import com.qualityplus.fastmessaging.api.serialization.MessagePackSerializable;
import com.qualityplus.fastmessaging.api.util.InstanceUtil;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;

@Slf4j
public abstract class FastMQListener {
    private final MessageHandlerRegistry registry = MessageHandlerRegistry.INSTANCE;
    protected final String prefix;

    public FastMQListener(final String prefix) {
        this.prefix = prefix;
    }

    public void handle(final String channel, final String clazzName, final byte[] message) {
        final String className = channel.replace(prefix, "");

        try {
            final Class<?> clazz = Class.forName(clazzName);

            if (!MessagePackSerializable.class.isAssignableFrom(clazz)) {
                log.error("{} does not implement MessagePackSerializable", className);
                return;
            }

            final MessagePackSerializable messagePackSerializable = (MessagePackSerializable) InstanceUtil.allocateInstance(clazz);

            messagePackSerializable.deserialize(message);

            registry.callAll(clazz, messagePackSerializable);
        } catch (ClassNotFoundException | InstantiationException | IOException e) {
            log.error("error while handling message on channel {} with classname {}", "channel", className, e);
        }
    }
}
