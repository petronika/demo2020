package ru.galaktika.eim.drools.support;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.junit.Test;
import static org.junit.Assert.*;
import static ru.galaktika.eim.drools.support.DroolsUtils.*;

import org.junit.Before;
import org.kie.api.runtime.Globals;
import org.kie.api.runtime.KieSession;
import org.kie.api.runtime.StatelessKieSession;

import ru.galaktika.eim.drools.support.exception.DroolsSessionNotFound;

/**
 * @author Peter Titov
 */
public class DroolsTemplateTest {

    private static final String SESSION_STATEFUL = "session-stateful";

    private static final String SESSION_STATELESS = "session-stateless";

    private final Map<String, Object> commonGlobals = new HashMap<>();

    private final Map<String, Object> sessionGlobals = new HashMap<>();

    private final Map<String, Object> allGlobals = new HashMap<>();

    {
        commonGlobals.put("key1", "value1");
        commonGlobals.put("key2", "value2");
        commonGlobals.put("key3", "value3");

        sessionGlobals.put("key4", "value5");
        sessionGlobals.put("key5", "value6");
        sessionGlobals.put("key6", "value7");

        allGlobals.putAll(commonGlobals);
        allGlobals.putAll(sessionGlobals);
    }

    private DroolsTemplate droolsTemplate;

    @Before
    public void setUp() throws Exception {
        droolsTemplate = new DroolsTemplate();
        droolsTemplate.setCommonGlobals(commonGlobals);
    }

    @Test
    public void testNewKieSession() {
        KieSession session = droolsTemplate.newKieSession(SESSION_STATEFUL, sessionGlobals);
        try {
            checkGlobals(session.getGlobals());
            session.fireAllRules();
        } finally {
            session.destroy(); // Don't forget!
        }
    }

    @Test
    public void testNewStatelessKieSession() {
        StatelessKieSession session = droolsTemplate.newStatelessKieSession(SESSION_STATELESS, sessionGlobals);
        checkGlobals(session.getGlobals());
        session.execute("");
    }

    @Test
    public void testWithKieSession() {
        String result = UUID.randomUUID().toString();
        String actualResult = droolsTemplate.withKieSession(SESSION_STATEFUL, sessionGlobals, session -> {
            checkGlobals(session.getGlobals());
            session.fireAllRules();
            return result;
        });
        assertEquals(result, actualResult);
    }

    @Test
    public void testWithStatelessKieSession() {
        String result = UUID.randomUUID().toString();
        String actualResult = droolsTemplate.withStatelessKieSession(SESSION_STATELESS, sessionGlobals, session -> {
            checkGlobals(session.getGlobals());
            session.execute("");
            return result;
        });
        assertEquals(result, actualResult);
    }

    @Test(expected = DroolsSessionNotFound.class)
    public void testSessionNotFound() {
        droolsTemplate.newKieSession("bla-bla-bla", null);
    }

    private void checkGlobals(Globals globals) {
        assertEquals(allGlobals, globalsToMap(globals));
    }
}