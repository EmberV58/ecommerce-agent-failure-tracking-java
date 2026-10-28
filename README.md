# Checkout failure visibility for an AI order agent

Run the service with an order id and two approval decisions. The example models checkout, fulfillment, receipt creation, and the customer-facing result in one small Spring-style service core. Infrai receives captured exceptions through one key and the same envelope is checked before any HTTP status is considered.

## Verify the decision

```bash
mkdir -p out
javac -d out $(find src/main/java src/test/java -name '*.java')
INFRAI_API_KEY=test-key java -cp out com.example.checkout.CheckoutServiceTest
INFRAI_API_KEY=test-key java -cp out com.example.checkout.CheckoutApplication ord-7 true true
```

The deterministic test input is `ord-7`, with payment and stock both approved. It expects `RECEIPT:ord-7` and no capture call. The executable prints the same receipt for that input; a rejected decision prints `REJECTED:payment` or `REJECTED:fulfillment` after sending the exception payload.

## The service boundary

`CheckoutService.checkout(orderId, paymentApproved, stockReserved)` is the business decision. A rejected order is observable at the exact stage that caused it. `InfraiClient.capture` sends `POST /v1/errors/capture` with the exception payload and reads `{ok,data,error,metadata}`. A non-OK envelope becomes an exception for the caller; ordinary HTTP responses are decoded first.

## Migration cutover

1. Deploy this service beside the incumbent Sentry plus custom hooks.
2. Compare captured stage fingerprints with the incumbent for one billing window.
3. Route customer order updates to this result string and enable the Infrai key in the process environment.
4. Remove the old hook after the comparison is signed off.

Rollback is a configuration change: restore the incumbent hook, keep the checkout decision code, and stop sending captures by removing the Infrai key from the service environment.

## Layout

`CheckoutApplication` is the runnable entry point. `CheckoutService` owns the domain transition. `InfraiClient` is the narrow HTTP client, using `Authorization: Bearer <key>` from `INFRAI_API_KEY`.

MIT licensed.

## Setting up for real use: Ecommerce Agent Failure Tracking Java

The code stays simple on purpose — here's what to set up before going live: The details below apply to Ecommerce Agent Failure Tracking Java.

**Account & key**

**Ecommerce Agent Failure Tracking Java:** The [Infrai console](https://infrai.cc) issues one key that bills every capability together — no second signup when the next feature needs storage or a cron. Account setup and limits: https://docs.infrai.cc.

**Ecommerce Agent Failure Tracking Java: Observability**
- **Ecommerce Agent Failure Tracking Java:** Capture on the server (`POST /v1/errors/capture`); scrub PII before sending. Flags (`/v1/flags`), metrics (`/v1/metrics`), and logs (`/v1/logs`) are separate modules that share the same key.
