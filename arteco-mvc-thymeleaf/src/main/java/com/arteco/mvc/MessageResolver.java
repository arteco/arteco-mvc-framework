package com.arteco.mvc;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.arteco.mvc.core.App;
import com.arteco.mvc.core.MainFilter;
import org.apache.commons.lang3.StringUtils;
import org.thymeleaf.context.ITemplateContext;
import org.thymeleaf.context.WebEngineContext;
import org.thymeleaf.messageresolver.AbstractMessageResolver;

/**
 * Created by rarnau on 13/11/16.
 * Arteco Consulting Sl.
 * mailto: info@arteco-consulting.com
 */
public class MessageResolver extends AbstractMessageResolver {

	private static final Logger LOGGER = Logger.getLogger(MessageResolver.class.getName());

	private final App app;

	private Map<Locale, Properties> messagesMap = new HashMap<Locale, Properties>();
	private boolean logUnresolved;

	public MessageResolver(App app, boolean logUnresolved) {
		this.app = app;
		this.logUnresolved = logUnresolved;
	}

	public String resolveMessage(ITemplateContext context, Class<?> origin, String key, Object[] messageParameters) {
		Locale locale = context.getLocale();
		Properties properties = getProperties(locale);
		String value = properties.getProperty(key);
		if (value == null && !app.getDefaultLocale().equals(locale)) {
			properties = getProperties(app.getDefaultLocale());
			value = properties.getProperty(key);
		}
		if (value != null && messageParameters != null) {
			value = appendParameters(value, messageParameters);
		}
		if (value == null){
			if (context instanceof WebEngineContext){
				if (logUnresolved){
					LOGGER.log(Level.SEVERE, "Texto no encontrado " + key);
				}
			}
			value = "¿¿¿"+key+"???";
		}
		return value;
	}

	private String appendParameters(String value, Object[] messageParameters) {
		for (int i = 0; i < messageParameters.length; i++) {
			Object argValue = messageParameters[i];
			if (argValue == null) {
				argValue = "null";
			}
			value = StringUtils.replace(value, "{" + i + "}", argValue.toString());
		}
		return value;
	}

	private Properties getProperties(Locale locale) {
		Properties properties = messagesMap.get(locale);
		if (properties == null) {
			properties = loadProperties(locale);
		}
		return properties;
	}

	private Properties loadProperties(Locale locale) {
		Properties properties;
		properties = new Properties();
		messagesMap.put(locale, properties);
		String prefix = app.getDefaultLocale().equals(locale) ? StringUtils.EMPTY : "_" + locale.getLanguage();
		String propFileName = "/messages" + prefix + ".properties";
		InputStream is = MessageResolver.class.getResourceAsStream(propFileName);
		if (is != null) {
			try {
				properties.load(is);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return properties;
	}

	public String createAbsentMessageRepresentation(ITemplateContext context, Class<?> origin, String key, Object[] messageParameters) {
		return "¿¿¿" + key + "???";
	}

	public void clear() {
		messagesMap.clear();
	}
}
