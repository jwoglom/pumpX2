Change cartridge:
* send SuspendPumpingRequest()
* send EnterChangeCartridgeModeRequest()
* pump sends one or more EnterChangeCartridgeModeStateStreamResponse (stream message sent from pump with no initiating request) to report progress.
* Wait for EnterChangeCartridgeModeStateStreamResponse with state=READY_TO_CHANGE (id 2)
* Wait for initial call response (EnterChangeCartridgeModeResponse) with status=0
Now safe to tell user cartridge can be changed.

When user says they are done changing cartridge:
* send ExitChangeCartridgeModeRequest()
* pump sends one or more DetectingCartridgeStateStreamResponse (stream message sent from pump with no initiating request) to report progress.
* multiple DetectingCartridgeStateStreamResponse's returned with a percentage status of detecting insulin amount in the cartridge. Wait for DetectingCartridgeStateStreamResponse with percentageComplete=100 (seems to send 5 messages in 20% intervals -- 20, 40, 60, 80, 100)
* Wait for call response (ExitChangeCartridgeModeResponse) with status=0
Cartridge fully detected and safe to move on to fill tubing.

Fill tubing:
* send EnterFillTubingModeRequest()
* wait for response EnterFillTubingModeResponse() with status=0
* Now, when the pump button is held down, insulin flows through the tubing.
* When the pump button is pressed down, we receive FillTubingStateStreamResponse() with buttonState=1. When pump button is released, we receive FillTubingStateStreamResponse() with buttonState=0. The Mobi app uses this to update the UI to tell you to hold down the button or release when you have filled enough insulin.
* When in buttonState=0 (aka while user is not actively holding down the button), UI should allow the user to complete the tubing fill process


When user done filling tubing:
* Send ExitFillTubingModeRequest()
* pump sends one or more ExitFillTubingModeStateStreamResponse (stream message sent from pump with no initiating request) to report progress.
* Wait for ExitFillTubingModeStateStreamResponse with state=TUBING_FILLED (id 0)
* wait for initial call response ExitFillTubingModeResponse() with status=0
Now safe to move on to fill cannula.

To fill cannula:
* send FillCannulaRequest() with primeSizeMilliUnits arg between 0 and 3000 milliunits (i.e. 0 to 3 units)
* pump sends one or more FillCannulaStateStreamResponse (stream message sent from pump with no initiating request) to report progress.
* Wait for FillCannulaStateStreamResponse with state=CANNULA_FILLED (id 2)
* Wait for initial call response FillCannulaResponse with status=0