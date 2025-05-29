package com.mateusz113.order_service_adapters.client;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.tomakehurst.wiremock.WireMockServer;
import com.mateusz113.order_service_adapters.mapper.InvoiceDataMapper;
import com.mateusz113.order_service_model.exception.ServiceCommunicationErrorException;
import com.mateusz113.order_service_model_public.dto.InvoiceDataDto;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.wiremock.spring.ConfigureWireMock;
import org.wiremock.spring.EnableWireMock;
import org.wiremock.spring.InjectWireMock;

import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.equalTo;
import static com.github.tomakehurst.wiremock.client.WireMock.post;
import static com.github.tomakehurst.wiremock.client.WireMock.serverError;
import static com.github.tomakehurst.wiremock.client.WireMock.urlPathEqualTo;
import static com.mateusz113.order_service_adapters.util.OrderServiceAdaptersUtil.getInvoiceData;
import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
@EnableWireMock(@ConfigureWireMock(name = "invoice-generator-client", port = 8085))
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class InvoiceGeneratorClientTest {
    @InjectWireMock("invoice-generator-client")
    private WireMockServer mockServer;
    @Autowired
    private InvoiceGeneratorClient client;
    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private InvoiceDataMapper invoiceDataMapper;

    @Test
    void generateInvoice_InvoiceGeneratingServiceThrowsUncaughtException_ThrowsServiceCommunicationErrorException() {
        mockServer.stubFor(post(urlPathEqualTo(""))
                .willReturn(serverError()));

        ServiceCommunicationErrorException exception = assertThrows(ServiceCommunicationErrorException.class, () -> client.generateInvoice(null));

        assertEquals("Could not communicate with invoice generator.", exception.getMessage());
        assertEquals(500, exception.getStatusCode());
    }

    @Test
    void generateInvoice_DataIsCorrect_ReturnsAnInvoiceData() throws JsonProcessingException {
        InvoiceDataDto invoiceDataDto = invoiceDataMapper.modelToDto(getInvoiceData());

        byte[] invoice = new byte[2];
        mockServer.stubFor(post(urlPathEqualTo("/"))
                .withRequestBody(equalTo(objectMapper.writeValueAsString(invoiceDataDto)))
                .willReturn(aResponse()
                        .withHeader("Content-Type", "application/pdf")
                        .withBody(invoice)));

        byte[] result = client.generateInvoice(invoiceDataDto);

        assertArrayEquals(invoice, result);
    }

    @AfterAll
    void cleanUp() {
        mockServer.stop();
    }
}
