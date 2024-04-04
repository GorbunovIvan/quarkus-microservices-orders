package org.example.resource;

import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.example.model.OrderDTO;
import org.example.repository.OrderRepository;

@Path("/api/v1/orders")
public class OrderResource {

    @Inject
    OrderRepository orderRepository;

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response getAll() {
        var orders = orderRepository.listAll();
        return Response.ok(orders).build();
    }

    @GET
    @Path("/{id}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getById(long id) {
        var order = orderRepository.findById(id);
        if (order == null) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }
        return Response.ok(order).build();
    }

    @GET
    @Path("/address/{address}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getByAddress(String address) {
        var orders = orderRepository.list("address", address);
        return Response.ok(orders).build();
    }

    @POST
    @Produces(MediaType.APPLICATION_JSON)
    @Transactional
    public Response create(OrderDTO orderDTO) {
        var order = orderDTO.toOrder();
        orderRepository.persist(order);
        return Response.ok(order).build();
    }

    @DELETE
    @Path("/{id}")
    @Transactional
    public Response delete(long id) {
        var result = orderRepository.deleteById(id);
        if (!result) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }
        return Response.ok().build();
    }
}
