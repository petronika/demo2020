package ru.galaktika.eim.drools.test.junit4.rule;

import java.lang.annotation.Annotation;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;
import static java.util.Optional.ofNullable;

import javax.annotation.Nullable;

import org.drools.core.impl.AbstractRuntime;

import org.hamcrest.BaseMatcher;
import org.hamcrest.Matcher;

import org.junit.rules.TestRule;
import org.junit.runner.Description;
import org.junit.runners.model.Statement;
import static org.junit.Assert.assertThat;

import org.kie.api.KieServices;
import org.kie.api.event.KieRuntimeEventManager;
import org.kie.api.io.Resource;
import org.kie.api.io.ResourceType;
import org.kie.api.logger.KieRuntimeLogger;
import org.kie.api.runtime.KieContainer;
import org.kie.api.runtime.KieSession;
import org.kie.api.runtime.rule.QueryResults;
import org.kie.internal.builder.KnowledgeBuilder;
import org.kie.internal.builder.KnowledgeBuilderFactory;
import org.kie.internal.io.ResourceFactory;

import lombok.Builder;
import ru.galaktika.eim.drools.support.DefaultWorkingMemoryLogger;
import ru.galaktika.eim.drools.test.junit4.annotation.DroolsResource;
import ru.galaktika.eim.drools.test.junit4.annotation.DroolsResourcePathType;
import ru.galaktika.eim.drools.test.junit4.annotation.DroolsResources;
import ru.galaktika.eim.drools.test.junit4.annotation.DroolsRules;
import ru.galaktika.eim.drools.test.junit4.annotation.DroolsSession;
import ru.galaktika.eim.drools.test.junit4.exception.DroolsTestException;
import ru.galaktika.eim.drools.test.junit4.exception.IncompatibleAnnotationsException;
import ru.galaktika.eim.drools.test.junit4.exception.KnowledgeBuilderException;
import ru.galaktika.eim.drools.test.junit4.exception.ResourceException;
import ru.galaktika.eim.drools.test.junit4.exception.SessionNotFoundException;

/**
 * @author Peter Titov 
 */
public class KieSessionRule implements TestRule {

    private static final Collection<Object>  ANNOTATIONS = Arrays.asList(new Object[] {
            DroolsResource.class.getSimpleName(),
            DroolsResources.class.getSimpleName(),
            DroolsSession.class.getSimpleName()});

    private String sessionName;

    private boolean ruleTrackingEnabled;

    private boolean testExceptions;

    @Nullable
    private DroolsTestException thrownException;

    @Nullable
    private KieSession session;

    private final Map<String, AtomicInteger> ruleFiredTimes = new HashMap<>();

    private final List<String> firedRules = new ArrayList<>();

    @Nullable
    private Set<String> rulesToFire;

    /*
     * Construction
     */

    @Builder
    protected KieSessionRule(String sessionName, boolean ruleTrackingEnabled, boolean testExceptions) {
        this.sessionName = sessionName;
        this.ruleTrackingEnabled = ruleTrackingEnabled;
        this.testExceptions = testExceptions;
    }

    /*
     * State
     */

    public KieSession getSession() {
        Objects.requireNonNull(session);
        return session;
    }

    @Nullable
    protected DroolsTestException getThrownException() {
        return thrownException;
    }

    public List<String> getFiredRules() {
        return Collections.unmodifiableList(firedRules);
    }

    /*
     * Implementation
     */

    @Override
    public Statement apply(Statement base, Description description) {
        return new Statement() {
            @Override
            public void evaluate() throws Throwable {
                Collection<Annotation> methodAnnotations = description.getAnnotations();
                Collection<Annotation> classAnnotations = Arrays.asList(description.getTestClass().getAnnotations());
                try {
                    session = tryNewKieSession(methodAnnotations)
                            .orElseGet(() -> tryNewKieSession(classAnnotations)
                                    .orElseGet(() -> newKieSession()));
                } catch (DroolsTestException e) {
                    if (testExceptions) {
                        thrownException = e;
                    } else {
                        throw e;
                    }
                }
                KieRuntimeLogger logger = null;
                if (session != null) {
                	logger = newLogger(session);
                }
                try {
                    base.evaluate();
                } finally {
                	if (logger != null) {
                		logger.close();	
                	}
                    if (session != null) {
                        session.destroy();
                    }
                    session = null;
                }
            }
        };
    }

