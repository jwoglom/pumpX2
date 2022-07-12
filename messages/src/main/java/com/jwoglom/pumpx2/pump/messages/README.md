## t:slim X2 Bluetooth Protocol

When the t:connect app initializes on first launch:

* sends **CentralChallengeRequest** with centralChallenge random bytes
* receives **CentralChallengeResponse** with new bytes


* sends **PumpChallengeRequest** with pumpChallengeHash (hmac sha1 of the pump's Pairing Code)
* receives **PumpChallengeResponse** with success status


* sends **ApiVersionRequest** with empty cargo
* receives **ApiVersionResponse** with major and minor bluetooth API version
