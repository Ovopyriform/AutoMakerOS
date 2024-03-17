package org.openautomaker.i18n;

import java.util.Currency;
import java.util.List;
import java.util.Locale;
import java.util.MissingResourceException;
import java.util.ResourceBundle;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class OpenAutomakerI18N {

	private static String PKG_NAME = OpenAutomakerI18N.class.getPackageName();
	private static String LANG_PREFIX = PKG_NAME + ".LanguageData";
	private static String UI_LANG_PREFIX = PKG_NAME + ".UI_LanguageData";
	private static String NO_UI_LANG_PREFIX = PKG_NAME + ".NoUI_LanguageData";

	private Locale fldLocale = null;
	//private Currency fldCurrency = null;

	private ResourceBundle language = null;
	private ResourceBundle noUILanguage = null;
	private ResourceBundle uiLanguage = null;

	private ResourceBundle multiResourceBundle = null;

	private static final Pattern TEMPLATE_PATTERN = Pattern.compile(".*\\*T(\\d\\d).*");


	//TODO: Is this needed?  Perhaps just selectable languages enumeration?
	public static final Set<Locale> AVAILABLE_LOCALES = Set.of(
			Locale.UK,
			Locale.US,
			Locale.GERMANY,
			Locale.FRANCE,
			Locale.JAPAN,
			Locale.KOREA,
			Locale.SIMPLIFIED_CHINESE,
			Locale.TAIWAN,
			Locale.forLanguageTag("tr-TR"), //Turkey
			Locale.forLanguageTag("es-ES"), //Spain
			Locale.forLanguageTag("fi-FI"), //Finland
			Locale.forLanguageTag("ru-RU"), //Russia
			Locale.forLanguageTag("sv-SE"), //Sweden
			Locale.forLanguageTag("zh-HK"), //Hong Kong
			Locale.forLanguageTag("zh-SG")); // Singapore

	//	POUND("£"),
	//	DOLLAR("$"),
	//	EURO("€"),
	//	YEN_YUAN("¥"),
	//	KOREAN_WON("₩"),
	//	KRONA("kr"),
	//	INDIAN_RUPEE("₹"),
	//	BAHT("฿"),
	//	SWISS_FRANC("CHF"),
	//	RAND("R")

	//TODO: Are currencies actually needed?  Shoud probably be java money objects
	public static final Set<Currency> AVAILABLE_CURRENCIES = Set.of(
			Currency.getInstance("GBP"),
			Currency.getInstance("USD"),
			Currency.getInstance("EUR"),
			Currency.getInstance("JPY"),
			Currency.getInstance("KRW"),
			Currency.getInstance("CNY"),
			Currency.getInstance("TWD"),
			Currency.getInstance("RUB"),
			Currency.getInstance("SEK"),
			Currency.getInstance("SGD"),
			Currency.getInstance("HKD"),
			Currency.getInstance("INR"),
			Currency.getInstance("CHF"),
			Currency.getInstance("ZAR"),
			Currency.getInstance("THB"));

	public OpenAutomakerI18N() {
		this(null);
	}

	public OpenAutomakerI18N(Locale locale) {
		fldLocale = locale != null ? locale : Locale.getDefault();

		// Load the resource bundles.
		language = ResourceBundle.getBundle(LANG_PREFIX, fldLocale);
		uiLanguage = ResourceBundle.getBundle(UI_LANG_PREFIX, fldLocale);
		noUILanguage = ResourceBundle.getBundle(NO_UI_LANG_PREFIX, fldLocale);
	}

	public String t(String stringId) {
		String langString = null;
		try {
			ResourceBundle bundle = null;
			for (ResourceBundle resBundle : List.of(language, noUILanguage, uiLanguage)) {
				if (resBundle.containsKey(stringId)) {
					bundle = resBundle;
					break;
				}
			}

			if (bundle == null)
				return stringId;

			langString = bundle.getString(stringId);
			langString = substituteTemplates(langString);
		}
		catch (MissingResourceException ex) {
			langString = stringId;
		}
		return langString;
	}

	/**
	 * Strings containing templates (eg *T14) should be substituted with the correct text.
	 *
	 * @param langString
	 * @return
	 */
	public String substituteTemplates(String langString) {
		while (true) {
			Matcher matcher = TEMPLATE_PATTERN.matcher(langString);
			if (matcher.find()) {
				String template = "*T" + matcher.group(1);
				String templatePattern = "\\*T" + matcher.group(1);
				langString = langString.replaceAll(templatePattern, t(template));
			}
			else {
				break;
			}
		}
		return langString;
	}

	public ResourceBundle getCombinedResourceBundle() {
		if (multiResourceBundle == null)
			multiResourceBundle = new MultiResourceBundle(language, noUILanguage, uiLanguage);

		return multiResourceBundle;
	}

	/*
	 * public Currency getCurrancy() { return fldCurrency != null ? fldCurrency : Currency.getInstance(fldLocale); }
	 * 
	 * public void setCurrency(Currency currency) { if (AVAILABLE_CURRENCIES.contains(currency)) fldCurrency = currency; }
	 */
}
