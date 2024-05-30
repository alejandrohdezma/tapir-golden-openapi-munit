/*
 * Copyright 2022-2024 Alejandro Hernández <https://github.com/alejandrohdezma>
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

import java.nio.file.Path

import sttp.apispec.openapi.Info
import sttp.tapir._
import sttp.tapir.docs.openapi.OpenAPIDocsOptions

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
