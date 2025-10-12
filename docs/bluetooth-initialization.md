# Tandem Pump Bluetooth Session Specification

This document defines the Bluetooth initialization sequence, authentication flows, and packet structure implemented by PumpX2 when communicating with Tandem insulin pumps. It is intended as a standalone specification that can be used to build an interoperable client.

## 1. Link-Layer Bootstrapping

### 1.1 Central creation and logging
PumpX2 instantiates a `BluetoothCentralManager` immediately during handler construction, optionally wiring Timber logging for diagnostics.【F:androidLib/src/main/java/com/jwoglom/pumpx2/pump/bluetooth/TandemBluetoothHandler.java†L81-L110】

### 1.2 Service discovery prerequisites
After connecting, the handler performs the following steps before application traffic is allowed to flow:

1. Request MTU 185 (minimum for long Tandem packets) and elevate the connection priority to `HIGH`. A failure indicates the pump is not ready (typically not in the pairing menu).【F:androidLib/src/main/java/com/jwoglom/pumpx2/pump/bluetooth/TandemBluetoothHandler.java†L148-L167】
2. Read manufacturer and model characteristics from the Device Information Service, then notify the frontend via `onPumpModel` once the model number is known.【F:androidLib/src/main/java/com/jwoglom/pumpx2/pump/bluetooth/TandemBluetoothHandler.java†L170-L189】【F:androidLib/src/main/java/com/jwoglom/pumpx2/pump/bluetooth/TandemBluetoothHandler.java†L329-L339】
3. Enable notifications on every Tandem characteristic (authorization, current status, history log, control, control stream, qualifying events, service-changed). PumpX2 tracks completion of this step via an internal set so the initialization gate opens only after all subscriptions succeed.【F:messages/src/main/java/com/jwoglom/pumpx2/pump/messages/bluetooth/CharacteristicUUID.java†L14-L59】【F:androidLib/src/main/java/com/jwoglom/pumpx2/pump/bluetooth/TandemBluetoothHandler.java†L179-L275】

Only when the MTU, connection parameters, and notifications are in place does `checkIfInitialPumpConnectionEstablished` fire, unblocking authentication or proceeding directly to `onPumpConnected` when connection sharing is detected.【F:androidLib/src/main/java/com/jwoglom/pumpx2/pump/bluetooth/TandemBluetoothHandler.java†L193-L240】

### 1.3 Connection sharing detection
If notifications arrive before PumpX2 has sent any requests, the handler marks the session as already authenticated by another actor (the Tandem t:connect app). It then delays the application-level `onPumpConnected` callback until transaction IDs can be synchronized with the shared connection.【F:androidLib/src/main/java/com/jwoglom/pumpx2/pump/bluetooth/TandemBluetoothHandler.java†L200-L240】【F:androidLib/src/main/java/com/jwoglom/pumpx2/pump/bluetooth/TandemBluetoothHandler.java†L358-L427】

## 2. Session State Providers

### 2.1 PumpState persistent cache
`PumpState` persists pairing metadata, API version, pump serial, and outstanding request metadata. It also seeds `PumpStateSupplier` delegates so that protocol code can obtain authentication material and gating flags without depending on Android APIs.【F:androidLib/src/main/java/com/jwoglom/pumpx2/pump/PumpState.java†L1-L187】【F:messages/src/main/java/com/jwoglom/pumpx2/pump/messages/bluetooth/PumpStateSupplier.java†L15-L64】

Key responsibilities:

- Persist pairing code (long-form legacy or short JPAKE), derived secret, server nonce, and time-since-reset value.【F:androidLib/src/main/java/com/jwoglom/pumpx2/pump/PumpState.java†L92-L155】
- Track the next transaction ID and outstanding request/response pairs to reconcile asynchronous notifications.【F:androidLib/src/main/java/com/jwoglom/pumpx2/pump/PumpState.java†L197-L255】
- Expose feature toggles for actions affecting insulin delivery and connection sharing heuristics.【F:androidLib/src/main/java/com/jwoglom/pumpx2/pump/PumpState.java†L256-L314】

### 2.2 Authentication key derivation
`PumpStateSupplier.determinePumpAuthKey` constructs the signing key by preference order:

1. If a JPAKE derived secret and server nonce exist, HKDF is applied to produce the session HMAC key.
2. Otherwise the stored 16-character pairing code is used as ASCII bytes.
If neither source is available, signing is not possible and the supplier throws.【F:messages/src/main/java/com/jwoglom/pumpx2/pump/messages/bluetooth/PumpStateSupplier.java†L18-L64】

