package com.bookstore.app.dto;

import java.util.List;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class OrderDTO {

    private int orderTrackId;
    private int userId;
    private List<OrderItemDTO> orderItems;

    public int getOrderTrackId() {
        return orderTrackId;
    }

    public void setOrderTrackId(int orderTrackId) {
        this.orderTrackId = orderTrackId;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public List<OrderItemDTO> getOrderItems() {
        return orderItems;
    }

    public void setOrderItems(List<OrderItemDTO> orderItems) {
        this.orderItems = orderItems;
    }
}
