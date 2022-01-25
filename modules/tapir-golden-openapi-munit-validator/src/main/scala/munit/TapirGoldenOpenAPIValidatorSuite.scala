package munit

import java.nio.file.Files

import scala.jdk.CollectionConverters._

import io.swagger.v3.parser.OpenAPIV3Parser

/** Mix-in trait for `TapirGoldenOpenAPISuite` that adds validation of the OpenAPI file using
  * https://github.com/swagger-api/swagger-parser.
  *
  * @example
  *   {{{
  *   class TapirGoldenOpenAPIValidatorSuiteSuite
  *       extends munit.TapirGoldenOpenAPISuite
  *       with munit.TapirGoldenOpenAPIValidatorSuite {
  *
  *     // Here you will add your list of endpoints
  *     override val endpoints: List[AnyEndpoint] = ???
  *
  *   }
  *   }}}
  * @author
  *   Alejandro Hernández
  * @author
  *   Jose Gutierrez de Ory
  */
trait TapirGoldenOpenAPIValidatorSuite { self: TapirGoldenOpenAPISuite =>

  if (Files.exists(tapirGoldenOpenAPIPath.resolve(tapirGoldenOpenAPIFileName))) {
    test(s"$tapirGoldenOpenAPIFileName validates") {
      val content = Files.readString(tapirGoldenOpenAPIPath.resolve(tapirGoldenOpenAPIFileName))

      val result = new OpenAPIV3Parser().readContents(content)

      val failures = result.getMessages().asScala.toList

      assert(failures.isEmpty, clues(failures))
    }
  }

}
