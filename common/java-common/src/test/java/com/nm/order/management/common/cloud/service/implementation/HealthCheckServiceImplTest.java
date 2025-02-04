package com.nm.order.management.common.cloud.service.implementation;

import com.nm.order.management.common.ServiceInfoUtil;
import com.nm.order.management.common.cloud.config.ServiceCloudConfig;
import com.nm.order.management.common.cloud.service.HealthCheckService;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import org.junit.jupiter.api.*;
import org.mockito.MockedStatic;
import org.springframework.cloud.client.ServiceInstance;


import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class HealthCheckServiceImplTest {

    MockedStatic<ServiceInfoUtil> serviceInfoUtilMockedStatic;
    private MockWebServer mockWebServer;
    private HealthCheckService healthCheckService;

    @BeforeEach
    void setUp() throws IOException {
        mockWebServer = new MockWebServer();
        mockWebServer.start(8000);
        ServiceCloudConfig serviceCloudConfig = new ServiceCloudConfig();
        healthCheckService = new HealthCheckServiceImpl(serviceCloudConfig.getWebClient());
        serviceInfoUtilMockedStatic = mockStatic(ServiceInfoUtil.class);
    }

    @AfterEach
    void tearDown() throws IOException {
        mockWebServer.shutdown();
        serviceInfoUtilMockedStatic.close();
    }

    @Test
    void shouldReturnTrueWhenServiceIsUp() throws Exception {
        mockWebServer.enqueue(new MockResponse()
                .setResponseCode(200)
                .setBody("{\"status\":\"UP\"}"));

        boolean isServerUp = healthCheckService.isServiceUp("http://localhost:8000");
        assertTrue(isServerUp);
    }

    @Test
    void shouldReturnTrueWhenServiceIsUpInstance() throws Exception {
        mockWebServer.enqueue(new MockResponse()
                .setResponseCode(200)
                .setBody("{\"status\":\"UP\"}"));

        ServiceInstance serviceInstance = mock(ServiceInstance.class);
        serviceInfoUtilMockedStatic.when(() -> ServiceInfoUtil.getServiceHostPort(serviceInstance))
                .thenReturn("http://localhost:8000");

        boolean isServerUp = healthCheckService.isServiceUp(serviceInstance);
        assertTrue(isServerUp);
    }

    @Test
    void shouldReturnFalseWhenServiceIsDown() throws Exception {
        mockWebServer.enqueue(new MockResponse()
                .setBody("{\"status\":\"DOWN\"}")
                .setResponseCode(200));

        boolean isServerUp = healthCheckService.isServiceUp("http://localhost:8000");
        assertFalse(isServerUp);
    }

    @Test
    void shouldReturnFalseWhenServiceIsUnreachable() {
        try {
            mockWebServer.shutdown();
            assertFalse(healthCheckService.isServiceUp(mockWebServer.url("/actuator/health").toString()));
        } catch (IOException e) {
            fail("Unexpected exception: " + e.getMessage());
        }
    }
}
