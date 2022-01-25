# Golden testing for Tapir endpoints using MUnit

Extension library for [Tapir](https://github.com/softwaremill/tapir) and [MUnit](https://scalameta.org/munit/) that allows golden-testing Tapir endpoints' OpenAPI documentation.

## Installation

Add the following line to your `build.sbt` file:

```sbt
libraryDependencies += "com.alejandrohdezma" %% "tapir-golden-openapi-munit" % "0.0.0" % Test)
```

## Usage

Given this endpoint definition...

```scala
import sttp.tapir._

val myEndpoint = endpoint.get
  .in("v1" / "users" / path[String]("user_id"))
  .in(query[Int]("size"))
  .out(stringBody)
  .errorOut(stringBody)
```

In order to golden-test the generation of the OpenAPI documentation you just need to create a new suite extending `TapirGoldenOpenAPISuite` and override the list of endpoints:

```scala
class EndpointsSuite extends munit.TapirGoldenOpenAPISuite {

  override val endpoints: List[AnyEndpoint] = List(myEndpoint)

}
```

Running this suite the first time will generate the following file under `resources/openapi.yaml`.

> Important! You should always commit this file. If this generation process is called from CI the test will fail.

<details><summary>See the generated OpenAPI file</summary>

```yaml
# This file has been autogenerated, don't try to edit it manually

openapi: 3.0.3
info:
  title: ''
  version: ''
paths:
  /v1/users/{user_id}:
    get:
      operationId: getV1UsersUser_id
      parameters:
      - name: user_id
        in: path
        required: true
        schema:
          type: string
      - name: size
        in: query
        required: true
        schema:
          type: integer
      responses:
        '200':
          description: ''
          content:
            text/plain:
              schema:
                type: string
        '400':
          description: 'Invalid value for: query parameter size'
          content:
            text/plain:
              schema:
                type: string
        default:
          description: ''
          content:
            text/plain:
              schema:
                type: string
```

</details>

### Modifying the generated file

There are available some methods that can be overriden to alter the generated file:

- `tapirGoldenOpenAPIInfo`: The info that will be used when generating the OpenAPI file.
- `tapirGoldenOpenAPIOptions`: The options that will be used when generating the OpenAPI file.
- `tapirGoldenOpenAPIHeader`: The value of this header will be prepended to the generated YAML file.
- `tapirGoldenOpenAPIFileName`: Name of the generated file with the OpenAPI documentation.
- `tapirGoldenOpenAPIPath`: Folder where the OpenAPI file will be generated. Defaults to the `resource` folder.

```scala
import java.nio.file.Path

import sttp.tapir._
import sttp.tapir.docs.openapi.OpenAPIDocsOptions
import sttp.tapir.openapi.Info

class AllModificationsSuite extends munit.TapirGoldenOpenAPISuite {

  override def tapirGoldenOpenAPIPath: Path = super.tapirGoldenOpenAPIPath.resolve("yaml")

  override def tapirGoldenOpenAPIOptions: OpenAPIDocsOptions =
    OpenAPIDocsOptions.default.copy(defaultDecodeFailureOutput = _ => None)

  override def tapirGoldenOpenAPIFileName: String = "open-api.yaml"

  override def tapirGoldenOpenAPIInfo: Info = Info("title", "version", Some("description"))

  override def tapirGoldenOpenAPIHeader: String = "# This is a header"

  override val endpoints: List[AnyEndpoint] = List(myEndpoint)

}
```

This suite :point_up: will generate the file `open-api.yaml` under `resources/yaml`.

<details><summary>See the generated OpenAPI file</summary>

```yaml
# This is a header

openapi: 3.0.3
info:
  title: title
  version: version
  description: description
paths:
  /v1/users/{user_id}:
    get:
      operationId: getV1UsersUser_id
      parameters:
      - name: user_id
        in: path
        required: true
        schema:
          type: string
      - name: size
        in: query
        required: true
        schema:
          type: integer
      responses:
        '200':
          description: ''
          content:
            text/plain:
              schema:
                type: string
        default:
          description: ''
          content:
            text/plain:
              schema:
                type: string
```

</details>

### Validating the generated OpenAPI

If you also want to validate the generated OpenAPI you can do the following:

1. Add `tapir-golden-openapi-munit-validator` to your `build.sbt`:
```sbt
libraryDependencies += "com.alejandrohdezma" %% "tapir-golden-openapi-munit-validator" % "0.0.0" % Test)
```
2. Extend your suite with `TapirGoldenOpenAPIValidatorSuite`:
```scala
class ValidatedEndpointsSuite
  extends munit.TapirGoldenOpenAPISuite
    with munit.TapirGoldenOpenAPIValidatorSuite {

  override val endpoints: List[AnyEndpoint] = List(myEndpoint)

}
```

## Contributors for this project

| <a href="https://github.com/alejandrohdezma"><img alt="alejandrohdezma" src="https://avatars.githubusercontent.com/u/9027541?v=4&s=120" width="120px" /></a> | <a href="https://github.com/gutiory"><img alt="gutiory" src="https://avatars.githubusercontent.com/u/3316502?v=4&s=120" width="120px" /></a> |
| :--: | :--: |
| <a href="https://github.com/alejandrohdezma"><sub><b>alejandrohdezma</b></sub></a> | <a href="https://github.com/gutiory"><sub><b>gutiory</b></sub></a> |
