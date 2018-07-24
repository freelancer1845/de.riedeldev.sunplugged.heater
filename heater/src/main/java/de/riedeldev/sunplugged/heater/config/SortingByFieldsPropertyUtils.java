package de.riedeldev.sunplugged.heater.config;

import java.lang.reflect.Field;
import java.util.Set;
import java.util.TreeSet;

import org.yaml.snakeyaml.introspector.BeanAccess;
import org.yaml.snakeyaml.introspector.Property;
import org.yaml.snakeyaml.introspector.PropertyUtils;

public class SortingByFieldsPropertyUtils extends PropertyUtils {

	private final Field[] fields;

	public SortingByFieldsPropertyUtils(Class<?> clazz) {
		fields = clazz.getDeclaredFields();
	}

	@Override
	protected Set<Property> createPropertySet(Class<? extends Object> type,
			BeanAccess bAccess) {
		Set<Property> result = new TreeSet<Property>((prop1, prop2) -> {
			int idxOfProp1 = -1;
			int idxOfProp2 = -1;
			for (int i = 0; i < fields.length; i++) {
				if (fields[i].getName().equals(prop1.getName())) {
					idxOfProp1 = i;
				} else if (fields[i].getName().equals(prop2.getName())) {
					idxOfProp2 = i;
				}
				if (idxOfProp1 != -1 && idxOfProp2 != -1) {
					break;
				}
			}
			System.out.println(prop1.getName() + ": " + idxOfProp1 + " "
					+ prop2.getName() + ": " + idxOfProp2);
			return Integer.compare(idxOfProp1, idxOfProp2);
		});
		result.addAll(super.createPropertySet(type, bAccess));

		return result;
	}

}
