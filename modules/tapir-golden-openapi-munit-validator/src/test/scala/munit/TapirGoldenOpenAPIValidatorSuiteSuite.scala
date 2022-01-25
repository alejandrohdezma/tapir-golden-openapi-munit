package munit

import sttp.tapir._

class TapirGoldenOpenAPIValidatorSuiteSuite extends TapirGoldenOpenAPISuite with TapirGoldenOpenAPIValidatorSuite {

  override val endpoints: List[AnyEndpoint] = List(
    endpoint.get
      .in("v1" / "users" / path[String]("user_id"))
      .in(query[Int]("size"))
      .out(stringBody)
      .errorOut(stringBody)
  )

}
