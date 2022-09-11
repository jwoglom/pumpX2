BolusPermissionRequest  -> cancel: BolusPermissionReleaseRequest
>BolusPermissionResponse

BolusCalcDataSnapshotRequest
>BolusCalcDataSnapshotResponse

TimeSinceResetRequest
>TimeSinceResetResponse

RemoteBgEntryRequest (opcode -74)
>RemoteBgEntryResponse (opcode -73)

optionally:
    RemoteCarbEntryRequest
    >RemoteCarbEntryResponse

somewhere:
    BolusPermissionChangeReasonRequest (opcode ??, in currentStatus)
    >BolusPermissionChangeReasonResponse (opcode ??, in currentStatus)


in one case:
    ? opcode -14
    >? opcode -13

? opcode -16 request
>? opcode -15 response

InitiateBolusRequest    -> cancel: BolusTerminationRequest
    with bolusID, timestamp from TimeSinceResetResponse
>InitiateBolusResponse
