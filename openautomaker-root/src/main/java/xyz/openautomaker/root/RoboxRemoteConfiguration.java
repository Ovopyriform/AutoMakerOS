package xyz.openautomaker.root;

import java.io.UnsupportedEncodingException;

import org.hibernate.validator.constraints.NotEmpty;

import com.fasterxml.jackson.annotation.JsonProperty;

import io.dropwizard.core.Configuration;

/**
 *
 * @author Ian
 */
public class RoboxRemoteConfiguration extends Configuration
{

	@NotEmpty
	private String defaultPIN;

	@JsonProperty
	public String getDefaultPIN()
	{
		return defaultPIN;
	}

	@JsonProperty
	public void setDefaultPIN(String defaultPIN)
	{
		this.defaultPIN = defaultPIN;
	}

	@NotEmpty
	private String jwtTokenSecret = "dfwzsdzwh82kp3ggadwdz772632gdsbd";

	public byte[] getJwtTokenSecret() throws UnsupportedEncodingException
	{
		return jwtTokenSecret.getBytes("UTF-8");
	}
}
