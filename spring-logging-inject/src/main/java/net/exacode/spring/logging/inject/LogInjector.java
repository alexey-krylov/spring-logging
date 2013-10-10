package net.exacode.spring.logging.inject;

import java.lang.reflect.Field;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.util.ReflectionUtils;
import org.springframework.util.ReflectionUtils.FieldCallback;

/**
 * Injects {@link Logger}s in fields marked with {@link Log}.
 * <p>
 * In order to activate {@link Log} injection register {@link LogInjector} in
 * the spring context.
 * 
 * @author mendlik
 * 
 */
public class LogInjector implements BeanPostProcessor {

	@Override
	public Object postProcessAfterInitialization(Object bean, String beanName)
			throws BeansException {
		return bean;
	}

	@Override
	public Object postProcessBeforeInitialization(final Object bean,
			String beanName) throws BeansException {
		ReflectionUtils.doWithFields(bean.getClass(), new FieldCallback() {

			@Override
			public void doWith(Field field) throws IllegalArgumentException,
					IllegalAccessException {
				// make the field accessible if defined private
				ReflectionUtils.makeAccessible(field);
				Log log = field.getAnnotation(Log.class);
				if (log != null) {
					if (field.getType().equals(Logger.class)) {
						Class<?> loggerClass = (log.fromClass() != Log.DEFAULT.class) ? log
								.fromClass() : bean.getClass();
						Logger logger = LoggerFactory.getLogger(loggerClass);
						field.set(bean, logger);
					} else {
						throw new IllegalArgumentException(
								"Field type of field annoteted with Log annotation. Expected field type: "
										+ Logger.class.getCanonicalName());
					}
				}
			}
		});
		return bean;
	}
}
