package com.bluewhale.yamllens.action;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.mutable.MutableInt;

import javax.swing.table.DefaultTableModel;
import java.util.List;
import java.util.Map;
import java.util.SortedMap;
import java.util.stream.Stream;

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

	public DefaultTableModel getTableModel(String propertyFilterText, String valueFilterText, String selectedProfile) {
		var filteredData = getProperties(propertyFilterText, valueFilterText, selectedProfile);
		var columnNames = new String[] {"Property", "Profile", "Value"};
		return new DefaultTableModel(filteredData, columnNames);

	}

	private Object[][] getProperties(String propertyFilterText, String valueFilterText, String selectedProfile) {
		return propertyProfileMap.entrySet()
								 .stream()
								 .filter(entry -> filterPropertyName(entry.getKey(), propertyFilterText))
								 .flatMap(entry -> StringUtils.isNotBlank(selectedProfile)
												   ? getMergedPropertiesByProfile(entry, valueFilterText, selectedProfile)
												   : getPropertiesWithAllProfiles(entry, valueFilterText))
								 .toArray(Object[][]::new);
	}

	private Stream<Object[]> getPropertiesWithAllProfiles(Map.Entry<String, Map<String, String>> entry, String valueFilterText) {
		var propertieMap = entry.getValue();
		var rowIndex = new MutableInt();
		return profiles.stream()
					   .filter(profile -> filterPropertyName(propertieMap.getOrDefault(profile, StringUtils.EMPTY), valueFilterText))
					   .map(profile ->
								new Object[] {rowIndex.getAndIncrement() == 0 ? entry.getKey() : StringUtils.EMPTY,
											  profile,
											  escapeBackSlash(propertieMap.getOrDefault(profile, StringUtils.EMPTY))});
	}

	private Stream<Object[]> getMergedPropertiesByProfile(Map.Entry<String, Map<String, String>> entry, String valueFilterText, String selectedProfile) {
		var propertieMap = entry.getValue();

		return Stream.of(propertieMap.get(selectedProfile))
					 .map(it -> StringUtils.isNotBlank(it)
								? new Object[] {entry.getKey(), selectedProfile, it}
								: new Object[] {entry.getKey(), DEFAULT_PROFILE, propertieMap.getOrDefault(DEFAULT_PROFILE, StringUtils.EMPTY)})
					 .filter(object -> filterPropertyName((String) object[2], valueFilterText));
	}

	private boolean filterPropertyName(String key, String filterText) {
		return StringUtils.isBlank(filterText) || StringUtils.contains(key, filterText);
	}

	public List<String> getProfiles() {
		return profiles;
	}

	public String[] getProfilesForSelect() {
		return Stream.concat(Stream.of(StringUtils.EMPTY), profiles.stream())
					 .toArray(String[]::new);
	}

	public SortedMap<String, Map<String, String>> getPropertyProfileMap() {
		return propertyProfileMap;
	}
}
