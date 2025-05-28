package com.mateusz113.order_service_adapters.controller;

import com.mateusz113.order_service_adapters.mapper.OrderMapper;
import com.mateusz113.order_service_adapters.mapper.PageableContentMapper;
import com.mateusz113.order_service_core.port.incoming.OrderServiceActionPorts;
import com.mateusz113.order_service_model.content.PageableContent;
import com.mateusz113.order_service_model.order.Order;
import com.mateusz113.order_service_model_public.command.InsertOrderCommand;
import com.mateusz113.order_service_model_public.dto.OrderDto;
import com.mateusz113.order_service_model_public.dto.PageableContentDto;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/orders")
@RequiredArgsConstructor
public class OrderServiceController {
    private final OrderServiceActionPorts actionPorts;
    private final OrderMapper orderMapper;
    private final PageableContentMapper pageableContentMapper;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public OrderDto placeNewOrder(@RequestBody InsertOrderCommand orderCommand) {
        Order order = orderMapper.commandToModel(orderCommand);
        order = actionPorts.place(order);
        return orderMapper.modelToDto(order);
    }

    @GetMapping("/{orderId}")
    public OrderDto getOrderById(@PathVariable Long orderId) {
        Order order = actionPorts.getById(orderId);
        return orderMapper.modelToDto(order);
    }

    @GetMapping("/client-id/{clientId}")
    public PageableContentDto<OrderDto> getClientOrders(@PathVariable Long clientId, Pageable pageable) {
        PageableContent<Order> orderPageableContent = actionPorts.getClientOrders(clientId, pageable.getPageNumber(), pageable.getPageSize());
        return pageableContentMapper.modelToDto(orderPageableContent);
    }

    @GetMapping
    public PageableContentDto<OrderDto> getAllOrders(Pageable pageable) {
        PageableContent<Order> orderPageableContent = actionPorts.getAllOrders(pageable.getPageNumber(), pageable.getPageSize());
        return pageableContentMapper.modelToDto(orderPageableContent);
    }

    @GetMapping(path = "/{orderId}/invoice", produces = "application/pdf")
    public byte[] getOrderInvoice(@PathVariable Long orderId) {
        return actionPorts.getOrderInvoice(orderId);
    }

    @DeleteMapping("/{orderId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteOrder(@PathVariable Long orderId) {
        actionPorts.delete(orderId);
    }
}
