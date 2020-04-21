package ru.galaktika.eim.drools.example;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CompletableFuture;

import org.junit.Ignore;
import org.junit.Test;
import static org.junit.Assert.*;

import org.kie.api.KieServices;
import org.kie.api.builder.KieBuilder;
import org.kie.api.builder.KieFileSystem;
import org.kie.api.builder.Message;
import org.kie.api.builder.ReleaseId;
import org.kie.api.io.KieResources;
import org.kie.api.io.Resource;
import org.kie.api.runtime.KieContainer;

/**
 * @author Mario Fusco
 * 
 * @see https://issues.jboss.org/browse/DROOLS-1197
 */
@Ignore
public class ConcurrencyTest {

    private final String returns1 = "import java.util.List;\n" +
                      "\n" +
                      "rule \"rule1\"\n" +
                      "\tdialect \"mvel\"\n" +
                      "\twhen\n" +
                      "\t\tres : List( )\n" +
                      "\tthen\n" +
                      " res.add(\"1\");\n" +
                      "end";

    private final String returns2 = "import java.util.List;\n" +
                      "\n" +
                      "rule \"rule1\"\n" +
                      "\tdialect \"mvel\"\n" +
                      "\twhen\n" +
                      "\t\tres : List( )\n" +
                      "\tthen\n" +
                      " res.add(\"1\");\n" +
                      " res.add(\"2\");\n" +
                      "end";

    @Test
    public void test() throws Exception {
        CompletableFuture<?> f1 = CompletableFuture.runAsync(() -> {
            for (int i = 0; i < 100; i++) {
                List<String> res1 = new ArrayList<>();
                createContainer(returns1, 1).newStatelessKieSession().execute(Arrays.asList(res1));
                assertEquals(1, res1.size());
            }
        });
        CompletableFuture<?> f2 = CompletableFuture.runAsync(() -> {
            for (int i = 0; i < 100; i++) {
                List<String> res2 = new ArrayList<>();
                createContainer(returns2, 2).newStatelessKieSession().execute(Arrays.asList(res2));
                assertEquals(2, res2.size());
            }

        });
        f1.get();
        f2.get();
    }

    private KieContainer createContainer(String rule, int id) {
        KieServices kieServices = KieServices.Factory.get();
        KieResources kieResources = kieServices.getResources();
        KieFileSystem kieFileSystem = kieServices.newKieFileSystem();
        ReleaseId releaseId = kieServices.newReleaseId("org", "test", id + ".0");

        Resource resource = kieResources.newByteArrayResource(rule.getBytes());
        kieFileSystem.write("src/main/resources/rule.drl", resource);
        kieFileSystem.writePomXML(getPom(releaseId));
        KieBuilder kb = kieServices.newKieBuilder(kieFileSystem).buildAll();

        if (kb.getResults().hasMessages(Message.Level.ERROR)) {
            throw new RuntimeException("Build Errors:\n" + releaseId);
        }

        return kieServices.newKieContainer(releaseId);
    }

    private String getPom( ReleaseId releaseId ) {
        return
                "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
                "<project xmlns=\"http://maven.apache.org/POM/4.0.0\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\"\n" +
                "         xsi:schemaLocation=\"http://maven.apache.org/POM/4.0.0 http://maven.apache.org/maven-v4_0_0.xsd\">\n" +
                "  <modelVersion>4.0.0</modelVersion>\n" +
                "\n" +
                "  <groupId>" + releaseId.getGroupId() + "</groupId>\n" +
                "  <artifactId>" + releaseId.getArtifactId() + "</artifactId>\n" +
                "  <version>" + releaseId.getVersion() + "</version>\n" +
                "</project>\n";
    }
}