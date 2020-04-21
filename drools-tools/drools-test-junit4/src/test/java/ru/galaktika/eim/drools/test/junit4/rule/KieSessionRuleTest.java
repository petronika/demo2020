package ru.galaktika.eim.drools.test.junit4.rule;

import org.junit.Rule;
import org.junit.Test;
import static org.junit.Assert.*;

import ru.galaktika.eim.drools.test.junit4.annotation.DroolsResource;
import ru.galaktika.eim.drools.test.junit4.annotation.DroolsResourcePathType;
import ru.galaktika.eim.drools.test.junit4.annotation.DroolsResources;
import ru.galaktika.eim.drools.test.junit4.annotation.DroolsRules;
import ru.galaktika.eim.drools.test.junit4.annotation.DroolsSession;
import ru.galaktika.eim.drools.test.junit4.exception.IncompatibleAnnotationsException;
import ru.galaktika.eim.drools.test.junit4.exception.KnowledgeBuilderException;
import ru.galaktika.eim.drools.test.junit4.exception.ResourceException;
import ru.galaktika.eim.drools.test.junit4.exception.SessionNotFoundException;

/**
 * @author Peter Titov
 */
public class KieSessionRuleTest {

    private static final String
            RULE1 = "Rule 1",
            RULE2 = "Rule 2";

    @Rule
    public KieSessionRule session = KieSessionRule.builder()
            .ruleTrackingEnabled(true)
            .testExceptions(true)
            .build();

    @Test
    public void testWithoutAnnotations() {
        fireAllRules();
    }

    @Test
    @DroolsResource("")
    @DroolsSession("")
    public void testIncompatibleAnnotations() {
        assertEquals(IncompatibleAnnotationsException.class, session.getThrownException().getClass());
    }

    @Test
    @DroolsResources({})
    @DroolsSession("")
    public void testIncompatibleAnnotations2() {
        assertEquals(IncompatibleAnnotationsException.class, session.getThrownException().getClass());
    }

    @Test
    @DroolsResource("")
    @DroolsResources({})
    public void testIncompatibleAnnotations3() {
        assertEquals(IncompatibleAnnotationsException.class, session.getThrownException().getClass());
    }

    @Test
    @DroolsResources({})
    public void testEmptyResources() {
        fireAllRules();
    }

    @Test
    @DroolsResource("rules/rules.drl")
    public void testClasspathResource() {
        fireAllRules(RULE1, RULE2);
    }

    @Test
    @DroolsResource(value = "src/test/resources/rules/rules.drl",
            pathType = DroolsResourcePathType.FILE)
    public void testFileResource() {
        fireAllRules(RULE1, RULE2);
    }

    @Test
    @DroolsResource(value = "file:src/test/resources/rules/rules.drl",
            pathType = DroolsResourcePathType.URL)
    public void testUrlResource() {
        fireAllRules(RULE1, RULE2);
    }

    @Test
    @DroolsResources(@DroolsResource("rules/rules.drl"))
    public void testClasspathResources() {
        fireAllRules(RULE1, RULE2);
    }

    @Test
    @DroolsResources(@DroolsResource(value = "src/test/resources/rules/rules.drl",
            pathType = DroolsResourcePathType.FILE))
    public void testFileResources() {
        fireAllRules(RULE1, RULE2);
    }

    @Test
    @DroolsResources(@DroolsResource(value = "file:src/test/resources/rules/rules.drl",
            pathType = DroolsResourcePathType.URL))
    public void testUrlResources() {
        fireAllRules(RULE1, RULE2);
    }

    @Test
    @DroolsResource("bla-bla-bla")
    public void testUnknownResource() {
        assertEquals(ResourceException.class, session.getThrownException().getClass());
    }

    @Test
    @DroolsResource("")
    public void testKnowledgeBuilderError() {
        assertEquals(KnowledgeBuilderException.class, session.getThrownException().getClass());
    }

    @Test
    @DroolsSession("")
    public void testDefaultSession() {
        fireAllRules(RULE1, RULE2);
    }

    @Test
    @DroolsSession("session-1-1")
    public void testSpecificSession() {
        fireAllRules(RULE1, RULE2);
    }

    @Test
    @DroolsSession("bla-bla-bla")
    public void testUnknownSession() {
        assertEquals(SessionNotFoundException.class, session.getThrownException().getClass());
    }

    @Test
    @DroolsSession("")
    @DroolsRules(RULE1)
    public void testRule1Only() {
        fireAllRules(RULE1);
    }

    @Test
    @DroolsSession("")
    @DroolsRules(RULE2)
    public void testRule2Only() {
        fireAllRules(RULE2);
    }

    @Test
    @DroolsSession("")
    @DroolsRules({RULE1, RULE2})
    public void testRuleList() {
        fireAllRules(RULE1, RULE2);
    }

    @Test
    @DroolsSession("")
    @DroolsRules({})
    public void testAllRulesByDefault() {
        fireAllRules(RULE1, RULE2);
    }

    @Test
    @DroolsRules({})
    public void testRulesWithoutAnnotations() {
        fireAllRules();
    }

    @Test
    @DroolsSession("")
    @DroolsRules("bla-bla-bla")
    public void testUknownRule() {
        fireAllRules();
    }

    private void fireAllRules(String... expectedFiredRules) {
        assertEquals(null, session.getThrownException());
        assertNotNull(session.getSession());

        assertEquals(expectedFiredRules.length, session.fireAllRules());
        for (String rule : expectedFiredRules) {
            assertThat(rule, session.firedTimes(1));
            session.assertRuleFiredTimes(rule, 1);
        }
    }
}