# WhatsOnUndip

It's "WhatsOn Undip". Through this app, You can find events from organizations or communities in University of Diponegoro (Undip) and you can also submit your event. 

It has two explicit intents (share intent and calendar intent). You can share the event to your social media or add it to your mobile calendar.

This app has four activities that moved by implicit intent. They are MainActivity, DetailActivity, SubmitEventActivity, and AboutActivity. Those activities work properly with the app lifecycle (rotate screen changes), except SubmitEventActivity. Because it uses Google Form as webView.

This app has two different UI between tablet and phone. At phone you will face the event list before going to event detail, while at tablet you can see event list and the item detail in one view.

It only uses INTERNET , READ_SYNC_SETTINGS, WRITE_SYNC_SETTINGS, and AUTHENTICATE_ACCOUNTS permission to support core functionality.

It also has contentProvider to retrieves and caches data from a server. 
it's also regularly pulls or sends data to/from server in its cache at regular intervals using a SyncAdapter. The server or database that be used is Google Spreadsheet API. Here's the spreadsheet link : https://docs.google.com/spreadsheets/d/1Yh6MxmVd0-pB_SmX2Y62TxnAPIZotEGHzrYT-6BDSqk/edit?usp=sharing

You can download this app here : https://drive.google.com/file/d/0B8zGUUE4pwmQei1VUUFPZWdXbFk/view?usp=sharing

Here's the app's screenshot

![alt tag](https://lh5.googleusercontent.com/-jSE3uoPruww/VUL2VhcsxxI/AAAAAAAADEA/KeVyPmFbzzc/w415-h553-no/11180361_1052138298133770_327356688_o.jpg)
![alt tag](https://lh5.googleusercontent.com/-wqm1INT1RAM/VUL2W9IGzdI/AAAAAAAADEQ/_uLYAa8-n6M/w415-h553-no/11188115_1052120728135527_1462547198_o.jpg)
![alt tag](https://lh5.googleusercontent.com/-r30-e1DXJEs/VUL2Wxbxc-I/AAAAAAAADEM/IsyEFxbo3RY/w415-h553-no/11196464_1052118448135755_1427274437_o.jpg)
![alt tag](https://lh5.googleusercontent.com/-UJ8Qm0A2vVo/VUL2ZwOCTSI/AAAAAAAADEg/6MsFRSmHnyU/w415-h553-no/11207904_1051597098187890_1146202266_o.jpg)
![alt tag](https://lh4.googleusercontent.com/-llQpPv9Ap_Y/VUL2Y09OJOI/AAAAAAAADEY/28U3Py2PE7M/w889-h553-no/Capture1.PNG)

This app will be developed more. Thank you

Regard

Afifatul Mukaroh
