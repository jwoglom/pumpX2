BolusPermissionRequest  -> cancel: BolusPermissionReleaseRequest
>BolusPermissionResponse

BolusCalcDataSnapshotRequest
>BolusCalcDataSnapshotResponse

TimeSinceResetRequest
>TimeSinceResetResponse

RemoteBgEntryRequest (opcode -74)
>RemoteBgEntryResponse (opcode -73)

? opcode -14
>? opcode -13

InitiateBolusRequest    -> cancel: BolusTerminationRequest
    with bolusID, timestamp from TimeSinceResetResponse
>InitiateBolusResponse
