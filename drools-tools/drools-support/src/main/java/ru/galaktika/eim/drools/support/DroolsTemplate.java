package ru.galaktika.eim.drools.support;

import java.util.Map;
import java.util.Objects;
import java.util.function.Function;

import javax.annotation.Nullable;

import org.drools.core.impl.AbstractRuntime;
import org.kie.api.KieServices;
import org.kie.api.event.KieRuntimeEventManager;
import org.kie.api.logger.KieRuntimeLogger;
import org.kie.api.runtime.Globals;
import org.kie.api.runtime.KieContainer;
import org.kie.api.runtime.KieSession;
import org.kie.api.runtime.StatelessKieSession;

import ru.galaktika.eim.drools.support.exception.DroolsSessionNotFound;

/**
 * @author Peter Titov
 */
public class DroolsTemplate {

    private final KieServices kieServices;

    private final KieContainer kieContainer;

    @Nullable
    private Map<String, Object> commonGlobals;

    public DroolsTemplate() {
        kieServices = KieServices.Factory.get();
        kieContainer = kieServices.getKieClasspathContainer();
    }

    public DroolsTemplate(String groupId, String artifactId, String version) {
    	kieServices = KieServices.Factory.get();
		kieContainer = kieServices.newKieContainer(kieServices.newReleaseId(groupId, artifactId, version));
    }

    public KieServices getKieServices() {
        return kieServices;
    }

    public KieContainer getKieContainer() {
        return kieContainer;
    }

    @Nullable
    public Map<String, Object> getCommonGlobals() {
        return commonGlobals;
    }

    public void setCommonGlobals(@Nullable Map<String, Object> commonGlobals) {
        this.commonGlobals = commonGlobals;
    }

    public KieSession newKieSession(String name, @Nullable Map<String, Object> sessionGlobals) {
        Objects.requireNonNull(name);
        KieSession session = kieContainer.newKieSession(name);
        if (session == null) {
            throw new DroolsSessionNotFound(name);
        }
        if (commonGlobals != null) {
            addGlobals(session.getGlobals(), commonGlobals);
        }
        if (sessionGlobals != null) {
            addGlobals(session.getGlobals(), sessionGlobals);
        }
        return session;
    }

    public StatelessKieSession newStatelessKieSession(String name, @Nullable Map<String, Object> sessionGlobals) {
        Objects.requireNonNull(name);
        StatelessKieSession session = kieContainer.newStatelessKieSession(name);
        if (session == null) {
            throw new DroolsSessionNotFound(name);
        }
        if (commonGlobals != null) {
            addGlobals(session.getGlobals(), commonGlobals);
        }
        if (sessionGlobals != null) {
            addGlobals(session.getGlobals(), sessionGlobals);
        }
        return session;
    }

    public <R> R withKieSession(String name, @Nullable Map<String, Object> sessionGlobals,
            Function<KieSession, R> callback) {
        KieSession session = newKieSession(name, sessionGlobals);
        KieRuntimeLogger logger = newLogger(session);
        try {
            return callback.apply(session);
        } finally {
            logger.close();
            session.destroy();
        }
    }

    public <R> R withStatelessKieSession(String name, @Nullable Map<String, Object> sessionGlobals,
            Function<StatelessKieSession, R> callback) {
        StatelessKieSession session = newStatelessKieSession(name, sessionGlobals);
        KieRuntimeLogger logger = newLogger(session);
        try {
            return callback.apply(session);
        } finally {
            logger.close();
        }
    }

    protected void addGlobals(Globals globals, Map<String, Object> map) {
        DroolsUtils.addGlobals(globals, map);        
    }

    /**
     * @see {@link org.drools.core.audit.KnowledgeRuntimeLoggerProviderImpl
     * #newConsoleLogger(KieRuntimeEventManager)}
     */
    protected KieRuntimeLogger newLogger(KieRuntimeEventManager session) {
        //return kieServices.getLoggers().newConsoleLogger(session);
        return registerRuntimeLogger(session, new DefaultWorkingMemoryLogger(session));
    }

    /**
     * @see {@link org.drools.core.audit.KnowledgeRuntimeLoggerProviderImpl
     * #registerRuntimeLogger(KieRuntimeEventManager, KieRuntimeLogger)}
     */
    protected KieRuntimeLogger registerRuntimeLogger(KieRuntimeEventManager session, KieRuntimeLogger logger) {
        if (session instanceof AbstractRuntime) {
            ((AbstractRuntime) session).setLogger(logger);
        }
        return logger;
    }
}