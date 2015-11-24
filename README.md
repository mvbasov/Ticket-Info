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

### Last changes:

- Add information about stations.
- Code reorganized.
- Bug with show large ticket number as negative value fixed.
- New tickets layout implemented.
- Link to CHANGELOG from README corrected

You can see [full change log](https://github.com/mvbasov/Ticket-Info/blob/master/CHANGELOG.md)


