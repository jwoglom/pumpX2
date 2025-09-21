# PumpX2 repository orientation

## High-level layout
- **Gradle multi-module project** exporting three libraries (`pumpx2-android`, `pumpx2-messages`, `pumpx2-shared`) plus utilities (`cliparser`) and a demo Android app (`sampleapp`).【F:settings.gradle†L19-L20】【F:build.gradle†L1-L35】
- All modules share the `com.jwoglom.pumpx2` namespace. `shared` holds reusable utilities, `messages` implements the Tandem Bluetooth protocol, `androidLib` layers Android/BLE integration on top, and `cliparser`/`sampleapp` demonstrate usage.
- Build artifacts are configured for Maven publishing (local and GitHub Packages) and target Java 11 / Android API 26+.【F:build.gradle†L1-L35】【F:androidLib/build.gradle†L18-L65】

## Build & test quick-reference
- `./gradlew build` builds every module, producing AAR/JAR artifacts for distribution.【F:README.md†L63-L88】
- `./gradlew test` (or module-specific variants) runs the Java unit tests located in `messages/src/test` (JUnit 4).【F:messages/build.gradle†L20-L43】【83f740†L1-L10】
- `./gradlew publishToMavenLocal` publishes artifacts locally; publishing destinations are configured in `build.gradle`.
- `./gradlew cliparser:run --args '<command ...>'` executes the CLI parser for offline message parsing/encoding.【F:cliparser/src/main/java/com/jwoglom/pumpx2/cliparser/Main.java†L1-L208】
- `./gradlew :sampleapp:installDebug` builds and installs the example Android client (requires Android SDK setup).

## Module breakdown
### shared (`shared/src/main/java/com/jwoglom/pumpx2/shared`)
- Lightweight utilities used by both the Java protocol library and Android stack.
  - `Hex` supplies a platform-independent hex encoder/decoder to avoid Android `org.apache.http.legacy` conflicts.【F:shared/src/main/java/com/jwoglom/pumpx2/shared/Hex.java†L1-L45】
  - `JavaHelpers` centralizes reflection-based `toString` helpers and debugging utilities.【F:shared/src/main/java/com/jwoglom/pumpx2/shared/JavaHelpers.java†L1-L103】
  - `L` is a logging façade whose delegates can be rebound (e.g., to Timber in Android via `LConfigurator`).【F:shared/src/main/java/com/jwoglom/pumpx2/shared/L.java†L1-L64】【F:androidLib/src/main/java/com/jwoglom/pumpx2/util/timber/LConfigurator.java†L1-L44】
  - `TriConsumer`/`QuadConsumer` functional interfaces and the `LOG_PREFIX` constant support cross-platform logging.

### messages (`messages/src/main/java/com/jwoglom/pumpx2/pump/messages`)
- Implements the Tandem Bluetooth message model, packetization, and authentication primitives. Exposed as a pure-Java library (`pumpx2-messages`).
- **Message abstraction**
  - `Message` base class encapsulates cargo bytes, `MessageProps` metadata, and helpers for JSON/debug serialization. Requests and responses are annotated with `@MessageProps` describing opcode, cargo size, characteristic, signing, and API/device constraints.【F:messages/src/main/java/com/jwoglom/pumpx2/pump/messages/Message.java†L1-L120】【F:messages/src/main/java/com/jwoglom/pumpx2/pump/messages/annotations/MessageProps.java†L1-L26】
  - `MessageType` differentiates request vs response; opcodes are even/odd pairs by convention.【F:messages/src/main/java/com/jwoglom/pumpx2/pump/messages/MessageType.java†L1-L15】
  - `Messages` enum is the authoritative opcode registry. It binds request/response classes, registers them per-characteristic, and exposes helpers to instantiate or parse by opcode. Update this enum when adding new message pairs.【F:messages/src/main/java/com/jwoglom/pumpx2/pump/messages/Messages.java†L1-L223】【F:messages/src/main/java/com/jwoglom/pumpx2/pump/messages/Messages.java†L223-L394】
  - When introducing a new message, ensure its annotation references the counterpart class and add the pair to `Messages`. Stream responses (e.g., history logs) set `stream=true` to route through `StreamPacketArrayList`.
