package com.mateusz113.order_service_adapters.integration;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.tomakehurst.wiremock.WireMockServer;
import com.mateusz113.order_service_adapters.mapper.ClientMapper;
import com.mateusz113.order_service_adapters.mapper.InvoiceDataMapper;
import com.mateusz113.order_service_core.port.outgoing.CartServiceCommunicator;
import com.mateusz113.order_service_core.port.outgoing.EmailSender;
import com.mateusz113.order_service_core.port.outgoing.EventSender;
import com.mateusz113.order_service_core.port.outgoing.InvoiceGenerator;
import com.mateusz113.order_service_core.port.outgoing.OrderServiceDatabase;
import com.mateusz113.order_service_core.port.outgoing.ProductServiceCommunicator;
import com.mateusz113.order_service_model.client.Address;
import com.mateusz113.order_service_model.client.Client;
import com.mateusz113.order_service_model.email.EmailData;
import com.mateusz113.order_service_model.event.EventType;
import com.mateusz113.order_service_model.invoice.InvoiceData;
import com.mateusz113.order_service_model.invoice.InvoiceItem;
import com.mateusz113.order_service_model.order.Order;
import com.mateusz113.order_service_model.order.OrderProcessingData;
import com.mateusz113.order_service_model.order.OrderStatus;
import com.mateusz113.order_service_model.product.Customization;
import com.mateusz113.order_service_model.product.CustomizationOption;
import com.mateusz113.order_service_model.product.Product;
import com.mateusz113.order_service_model_public.command.ProcessOrderCommand;
import com.mateusz113.order_service_model_public.command.UpdateOrderStatusCommand;
import com.mateusz113.order_service_model_public.dto.InvoiceDataDto;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.mockito.ArgumentCaptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.kafka.config.KafkaListenerEndpointRegistry;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.listener.MessageListenerContainer;
import org.springframework.kafka.test.context.EmbeddedKafka;
import org.springframework.kafka.test.utils.ContainerTestUtils;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.context.bean.override.mockito.MockitoSpyBean;
import org.wiremock.spring.ConfigureWireMock;
import org.wiremock.spring.EnableWireMock;
import org.wiremock.spring.InjectWireMock;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Clock;
import java.time.Duration;
import java.time.Instant;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.delete;
import static com.github.tomakehurst.wiremock.client.WireMock.equalTo;
import static com.github.tomakehurst.wiremock.client.WireMock.noContent;
import static com.github.tomakehurst.wiremock.client.WireMock.patch;
import static com.github.tomakehurst.wiremock.client.WireMock.post;
import static com.github.tomakehurst.wiremock.client.WireMock.urlPathEqualTo;
import static com.github.tomakehurst.wiremock.client.WireMock.urlPathTemplate;
import static com.mateusz113.order_service_adapters.util.OrderServiceAdaptersUtil.getDefaultTimeString;
import static com.mateusz113.order_service_adapters.util.OrderServiceAdaptersUtil.getOrderWithoutIds;
import static com.mateusz113.order_service_adapters.util.OrderServiceAdaptersUtil.getProcessOrderCommand;
import static com.mateusz113.order_service_adapters.util.OrderServiceAdaptersUtil.getUpdateOrderStatusCommand;
import static java.util.Objects.nonNull;
import static org.awaitility.Awaitility.await;
import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@Slf4j
@SpringBootTest
@ActiveProfiles("kafka-enabled")
@EmbeddedKafka(partitions = 1, bootstrapServersProperty = "${spring.kafka.bootstrap-servers}", topics = {
        "${order.paid.topic}", "${order.paid.topic.dlt}", "${order.shipment.topic}",
        "${order.delivered.topic}", "${generate.invoice.topic}", "${generate.invoice.topic.dlt}",
        "${send.email.topic}", "${send.email.topic.dlt}", "${send.email.with.invoice.topic}", "${send.email.with.invoice.topic.dlt}"
})
@EnableWireMock(value = {
        @ConfigureWireMock(name = "product-service-client", port = 8080),
        @ConfigureWireMock(name = "cart-service-client", port = 8081),
        @ConfigureWireMock(name = "invoice-generator-client", port = 8085)
})
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class OrderServiceEventIntegrationTest {
    private final static Duration AWAIT_TIME = Duration.ofSeconds(10);
    @InjectWireMock("product-service-client")
    private WireMockServer productServer;
    @InjectWireMock("cart-service-client")
    private WireMockServer cartServer;
    @InjectWireMock("invoice-generator-client")
    private WireMockServer invoiceServer;
    @Autowired
    private KafkaListenerEndpointRegistry registry;
    @Autowired
    private KafkaTemplate<String, Object> kafkaTemplate;
    @Autowired
    private ClientMapper clientMapper;
    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private InvoiceDataMapper invoiceDataMapper;
    @MockitoSpyBean
    private OrderServiceDatabase database;
    @MockitoSpyBean
    private EventSender eventSender;
    @MockitoSpyBean
    private InvoiceGenerator invoiceGenerator;
    @MockitoSpyBean
    private ProductServiceCommunicator productServiceCommunicator;
    @MockitoSpyBean
    private CartServiceCommunicator cartServiceCommunicator;
    @MockitoBean
    private EmailSender emailSender;
    @MockitoBean
    private Clock clock;
    @Value("${order.paid.topic}")
    private String orderPaidTopic;
    @Value("${order.paid.topic.dlt}")
    private String orderPaidTopicDlt;
    @Value("${order.shipment.topic}")
    private String orderShipmentTopic;
    @Value("${order.delivered.topic}")
    private String orderDeliveredTopic;
    @Value("${generate.invoice.topic}")
    private String generateInvoiceTopic;
    @Value("${send.email.topic}")
    private String sendEmailTopic;
    @Value("${send.email.with.invoice.topic}")
    private String sendEmailWithInvoiceTopic;

    @BeforeEach
    void setUp() {
        when(clock.instant()).thenReturn(Instant.parse(getDefaultTimeString()));
        when(clock.getZone()).thenReturn(ZoneOffset.UTC);
        database.save(getOrderWithoutIds());
        // Wait for the listener containers to be assigned, before testing
        // Not doing it makes tests inconsistent
        for (MessageListenerContainer container : registry.getListenerContainers()) {
            ContainerTestUtils.waitForAssignment(container, 1);
        }
    }

    @Test
    void listenOrderPaidTopic_UpdatesOrderInfoAndSendsGenerateInvoiceEvent() throws JsonProcessingException {
        UpdateOrderStatusCommand command = getUpdateOrderStatusCommand();
        Client client = clientMapper.dtoToModel(command.clientDto());
        productServer.stubFor(patch(urlPathEqualTo("/products/available-amount"))
                .withRequestBody(equalTo(objectMapper.writeValueAsString(Map.of(1L, 20))))
                .willReturn(noContent()));
        cartServer.stubFor(delete(urlPathTemplate("/carts/{cartId}"))
                .withPathParam("cartId", equalTo(String.valueOf(1L)))
                .willReturn(noContent()));
        ArgumentCaptor<OrderProcessingData> dataCaptor = ArgumentCaptor.forClass(OrderProcessingData.class);

        kafkaTemplate.send(orderPaidTopic, command);

        await().atMost(AWAIT_TIME).untilAsserted(() -> {
            Order order = getOrderById(1L);
            assertEquals(OrderStatus.PAID, order.getOrderStatus());
            verify(productServiceCommunicator).updateSoldProductsStock(Map.of(1L, 20));
            verify(cartServiceCommunicator).deleteCart(1L);
            verify(eventSender).sendEvent(dataCaptor.capture());
            assertOrderProcessingData(dataCaptor.getValue(), order, EventType.GENERATE_INVOICE, client);
        });
    }

    @Test
    void listenOrderPaidTopicDlt_UpdatesOrderInfoAndSendsSendEmailEvent() {
        UpdateOrderStatusCommand command = getUpdateOrderStatusCommand();
        Client client = clientMapper.dtoToModel(command.clientDto());
        ArgumentCaptor<OrderProcessingData> dataCaptor = ArgumentCaptor.forClass(OrderProcessingData.class);

        kafkaTemplate.send(orderPaidTopicDlt, command);

        await().atMost(AWAIT_TIME).untilAsserted(() -> {
            Order order = getOrderById(command.orderId());
            assertEquals(OrderStatus.CANCELLED, order.getOrderStatus());
            verify(eventSender).sendEvent(dataCaptor.capture());
            assertOrderProcessingData(dataCaptor.getValue(), order, EventType.SEND_EMAIL, client);
        });
    }

    @Test
    void listenOrderShipmentTopic_UpdatesOrderInfoAndSendsSendEmailEvent() {
        updateOrderStatus(1L, OrderStatus.PAID);
        UpdateOrderStatusCommand command = getUpdateOrderStatusCommand();
        Client client = clientMapper.dtoToModel(command.clientDto());
        ArgumentCaptor<OrderProcessingData> dataCaptor = ArgumentCaptor.forClass(OrderProcessingData.class);

        kafkaTemplate.send(orderShipmentTopic, command);

        await().atMost(AWAIT_TIME).untilAsserted(() -> {
            Order order = getOrderById(command.orderId());
            assertEquals(OrderStatus.IN_SHIPPING, order.getOrderStatus());
            verify(eventSender).sendEvent(dataCaptor.capture());
            assertOrderProcessingData(dataCaptor.getValue(), order, EventType.SEND_EMAIL, client);
        });
    }

    @Test
    void listenOrderDeliveredTopic_UpdatesOrderInfoAndSendsSendEmailEvent() {
        updateOrderStatus(1L, OrderStatus.IN_SHIPPING);
        UpdateOrderStatusCommand command = getUpdateOrderStatusCommand();
        Client client = clientMapper.dtoToModel(command.clientDto());
        ArgumentCaptor<OrderProcessingData> dataCaptor = ArgumentCaptor.forClass(OrderProcessingData.class);

        kafkaTemplate.send(orderDeliveredTopic, command);

        await().atMost(AWAIT_TIME).untilAsserted(() -> {
            Order order = getOrderById(command.orderId());
            assertEquals(OrderStatus.DELIVERED, order.getOrderStatus());
            verify(eventSender).sendEvent(dataCaptor.capture());
            assertOrderProcessingData(dataCaptor.getValue(), order, EventType.SEND_EMAIL, client);
        });
    }

    @Test
    void listenGenerateInvoiceTopic_GeneratesInvoiceAndSendsSendEmailWithInvoiceEvent() throws JsonProcessingException {
        updateOrderStatus(1L, OrderStatus.PAID);
        ProcessOrderCommand command = getProcessOrderCommand();
        Client client = clientMapper.dtoToModel(command.clientDto());
        byte[] invoice = new byte[2];
        InvoiceData invoiceData = getInvoiceData(client, getOrderById(command.orderId()), clock);
        InvoiceDataDto invoiceDataDto = invoiceDataMapper.modelToDto(invoiceData);
        invoiceServer.stubFor(post(urlPathEqualTo("/"))
                .withRequestBody(equalTo(objectMapper.writeValueAsString(invoiceDataDto)))
                .willReturn(aResponse()
                        .withHeader("Content-Type", "application/pdf")
                        .withBody(invoice)));
        ArgumentCaptor<OrderProcessingData> processingDataArgumentCaptor = ArgumentCaptor.forClass(OrderProcessingData.class);
        ArgumentCaptor<InvoiceData> invoiceDataArgumentCaptor = ArgumentCaptor.forClass(InvoiceData.class);

        kafkaTemplate.send(generateInvoiceTopic, command);

        await().atMost(AWAIT_TIME).untilAsserted(() -> {
            Order order = getOrderById(command.orderId());
            assertArrayEquals(invoice, order.getInvoice());
            verify(invoiceGenerator).generateInvoice(invoiceDataArgumentCaptor.capture());
            assertInvoiceData(invoiceDataArgumentCaptor.getValue(), client, order);
            verify(eventSender).sendEvent(processingDataArgumentCaptor.capture());
            assertOrderProcessingData(processingDataArgumentCaptor.getValue(), order, EventType.SEND_EMAIL_WITH_INVOICE, client);
        });
    }

    @Test
    void listenSendEmailTopic_SendsEmail() {
        updateOrderStatus(1L, OrderStatus.PAID);
        ProcessOrderCommand command = getProcessOrderCommand();
        Client client = clientMapper.dtoToModel(command.clientDto());
        ArgumentCaptor<EmailData> emailDataArgumentCaptor = ArgumentCaptor.forClass(EmailData.class);

        kafkaTemplate.send(sendEmailTopic, command);

        await().atMost(AWAIT_TIME).untilAsserted(() -> {
            verify(emailSender).sendEmail(emailDataArgumentCaptor.capture());
            assertEmailData(emailDataArgumentCaptor.getValue(), client);
        });
    }

    @Test
    void listenSendEmailWithInvoiceTopic_SendsEmailWithInvoice() {
        updateOrderStatus(1L, OrderStatus.PAID);
        ProcessOrderCommand command = getProcessOrderCommand();
        Client client = clientMapper.dtoToModel(command.clientDto());
        ArgumentCaptor<EmailData> emailDataArgumentCaptor = ArgumentCaptor.forClass(EmailData.class);
        String attachmentName = "%s.pdf".formatted(getInvoiceNumber(1L, clock));

        kafkaTemplate.send(sendEmailWithInvoiceTopic, command);

        await().atMost(AWAIT_TIME).untilAsserted(() -> {
            verify(emailSender).sendEmailWithInvoice(emailDataArgumentCaptor.capture(), eq(null), eq(attachmentName));
            assertEmailData(emailDataArgumentCaptor.getValue(), client);
        });
    }

    private Order getOrderById(Long orderId) {
        return database.findById(orderId)
                .orElseThrow(() -> new IllegalStateException("No order with id: %d".formatted(orderId)));
    }

    private void updateOrderStatus(Long orderId, OrderStatus status) {
        Order order = getOrderById(orderId);
        order.setOrderStatus(status);
        database.save(order);
    }

    private void assertOrderProcessingData(OrderProcessingData data, Order expectedOrder, EventType expectedEventType, Client expectedClient) {
        assertEquals(expectedOrder.getId(), data.orderId());
        assertEquals(expectedEventType, data.eventType());
        assertEquals(expectedClient, data.client());
    }

    private void assertInvoiceData(InvoiceData invoiceData, Client expectedClient, Order expectedOrder) {
        assertEquals("Online Shop", invoiceData.getFrom());
        assertEquals("%s %s".formatted(expectedClient.firstName(), expectedClient.lastName()), invoiceData.getTo());
        assertEquals(getShipmentDataString(expectedClient.address()), invoiceData.getShipTo());
        assertEquals(getInvoiceNumber(expectedOrder.getId(), clock), invoiceData.getNumber());
        assertEquals(getInvoiceItemsFromProducts(expectedOrder.getProducts()), invoiceData.getItems());
    }

    private InvoiceData getInvoiceData(
            Client client,
            Order order,
            Clock clock
    ) {
        List<InvoiceItem> invoiceItems = getInvoiceItemsFromProducts(order.getProducts());
        return InvoiceData.builder()
                .from("Online Shop")
                .to("%s %s".formatted(client.firstName(), client.lastName()))
                .shipTo(getShipmentDataString(client.address()))
                .number(getInvoiceNumber(order.getId(), clock))
                .items(invoiceItems)
                .amountPaid(calculateTotalPaidAmount(invoiceItems))
                .build();
    }

    private String getInvoiceNumber(Long orderId, Clock clock) {
        OffsetDateTime currentTime = OffsetDateTime.now(clock);
        return "PL/%d/%d/%d/%d".formatted(currentTime.getDayOfMonth(), currentTime.getMonth().getValue(), currentTime.getYear(), orderId);
    }

    private String getShipmentDataString(Address address) {
        List<String> addressElements = new ArrayList<>();
        addressElements.add("Country: %s\n".formatted(address.country()));
        addressElements.add("City: %s\n".formatted(address.city()));
        addressElements.add("Zip code: %s\n".formatted(address.zipCode()));
        addressElements.add("Street: %s\n".formatted(address.street()));
        addressElements.add("Building number: %s".formatted(address.buildingNumber()));
        if (nonNull(address.apartmentNumber())) {
            addressElements.add("/%s".formatted(address.apartmentNumber()));
        }
        return addressElements.stream()
                .reduce(String::concat)
                .toString();
    }

    private List<InvoiceItem> getInvoiceItemsFromProducts(List<Product> products) {
        return products.stream()
                .map(this::getInvoiceItemFromProduct)
                .toList();
    }

    private InvoiceItem getInvoiceItemFromProduct(Product product) {
        return InvoiceItem.builder()
                .name("%s %s".formatted(product.getBrand(), product.getName()))
                .quantity(product.getQuantity())
                .price(getProductFinalPrice(product))
                .description(getProductDescription(product))
                .build();
    }

    private BigDecimal getProductFinalPrice(Product product) {
        BigDecimal finalPrice = product.getPrice();
        for (Customization customization : product.getCustomizations()) {
            for (CustomizationOption option : customization.getOptions()) {
                finalPrice = finalPrice.add(option.getPriceDifference());
            }
        }
        return finalPrice.setScale(2, RoundingMode.HALF_UP);
    }

    private String getProductDescription(Product product) {
        List<String> descriptionPoints = new ArrayList<>();
        if (!product.getCustomizations().isEmpty()) {
            descriptionPoints.add("%s %s with:\n".formatted(product.getBrand(), product.getName()));
            product.getCustomizations().forEach(customization -> {
                customization.getOptions().forEach(option -> {
                    String point = "- %s: %s".formatted(customization.getName(), option.getName());
                    if (option.getPriceDifference().compareTo(BigDecimal.ZERO) > 0) {
                        point += " (+%.2f)\n".formatted(option.getPriceDifference());
                    }
                    descriptionPoints.add(point);
                });
            });
        }
        return descriptionPoints.stream()
                .reduce(String::concat)
                .toString();
    }

    private BigDecimal calculateTotalPaidAmount(List<InvoiceItem> items) {
        BigDecimal total = BigDecimal.ZERO;
        for (InvoiceItem item : items) {
            BigDecimal itemTotalPrice = item.price().multiply(BigDecimal.valueOf(item.quantity()));
            total = total.add(itemTotalPrice);
        }
        return total;
    }


    private void assertEmailData(EmailData emailData, Client expectedClient) {
        assertEquals(expectedClient.email(), emailData.recipientEmail());
        assertEquals("Order confirmation", emailData.subject());
        assertEquals(getEmailMessageForPaidStatus(), emailData.message());
    }

    private String getEmailMessageForPaidStatus() {
        return """
                Dear %s,
                
                We’re pleased to confirm that your order has been placed successfully.
                
                Please find the invoice for your order attached to this message.
                If you have any questions or need further assistance, feel free to reach out to us.
                
                Best regards,
                Online Shop Team
                """.formatted("firstName");
    }

    @AfterAll
    void cleanUp() {
        productServer.stop();
        cartServer.stop();
        invoiceServer.stop();
    }
}
