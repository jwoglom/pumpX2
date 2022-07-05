
<a href="https://github.com/jwoglom/pumpX2/actions/workflows/android.yml">
<img src="https://github.com/jwoglom/pumpx2/actions/workflows/android.yml/badge.svg" />
</a>


# PumpX2

Reverse-engineered Bluetooth protocol for the Tandem t:slim X2.

Currently functions as a basic Android application which pairs to the pump
and allows sending read-only message requests and receiving responses about
the pump's status and history. The Bluetooth protocol parsing code is exported
as an Android library which can be used by other projects.

[View supported request/response/history log messages][sheet]

[Discuss in #tslim-x2-dev on Discord][discord]

**Currently supported:**

* Bluetooth connection
* Pump authentication and pairing
* currentStatus characteristic request messages

**Partially supported:**

* History Log parsing

**Not supported:**

* "V2" pump request/response messages (self-reported API version >= 2.2)
* Control features

[sheet]: https://docs.google.com/spreadsheets/d/e/2PACX-1vTDnXBbJfiwVh-5PDK78RZqgI7C7ymOl-aEw5JLCV8rl7AiYZdoTwx_gBkWUZoducIxh7JXlOJJd9p6/pubhtml?gid=1917691101&single=true
[discord]: https://discord.gg/4fQUWHZ4Mw
