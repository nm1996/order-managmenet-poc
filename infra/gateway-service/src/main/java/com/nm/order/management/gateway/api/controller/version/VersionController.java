package com.nm.order.management.gateway.api.controller.version;

import com.nm.order.management.gateway.api.model.response.version.GetVersionResponse;
import com.nm.order.management.gateway.service.version.VersionService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RequestMapping("/api")
@RestController
public class VersionController {

    private final VersionService versionService;

    @GetMapping("/version-list")
    public ResponseEntity<GetVersionResponse> getVersions() {
        GetVersionResponse response = versionService.getVersions();
        return ResponseEntity.ok(response);
    }
}
