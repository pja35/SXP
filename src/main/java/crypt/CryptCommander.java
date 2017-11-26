package crypt;

import crypt.api.hashs.Hasher;
import crypt.factories.HasherFactory;
import rest.api.ServletPath;
import rest.factories.RestServerFactory;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;

@ServletPath("/command/hash/*") // url path. PREFIX WITH COMMAND/ !!!
@Path("/")
public class CryptCommander {
    public static void main(String[] args) {

        RestServerFactory.createAndStartRestServer("jetty", 8080, "crypt");
    }

    @GET
    @Path("/{input}") // a way to name the pieces of the query
    public String hash(@PathParam("input") String input) { // this argument will be initialized with
        // the piece of the query
        Hasher hasher = HasherFactory.createDefaultHasher();
        return new String(hasher.getHash(input.getBytes()));
    }
}
