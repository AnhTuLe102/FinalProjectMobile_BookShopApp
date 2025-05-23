package com.bookstore.api.service;

import com.bookstore.api.dto.OrderDTO;
import com.bookstore.api.dto.OrderItemDTO;
import com.bookstore.api.entity.order.Order;
import com.bookstore.api.entity.order.OrderItem;
import com.bookstore.api.entity.order.OrderTrack;
import com.bookstore.api.entity.user.User;
import com.bookstore.api.repository.OrderRepository;
import com.bookstore.api.repository.OrderTrackRepository;
import com.bookstore.api.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

@Service
public class OrderServiceImpl implements OrderService {

    private final OrderRepository orderRepository;
    private final OrderTrackRepository orderTrackRepository;
    private final UserRepository userRepository;

    @Autowired
    public OrderServiceImpl(OrderRepository orderRepository, OrderTrackRepository orderTrackRepository,
            UserRepository userRepository) {
        this.orderRepository = orderRepository;
        this.orderTrackRepository = orderTrackRepository;
        this.userRepository = userRepository;
    }

    @Override
    public List<Order> findAll() {
        return orderRepository.findAll();
    }

    @Override
    public Order createOrder(OrderDTO orderDTO) {
        Order order = new Order();

        // Set OrderTrack
        OrderTrack orderTrack = orderTrackRepository.findById(orderDTO.getOrderTrackId())
                .orElseGet(() -> {
                    OrderTrack newOrderTrack = new OrderTrack();
                    return orderTrackRepository.save(newOrderTrack);
                });
        order.setOrderTrack(orderTrack);

        // Set User
        User user = userRepository.findById(orderDTO.getUserId())
                .orElseThrow(() -> new RuntimeException("User not found"));
        order.setUser(user);

        // Set OrderItems
        List<OrderItem> orderItems = new ArrayList<>();
        for (OrderItemDTO itemDTO : orderDTO.getOrderItems()) {
            OrderItem orderItem = new OrderItem();
            orderItem.setNameProduct(itemDTO.getNameProduct());
            orderItem.setQuantity(itemDTO.getQuantity());
            orderItem.setPrice(itemDTO.getPrice());
            orderItem.setOrder(order); // Set reference to the Order
            orderItems.add(orderItem);
        }
        order.setOrderItems(orderItems);

        // Set Date
        Date date = new Date();
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy",
                new Locale.Builder().setLanguage("vi").setRegion("VN").build());
        order.setDate(dateFormat.format(date));

        // Save Order and OrderItems (CascadeType.ALL will save orderItems
        // automatically)
        return orderRepository.save(order);
    }

    @Override
    public List<Order> getOrdersByUserIdAndOrderTrackId(int userId, int orderTrackId) {
        return orderRepository.findByUserIdAndOrderTrackId(userId, orderTrackId);
    }

    @Override
    public List<Order> getOrdersByOrderTrackId(int orderTrackId) {
        return orderRepository.findByOrderTrackId(orderTrackId);
    }

    @Override
    public Order updateStatus(int orderId, int orderTrackId) {
        Order order = orderRepository.findById(orderId).orElseThrow(() -> new RuntimeException("Order not found"));

        // Set OrderTrack
        OrderTrack orderTrack = orderTrackRepository.findById(orderTrackId)
                .orElseThrow(() -> new RuntimeException("OrderTrack not found"));
        order.setOrderTrack(orderTrack);

        return orderRepository.save(order);
    }
}
