package xyz.openautomaker.root.security;

import static java.util.Collections.singletonMap;
import static jakarta.ws.rs.core.MediaType.APPLICATION_JSON;
import static org.jose4j.jws.AlgorithmIdentifiers.HMAC_SHA256;

import java.security.Principal;
import java.util.Map;

import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;

import org.jose4j.jws.JsonWebSignature;
import org.jose4j.jwt.JwtClaims;
import org.jose4j.keys.HmacKey;
import org.jose4j.lang.JoseException;

import com.google.common.base.Throwables;
import com.google.common.collect.ImmutableMap;

import io.dropwizard.auth.Auth;

@Path("/jwt")
@Produces(APPLICATION_JSON)
public class TestSecuredResource
{

	private final byte[] tokenSecret;

	public TestSecuredResource(byte[] tokenSecret)
	{
		this.tokenSecret = tokenSecret;
	}

	@GET
	@Path("/generate-expired-token")
	public Map<String, String> generateExpiredToken()
	{
		final JwtClaims claims = new JwtClaims();
		claims.setExpirationTimeMinutesInTheFuture(-20);
		claims.setSubject("good-guy");

		final JsonWebSignature jws = new JsonWebSignature();
		jws.setPayload(claims.toJson());
		jws.setAlgorithmHeaderValue(HMAC_SHA256);
		jws.setKey(new HmacKey(tokenSecret));

		try
		{
			return singletonMap("token", jws.getCompactSerialization());
		} catch (JoseException e)
		{
			throw Throwables.propagate(e);
		}
	}

	@GET
	@Path("/generate-valid-token")
	public Map<String, String> generateValidToken()
	{
		final JwtClaims claims = new JwtClaims();
		claims.setSubject("good-guy");
		claims.setExpirationTimeMinutesInTheFuture(30);

		final JsonWebSignature jws = new JsonWebSignature();
		jws.setPayload(claims.toJson());
		jws.setAlgorithmHeaderValue(HMAC_SHA256);
		jws.setKey(new HmacKey(tokenSecret));

		try
		{
			return singletonMap("token", jws.getCompactSerialization());
		} catch (JoseException e)
		{
			throw Throwables.propagate(e);
		}
	}

	@GET
	@Path("/check-token")
	public Map<String, Object> get(@Auth Principal user)
	{
		return ImmutableMap.<String, Object>of("username", user.getName(), "id", ((User) user).getId());
	}
}