- **Packet handling & authentication**
  - `Packetize` converts a `Message` into BLE packets: adds opcode, transaction ID, length, optional HMAC-SHA1 signature (24-byte trailer), CRC16, and chunks into MTU-sized `Packet`s. It enforces `modifiesInsulinDelivery` gating via `PumpStateSupplier.actionsAffectingInsulinDeliveryEnabled`.【F:messages/src/main/java/com/jwoglom/pumpx2/pump/messages/Packetize.java†L1-L120】
  - `TransactionId` tracks the next transaction identifier (0–255) shared across the session.【F:messages/src/main/java/com/jwoglom/pumpx2/pump/messages/TransactionId.java†L1-L33】
  - `PacketArrayList` (and `StreamPacketArrayList` for streaming opcodes) reassembles multi-packet responses, validates CRC/signature, and enforces expected opcode/size/transaction ID. Use `PacketArrayList.build` to obtain the correct parser for a given message/characteristic.【F:messages/src/main/java/com/jwoglom/pumpx2/pump/messages/PacketArrayList.java†L1-L189】【F:messages/src/main/java/com/jwoglom/pumpx2/pump/messages/StreamPacketArrayList.java†L1-L95】
  - `PumpStateSupplier` provides Suppliers for authentication material (pairing code or JPAKE-derived secret), pump reset time, API version, and gating flags. Non-Android callers must seed these Suppliers before sending signed requests or parsing signed responses.【F:messages/src/main/java/com/jwoglom/pumpx2/pump/messages/bluetooth/PumpStateSupplier.java†L1-L58】
  - `bluetooth` subpackage supplies static metadata (`Characteristic`, `CharacteristicUUID`, `ServiceUUID`, `BluetoothConstants`) and runtime helpers (`BTResponseParser`, `TronMessageWrapper`, BLE `Packet`/`PumpResponseMessage` models). These utilities are reused by both the Android stack and CLI for parsing raw packets.【F:messages/src/main/java/com/jwoglom/pumpx2/pump/messages/bluetooth/BTResponseParser.java†L1-L120】【F:messages/src/main/java/com/jwoglom/pumpx2/pump/messages/bluetooth/TronMessageWrapper.java†L1-L104】
- **Message catalog**
  - Requests/responses are grouped by domain (`authentication`, `currentStatus`, `control`, `controlStream`, `historyLog`, etc.). Authentication supports both legacy 16-character pairing (`CentralChallenge`/`PumpChallenge`) and modern 6-digit JPAKE flow (`Jpake1a`–`Jpake4`).【F:messages/src/main/java/com/jwoglom/pumpx2/pump/messages/request/authentication/Jpake1aRequest.java†L1-L57】【F:messages/src/main/java/com/jwoglom/pumpx2/pump/messages/response/authentication/Jpake1aResponse.java†L1-L47】
  - Control requests that alter insulin delivery set `signed=true` and `modifiesInsulinDelivery=true` to enforce safety (e.g., `InitiateBolusRequest`).【F:messages/src/main/java/com/jwoglom/pumpx2/pump/messages/request/control/InitiateBolusRequest.java†L1-L104】
  - History log parsing is centralized in `response/historyLog/HistoryLogParser`, which maps 26-byte log records to typed subclasses annotated with `@HistoryLogProps`. Add new log types here for automatic discovery.【F:messages/src/main/java/com/jwoglom/pumpx2/pump/messages/response/historyLog/HistoryLogParser.java†L1-L105】
  - `response/qualifyingEvent/QualifyingEvent` enumerates pump events (bitmask) and recommended follow-up requests to pull detailed state.【F:messages/src/main/java/com/jwoglom/pumpx2/pump/messages/response/qualifyingEvent/QualifyingEvent.java†L1-L157】
