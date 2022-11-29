package com.function;

import com.microsoft.azure.functions.*;
import com.microsoft.azure.functions.annotation.AuthorizationLevel;
import com.microsoft.azure.functions.annotation.FunctionName;
import com.microsoft.azure.functions.annotation.HttpTrigger;

import java.util.Map;
import java.util.Optional;

/**
 * Azure Functions with HTTP Trigger.
 */
public class Function {
    /**
     * This function listens at endpoint "/api/HttpExample". Two ways to invoke it using "curl" command in bash:
     * 1. curl -d "HTTP Body" {your host}/api/HttpExample
     * 2. curl "{your host}/api/HttpExample?name=HTTP%20Query"
     */
    @FunctionName("HttpExample")
    public HttpResponseMessage run(
            @HttpTrigger(
                    name = "req",
                    methods = {HttpMethod.GET, HttpMethod.POST},
                    authLevel = AuthorizationLevel.ANONYMOUS)
            HttpRequestMessage<Optional<String>> request,
            final ExecutionContext context) {
        context.getLogger().info("Java HTTP trigger processed a request.");

        // Parse query parameter
        final String query = request.getQueryParameters().get("name");
        final String name = request.getBody().orElse(query);

        if (name == null) {
            return request.createResponseBuilder(HttpStatus.BAD_REQUEST).body("Please pass a name on the query string or in the request body").build();
        } else {
            return request.createResponseBuilder(HttpStatus.OK).body("Hello, " + name).build();
        }
    }


    @FunctionName("webhook")
    public HttpResponseMessage webhookVerify(
            @HttpTrigger(
                    name = "req",
                    methods = {HttpMethod.GET, HttpMethod.POST},
                    authLevel = AuthorizationLevel.ANONYMOUS)
            HttpRequestMessage<Optional<String>> request,
            final ExecutionContext context) {

        context.getLogger().info("**** request: " + request);

        final String body = String.valueOf(request.getBody());

        if (request.getHttpMethod() == HttpMethod.GET) {
            //Webhook configuration

            context.getLogger().info("** Java HTTP trigger processed a GET WEBHOOK request **");

            // Parse query parameter
            final String mode = request.getQueryParameters().get("hub.mode");
            final String verifyToken = request.getQueryParameters().get("hub.verify_token");
            final String challenge = request.getQueryParameters().get("hub.challenge");

            context.getLogger().info("*** body: " + body);

            if (mode != null && challenge == null) {
                context.getLogger().info("*** response: BAD_REQUEST Invalid challenge *** ");
                return request.createResponseBuilder(HttpStatus.BAD_REQUEST).body("Please pass a name on the query string or in the request body").build();
            } else {
                context.getLogger().info("*** response: SUCCESSFUL VALIDATED *** ");
                return request.createResponseBuilder(HttpStatus.OK).body(challenge).build();
            }

        } else {
            //Post request (user interaction by whatsapp message)

            context.getLogger().info("** Java HTTP trigger processed a POST request **");

            // Parse query parameter
            final Map<String, String> mode = request.getQueryParameters();

            context.getLogger().info("*** body: " + body);

//            for (String name : mode.keySet())
//                context.getLogger().info("set: " + name);

            return request.createResponseBuilder(HttpStatus.OK).body("ok").build();
        }
    }

}
