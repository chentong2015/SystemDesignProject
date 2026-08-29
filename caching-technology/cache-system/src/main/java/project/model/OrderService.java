package project.model;

import java.util.ArrayList;
import java.util.List;

public class OrderService {

    public static Order selectOrderById(int id) {
        if (id > 10) {
            return null;
        }
        return new Order("apple");
    }

    public static List<Order> selectAllOrders() {
        List<Order> orders = new ArrayList<>();
        orders.add(new Order("item01"));
        orders.add(new Order("item02"));
        orders.add(new Order("item03"));
        return orders;
    }
}
