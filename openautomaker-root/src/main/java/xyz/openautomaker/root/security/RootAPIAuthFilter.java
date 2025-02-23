package xyz.openautomaker.root.security;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.security.Principal;

import javax.annotation.Nullable;

import com.google.common.io.BaseEncoding;

import io.dropwizard.auth.AuthFilter;
import io.dropwizard.auth.Authenticator;
import io.dropwizard.auth.UnauthorizedHandler;
import io.dropwizard.auth.basic.BasicCredentialAuthFilter;
import io.dropwizard.auth.basic.BasicCredentials;
import jakarta.annotation.Priority;
import jakarta.ws.rs.Priorities;
import jakarta.ws.rs.WebApplicationException;
import jakarta.ws.rs.container.ContainerRequestContext;
import jakarta.ws.rs.core.HttpHeaders;
import jakarta.ws.rs.core.SecurityContext;

/**
 *
 * @author Ian
 */
@Priority(Priorities.AUTHENTICATION)
public class RootAPIAuthFilter<P extends Principal> extends AuthFilter<BasicCredentials, P>
{

	private UnauthorizedHandler unauthorizedHandler = new RootAPIUnauthorisedHandler();

	private RootAPIAuthFilter()
	{

	}

	@Override
	public void filter(ContainerRequestContext requestContext) throws IOException
	{
		final BasicCredentials credentials
		= getCredentials(requestContext.getHeaders().getFirst(HttpHeaders.AUTHORIZATION));
		if (!authenticate(requestContext, credentials, SecurityContext.BASIC_AUTH))
		{
			throw new WebApplicationException(unauthorizedHandler.buildResponse(prefix, realm));
		}
	}

	/**
	 * Parses a Base64-encoded value of the `Authorization` header in the form
	 * of `Basic dXNlcm5hbWU6cGFzc3dvcmQ=`.
	 *
	 * @param header the value of the `Authorization` header
	 * @return a username and a password as {@link BasicCredentials}
	 */
	@Nullable
	private BasicCredentials getCredentials(String header)
	{
		if (header == null)
		{
			return null;
		}

		final int space = header.indexOf(' ');
		if (space <= 0)
		{
			return null;
		}

		final String method = header.substring(0, space);
		if (!prefix.equalsIgnoreCase(method))
		{
			return null;
		}

		final String decoded;
		try
		{
			decoded = new String(BaseEncoding.base64().decode(header.substring(space + 1)), StandardCharsets.UTF_8);
		} catch (IllegalArgumentException e)
		{
			logger.warn("Error decoding credentials", e);
			return null;
		}

		// Decoded credentials is 'username:password'
		final int i = decoded.indexOf(':');
		if (i <= 0)
		{
			return null;
		}

		final String username = decoded.substring(0, i);
		final String password = decoded.substring(i + 1);
		return new BasicCredentials(username, password);
	}

	/**
	 * Builder for {@link BasicCredentialAuthFilter}.
	 * <p>
	 * An {@link Authenticator} must be provided during the building
	 * process.</p>
	 *
	 * @param <P> the principal
	 */
	public static class Builder<P extends Principal> extends
	AuthFilterBuilder<BasicCredentials, P, RootAPIAuthFilter<P>>
	{

		@Override
		protected RootAPIAuthFilter<P> newInstance()
		{
			return new RootAPIAuthFilter<>();
		}
	}
}
