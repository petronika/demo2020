package ru.galaktika.eim.drools.support;

import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

import org.drools.core.audit.WorkingMemoryLogger;
import org.drools.core.audit.event.ActivationLogEvent;
import org.drools.core.audit.event.LogEvent;
import org.drools.core.audit.event.ObjectLogEvent;
import org.drools.core.impl.StatelessKnowledgeSessionImpl;
import org.kie.api.event.KieRuntimeEventManager;
import org.kie.api.logger.KieRuntimeLogger;

import lombok.extern.slf4j.Slf4j;

/**
 * @see {@link org.drools.core.audit.KnowledgeRuntimeLoggerProviderImpl}
 * @see {@link org.drools.core.audit.WorkingMemoryConsoleLogger}
 * 
 * @author Peter Titov
 */
@Slf4j
public class DefaultWorkingMemoryLogger extends WorkingMemoryLogger implements KieRuntimeLogger {

    private static final String NL = System.lineSeparator();

    private static final char TAB = '\t';

    private KieRuntimeEventManager session;

    private StringBuilder buffer = new StringBuilder();

    private Map<String, AtomicInteger> objectStats = new LinkedHashMap<>();

    private Map<String, AtomicInteger> ruleStats = new LinkedHashMap<>();

    private long startTime;

    public DefaultWorkingMemoryLogger(KieRuntimeEventManager session) {
        super(session);
        this.session = session;
    }

    @Override
    public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
        super.readExternal(in);
    }

    @Override
    public void writeExternal(ObjectOutput out) throws IOException {
        super.writeExternal(out);
    }

    @Override
    public void logEventCreated(LogEvent logEvent) {
        if (log.isTraceEnabled()) {
            if (startTime == 0) {
                startTime = System.currentTimeMillis();
            }
            if (logEvent instanceof ObjectLogEvent
                    && logEvent.getType() == LogEvent.INSERTED) {
                String objectToString = ((ObjectLogEvent) logEvent).getObjectToString();
                int pos = objectToString.indexOf('(');
                String name = pos == -1 ? objectToString : objectToString.substring(0, pos);
                updateStats(objectStats, name);
                buffer
                    .append(NL)
                    .append(TAB).append(TAB)
                    .append("object: ")
                    .append(objectToString);
            } else if (logEvent instanceof ActivationLogEvent
                    && logEvent.getType() == LogEvent.BEFORE_ACTIVATION_FIRE) {
                String rule = ((ActivationLogEvent) logEvent).getRule();
                updateStats(ruleStats, rule);
                buffer
                    .append(NL)
                    .append(TAB)
                    .append("rule: ")
                    .append(rule);
            }
        }
    }

    @Override
    public void close() {
        if (log.isTraceEnabled()) {
            long endTime = System.currentTimeMillis();
            buffer.insert(0, String.format("%sObjects & Rules (%sms):",  NL, endTime - startTime));
            buffer.append(NL).append("Object statistics:");
            objectStats.forEach((name, count) ->
                buffer.append(NL).append(TAB).append(name).append(": ").append(count.get()));
            buffer.append(NL).append("Rule statistics:");
            ruleStats.forEach((name, count) ->
                buffer.append(NL).append(TAB).append(name).append(": ").append(count.get()));
            log.trace("Drools session log:{}", buffer.toString());
        }

        // To avoid memory leaking: remove link to the logger from the Knowledge Base
        // See: (http://drools-moved.46999.n3.nabble.com/
        // rules-users-WorkingMemoryLogger-statelessSessions-memory-leak-td3440868.html)
        // See: org.drools.core.audit.WorkingMemoryLogger#WorkingMemoryLogger(KieRuntimeEventManager)
        if (session instanceof StatelessKnowledgeSessionImpl) {
            ((StatelessKnowledgeSessionImpl) session).getKnowledgeBase().removeEventListener(this);
        }

        session = null;
        buffer = null;
        objectStats = null;
        ruleStats = null;
    }

    private void updateStats(Map<String, AtomicInteger> stats, String name) {
        AtomicInteger counter = stats.get(name);
        if (counter == null) {
            counter = new AtomicInteger();
            stats.put(name, counter);
        }
        counter.incrementAndGet();
    }
}