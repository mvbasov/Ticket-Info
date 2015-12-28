This is Android application to see what inside Moscow trasportation system tickets.

Tis project is update to actual state of another [old project](https://github.com/ValleZ/Ticket-Info.git)

This project has no value as Android application because several projects with better functionality exist in Android Market now (for example [Транспортные карты Москвы](https://play.google.com/store/apps/details?id=eu.dedb.nfc.moscow)).
This project interesting as open source example of working with NFC and for understanding "How things made" (about Moscow transportation system tickets). It is not a target of this project "brake" security system to make "unlimited" ticket.

source files encoding is utf-8

Author of original project said:
> I live in california now, so I can not update the app.

I doing my best to make this project actual.

Some restrictions exist:

* Work only with "paper" tickets (Mifare Ultralight and modern Micron JSC). It because new android phones use NFC chip with reduced functionality. Modern Broadcom NFC chip can't read Mifare Classic 1K (Troika ticket based on it)
* All information for this project gathered by reverce engeneering and information may be not correct. I don't know official source of information about tickets format and used chips. 

### Version numbering:

* 2.00  - alpha version, very unstable, experimental
* 2.00b - betta version, based on alpha version with the same number, more stable
* 2.00r - stable release

I will never create release version for this application. This projech has no value as application (see below)

### How to build:

Open this project in Android Studio. Allow Android Studio renerate gradle wrapper. Click on "app" in Project tree. Select "Build>Edit Build Types". Select module "app" and "Signing" tab.
Set Signing options according to you local settings. If you want to build only unsigned debug version - clean all settings there and select "Build>Build APK". In this case created APK can be found in app/build/outputs/apk directory.

### Tools to get information:

* [NFC TagInfo](https://play.google.com/store/apps/details?id=at.mroland.android.apps.nfctaginfo) - to get information about chip, memory r/w status etc.
* [MIFARE++ Ultralight](https://play.google.com/store/apps/details?id=com.samsung.sprc.fileselector) - to dump tickets.

### Last changes:

- Check UID CRC
- Get and display answer to GET_VERSION command
- Chip detection.
- Page acces condition displayed in dump.
- One way counters display.
- Read and display IC signature.
- Display 90 minutes trip details.
- Several new tickets types, stations and turnstails.
- MainActivity as small as possible. NFCaDump code ready to use in another progects.
- Last trip type (metro|ground) detection.
- Check expired by date.
- Code reorganization. Prepared to dump functionality.

You can see [full change log](https://github.com/mvbasov/Ticket-Info/blob/master/CHANGELOG.md)


