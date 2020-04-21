package ru.galaktika.eim.drools.support;

import java.util.HashMap;
import java.util.Map;

import javax.annotation.Nullable;

import org.kie.api.KieServices;
import org.kie.api.runtime.KieContainer;
import org.kie.api.runtime.KieSession;
import org.kie.api.runtime.StatelessKieSession;

/**
 * The experimental component. Please don't use it. 
 * 
 * @author Peter Titov
 */
public class DroolsSessionBuilder {

    @Nullable
    private String name;

    private final Map<String, Object> globals = new HashMap<>();

    public DroolsSessionBuilder setName(@Nullable String name) {
        this.name = name;
        return this;
    }

    public DroolsSessionBuilder addGlobals(@Nullable Map<String, Object> globalsToAdd) {
        if (globalsToAdd != null) {
            globals.putAll(globalsToAdd);
        }
        return this;
    }

    public KieSession buildKieSession() {
        KieServices kieServices = KieServices.Factory.get();
        KieContainer kieContainer = kieServices.getKieClasspathContainer();
        KieSession session;
        if (name == null) {
            session = kieContainer.newKieSession();
        } else {
            session = kieContainer.newKieSession(name);
        }
        if (!globals .isEmpty()) {
            DroolsUtils.addGlobals(session.getGlobals(), globals);
        }
        return session;
    }

    public StatelessKieSession buildStatelessKieSession() {
        KieServices kieServices = KieServices.Factory.get();
        KieContainer kieContainer = kieServices.getKieClasspathContainer();
        StatelessKieSession session;
        if (name == null) {
            session = kieContainer.newStatelessKieSession();
        } else {
            session = kieContainer.newStatelessKieSession(name);
        }
        if (!globals .isEmpty()) {
            DroolsUtils.addGlobals(session.getGlobals(), globals);
        }
        return session;
    }
}