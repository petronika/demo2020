package ru.galaktika.eim.drools.test.junit4.rule;

import static org.junit.Assert.assertEquals;

import org.junit.Rule;
import org.junit.Test;

import ru.galaktika.eim.drools.test.junit4.annotation.DroolsSession;
import ru.galaktika.eim.drools.test.junit4.exception.SessionNotFoundException;

/**
 * @author Peter Titov
 */
public class KieSessionRule2Test {

    private static final String
            RULE1 = "Rule 1",
            RULE2 = "Rule 2";

    @Rule
    public KieSessionRule session = KieSessionRule.builder()
            .ruleTrackingEnabled(true)
            .testExceptions(true)
            .sessionName("session-1-1")
            .build();

    @Test
    public void testGlobalSession() {
        session.fireAllRules();
        session.assertRuleFiredTimes(RULE1, 1);
        session.assertRuleFiredTimes(RULE2, 1);
    }

    @Test
    @DroolsSession("bla-bla-bla")
    public void testSpecificSession() {
        assertEquals(SessionNotFoundException.class, session.getThrownException().getClass());
    }
}