    /*
     * Session construction
     */

    protected Optional<KieSession> tryNewKieSession(Collection<Annotation> annotations) {
        DroolsResource droolsResource = null;
        DroolsResources droolsResources = null;
        DroolsSession droolsSession = null;
        int annotationCount = 0;

        for (Annotation a : annotations) {
            if (DroolsResource.class.equals(a.annotationType())) {
                droolsResource = (DroolsResource) a;
                annotationCount++;
            } else if (DroolsResources.class.equals(a.annotationType())) {
                droolsResources = (DroolsResources) a;
                annotationCount++;
            } else if (DroolsSession.class.equals(a.annotationType())) {
                droolsSession = (DroolsSession) a;
                annotationCount++;
            } else if (DroolsRules.class.equals(a.annotationType())) {
                rulesToFire = new HashSet<>(Arrays.asList(((DroolsRules) a).value()));
            }
        }

        if (annotationCount == 0 && sessionName == null) {
            return Optional.empty();
        }

        if (annotationCount > 1) {
            String message = String.format("Only one of the following annotations is allowed: %s", ANNOTATIONS);
            throw new IncompatibleAnnotationsException(message);
        }

        KieSession newSession = null;

        if (droolsResource != null) {
            newSession = newKieSession(droolsResource);
        } else if (droolsResources != null) {
            newSession = newKieSession(droolsResources.value());
        } else if (droolsSession != null) {
            newSession = newKieSession(droolsSession);
        } else if (sessionName != null) {
            newSession = newKieSession(sessionName);
        }

        return Optional.ofNullable(newSession);
    }

    protected KieSession newKieSession(DroolsResource... resources) {
        KnowledgeBuilder kbuilder = KnowledgeBuilderFactory.newKnowledgeBuilder();
        for (DroolsResource r : resources) {
            try {
                Resource resource = newResource(r);
                ResourceType resourceType = r.type().getResourseType();
                kbuilder.add(resource, resourceType);
            } catch (Exception e) {
                String message = String.format("Unable to load the resource, %s", r.value());
                throw new ResourceException(message, e);
            }
        }
        if (kbuilder.hasErrors()) {
            throw new KnowledgeBuilderException(kbuilder.getErrors().toString());
        }
        return kbuilder.newKieBase().newKieSession();
    }

    protected KieSession newKieSession(DroolsSession droolsSession) {
        return newKieSession(droolsSession.value());
    }

    protected KieSession newKieSession(String sessionName) {
        KieServices kServices = KieServices.Factory.get();
        KieContainer kContainer = kServices.getKieClasspathContainer();
        KieSession newSession = sessionName.isEmpty()
                ? kContainer.newKieSession()
                : kContainer.newKieSession(sessionName);
        if (newSession == null) {
            String message = String.format("Session with name '%s' is not found", sessionName);
            throw new SessionNotFoundException(message);
        }
        return newSession;
    }
   
    protected KieSession newKieSession() {
        KnowledgeBuilder kbuilder = KnowledgeBuilderFactory.newKnowledgeBuilder();
        return kbuilder.newKieBase().newKieSession();
    }

    protected static Resource newResource(DroolsResource droolsResource) {
        String path = droolsResource.value();
        DroolsResourcePathType pathType = droolsResource.pathType();
        Resource resource;
        switch (pathType) {
            case CLASSPATH:
                resource = ResourceFactory.newClassPathResource(path);
                break;
            case FILE:
                resource = ResourceFactory.newFileResource(path);
                break;
            case URL:
                resource = ResourceFactory.newUrlResource(path);
                break;
            default:
                throw new IllegalStateException("Unknown resource path type: " + pathType);
        }
        return resource;
    }

