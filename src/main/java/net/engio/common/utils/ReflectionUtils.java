package net.engio.common.utils;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.LinkedList;
import java.util.List;

import org.jboss.logging.Logger;

public class ReflectionUtils {
	
	private static Logger log = Logger.getLogger(ReflectionUtils.class);

	private static String getterName(String fieldName) {
		return getterSetterName("get", fieldName);
	}

	private static String setterName(String fieldName) {
		return getterSetterName("set", fieldName);
	}

	private static String getterSetterName(String prefix, String fieldName) {
		if(fieldName == null || fieldName.isEmpty()) return "null";
		return prefix + fieldName.substring(0, 1)
			.toUpperCase() + fieldName.substring(1, fieldName.length());
	}

	public static Field getField(String fieldName, Class<?> target) {
		try {
			return target.getDeclaredField(fieldName);
		} catch (Exception e) {
			return target.equals(Object.class) ? null : getField(fieldName, target.getSuperclass());
		}
	}

	public static Method getMethod(Class<?> target, String methodName, Class<?>... parameterTypes) {
		try {
			Method m = target.getDeclaredMethod(methodName, parameterTypes);
			if (m != null) {
				return m;
			} else {
				return target.equals(Object.class) ? null : getMethod(target.getSuperclass(),
					methodName, parameterTypes);
			}
		} catch (Exception e) {
			return target.equals(Object.class) ? null : getMethod(target.getSuperclass(),
				methodName, parameterTypes);
		}
	}

	public static List<Field> getFields(Predicate<Field> condition, Class<?> target) {
		List<Field> fields = new LinkedList<Field>();
		try {
			for (Field field : target.getDeclaredFields()) {
				if (condition.matches(field)) {
					fields.add(field);
				}
			}
		} catch (Exception e) {
			//nop
		}
		if (!target.equals(Object.class)) {
			fields.addAll(getFields(condition, target.getSuperclass()));
		}
		return fields;
	}
	
	public static List<Method> getMethods(Predicate<Method> condition, Class<?> target) {
		List<Method> methods = new LinkedList<Method>();
		try {
			for (Method method : target.getDeclaredMethods()) {
				if (condition.matches(method)) {
					methods.add(method);
				}
			}
		} catch (Exception e) {
			//nop
		}
		if (!target.equals(Object.class)) {
			methods.addAll(getMethods(condition, target.getSuperclass()));
		}
		return methods;
	}

	public static Object getProperty(Object source, String property, boolean useJavaBeanGetter) {
		if (!useJavaBeanGetter) {
			return getValueFromField(source, property);
		} else {
			try {
				Method getter = getMethod(source.getClass(), getterName(property));
				getter.setAccessible(true);
				return getter.invoke(source, null);
			} catch (Exception e) {
				log.error("Could not retrieve value from property " + property + " in " +  (source != null ? source.getClass() : "null") 
					+ (useJavaBeanGetter? " using getter method " + getterName(property) : " using direct field access"));
				return null;
			}
		}
	}

	public static Object getPropertyByExpression(Object source, String propertyExpression,
		boolean useJavaBeanGetter) {
		String propertyName = propertyExpression.contains("[")
			? propertyExpression.substring(0, propertyExpression.indexOf("["))
			: propertyExpression;
		Object property = getProperty(source, propertyName, useJavaBeanGetter);
		if (propertyName.length() < propertyExpression.length()) {
			int arrayIndex =
				Integer.parseInt(propertyExpression.substring(propertyExpression.indexOf("[") + 1,
					propertyExpression.indexOf("]")));
			property = ((Object[])property)[arrayIndex];
		}
		return property;
	}

	public static void setProperty(Object target, String property, Object value,
		boolean useJavaBeanGetter) {
		if(target == null || property == null){
			log.error("Target[" + target +"] or property[" + property +"] may not be null");
			return;
		}
		if (!useJavaBeanGetter) {
			setValueOnField(target, property, value);
		} else {
			try {
				Method m = getMethod(target.getClass(), setterName(property), value.getClass());
				if(m == null){
					log.error("Error retrieving setter with name " + setterName(property) + " and parameter " + value.getClass());
					return;
				}
				m.invoke(target,value);
			} catch (Exception e) {
				e.printStackTrace();
				log.error("Could not set value on property " + property + " in class " +  (target != null ? target.getClass() : "null") 
						+ (useJavaBeanGetter? " using setter method " + setterName(property) : " using direct field access"));
			}
		}
	}

	public static Object getValueFromField(Object source, String fieldName) {
		try {
			Field srcField = source.getClass()
				.getDeclaredField(fieldName);
			srcField.setAccessible(true);
			return srcField.get(source);
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	public static void setValueOnField(Object target, String fieldName, Object value) {
		try {
			Field targetField = getField(fieldName, target.getClass());
			targetField.setAccessible(true);
			targetField.set(target, value);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public abstract static class Predicate<T> {

		public abstract boolean matches(T target);

	}

}
