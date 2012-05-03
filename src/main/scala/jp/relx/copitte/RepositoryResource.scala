package jp.relx.copitte
import javax.ws.rs.core.Response
import javax.ws.rs.POST
import javax.ws.rs.Path

@Path("/repos")
class RepositoryResource {

  @POST
  def registerRepo(): Response = {
    val res =
      <html xmlns="http://www.w3.org/1999/xhtml">
        <body>Repogitory was registerd.</body>
      </html>
    Response.ok(res.toString()).build()
  }

}