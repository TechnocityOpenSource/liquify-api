package liquify.api.rest;

import io.micronaut.http.HttpResponse;
import io.micronaut.http.MediaType;
import io.micronaut.http.annotation.Body;
import io.micronaut.http.annotation.Controller;
import io.micronaut.http.annotation.Post;
import liquibase.exception.LiquibaseException;
import liquify.api.service.LiquifyService;
import liquify.api.utils.dto.ConversionArguments;

import java.io.IOException;

@Controller("/api/v1/liquify")
public class LiquifyEndpoint {

    private final LiquifyService liquifyService;

    public LiquifyEndpoint(LiquifyService liquifyService) {
        this.liquifyService = liquifyService;
    }

    @Post(value = "/", produces = MediaType.TEXT_PLAIN, consumes = MediaType.APPLICATION_JSON)
    public HttpResponse<?> doLiquify(@Body ConversionArguments conversionArguments) throws IOException, LiquibaseException {
        return HttpResponse.ok(liquifyService.liquifyDatabaseChangeLog(conversionArguments));
    }
}
