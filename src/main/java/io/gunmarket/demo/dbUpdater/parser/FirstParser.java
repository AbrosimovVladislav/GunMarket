package io.gunmarket.demo.dbUpdater.parser;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * http://armsline.ru/ http://armsline.ru/s/43/oruzhie_samooborony.html
 */
public class FirstParser {

	private static final String prefix = "http://armsline.ru";

	private static final String postfix = "?&from=";

	private static final int pageSize = 20;

	private static final String item1 = prefix + "/s/43/oruzhie_samooborony.html";

	private static final String item2 = prefix + "/s/757/trawmaticheskoe_oruzhie.html";

	private static final String item3 = prefix + "/s/263/sredstwa_samooborony_bez_litsenzii.html";

	private static final String item4 = prefix + "/s/44/gladkostwolynoe_oruzhie.html";

	private static final String item5 = prefix + "/s/45/nareznoe_oruzhie.html";

	private static final String item6 = prefix + "/s/1062/eksklyuziwnoe_oruzhie.html";

	private static final String item7 = prefix + "/s/742/komissionnoe_oruzhie.html";

	private static final String item8 = prefix + "/s/389/makety_i_oholoschennoe.html";

	private static final String item9 = prefix + "/s/19/patrony.html";

	private static final String item0 = prefix + "/s/1249/aksessuary_dlya_oruzhiya.html";

	public static void main(String[] args) {
		FirstParser firstParser = new FirstParser();
		Map<String, String> categories = new HashMap<>();
		categories.put(item1, "cat1");
		categories.put(item2, "cat1");
		categories.put(item3, "cat1");
		categories.put(item4, "cat1");
		categories.put(item5, "cat1");
		categories.put(item6, "cat1");
		categories.put(item7, "cat1");
		categories.put(item8, "cat1");
		categories.put(item9, "cat1");
		categories.put(item0, "cat1");

		Set<ArmsLineProduct> collect = categories.entrySet()
				.stream()
				.map(e -> firstParser.getProductsFromCategory(e.getKey(), e.getValue()))
				.flatMap(Collection::stream)
				.collect(Collectors.toSet());

		System.out.println(collect.size());
	}

	public List<ArmsLineProduct> getProductsFromCategory(String categoryUrl, String categoryName) {
		int totalQuantityProducts = getQuantityOfCategory(categoryUrl);
		return Stream.iterate(0, current -> current + pageSize)
				.limit(totalQuantityProducts / pageSize)
				.map(offset -> categoryUrl + postfix + offset)
				.map(resultCategoryUrl -> parseArmsLinePage(resultCategoryUrl, categoryName))
				.flatMap(Collection::stream)
				.collect(Collectors.toList());
	}

	public int getQuantityOfCategory(String categoryUrl) {
		Document doc;
		try {
			doc = Jsoup.connect(categoryUrl).get();
		} catch (IOException e) {
			throw new UncheckedIOException(e);
		}
		Elements aElementsInPagenavi = doc.body().getElementsByClass("pagenavi").select("a");
		if (aElementsInPagenavi.size() == 0) return 0;
		String hrefVal = aElementsInPagenavi.get(aElementsInPagenavi.size() - 2).attr("href");
		return Integer.valueOf(hrefVal.substring(hrefVal.indexOf('=') + 1));
	}

	public List<ArmsLineProduct> parseArmsLinePage(String pageUrl, String categoryName) {
		Document doc;
		try {
			doc = Jsoup.connect(pageUrl).get();
		} catch (IOException e) {
			throw new UncheckedIOException(e);
		}
		return doc.body().getElementsByClass("product").stream().map(element -> {
			String name = element.select("a").text();
			String link = element.select("a").attr("href");
			String price = element.select("strong").text();
			String inStock = element.select("span").text();
			return new ArmsLineProduct(name, link, price, inStock, categoryName);
		}).peek(System.out::println).collect(Collectors.toList());
	}

}