- **Builders & higher-level logic**
  - `builders` package houses factory helpers that choose version-appropriate messages (`ControlIQInfoRequestBuilder`, `CurrentBatteryRequestBuilder`, etc.), orchestration classes like `JpakeAuthBuilder` (state machine for the JPAKE pairing handshake), and IDP management helpers. JPAKE builder progresses through `JpakeStep` stages, generating requests and consuming responses while caching derived secrets/nonces.【F:messages/src/main/java/com/jwoglom/pumpx2/pump/messages/builders/JpakeAuthBuilder.java†L1-L160】【F:messages/src/main/java/com/jwoglom/pumpx2/pump/messages/builders/JpakeAuthBuilder.java†L161-L320】
  - `calculator` models (`BolusCalculator`, `BolusCalcComponent`, `BolusCalcDecision`, etc.) replicate Tandem’s bolus advisor logic, using helper conversions in `models/InsulinUnit` and data from `BolusCalcDataSnapshotResponse`.【F:messages/src/main/java/com/jwoglom/pumpx2/pump/messages/calculator/BolusCalculator.java†L1-L120】
  - `helpers/Bytes` and `helpers/Dates` encapsulate endian-aware numeric and date conversions, CRC-16 computation, and secure randomness. Always use these utilities for parsing/building cargo to avoid manual endian bugs.【F:messages/src/main/java/com/jwoglom/pumpx2/pump/messages/helpers/Bytes.java†L1-L200】
  - `util/MessageHelpers` gathers reflection helpers (e.g., list all request messages) and `ArbitraryMessageParser` can parse raw hex by inferring characteristic/opcode, optionally overriding pump time/auth key via `PumpStateSupplier`.【F:messages/src/main/java/com/jwoglom/pumpx2/pump/messages/util/ArbitraryMessageParser.java†L1-L72】
- **Models & annotations**
  - Core value objects include `ApiVersion`, `KnownApiVersion`, `PairingCodeType`, `SupportedDevices`, `NotificationBundle`, `StatusMessage` (responses with `getStatus()`), `HistoryLog` base class, and error wrappers for unexpected opcodes/transaction IDs.【F:messages/src/main/java/com/jwoglom/pumpx2/pump/messages/models/ApiVersion.java†L1-L44】【F:messages/src/main/java/com/jwoglom/pumpx2/pump/messages/models/NotificationBundle.java†L1-L170】
  - `@ApiVersionDependent` marks classes whose usage varies between API revisions; look for companion builders.

### androidLib (`androidLib/src/main/java/com/jwoglom/pumpx2/pump` and `/util`)
- Android-facing library (`pumpx2-android`) that drives BLE connectivity, state persistence, and logging.
  - `TandemPump` is the abstract frontend API: override callbacks (`onInitialPumpConnection`, `onWaitingForPairingCode`, `onReceiveMessage`, `onReceiveQualifyingEvent`, etc.), call `sendCommand` to transmit requests, and expose pairing UX. It also exposes toggles for connection sharing with the official t:connect app and gating of insulin-affecting actions.【F:androidLib/src/main/java/com/jwoglom/pumpx2/pump/bluetooth/TandemPump.java†L1-L200】【F:androidLib/src/main/java/com/jwoglom/pumpx2/pump/bluetooth/TandemPump.java†L200-L300】
  - `TandemBluetoothHandler` owns the BLE central (via the BLESSED library). It manages scanning/bonding, sets MTU and notifications, coordinates authentication (both legacy and JPAKE flows), parses incoming packets with the shared `BTResponseParser`, and forwards parsed messages/events to the `TandemPump` callbacks. It also supports connection sharing heuristics with the official app.【F:androidLib/src/main/java/com/jwoglom/pumpx2/pump/bluetooth/TandemBluetoothHandler.java†L1-L200】【F:androidLib/src/main/java/com/jwoglom/pumpx2/pump/bluetooth/TandemBluetoothHandler.java†L300-L492】
  - `PumpState` is the Android persistence layer: backs the `PumpStateSupplier` via SharedPreferences, tracks request/response queues for transaction IDs, caches pairing/JPAKE secrets, manages connection-sharing flags, and stores pump metadata like serial numbers or reset time.【F:androidLib/src/main/java/com/jwoglom/pumpx2/pump/PumpState.java†L1-L200】【F:androidLib/src/main/java/com/jwoglom/pumpx2/pump/PumpState.java†L200-L314】
  - `TandemConfig` collects optional construction parameters (Bluetooth MAC filter, explicit pairing code type, periodic TimeSinceReset polling).【F:androidLib/src/main/java/com/jwoglom/pumpx2/pump/bluetooth/TandemConfig.java†L1-L40】
  - `TandemPumpFinder` provides a scanning-only abstraction to surface available pumps before connecting, using similar BLE setup but without sending commands.【F:androidLib/src/main/java/com/jwoglom/pumpx2/pump/bluetooth/TandemPumpFinder.java†L1-L200】
  - `TandemError` enumerates user-visible error causes used by the handler to surface failures.【F:androidLib/src/main/java/com/jwoglom/pumpx2/pump/TandemError.java†L1-L34】
  - `util/timber` integrates the shared logging façade with Timber (`DebugTree`, `LConfigurator`). `EventService` is a stub `Service` placeholder for broadcast handling.

