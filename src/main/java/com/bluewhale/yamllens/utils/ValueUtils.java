package com.bluewhale.yamllens.utils;

import org.apache.commons.lang3.StringUtils;

public class ValueUtils {
	public static String escapeBackSlash(String str) {
		return str.replace("\t", "\\t")
				  .replace("\t", "\\t")
				  .replace("\n", "\\n");
	}

	public static String getEscapedValue(String value) {
		if (StringUtils.isNumeric(value)) {
			return ValueUtils.getEscapedValue("=\"" + value + "\"");
		}
		return ValueUtils.getEscapedValue(value);
	}
}
