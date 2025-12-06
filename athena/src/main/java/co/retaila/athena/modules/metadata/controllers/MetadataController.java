package co.retaila.athena.modules.metadata.controllers;

import co.retaila.athena.modules.metadata.models.Metadata;
import co.retaila.athena.modules.metadata.services.MetadataService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class MetadataController {

    private final MetadataService metadataService;

    @RequestMapping(path = ".well-known/oauth-authorization-server", method = RequestMethod.GET)
    @ResponseStatus(HttpStatus.OK)
    public Metadata fetchMetadata() {
        return metadataService.fetchMetadata();
    }

}
