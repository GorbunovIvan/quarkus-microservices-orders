package org.example.resource;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.quarkus.test.junit.QuarkusTest;
import io.quarkus.test.junit.mockito.InjectSpy;
import jakarta.annotation.PostConstruct;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import org.example.model.Order;
import org.example.model.OrderDTO;
import org.example.repository.OrderRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Optional;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@QuarkusTest
class OrderResourceTest {

    @InjectSpy
    OrderRepository orderRepository;

    @Inject
    ObjectMapper objectMapper;

    private List<Order> orders;

    private static final String BASE_PATH = "/api/v1/orders";

    @PostConstruct
    private void init() {
        objectMapper.findAndRegisterModules();
    }

    @BeforeEach
    @Transactional
    void setUp() {
        orders = List.of(
                new Order(null, "test address 1", LocalDateTime.now().minusMonths(1), LocalDateTime.now().plusMonths(1)),
                new Order(null, "test address 2", LocalDateTime.now().minusWeeks(1), LocalDateTime.now().plusWeeks(1)),
                new Order(null, "test address 3", LocalDateTime.now().minusDays(1), LocalDateTime.now().plusDays(1)),
                new Order(null, "test address 4", LocalDateTime.now().minusHours(1), LocalDateTime.now().plusHours(1))
        );
        orders.forEach(orderRepository::persist);
        Mockito.clearInvocations(orderRepository);
    }

    @AfterEach
    @Transactional
    void tearDown() {
        orders.forEach(book -> orderRepository.deleteById(book.getId()));
        for (var order : orderRepository.listAll()) {
            orderRepository.deleteById(order.getId());
        }
    }

    @Test
    void getAll() throws JsonProcessingException {

        var jsonExpected = objectMapper.writeValueAsString(orders);

        given()
                .basePath(BASE_PATH)
                .when()
                .get()
                .then()
                .statusCode(200)
                .body(equalTo(jsonExpected));

        verify(orderRepository, times(1)).listAll();
    }

    @Test
    void getById() throws JsonProcessingException {

        for (var order : orders) {

            var jsonExpected = objectMapper.writeValueAsString(order);

            var id = order.getId();

            given()
                    .basePath(BASE_PATH)
                    .when()
                    .get("/" + id)
                    .then()
                    .statusCode(200)
                    .body(equalTo(jsonExpected));

            verify(orderRepository, times(1)).findById(id);
        }

        verify(orderRepository, times(orders.size())).findById(anyLong());
    }

    @Test
    void getByIdNotFound() {

        var id = -1L;

        given()
                .basePath(BASE_PATH)
                .when()
                .get("/" + id)
                .then()
                .statusCode(404);

        verify(orderRepository, times(1)).findById(id);
    }

    @Test
    void getByAddress() throws JsonProcessingException {

        for (var order : orders) {

            var jsonExpected = objectMapper.writeValueAsString(List.of(order));

            var address = order.getAddress();

            given()
                    .basePath(BASE_PATH)
                    .when()
                    .get("/address/" + address)
                    .then()
                    .statusCode(200)
                    .body(equalTo(jsonExpected));

            verify(orderRepository, times(1)).list("address", address);
        }

        verify(orderRepository, times(orders.size())).list(anyString(), Optional.ofNullable(any()));
    }

    @Test
    void getByAddressNotFound() {

        var address = "-";

        given()
                .basePath(BASE_PATH)
                .when()
                .get("/address/" + address)
                .then()
                .statusCode(200)
                .body(equalTo("[]"));

        verify(orderRepository, times(1)).list("address", address);
    }

    @Test
    void create() throws JsonProcessingException {

        var orderDTO = new OrderDTO(
                "new address",
                LocalDateTime.now().plusMinutes(14L).truncatedTo(ChronoUnit.MINUTES)
        );

        var jsonBodyRequest = objectMapper.writeValueAsString(orderDTO);

        var jsonResponse =
                given()
                        .basePath(BASE_PATH)
                        .contentType("application/json")
                        .body(jsonBodyRequest)
                        .when()
                        .post()
                        .then()
                        .statusCode(200)
                        .extract()
                        .asPrettyString();

        var orderCreated = objectMapper.readValue(jsonResponse, Order.class);
        assertNotNull(orderCreated);
        assertNotNull(orderCreated.getId());
        assertEquals(orderDTO.toOrder(), orderCreated);

        verify(orderRepository, times(1)).persist(any(Order.class));
    }

    @Test
    void delete() {

        for (var order : orders) {

            var id = order.getId();

            given()
                    .basePath(BASE_PATH)
                    .when()
                    .delete("/" + id)
                    .then()
                    .statusCode(200);

            verify(orderRepository, times(1)).deleteById(id);
        }

        verify(orderRepository, times(orders.size())).deleteById(anyLong());
    }

    @Test
    void deleteNotFound() {

        var id = -1L;

        given()
                .basePath(BASE_PATH)
                .when()
                .delete("/" + id)
                .then()
                .statusCode(404);

        verify(orderRepository, times(1)).deleteById(id);
    }
}