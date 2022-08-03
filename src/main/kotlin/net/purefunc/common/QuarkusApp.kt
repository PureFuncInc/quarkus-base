package net.purefunc.common

import org.eclipse.microprofile.openapi.annotations.OpenAPIDefinition
import org.eclipse.microprofile.openapi.annotations.info.Info
import org.eclipse.microprofile.openapi.annotations.servers.Server
import org.eclipse.microprofile.openapi.annotations.servers.ServerVariable
import javax.ws.rs.core.Application

@OpenAPIDefinition(
    info = Info(
        title = "Quarkus API",
        description =
        """
        Quarkus Base Demo
        🚀🚀🚀
        """,
        version = "1.0.0"
    ),
    servers = [
        Server(
            url = "{schema}://localhost:8080",
            variables = [
                ServerVariable(
                    name = "schema",
                    enumeration = ["http"],
                    defaultValue = "http",
                )
            ],
            description = "ide",
        ),
        Server(
            url = "{schema}://qat",
            variables = [
                ServerVariable(
                    name = "schema",
                    enumeration = ["https"],
                    defaultValue = "https",
                ),
            ],
            description = "qat",
        ),
    ],
)
//@SecurityScheme(
//    type = SecuritySchemeType.HTTP,
//    description = "Basic Authorization",
//    scheme = "Basic",
//)
class QuarkusApp : Application() {
}