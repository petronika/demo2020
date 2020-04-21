package ru.galaktika.eim.drools.autoconfigure;

import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.context.event.EventListener;

import ru.galaktika.eim.drools.support.DroolsTemplate;

/**
 * Auto configuration for {@link DroolsTemplate}
 * 
 * @see {@link DroolsSupportProperties}
 * 
 * @author Petr Titov
 */
@Configuration
@ConditionalOnProperty(prefix = DroolsSupportProperties.PREFIX,
	name = "enabled", havingValue = "true", matchIfMissing = true)
@EnableConfigurationProperties(DroolsSupportProperties.class)
public class DroolsSupportAutoConfiguration {

    @Autowired
    private ApplicationContext appCtx;

    @Autowired
    private DroolsSupportProperties props;

    private DroolsTemplate droolsTemplate;

	@Bean
	public DroolsTemplate droolsTemplate() {
		if (props.getReleaseId() == null) {
			droolsTemplate = new DroolsTemplate();
		} else {
			droolsTemplate = new DroolsTemplate(
				props.getReleaseId().getGroupId(),
				props.getReleaseId().getArtifactId(),
				props.getReleaseId().getVersion());
		}

		if (props.getProbeSessions() != null) {
			props.getProbeSessions().forEach(sessionName ->
				droolsTemplate.withKieSession(sessionName, null, session -> null)
			);
		}

		return droolsTemplate;
	}

    @EventListener
    public void onApplicationEvent(ContextRefreshedEvent event) {
        if (appCtx.equals(event.getApplicationContext())) {
            setupCommonGlobals();
        }
    }

    private void setupCommonGlobals() {
    	Map<String, Object> commonGlobals = new HashMap<>();

    	if (props.getLoggerName() != null) {
    		Logger logger = LoggerFactory.getLogger(props.getLoggerName());
    		commonGlobals.put("logger", logger);
    	}

    	commonGlobals.putAll(appCtx.getBeansWithAnnotation(AvailableForRules.class));

    	droolsTemplate.setCommonGlobals(commonGlobals);
    }
}