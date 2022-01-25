package munit

import java.nio.file.Path

import sttp.tapir._
import sttp.tapir.docs.openapi.OpenAPIDocsOptions
import sttp.tapir.openapi.Info

class TapirGoldenOpenAPISuiteSuite extends TapirGoldenOpenAPISuite {

  override def tapirGoldenOpenAPIPath: Path = super.tapirGoldenOpenAPIPath.resolve("yaml")

  override def tapirGoldenOpenAPIOptions: OpenAPIDocsOptions =
    OpenAPIDocsOptions.default.copy(defaultDecodeFailureOutput = _ => None)

  override def tapirGoldenOpenAPIFileName: String = "open-api.yaml"

  override def tapirGoldenOpenAPIInfo: Info = Info("title", "version", Some("description"))

  override def tapirGoldenOpenAPIHeader: String = "# This is a header"

  override val endpoints: List[AnyEndpoint] = List(
    endpoint.get
      .in("v1" / "users" / path[String]("user_id"))
      .in(query[Int]("size"))
      .out(stringBody)
      .errorOut(stringBody)
  )

}
