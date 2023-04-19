package com.bluewhale.yamllens.action;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.mutable.MutableInt;

import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableModel;
import java.util.List;
import java.util.Map;
import java.util.SortedMap;

import static com.bluewhale.yamllens.utils.ValueUtils.escapeBackSlash;

public class PropertyContainer {
	public static final String DEFAULT_PROFILE = "default";

	private final List<String> profiles;
	private final Map<String, Map<String, String>> profilePropertyMap;
	private final SortedMap<String, Map<String, String>> propertyProfileMap;

	public PropertyContainer(List<String> profiles, Map<String, Map<String, String>> profilePropertyMap, SortedMap<String, Map<String, String>> propertyProfileMap) {
		this.profiles = profiles;
		this.profilePropertyMap = profilePropertyMap;
		this.propertyProfileMap = propertyProfileMap;
	}

	public DefaultTableModel getTableModel(String filterText) {
		var model = new DefaultTableModel();
		model.addColumn("Property");
		model.addColumn("Profile");
		model.addColumn("Value");

		propertyProfileMap.entrySet()
						  .stream()
						  .filter(entry -> filterPropertyName(entry.getKey(), filterText))
						  .forEach(entry -> {
							  var rowIndex = new MutableInt();
							  profiles.forEach(profile ->
												   model.addRow(new Object[] {rowIndex.getAndIncrement() == 0 ? entry.getKey() : StringUtils.EMPTY,
																			  profile,
																			  escapeBackSlash(entry.getValue()
																								   .getOrDefault(profile, StringUtils.EMPTY))})
											  );
						  });

		return model;
	}

	private boolean filterPropertyName(String key, String filterText) {
		return StringUtils.isBlank(filterText) || StringUtils.contains(key, filterText);
	}

	public List<String> getProfiles() {
		return profiles;
	}

	public SortedMap<String, Map<String, String>> getPropertyProfileMap() {
		return propertyProfileMap;
	}

	public TableModel getTableModel() {
		return getTableModel(null);
	}
}
