/*
 * Copyright 2022 Alejandro Hernández <https://github.com/alejandrohdezma>
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package munit

import java.nio.file.Files
import java.nio.file.Path

import scala.jdk.CollectionConverters._

import sttp.tapir._
import sttp.tapir.docs.openapi.OpenAPIDocsInterpreter
import sttp.tapir.docs.openapi.OpenAPIDocsOptions
import sttp.tapir.openapi.Info
import sttp.tapir.openapi.circe.yaml._

/** Base class for golden testing the generation of OpenAPI documentation for Tapir endpoints.
  *
  * To use this class you just need to override the `val endpoints: List[AnyEndpoint]` definition with your list of
  * endpoints to test.
  *
  * There are a bunch of things that can be overriden when generating the documentation:
  *
  *   - `tapirGoldenOpenAPIInfo`: The info that will be used when generating the OpenAPI file.
  *   - `tapirGoldenOpenAPIOptions`: The options that will be used when generating the OpenAPI file.
  *   - `tapirGoldenOpenAPIHeader`: The value of this header that will be prepended to the generated YAML file.
  *   - `tapirGoldenOpenAPIFileName`: Name of the generated file with the OpenAPI documentation.
  *   - `tapirGoldenOpenAPIPath`: Folder where the OpenAPI file will be generated.
  *
  * @example
  *   {{{
  *   class EndpointsSuite extends munit.TapirGoldenOpenAPISuite {
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
abstract class TapirGoldenOpenAPISuite extends FunSuite {

  /** The list of endpoints for which to generate open-api documentation. */
  val endpoints: List[AnyEndpoint]

  /** The info that will be used when generating the OpenAPI file.
    *
    * Defaults to empty `Info`.
    *
    * @example
    *   {{{
    *   override def tapirGoldenOpenAPIInfo: Info = Info("title", "version", Some("description"))
    *   }}}
    *
    * @see
    *   https://tapir.softwaremill.com/en/stable/docs/openapi.html#id1
    */
  def tapirGoldenOpenAPIInfo: Info = Info("", "")

  /** The options that will be used when generating the OpenAPI file.
    *
    * Defaults to `OpenAPIDocsOptions.default`
    *
    * @see
    *   https://tapir.softwaremill.com/en/stable/docs/openapi.html#options
    */
  def tapirGoldenOpenAPIOptions: OpenAPIDocsOptions = OpenAPIDocsOptions.default

  /** The value of this header will be prepended to the generated YAML file. If you want to disable this behaviour just
    * set this value to an empty string.
    */
  def tapirGoldenOpenAPIHeader: String = "# This file has been autogenerated, don't try to edit it manually"

  /** Name of the generated file with the OpenAPI documentation.
    *
    * Defaults to `openapi.yaml`
    */
  def tapirGoldenOpenAPIFileName: String = "openapi.yaml"

  /** Folder where the OpenAPI file will be generated.
    *
    * Defaults to `resources` folder.
    */
  def tapirGoldenOpenAPIPath: Path =
    Path
      .of(getClass.getResource("/").toURI())
      .iterator()
      .asScala
      .toList
      .takeWhile(_.toString() != "target") // scalafix:ok
      .fold(Path.of("/"))(_ resolve _)
      .resolve("src")
      .resolve("test")
      .resolve("resources")

  private def yaml: String = {
    val openAPI = OpenAPIDocsInterpreter(tapirGoldenOpenAPIOptions).toOpenAPI(endpoints, tapirGoldenOpenAPIInfo).toYaml

    tapirGoldenOpenAPIHeader match {
      case ""     => openAPI
      case header => s"$header\n\n$openAPI"
    }
  }

  if (Files.notExists(tapirGoldenOpenAPIPath.resolve(tapirGoldenOpenAPIFileName)))
    test(s"Generating $tapirGoldenOpenAPIFileName") {
      Files.createDirectories(tapirGoldenOpenAPIPath)
      Files.writeString(tapirGoldenOpenAPIPath.resolve(tapirGoldenOpenAPIFileName), yaml)

      assert(
        !sys.env.contains("CI"),
        s"You forgot to commit the `${tapirGoldenOpenAPIPath.resolve(tapirGoldenOpenAPIFileName)}` file"
      )
    }
  else
    test(s"$tapirGoldenOpenAPIFileName is correct") {
      val content = Files.readString(tapirGoldenOpenAPIPath.resolve(tapirGoldenOpenAPIFileName))

      assertNoDiff(yaml, content)
    }

}
