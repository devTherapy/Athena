package co.retaila.athena.common.services.impl;

import co.retaila.athena.common.services.CurrentRequestService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import javax.servlet.http.HttpServletRequest;

@Service
@RequiredArgsConstructor
public class CurrentRequestServiceImpl implements CurrentRequestService {

    private final HttpServletRequest request;

    @Override
    public String getBaseUrl() {
        return ServletUriComponentsBuilder.fromRequestUri(request)
                .replacePath(null)
                .build()
                .toUriString();
    }

}
