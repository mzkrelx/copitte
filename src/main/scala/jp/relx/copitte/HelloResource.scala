package jp.relx.copitte
import javax.ws.rs.GET
import javax.ws.rs.Path
import javax.ws.rs.Produces
import javax.ws.rs.core.Response

@Path("/")
class HelloResource {
  @GET
  def hello(): Response = {
    val res =
      <html xmlns="http://www.w3.org/1999/xhtml">
        <body>Hello, World</body>
      </html>
    Response.ok(res.toString()).build()
  }
}