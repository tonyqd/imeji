package de.mpg.imeji.rest.resources;

import com.wordnik.swagger.annotations.Api;
import com.wordnik.swagger.annotations.ApiOperation;
import de.mpg.imeji.rest.process.AlbumProcess;
import de.mpg.imeji.rest.process.RestProcessUtils;
import de.mpg.imeji.rest.to.JSONResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.io.InputStream;

import static de.mpg.imeji.rest.process.AlbumProcess.readAllAlbums;
import static de.mpg.imeji.rest.process.RestProcessUtils.buildJSONResponse;


@Path("/albums")
@Api(value = "rest/albums", description = "Operations on albums")
public class AlbumResource implements ImejiResource{

	private static final Logger LOGGER = LoggerFactory
			.getLogger(CollectionResource.class);
	
	@POST
	@Consumes(MediaType.APPLICATION_JSON)
	@ApiOperation(value = "Create album or new version of album", notes = "The body parameter is the json of an album. You can get an example by using the get album method.")
	@Produces(MediaType.APPLICATION_JSON)
	public Response create(@Context HttpServletRequest req, InputStream json) {
		JSONResponse resp = AlbumProcess.createAlbum(req);
		return RestProcessUtils.buildJSONResponse(resp);
	}

	@GET
	@ApiOperation(value = "Read all albums filtered by query")
	@Produces(MediaType.APPLICATION_JSON)
	public Response readAll(@Context HttpServletRequest req,  @QueryParam("q") String q) {
		JSONResponse resp = readAllAlbums(req, q);
		return buildJSONResponse(resp);
	}
	
	@PUT
	@Path("/{id}")
	@ApiOperation(value = "Update album by id")
	@Produces(MediaType.APPLICATION_JSON)
	public Response update(@PathParam("id") String id, @Context HttpServletRequest req, InputStream json) throws Exception {
		JSONResponse resp = AlbumProcess.updateAlbum(req, id);
		return buildJSONResponse(resp);
	}

	@GET
	@Path("/{id}")
	@ApiOperation(value = "Get album by id")
	@Produces(MediaType.APPLICATION_JSON)
	@Override
	public Response read(@Context HttpServletRequest req,
			@PathParam("id") String id) {
		JSONResponse resp = AlbumProcess.readAlbum(req, id);
		return RestProcessUtils.buildJSONResponse(resp);
	}

	@Override
	public Response create(HttpServletRequest req) {
		// TODO Auto-generated method stub
		return null;
	}

	@DELETE
	@Path("/{id}")
	@ApiOperation(value = "Delete album by id")
	@Produces(MediaType.APPLICATION_JSON)
	public Response delete(@Context HttpServletRequest req,
			@PathParam("id") String id) {
		JSONResponse resp = AlbumProcess.deleteAlbum(req, id);
		return RestProcessUtils.buildJSONResponse(resp);
	}
	 
	@PUT
	@Path("/{id}/discard")
	@Consumes(MediaType.APPLICATION_FORM_URLENCODED)
	@ApiOperation(value = "Withraw a album by id, with mandatory discard commenrt")
	@Produces(MediaType.APPLICATION_JSON)
	public Response withdraw(@Context HttpServletRequest req,
			@FormParam("id") String id, @FormParam("discardComment") String discardComment) throws Exception {
		JSONResponse resp = AlbumProcess.withdrawAlbum(req, id, discardComment);
		return RestProcessUtils.buildJSONResponse(resp);
	}
	
	@PUT
	@Path("/{id}/release")
	@ApiOperation(value = "Release album by id")
	@Produces(MediaType.APPLICATION_JSON)
	public Response release(@Context HttpServletRequest req, @PathParam("id") String id) throws Exception {
		JSONResponse resp = AlbumProcess.releaseAlbum(req, id);
		return RestProcessUtils.buildJSONResponse(resp);
	}
	
	@PUT
	@Path("/{id}/members/link")
	@ApiOperation(value = "Link Items to album", notes = "Link items to an album with following parameters:"
			+ "<br/> 1) Album ID "
			+ "<br/> 2) List of item IDs"
			+ "<br/><br/>"
			+ "Json example:"
			+ "<div class=\"json_example\">"
			+ "[\"Item-ID 1\" , \"Item-ID 2\" , \"Item-ID 3\" , \"Item-ID 4\" ...]"
			+ "</div>"
			+ "<br/>")
	@Produces(MediaType.APPLICATION_JSON)
	public Response addItem(@PathParam("id") String id, @Context HttpServletRequest req, InputStream json) throws Exception {
		JSONResponse resp = AlbumProcess.addItems(req, id);
		return RestProcessUtils.buildJSONResponse(resp);
	}
	
	@PUT
	@Path("/{id}/members/unlink")
	@ApiOperation(value = "Unlink Items from an album", notes = "Unlink items from an album with following parameters:"
			+ "<br/> 1) Album ID "
			+ "<br/> 2) List of item IDs"
			+ "<br/><br/>"
			+ "Json example:"
			+ "<div class=\"json_example\">"
			+ "[\"Item-ID 1\" , \"Item-ID 2\" , \"Item-ID 3\" , \"Item-ID 4\" ...]"
			+ "</div>"
			+ "<br/>")
	@Produces(MediaType.APPLICATION_JSON)
	public Response removeItem(@PathParam("id") String id, @Context HttpServletRequest req, InputStream json) throws Exception {
		JSONResponse resp = AlbumProcess.removeItems(req, id, false);
		return RestProcessUtils.buildJSONResponse(resp);
	}
	
	@PUT
	@Path("/{id}/members/unlink/all")
	@ApiOperation(value = "Unlink all items from an album (empty an album from items)", notes = "Empty album with provided ID from items")
	@Produces(MediaType.APPLICATION_JSON)
	public Response removeAllItems(@PathParam("id") String id, @Context HttpServletRequest req) throws Exception {
		JSONResponse resp = AlbumProcess.removeItems(req, id, true);
		return RestProcessUtils.buildJSONResponse(resp);
	}
	
}