### cliparser (`cliparser/src/main/java/com/jwoglom/pumpx2/cliparser`)
- Command-line utilities for offline analysis, testing, and message encoding/decoding.
  - `Main` dispatches subcommands such as `opcode`, `parse`, `guesscargo`, `historylog`, `json`, `encode`, and `jpake`. It relies on the shared `messages` module for parsing and on environment variables (`PUMP_AUTHENTICATION_KEY`, `PUMP_PAIRING_CODE`, `PUMP_TIME_SINCE_RESET`) to configure `PumpStateSupplier`.【F:cliparser/src/main/java/com/jwoglom/pumpx2/cliparser/Main.java†L1-L208】【F:cliparser/src/main/java/com/jwoglom/pumpx2/cliparser/Main.java†L208-L320】
  - Utilities like `CharacteristicGuesser` and `JsonMessageParser` (not shown above) assist with inference and structured output.

### sampleapp (`sampleapp/src/main/java/com/jwoglom/pumpx2/example`)
- Reference Android application showing how to extend `TandemPump` and orchestrate UI flows.
  - `PumpX2TandemPump` subclasses `TandemPump`, wires broadcast intents for UI updates, demonstrates history log streaming, bolus flows, and connection sharing enablement.【F:sampleapp/src/main/java/com/jwoglom/pumpx2/example/PumpX2TandemPump.java†L1-L200】
  - `MainActivity` drives user interaction, enumerates request message classes, issues commands (bolus permission, history log requests), and handles broadcast responses from the pump callbacks.【F:sampleapp/src/main/java/com/jwoglom/pumpx2/example/MainActivity.java†L1-L160】
- Layout resources (`sampleapp/src/main/res`) and manifest configure the example UI.

## Key flows & design patterns
- **Authentication**
  - Legacy pumps (API < 3.x) use `CentralChallengeRequest` → `PumpChallengeRequest` with a 16-character pairing code. Newer pumps use a multi-step JPAKE handshake (`Jpake1a`→`Jpake4`). `JpakeAuthBuilder` drives the state machine, caching derived secrets/nonces in `PumpState` for subsequent sessions.【F:messages/src/main/java/com/jwoglom/pumpx2/pump/messages/request/authentication/Jpake1aRequest.java†L1-L57】【F:androidLib/src/main/java/com/jwoglom/pumpx2/pump/bluetooth/TandemPump.java†L200-L300】
- **Sending commands**
  - `TandemPump.sendCommand` records the pending request in `PumpState`, packetizes it (including optional signing), and writes the resulting BLE chunks via BLESSED. Signed requests require `PumpStateSupplier.authenticationKey` (pairing code or derived secret) and a recent `PumpStateSupplier.pumpTimeSinceReset` value.【F:androidLib/src/main/java/com/jwoglom/pumpx2/pump/bluetooth/TandemPump.java†L60-L135】【F:messages/src/main/java/com/jwoglom/pumpx2/pump/messages/Packetize.java†L1-L120】
