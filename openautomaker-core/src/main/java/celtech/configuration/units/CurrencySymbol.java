package celtech.configuration.units;

/**
 *
 * @author Ian
 */
//TODO: use java currency rather than bespoke  JavaMoney for conversion if needed
public enum CurrencySymbol
{

	POUND("£"),
	DOLLAR("$"),
	EURO("€"),
	YEN_YUAN("¥"),
	KOREAN_WON("₩"),
	KRONA("kr"),
	INDIAN_RUPEE("₹"),
	BAHT("฿"),
	SWISS_FRANC("CHF"),
	RAND("R");

	private final String currencySymbol;

	private CurrencySymbol(String currencySymbol)
	{
		this.currencySymbol = currencySymbol;
	}

	public String getDisplayString()
	{
		return currencySymbol;
	}
}
