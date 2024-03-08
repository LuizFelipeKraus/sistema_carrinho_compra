package com.store.order.controller;

import com.store.order.domain.Order;

import com.store.order.service.impl.OrderServiceImpl;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("api/order")
public class OrderController extends GenericController<Order>{
    private final OrderServiceImpl OrderService;

    public OrderController(OrderServiceImpl service){
        super(service);
        this.OrderService = service;
    }
    @PutMapping("/{id}/updateOrder")
    public ResponseEntity<Void> updateStatusOrder(@PathVariable Long id, @RequestParam int statusCode) {
        OrderService.updateStatus(id, statusCode);
        return ResponseEntity.ok().build();
    }

}