- **Receiving responses**
  - `TandemBluetoothHandler` reassembles packets with `PacketArrayList`, resolves the original request from `PumpState`, parses the message via `Messages`, and routes it to the appropriate callback. It tolerates concurrent traffic from the official t:connect app by inspecting unknown transaction IDs and synchronizing the global `TransactionId` counter.【F:androidLib/src/main/java/com/jwoglom/pumpx2/pump/bluetooth/TandemBluetoothHandler.java†L300-L492】
- **History logs**
  - Request/response flows (`HistoryLogStatusRequest`, `HistoryLogRequest`, `HistoryLogStreamResponse`) stream fixed-width 26-byte records. `HistoryLogParser` maps raw cargo to typed logs; logs expose timestamps relative to Jan 1 2008 via `helpers/Dates`.【F:messages/src/main/java/com/jwoglom/pumpx2/pump/messages/response/historyLog/HistoryLogParser.java†L1-L105】【F:messages/src/main/java/com/jwoglom/pumpx2/pump/messages/helpers/Dates.java†L1-L40】
- **Bolus workflow**
  - Bolus control uses a sequence of `BolusPermissionRequest` → `BolusCalcDataSnapshotRequest`/`LastBGRequest` (optional) → `InitiateBolusRequest`, tracked through `PumpStateSupplier.inProgressBolusId`. Calculations leverage `calculator/BolusCalculator` when replicating pump UI logic.【F:messages/src/main/java/com/jwoglom/pumpx2/pump/messages/request/control/InitiateBolusRequest.java†L1-L120】【F:messages/src/main/java/com/jwoglom/pumpx2/pump/messages/calculator/BolusCalculator.java†L1-L120】
- **Qualifying events**
  - The pump pushes bitmasks on the `QUALIFYING_EVENTS` characteristic; `QualifyingEvent` translates these to enums and suggests follow-up requests. The Android handler treats them separately from standard request/response traffic.【F:messages/src/main/java/com/jwoglom/pumpx2/pump/messages/response/qualifyingEvent/QualifyingEvent.java†L1-L157】【F:androidLib/src/main/java/com/jwoglom/pumpx2/pump/bluetooth/TandemBluetoothHandler.java†L320-L370】

## Implementation guidance & gotchas
- **Always seed `PumpStateSupplier`** when using the pure-Java library outside Android. Provide pairing code/derived secret, pump time since reset, API version, and optionally Control-IQ support before sending signed messages or verifying signatures.【F:messages/src/main/java/com/jwoglom/pumpx2/pump/messages/bluetooth/PumpStateSupplier.java†L1-L58】
- **Guard insulin-delivery commands** by calling `PumpState.enableActionsAffectingInsulinDelivery()` (or `TandemPump.enableActionsAffectingInsulinDelivery()`) before invoking messages with `modifiesInsulinDelivery=true`; otherwise `Packetize` throws an exception.【F:messages/src/main/java/com/jwoglom/pumpx2/pump/messages/Packetize.java†L60-L101】【F:androidLib/src/main/java/com/jwoglom/pumpx2/pump/bluetooth/TandemPump.java†L230-L246】
- **Updating message catalog**
  - Create new request/response classes with `@MessageProps`, implement `parse`, and supply static `buildCargo` helpers for deterministic construction.
  - Add the pair to `Messages` (respect the `// IMPORT_END` / `// MESSAGES_END` markers that scripts use) so parsing works globally.【F:messages/src/main/java/com/jwoglom/pumpx2/pump/messages/Messages.java†L1-L223】【F:messages/src/main/java/com/jwoglom/pumpx2/pump/messages/Messages.java†L223-L394】
  - For new history logs, implement a `HistoryLog` subclass annotated with `@HistoryLogProps` and register it in `HistoryLogParser.LOG_MESSAGE_TYPES`.