    /*
     * Session wrapping
     */

    /**
     * @see {@link org.kie.api.runtime.KieSession#fireAllRules()}
     */
    public int fireAllRules() {
        return fireAllRules(Collections.emptyList());
    }

    /**
     * @param facts facts to be inserted
     * 
     * @see {@link org.kie.api.runtime.KieSession#insert(Object)}
     * @see {@link org.kie.api.runtime.KieSession#fireAllRules()}
     */
    public int fireAllRules(Object... facts) {
        return fireAllRules(Arrays.asList(facts));
    }

    /**
     * @param facts facts to be inserted
     * 
     * @see {@link org.kie.api.runtime.KieSession#insert(Object)}
     * @see {@link org.kie.api.runtime.KieSession#fireAllRules()}
     */
    public int fireAllRules(Collection<Object> facts) {
        ruleFiredTimes.clear();
        firedRules.clear();
        facts.forEach(session::insert);
        if ((rulesToFire == null || rulesToFire.isEmpty()) && !ruleTrackingEnabled) {
            return session.fireAllRules();
        }
        return session.fireAllRules(match -> {
            String name = match.getRule().getName();
            if (rulesToFire != null && !rulesToFire.isEmpty() && !rulesToFire.contains(name)) {
                return false;
            }
            if (ruleTrackingEnabled) {
                AtomicInteger counter = ruleFiredTimes.get(name);
                if (counter == null) {
                    counter = new AtomicInteger();
                    ruleFiredTimes.put(name, counter);
                }
                counter.incrementAndGet();
                firedRules.add(name);
            }
            return true;
        });
    }

    /**
     * @see {@link org.kie.api.runtime.KieSession#getQueryResults(String, Object...)}
     */
    public QueryResults getQueryResults(String query, Object... arguments) {
        return session.getQueryResults(query, arguments);
    }

    /*
     * Assertions
     */

    public void assertRuleFiredTimes(String ruleName, int times) {
        assertThat(ruleName, firedTimes(times));
    }

    /*
     * Matchers
     */

    //CHECKSTYLE:OFF MultipleStringLiterals
    public Matcher<String> firedTimes(int times) {
        return new AbstractMatcher<String>() {
            @Override
            protected boolean matchesInternal(Object item) {
                return getFiredTimes(item) == times;
            }
            @Override
            protected void describeToInternal(org.hamcrest.Description description) {
                description.appendText("rule fired ").appendValue(times).appendText(" times");
            }
            @Override
            protected void describeMismatchInternal(Object item, org.hamcrest.Description description) {
                description.appendText("rule fired ").appendValue(getFiredTimes(item)).appendText(" times");
            }
            private int getFiredTimes(Object item) {
                return ofNullable(ruleFiredTimes.get(item))
                        .map(AtomicInteger::get)
                        .orElse(0);
            }
        };
    }
    //CHECKSTYLE:OFF MultipleStringLiterals

    protected abstract class AbstractMatcher<T> extends BaseMatcher<T> {
        @Override
        public boolean matches(Object item) {
            if (!ruleTrackingEnabled) {
                return false;
            }
            return matchesInternal(item);
        }
        @Override
        public void describeTo(org.hamcrest.Description description) {
            if (!ruleTrackingEnabled) {
                description.appendText("the rule tracking is enabled ");
            } else {
                describeToInternal(description);                    
            }
        }
        @Override
        public void describeMismatch(Object item, org.hamcrest.Description description) {
            if (!ruleTrackingEnabled) {
                description.appendText("the rule tracking is NOT enabled ");
            } else {
                describeMismatchInternal(item, description);
            }
        }
        protected abstract boolean matchesInternal(Object item);
        protected abstract void describeToInternal(org.hamcrest.Description description);
        protected abstract void describeMismatchInternal(Object item, org.hamcrest.Description description);
    }

    /*
     * Logging
     */

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