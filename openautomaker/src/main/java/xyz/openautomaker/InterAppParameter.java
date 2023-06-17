package xyz.openautomaker;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.fasterxml.jackson.annotation.JsonIgnore;

/**
 *
 * @author Ian
 */
public class InterAppParameter
{

	@JsonIgnore
	private static final Logger LOGGER = LogManager.getLogger();

	private InterAppParameterType type;
	private String urlEncodedParameter;

	public InterAppParameter()
	{
	}

	public InterAppParameter(InterAppParameterType type, String urlEncodedParameter)
	{
		this.type = type;
		this.urlEncodedParameter = urlEncodedParameter;
	}

	public InterAppParameterType getType()
	{
		return type;
	}

	public void setType(InterAppParameterType type)
	{
		this.type = type;
	}

	public String getUrlEncodedParameter()
	{
		return urlEncodedParameter;
	}

	public void setUrlEncodedParameter(String urlEncodedParameter)
	{
		this.urlEncodedParameter = urlEncodedParameter;
	}

	@JsonIgnore
	public String getUnencodedParameter()
	{
		String unencodedParameter = null;

		try
		{
			unencodedParameter = URLDecoder.decode(urlEncodedParameter, "UTF-8");
		} catch (UnsupportedEncodingException ex)
		{
			LOGGER.error("Failed to decode param: " + urlEncodedParameter, ex);
		}

		return unencodedParameter;
	}

	public static InterAppParameter fromParts(String attribute_value_string)
	{
		InterAppParameter returnValue = null;

		//Expects something like projectName=hello
		String[] parts = attribute_value_string.split("=");
		if (parts.length == 2)
		{
			InterAppParameterType paramType = InterAppParameterType.fromTextValue(parts[0]);
			if (paramType != null)
			{
				returnValue = new InterAppParameter(paramType, parts[1]);
			}
		}

		return returnValue;
	}

	@Override
	public int hashCode()
	{
		return new HashCodeBuilder(11, 35).
				append(type).
				append(urlEncodedParameter).
				toHashCode();
	}

	@Override
	public boolean equals(Object obj)
	{
		if (!(obj instanceof InterAppParameter))
		{
			return false;
		}
		if (obj == this)
		{
			return true;
		}

		InterAppParameter rhs = (InterAppParameter) obj;
		return new EqualsBuilder().
				// if deriving: appendSuper(super.equals(obj)).
				append(type, rhs.type).
				append(urlEncodedParameter, rhs.urlEncodedParameter).
				isEquals();
	}
}