## 3. Message Model and Transaction Management

### 3.1 Message metadata
Every request/response class extends `Message`, which reads annotation metadata (`@MessageProps`) to expose opcode, expected payload size, target characteristic, and whether the message must be signed. Request classes also embed their response counterpart for automatic routing.【F:messages/src/main/java/com/jwoglom/pumpx2/pump/messages/Message.java†L14-L138】

`Messages` maintains the opcode registry, mapping `(Characteristic, opcode)` pairs to concrete classes and providing the reflection-driven `parse` helper used during reception.【F:messages/src/main/java/com/jwoglom/pumpx2/pump/messages/Messages.java†L352-L392】

### 3.2 Transaction IDs
`TransactionId` stores the *next* byte value to send. After `sendCommand` consumes the current value, it increments in modulo-256 arithmetic. The global counter can also be reset or forcibly set when synchronizing with an existing connection.【F:messages/src/main/java/com/jwoglom/pumpx2/pump/messages/TransactionId.java†L6-L33】【F:androidLib/src/main/java/com/jwoglom/pumpx2/pump/bluetooth/TandemPump.java†L116-L123】

`PumpState.pushRequestMessage` records each outbound message alongside the transaction ID and characteristic so responses can be matched later, even across multi-packet transfers or connection sharing scenarios.【F:androidLib/src/main/java/com/jwoglom/pumpx2/pump/PumpState.java†L197-L233】

## 4. Outbound Command Encoding

### 4.1 High-level send pipeline
`TandemPump.sendCommand` is the single entry point for issuing requests. It aborts if the library is configured for passive snooping, otherwise it:

1. Reads and reserves the next transaction ID from `Packetize.txId`.
2. Pushes the request into `PumpState` for later correlation.
3. Wraps the message with `TronMessageWrapper`, which handles signing and packetization.
4. Writes each chunk to the BLE characteristic determined by `CharacteristicUUID.determine` using Write With Response semantics.【F:androidLib/src/main/java/com/jwoglom/pumpx2/pump/bluetooth/TandemPump.java†L110-L136】【F:messages/src/main/java/com/jwoglom/pumpx2/pump/messages/bluetooth/TronMessageWrapper.java†L12-L90】【F:messages/src/main/java/com/jwoglom/pumpx2/pump/messages/bluetooth/CharacteristicUUID.java†L61-L83】

### 4.2 Packet layout
`Packetize.packetize` constructs the raw byte stream for a single message prior to chunking. The payload structure is:

```
byte 0   : opcode (from MessageProps)
byte 1   : transaction ID
byte 2   : cargo length (excluding header)
bytes 3..(3+N-1) : cargo bytes
optional : 4-byte time-since-reset followed by 20-byte HMAC-SHA1 when message.signed() is true
trailer  : 2-byte CRC16 over all preceding bytes
```

The method enforces that insulin-affecting requests cannot be sent unless `actionsAffectingInsulinDeliveryEnabled` is true, injects the pump time-since-reset before computing HMAC-SHA1, appends CRC16, and finally partitions the result into 18- or 40-byte chunks depending on characteristic. Each chunk becomes a `Packet` whose first byte encodes “packets remaining” and whose second byte repeats the transaction ID.【F:messages/src/main/java/com/jwoglom/pumpx2/pump/messages/Packetize.java†L37-L127】【F:messages/src/main/java/com/jwoglom/pumpx2/pump/messages/bluetooth/models/Packet.java†L11-L40】

### 4.3 Signing key usage
For signed messages the wrapper pulls the authentication key via `PumpStateSupplier`. Missing keys raise an exception so application code must configure `PumpState` before attempting operations that require signatures.【F:messages/src/main/java/com/jwoglom/pumpx2/pump/messages/bluetooth/TronMessageWrapper.java†L17-L44】

## 5. Inbound Packet Reassembly and Validation

### 5.1 Packet assembly
`PacketArrayList` accumulates BLE notifications for a single `(characteristic, transaction ID)` pair. The parser tracks the “packets remaining” nibble, enforces the expected transaction ID and declared size, concatenates packet bodies, and stores the CRC trailer for later validation.【F:messages/src/main/java/com/jwoglom/pumpx2/pump/messages/PacketArrayList.java†L20-L229】

Multi-packet responses are cached in `PumpState` until complete so subsequent fragments reuse the same `PacketArrayList` instance.【F:androidLib/src/main/java/com/jwoglom/pumpx2/pump/bluetooth/TandemBluetoothHandler.java†L431-L440】【F:androidLib/src/main/java/com/jwoglom/pumpx2/pump/PumpState.java†L245-L255】