- **Packet parsing edge cases**
  - Some responses have variable cargo sizes (e.g., `ErrorResponse`, `ApiVersionResponse`). `PacketArrayList.parse` contains special cases—mirror those patterns if you add new variable-length messages.【F:messages/src/main/java/com/jwoglom/pumpx2/pump/messages/PacketArrayList.java†L80-L142】
  - HMAC validation can be bypassed for debugging by setting the authentication key prefix to `IGNORE_HMAC_SIGNATURE_EXCEPTION` (used by the CLI when no pairing code is available).【F:messages/src/main/java/com/jwoglom/pumpx2/pump/messages/PacketArrayList.java†L156-L189】
- **Transaction ID management**
  - `PumpState` tracks outstanding requests by `(Characteristic, txId)`. Always push requests via `TandemPump.sendCommand` (or replicate its bookkeeping) to avoid parse failures on incoming responses.【F:androidLib/src/main/java/com/jwoglom/pumpx2/pump/PumpState.java†L200-L259】
- **Connection sharing**
  - `PumpState` exposes flags to cooperate with the official t:connect app (e.g., rely on shared authentication, suppress critical errors). Set these early (in your `TandemPump` constructor) if you intend to piggyback on an existing connection.【F:androidLib/src/main/java/com/jwoglom/pumpx2/pump/PumpState.java†L314-L339】【F:androidLib/src/main/java/com/jwoglom/pumpx2/pump/bluetooth/TandemPump.java†L230-L300】
- **Logging**
  - For Android, install a Timber tree (by default `TandemBluetoothHandler` plants `DebugTree`) to route shared logging through Timber via `LConfigurator`. CLI utilities redirect logging to stderr via `L.getPrintln`.【F:androidLib/src/main/java/com/jwoglom/pumpx2/util/timber/DebugTree.java†L1-L25】【F:cliparser/src/main/java/com/jwoglom/pumpx2/cliparser/Main.java†L1-L50】

## Testing & debugging tips
- Unit tests cover byte helpers, crypto helpers, message builders, and JPAKE flows. Use them as references when implementing new features.【83f740†L1-L10】
- The CLI `cliparser` is invaluable for parsing captured Bluetooth logs, testing HMAC signing (via `encode`), or walking through JPAKE handshake steps (`jpake` command expects interactive input/output).【F:cliparser/src/main/java/com/jwoglom/pumpx2/cliparser/Main.java†L1-L208】
- Sample app broadcasts (constants in `PumpX2TandemPump`) make it easy to instrument UI listeners without modifying library internals.【F:sampleapp/src/main/java/com/jwoglom/pumpx2/example/PumpX2TandemPump.java†L1-L120】
- When debugging connection issues, inspect logs around MTU negotiation, notification enabling, and transaction ID alignment—the handler logs every sent/received packet and opcode in verbose mode.【F:androidLib/src/main/java/com/jwoglom/pumpx2/pump/bluetooth/TandemBluetoothHandler.java†L300-L492】

## Additional resources in the repo
- Markdown guides such as `mobile_bolus.md` and `fill_cart_tubing_cannula.md` document observed pump behavior for specific workflows (bolus, fill tubing/cannula). Reference these when implementing higher-level flows.
- `messages/src/main/java/.../README.md` summarizes the initial handshake steps (`CentralChallenge`, `PumpChallenge`, `ApiVersion`).【F:messages/src/main/java/com/jwoglom/pumpx2/pump/messages/README.md†L1-L15】
- Shell/Python scripts in `scripts/` assist with parsing captured BLE traffic; inspect them if you need concrete examples of encoded packets.

## Working in this repository
- Follow the existing formatting and helper patterns; use the `Bytes` helper for all numeric conversions and prefer `JavaHelpers.autoToString` for `toString` implementations.
- Respect the safety gates around insulin-affecting commands and ensure new features continue to enforce them.
- Update this `AGENTS.md` if you discover additional architectural conventions that would help future contributors.
