package ru.galaktika.eim.drools.support;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import org.kie.api.runtime.Globals;
import org.kie.api.runtime.KieSession;
import org.kie.api.runtime.rule.QueryResults;

/**
 * @author Peter Titov
 */
public final class DroolsUtils {

    private DroolsUtils() {}

    public static void addGlobals(Globals globals, Map<String, Object> map) {
        Objects.requireNonNull(globals);
        Objects.requireNonNull(map);
        for (Map.Entry<String, Object> entry : map.entrySet()) {
            globals.set(entry.getKey(), entry.getValue());
        }
    }

    public static Map<String, Object> globalsToMap(Globals globals) {
        Objects.requireNonNull(globals);
        Map<String, Object> map = new HashMap<>();
        for (String key : globals.getGlobalKeys()) {
            map.put(key, globals.get(key));
        }
        return map;
    }

    public static <T> List<T> queryResults(KieSession session, String query, String var, Class<T> clazz) {
        QueryResults queryResults = session.getQueryResults(query);
        return StreamSupport.stream(queryResults.spliterator(), false)
                .map(row -> row.get(var))
                .map(obj -> clazz.cast(obj))
                .collect(Collectors.toList());
    }
}