### 5.2 Integrity checks
Once all fragments arrive, `PacketArrayList.validate` recomputes CRC16 over the reconstructed message bytes. For signed messages it also recomputes HMAC-SHA1 using the current authentication key and compares the signature trailer. Invalid CRC or HMAC raise dedicated exceptions unless the key explicitly begins with `IGNORE_HMAC_SIGNATURE_EXCEPTION` for debugging.【F:messages/src/main/java/com/jwoglom/pumpx2/pump/messages/PacketArrayList.java†L83-L190】【F:messages/src/main/java/com/jwoglom/pumpx2/pump/messages/PacketArrayList.java†L237-L249】

### 5.3 Message parsing
`BTResponseParser` orchestrates validation and decoding:

1. Build a `PacketArrayList` tailored to the expected opcode/size (derived from the original request or message catalog).
2. Feed each notification to `validatePacket` to ensure packet ordering.
3. After validation succeeds, call `Messages.parse` to instantiate and populate the message object, logging a canonical `PARSED-MESSAGE` line.【F:messages/src/main/java/com/jwoglom/pumpx2/pump/messages/bluetooth/BTResponseParser.java†L21-L66】【F:messages/src/main/java/com/jwoglom/pumpx2/pump/messages/Messages.java†L352-L392】

## 6. Receive-Side Flow Control

Within `TandemBluetoothHandler.innerCharacteristicUpdate` the following processing occurs for each notification on the Tandem characteristics:

1. Parse the transaction ID, increment `processedResponseMessages`, and attempt to locate the associated request in `PumpState` unless the message was sent by another client.【F:androidLib/src/main/java/com/jwoglom/pumpx2/pump/bluetooth/TandemBluetoothHandler.java†L340-L427】
2. If the notification belongs to another actor, optionally synchronize the global transaction ID and emit best-effort parsed messages when connection sharing is enabled.【F:androidLib/src/main/java/com/jwoglom/pumpx2/pump/bluetooth/TandemBluetoothHandler.java†L378-L427】
3. Otherwise packetize and parse using `BTResponseParser`. Errors in transaction ID, opcode, or signature raise critical errors unless suppressed due to detected connection sharing.【F:androidLib/src/main/java/com/jwoglom/pumpx2/pump/bluetooth/TandemBluetoothHandler.java†L431-L506】
4. Completed responses clear the outstanding request entry; `ErrorResponse` triggers an application-level error callback. Partial responses are cached until remaining packets arrive.【F:androidLib/src/main/java/com/jwoglom/pumpx2/pump/bluetooth/TandemBluetoothHandler.java†L492-L510】
5. Authentication responses are intercepted and routed through the handshake state machine; non-authentication responses are forwarded to the application via `onReceiveMessage`. Pump metadata responses also update `PumpState` (API version, serial number, time-since-reset).【F:androidLib/src/main/java/com/jwoglom/pumpx2/pump/bluetooth/TandemBluetoothHandler.java†L513-L589】

## 7. Authentication Flows

### 7.1 Legacy 16-character pairing
For pumps running API ≤ 3.0, PumpX2 issues a `CentralChallengeRequest` immediately after the link-layer handshake unless connection sharing disables authentication. The request contains an app instance ID and a random challenge. The pump responds with a `CentralChallengeResponse` containing an HMAC key. The client prompts the user for the 16-character code, normalizes it, computes HMAC-SHA1 over the challenge key, and submits a `PumpChallengeRequest`. Successful responses store the pump MAC and call `internalOnPumpConnected` to trigger post-authentication setup.【F:androidLib/src/main/java/com/jwoglom/pumpx2/pump/bluetooth/TandemPump.java†L88-L136】【F:messages/src/main/java/com/jwoglom/pumpx2/pump/messages/builders/CentralChallengeRequestBuilder.java†L11-L23】【F:messages/src/main/java/com/jwoglom/pumpx2/pump/messages/builders/PumpChallengeRequestBuilder.java†L12-L81】【F:androidLib/src/main/java/com/jwoglom/pumpx2/pump/bluetooth/TandemBluetoothHandler.java†L515-L526】

### 7.2 Modern 6-digit JPAKE pairing
Newer pumps (and Tandem Mobi) use a six-digit code with an elliptic-curve JPAKE handshake coordinated by `JpakeAuthBuilder`:

1. `JpakeAuthBuilder.initializeWithPairingCode` (bootstrap) or `initializeWithDerivedSecret` (reconnect) seeds the state machine and generates the first round payload (`Jpake1aRequest`).【F:androidLib/src/main/java/com/jwoglom/pumpx2/pump/bluetooth/TandemPump.java†L253-L275】【F:messages/src/main/java/com/jwoglom/pumpx2/pump/messages/builders/JpakeAuthBuilder.java†L31-L200】
2. Each JPAKE response updates internal state (`processResponse`) and triggers the next request until the session key is derived and mutual HMAC verification succeeds. Nonces for key confirmation are generated from a secure random source.【F:messages/src/main/java/com/jwoglom/pumpx2/pump/messages/builders/JpakeAuthBuilder.java†L205-L288】
3. When complete, the derived secret and server nonce are persisted in `PumpState`; subsequent sessions can skip the expensive rounds and resume from the key confirmation stage by reusing the stored secret.【F:androidLib/src/main/java/com/jwoglom/pumpx2/pump/bluetooth/TandemBluetoothHandler.java†L560-L575】【F:androidLib/src/main/java/com/jwoglom/pumpx2/pump/PumpState.java†L92-L155】
4. `PumpStateSupplier` automatically converts the stored secret and nonce into the HKDF-derived authentication key, enabling signed requests without user interaction on reconnect.【F:messages/src/main/java/com/jwoglom/pumpx2/pump/messages/bluetooth/PumpStateSupplier.java†L32-L64】

### 7.3 Authentication bypass via connection sharing
When another client already holds the connection, PumpX2 refrains from emitting legacy or JPAKE messages. Instead it waits for remote traffic to synchronize transaction IDs and, if configured, trusts the existing session while still requiring a valid authentication key for signed requests (supplied through `PumpState`).【F:androidLib/src/main/java/com/jwoglom/pumpx2/pump/bluetooth/TandemBluetoothHandler.java†L200-L427】【F:androidLib/src/main/java/com/jwoglom/pumpx2/pump/PumpState.java†L267-L314】

## 8. Post-Authentication Baseline Requests

Once authenticated, PumpX2 automatically issues `ApiVersionRequest`, `PumpVersionRequest`, and `TimeSinceResetRequest` to populate `PumpState` with protocol capabilities, serial number, and current time-since-reset (required for signed command trailers). Applications overriding `onPumpConnected` can opt out by not calling `super`.【F:androidLib/src/main/java/com/jwoglom/pumpx2/pump/bluetooth/TandemPump.java†L204-L220】【F:androidLib/src/main/java/com/jwoglom/pumpx2/pump/bluetooth/TandemBluetoothHandler.java†L581-L588】

### 8.1 Time-since-reset maintenance
`TimeSinceResetResponse` values are cached and timestamped so packetization can pull a current `pumpTimeSinceReset`. A periodic refresh can be scheduled via the handler configuration, ensuring signatures stay valid for long-lived sessions.【F:androidLib/src/main/java/com/jwoglom/pumpx2/pump/PumpState.java†L142-L155】【F:androidLib/src/main/java/com/jwoglom/pumpx2/pump/bluetooth/TandemBluetoothHandler.java†L595-L600】

## 9. Error Handling and Recovery

- CRC or HMAC failures raise explicit exceptions; applications can supply a sentinel authentication key prefix to ignore HMAC errors during debugging captures.【F:messages/src/main/java/com/jwoglom/pumpx2/pump/messages/PacketArrayList.java†L103-L189】
- Unexpected transaction IDs trigger errors unless global `ignoreInvalidTxId` is enabled; shared connections update the global counter to follow the external actor.【F:messages/src/main/java/com/jwoglom/pumpx2/pump/messages/PacketArrayList.java†L199-L249】【F:androidLib/src/main/java/com/jwoglom/pumpx2/pump/bluetooth/TandemBluetoothHandler.java†L443-L472】
- Pump-generated `ErrorResponse` messages are surfaced to the application unless connection sharing is active, preventing false alarms when the official app is issuing commands.【F:androidLib/src/main/java/com/jwoglom/pumpx2/pump/bluetooth/TandemBluetoothHandler.java†L495-L504】
- Disconnect events reset transaction IDs and clear request caches before optionally scheduling an automatic reconnect attempt.【F:androidLib/src/main/java/com/jwoglom/pumpx2/pump/bluetooth/TandemBluetoothHandler.java†L672-L682】

---

By following the stages and data formats described above—link setup, state provisioning, packet encoding, validation, and authentication—a third-party implementation can interoperate with Tandem pumps using the same BLE transport semantics enforced by PumpX2.
