package io.github.lmller.thesaurusrex;

import java.util.ArrayList;
import java.util.List;

/**
 * No java8 or kotlin for you
 */
public final class ListUtil {
	public static <T, R> List<R> map(List<T> source, Transform<T, R> transform) {
		ArrayList<R> result = new ArrayList<>(source.size());
		for (T item : source) {
			result.add(transform.apply(item));
		}

		return result;
	}

	public static String join(List<String> source, char separator) {
		StringBuilder buffer = new StringBuilder();
		int count = 0;
		for (String s : source) {
			if (++count > 1) {
				buffer.append(separator);
			}
			buffer.append(s);

		}
		return buffer.toString();
	}

	public interface Transform<T, R> {
		R apply(T t);
	}
}
