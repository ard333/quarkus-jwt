package com.ard333.quarkusjwt.rest;

import javax.annotation.security.PermitAll;
import javax.inject.Inject;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import com.ard333.quarkusjwt.model.AuthRequest;
import com.ard333.quarkusjwt.model.AuthResponse;
import com.ard333.quarkusjwt.model.User;
import com.ard333.quarkusjwt.util.PBKDF2Encoder;
import com.ard333.quarkusjwt.util.TokenUtils;

import org.eclipse.microprofile.config.inject.ConfigProperty;

/**
 *
 * @author ard333
 */
@Path("/auth")
public class AuthenticationREST {

	@Inject
	PBKDF2Encoder passwordEncoder;

	@ConfigProperty(name = "com.ard333.quarkusjwt.jwt.duration") public Long duration;
	@ConfigProperty(name = "mp.jwt.verify.issuer") public String issuer;

	@PermitAll
	@POST @Path("/login") @Produces(MediaType.APPLICATION_JSON)
	public Response login(AuthRequest authRequest) {
		User u = User.findByUsername(authRequest.username);
		if (u != null && u.password.equals(passwordEncoder.encode(authRequest.password))) {
			try {
				return Response.ok(new AuthResponse(TokenUtils.generateToken(u.username, u.roles, duration, issuer))).build();
			} catch (Exception e) {
				return Response.status(Status.UNAUTHORIZED).build();
			}
		} else {
			return Response.status(Status.UNAUTHORIZED).build();
		}
	}

}
