package controller;

import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import javax.ws.rs.GET;
import javax.ws.rs.HeaderParam;

import rest.api.Authentifier;
import rest.api.ServletPath;

@ServletPath("/api/contracts/*")
@Path("/")
public class Contracts {
	@GET
	@Path("/")
	@Produces(MediaType.APPLICATION_JSON)
	public String get(@HeaderParam(Authentifier.PARAM_NAME) String token) {
		return null;
	}
}
