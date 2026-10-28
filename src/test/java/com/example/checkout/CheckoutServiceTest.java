package com.example.checkout;

public final class CheckoutServiceTest {
    private static final class Spy extends InfraiClient {
        boolean called;
        Spy() { super(); }
        @Override public String capture(String exception, String orderId, String stage) { called = true; return "{\"ok\":true}"; }
    }
    public static void main(String[] args) throws Exception {
        Spy spy = new Spy();
        String result = new CheckoutService(spy).checkout("ord-7", true, true);
        if (!"RECEIPT:ord-7".equals(result) || spy.called) throw new AssertionError("approved order must produce a receipt without capture");
        System.out.println("checkout decision test passed");
    }
}
