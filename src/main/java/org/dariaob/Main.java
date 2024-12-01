package org.dariaob;

public class Main {
    public static void main(String[] args) {
        Product product = new Product(1, "Творог", 139.00, 2);
        Product product1 = new Product(2, "Молоко", 70.50, 5);
        System.out.println(product);
        System.out.println(product1);
    }
}