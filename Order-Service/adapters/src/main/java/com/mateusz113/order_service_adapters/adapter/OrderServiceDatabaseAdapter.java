package com.mateusz113.order_service_adapters.adapter;

import com.mateusz113.order_service_adapters.entity.OrderEntity;
import com.mateusz113.order_service_adapters.mapper.OrderMapper;
import com.mateusz113.order_service_adapters.repository.OrderEntityRepository;
import com.mateusz113.order_service_core.port.outgoing.OrderServiceDatabase;
import com.mateusz113.order_service_model.content.PageableContent;
import com.mateusz113.order_service_model.order.Order;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Component
@RequiredArgsConstructor
public class OrderServiceDatabaseAdapter implements OrderServiceDatabase {
    private final OrderEntityRepository repository;
    private final OrderMapper mapper;

    @Override
    @Transactional
    public Order save(Order order) {
        OrderEntity orderEntity = mapper.modelToEntity(order);
        orderEntity = repository.save(orderEntity);
        return mapper.entityToModel(orderEntity);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Order> findById(Long orderId) {
        Optional<OrderEntity> orderEntityOptional = repository.findById(orderId);
        return orderEntityOptional.map(mapper::entityToModel);
    }

    @Override
    @Transactional(readOnly = true)
    public PageableContent<Order> findAll(Integer pageNumber, Integer pageSize) {
        Pageable pageable = PageRequest.of(pageNumber, pageSize);
        Page<OrderEntity> orderEntityPage = repository.findAll(pageable);
        return getPageableContentFromPage(orderEntityPage, pageable);
    }

    @Override
    @Transactional(readOnly = true)
    public PageableContent<Order> findByClientId(Long clientId, Integer pageNumber, Integer pageSize) {
        Pageable pageable = PageRequest.of(pageNumber, pageSize);
        Page<OrderEntity> orderEntityPage = repository.findAllByClientId(clientId, pageable);
        return getPageableContentFromPage(orderEntityPage, pageable);
    }

    @Override
    @Transactional
    public void delete(Order order) {
        OrderEntity orderEntity = mapper.modelToEntity(order);
        repository.delete(orderEntity);
    }

    private PageableContent<Order> getPageableContentFromPage(Page<OrderEntity> page, Pageable pageable) {
        return PageableContent.<Order>builder()
                .totalPages(page.getTotalPages())
                .totalElements(page.getTotalElements())
                .pageNumber(pageable.getPageNumber())
                .pageSize(pageable.getPageSize())
                .elements(page.get().map(mapper::entityToModel).toList())
                .build();
    }
}
