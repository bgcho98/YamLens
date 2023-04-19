package com.bluewhale.yamllens.action;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.mutable.MutableInt;

import java.io.FileWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;
import java.util.SortedMap;

import static com.bluewhale.yamllens.action.PropertyContainer.DEFAULT_PROFILE;
import static com.bluewhale.yamllens.utils.ValueUtils.escapeBackSlash;
import static com.bluewhale.yamllens.utils.ValueUtils.getEscapedValue;

public class CsvFileWriter {
	private static final String DELIMITER = ",";
	private static final String NEWLINE = "\n";

	public void writeToCsvFile(String csvFilePath, PropertyContainer propertyContainer) throws IOException {
		try (var writer = new FileWriter(csvFilePath, StandardCharsets.UTF_8)) {
			var output = outputV2(propertyContainer.getProfiles(), propertyContainer.getPropertyProfileMap());
			writer.write('\ufeff');
			writer.write(output);
		}

	}

	private String outputV2(List<String> profiles, SortedMap<String, Map<String, String>> propertyProfileMap) {
		var sb = new StringBuilder(100000);
		sb.append("PropertyName")
		  .append(DELIMITER)
		  .append("Profile")
		  .append(DELIMITER)
		  .append("Value")
		  .append(NEWLINE);
		propertyProfileMap.forEach((propertyName, env) -> {
			if (env.values().stream().filter(StringUtils::isNotBlank).count() == 1 &&
				StringUtils.isNotBlank(env.get(DEFAULT_PROFILE))) {
				appendProperties(sb, propertyName, env, DEFAULT_PROFILE);
			} else {
				var first = new MutableInt(0);
				profiles.forEach(profile -> appendProperties(sb, first.getAndIncrement() == 0 ? propertyName : StringUtils.EMPTY, env, profile));
			}

		});

		return sb.toString();
	}

	private void appendProperties(StringBuilder sb, String propertyName, Map<String, String> env, String profile) {
		sb.append(propertyName)
		  .append(DELIMITER)
		  .append(profile)
		  .append(DELIMITER)
		  .append(getEscapedValue(escapeBackSlash(env.getOrDefault(profile, StringUtils.EMPTY))))
		  .append(NEWLINE);
	}

}
