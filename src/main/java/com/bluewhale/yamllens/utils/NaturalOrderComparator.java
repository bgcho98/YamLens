package com.bluewhale.yamllens.utils;

import java.util.Comparator;

public class NaturalOrderComparator implements Comparator<String> {

	public static final NaturalOrderComparator INSTANCE = new NaturalOrderComparator();

	@Override
	public int compare(String s1, String s2) {
		int i1 = 0, i2 = 0;
		int len1 = s1.length(), len2 = s2.length();

		while (i1 < len1 && i2 < len2) {
			char c1 = s1.charAt(i1);
			char c2 = s2.charAt(i2);

			if (Character.isDigit(c1) && Character.isDigit(c2)) {
				// skip leading zeros
				int start1 = i1, start2 = i2;
				while (i1 < len1 && s1.charAt(i1) == '0') i1++;
				while (i2 < len2 && s2.charAt(i2) == '0') i2++;

				int numStart1 = i1, numStart2 = i2;
				while (i1 < len1 && Character.isDigit(s1.charAt(i1))) i1++;
				while (i2 < len2 && Character.isDigit(s2.charAt(i2))) i2++;

				int numLen1 = i1 - numStart1;
				int numLen2 = i2 - numStart2;

				// longer numeric part = bigger number
				if (numLen1 != numLen2) {
					return numLen1 - numLen2;
				}

				// same length, compare digit by digit
				for (int j = 0; j < numLen1; j++) {
					int diff = s1.charAt(numStart1 + j) - s2.charAt(numStart2 + j);
					if (diff != 0) return diff;
				}

				// same numeric value, more leading zeros = smaller
				int zeros1 = numStart1 - start1;
				int zeros2 = numStart2 - start2;
				if (zeros1 != zeros2) {
					return zeros2 - zeros1;
				}
			} else {
				if (c1 != c2) {
					return c1 - c2;
				}
				i1++;
				i2++;
			}
		}

		return len1 - len2;
	}
}
