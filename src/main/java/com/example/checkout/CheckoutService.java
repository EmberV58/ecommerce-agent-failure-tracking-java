package com.example.checkout;

public final class CheckoutService {
    private final InfraiClient infrai;
    public CheckoutService(InfraiClient infrai) { this.infrai = infrai; }

    public String checkout(String orderId, boolean paymentApproved, boolean stockReserved) throws Exception {
        if (!paymentApproved || !stockReserved) {
            String stage = !paymentApproved ? "payment" : "fulfillment";
            infrai.capture("order " + orderId + " rejected at " + stage, orderId, stage);
            return "REJECTED:" + stage;
        }
        return "RECEIPT:" + orderId;
    }
}
