package controller;

import java.io.File;
import java.nio.file.Files;

import javax.ws.rs.GET;  //REST-related dependencies
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;

import rest.api.ServletPath;

@ServletPath("/.well-know/acme-challenge/*")
@Path("/")
public class CASingingRequest {   
	@GET
    @Path("/{input}") 
    public String hash(@PathParam("input") String input) throws Exception
	{
		//Send the file for the CA. (let's encrypt)
		File file = new File("." + input);
		if( file.exists() )
		{
			String res = new String(Files.readAllBytes(file.toPath()));
			return res;
		}
		else
			return "404 not found";
    }
}
