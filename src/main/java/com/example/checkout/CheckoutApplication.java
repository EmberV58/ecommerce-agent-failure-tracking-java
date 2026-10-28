package com.example.checkout;

public final class CheckoutApplication {
    public static void main(String[] args) throws Exception {
        if (args.length != 3) { System.out.println("usage: order-id payment-approved stock-reserved"); return; }
        boolean payment = Boolean.parseBoolean(args[1]);
        boolean stock = Boolean.parseBoolean(args[2]);
        String result = new CheckoutService(new InfraiClient()).checkout(args[0], payment, stock);
        System.out.println(result);
    }
}
