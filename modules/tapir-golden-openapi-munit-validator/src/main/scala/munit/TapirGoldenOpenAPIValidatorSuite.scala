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
