package com.function;

import com.microsoft.azure.functions.*;
import com.microsoft.azure.functions.annotation.AuthorizationLevel;
import com.microsoft.azure.functions.annotation.FunctionName;
import com.microsoft.azure.functions.annotation.HttpTrigger;

import java.util.Optional;

/**
 * Azure Functions with HTTP Trigger.
 */
public class WebhookFunction {

    private static final String VERIFY_TOKEN = "1234";


    /**
     * This function listens at endpoint "/api/webhook".
     */
    @FunctionName("webhook")
    public HttpResponseMessage run(
            @HttpTrigger(
                    name = "req",
                    methods = {HttpMethod.GET, HttpMethod.POST},
                    authLevel = AuthorizationLevel.ANONYMOUS)
            HttpRequestMessage<Optional<String>> request,
            final ExecutionContext context) {

        context.getLogger().info("**** BIA **** " +     request.getHttpMethod() +
                " request: " + request.getQueryParameters());

        context.getLogger().info("*** BIA *** body: " + request.getBody());

        if (request.getHttpMethod().equals(HttpMethod.GET))
            return getHttpResponse(request, context);
        else
            return request.createResponseBuilder(HttpStatus.OK).body("*** BIA ***").build();

    }

    private static HttpResponseMessage getHttpResponse(HttpRequestMessage<Optional<String>> request,
                                                       ExecutionContext context) {
        context.getLogger().info("*** BIA *** Java HTTP trigger processed a GET WEBHOOK request **");

        // Parse query parameter
        final String mode = request.getQueryParameters().get("hub.mode");
        final String verifyToken = request.getQueryParameters().get("hub.verify_token");
        final String challenge = request.getQueryParameters().get("hub.challenge");

        if (mode == null || challenge == null || !verifyToken.equals(VERIFY_TOKEN)) {

            context.getLogger().info("*** BIA *** response: BAD_REQUEST Invalid challenge *** ");
            return request.createResponseBuilder(HttpStatus.BAD_REQUEST).
                    body("Please pass a name on the query string or in the request body").build();
        } else {

            context.getLogger().info("*** BIA *** response: SUCCESSFUL VALIDATED *** ");
            return request.createResponseBuilder(HttpStatus.OK).body(challenge).build();
        }
    }

}
