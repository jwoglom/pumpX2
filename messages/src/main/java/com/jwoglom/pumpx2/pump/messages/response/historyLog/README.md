# Making a History Log Request

Via the currentStatus characteristic, a HistoryLogRequest is sent to the pump, with a startLog ID
and a number of logs to receive. The pump stores all history log messages sequentially with an ID
and history logs can only be fetched by asking for data from a specific ID range.

After a HistoryLogRequest is sent, a HistoryLogResponse will be received containing a stream ID.
Concurrently, on the separate historyLog Bluetooth characteristic, a series of HistoryLogStreamResponse's
are sent, each with the stream ID. Each message in the stream contains a packed representation of
individual history log events. The HistoryLogStreamResponse contains a count of the number of packed history
log entries which are in that specific stream message, and the size of the response is variable based on
the number of entries -- each entry is 26 bytes.

Each entry inside the HistoryLogStreamResponse is parsed with HistoryLogParser, and contains a
message type ID, all of which are listed below. To add processing for a new history log type,
a HistoryLog class is added inside `com.jwoglom.pumpx2.pump.messages.response.historyLog`.

There is, to my knowledge, no consistent timestamp representation in each message. Some contain a
timestamp (such as CGMHistoryLog) while some do not. There may be a way of identifying the timestamp
for each history log message more abstractly but this requires further investigation.


## History Log Types

```
    LID_TEMP_RATE_ACTIVATED(2),
    LID_BASAL_RATE_CHANGE(3),
    LID_PUMPING_SUSPENDED(11),
    LID_PUMPING_RESUMED(12),
    LID_TIME_CHANGED(13),
    LID_DATE_CHANGED(14),
    LID_TEMP_RATE_COMPLETED(15),
    LID_BG_READING_TAKEN(16),
    LID_BOLUS_COMPLETED(20),
    LID_BOLEX_COMPLETED(21),
    LID_BOLUS_ACTIVATED(55),
    LID_IDP_MSG2(57),
    LID_BOLEX_ACTIVATED(59),
    LID_IDP_TD_SEG(68),
    LID_IDP(69),
    LID_IDP_BOLUS(70),
    LID_IDP_LIST(71),
    LID_PARAM_PUMP_SETTINGS(73),
    LID_PARAM_GLOBAL_SETTINGS(74),
    LID_NEW_DAY(90),
    LID_PARAM_REMINDER(96),
    LID_HOMIN_SETTINGS_CHANGE(142),
    LID_CGM_ANNU_SETTINGS(157),
    LID_CGM_TRANSMITTER_ID(156),
    LID_CGM_HGA_SETTINGS(165),
    LID_CGM_LGA_SETTINGS(166),
    LID_CGM_RRA_SETTINGS(167),
    LID_CGM_FRA_SETTINGS(168),
    LID_CGM_OOR_SETTINGS(169),
    LID_HYPO_MINIMIZER_SUSPEND(198),
    LID_HYPO_MINIMIZER_RESUME(199),
    LID_CGM_DATA_GXB(256),
    LID_BASAL_DELIVERY(279),
    LID_BOLUS_DELIVERY(280);
```