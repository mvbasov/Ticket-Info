### Change log.

#### Version 3.02b
##### Date:   Tue Jul 4 20:58:10 2017 +0300, commit c617b9e989d371f1c45e6fe3adbf8288b7b16b30

- Add remark to dump from application

#### Version 3.01b
##### Date:   Mon Jul 3 19:06:23 2017 +0300, commit 01f28cff1ee641be083455a57a2885c2ba3042be

- New ticket type: Baggage fo metro and monorail.
- New file name system for unknown or partialy known tickets.
- Send dump to E-Mail.

#### Version 3.00b
##### Date:   Fri Jun 30 16:50:23 2017 +0300, commit b30c4d3f980266b828eb2dc61f334503fbd64afe

- New user interface
- Support all "paper" tickets using on present time. (I think it's true :)
- Replacable metro station DB (XML based)
- Debug time interface using special DDD: string in dump file
- Application language and station name transliteration may be switched by settings
- Various fixes and code cleanup

#### Version 2.08b
##### Date:   Wed Sep 28 20:34:59 2016 +0300, commit b5e4018f80ed2f43be6ee2e9c0d6314fe67ecd05

- New tickets layout (since 2016-09-01) almost completely supported.
- If dump already exists it's comment field displayed.
- Make text on screen selectable.
- Code provided with JUnit tests.
- Source code little bit more documented and logicaly reorganized.

#### Version 2.07b
##### Date:   Wed May 25 21:44:21 2016 +0300, commit 5b319f941bc0f5e653fdf5ff5cbb28f454ac62e7

- Read saved dumps.
- Several new tickets types:
  - Zone A and B. Ground
  - Temporary Universal Social Ticket
- Several new turnstiles.
- Fix bug with reverse order of bytes in IC signature.

#### Version 2.06b
##### Date:   Wed Feb 3 15:30:54 2016 +0300, commit 903ccff180747207a9370fd56d40d5c3a325f192

- Automaticaly save dumps to /sdcard/Android/data/ru.valle.tickets/files/AutoDumps
- New tickets types:
  - Universal, unlimited passes (20 minutes between passes), limited to 1,3 or 7 days
  - Zone B ground 1 pass
- Several new turnstiles and stations.

#### Version 2.05b
##### Date:   Mon Dec 28 14:59:24 2015 +0300, commit ffe98ad551ee48c501c8c87f999ba6f17556ab11

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

#### Version 2.04b
##### Date:   Wed Dec 9 16:43:29 2015 +0300, commit 3ee9a208b59621360024a5d8b0e289f811484972

- Make screen scrollable
- Read all information from card
- Display dump
- Display memory capacity information

#### Version 2.03b
##### Date:   Thu Dec 3 14:55:58 2015 +0300, commit 52e6340c893d816fa944b76eb80c28096dd8a19d

- Display ATQA, SAK, internal ID (and its checksum), internal manufacturer byte (probabli depend on chip info)
- Display Unique ID (UID) of card and it chechsums (BCC0 and BCC1)
- Display Android discovered technologyes
- Start implementing code to determinate used chip

#### Version 2.02b
##### Date:   Thu Nov 26 16:51:35 2015 +0300, commit 1117bb660a503d8f156c9174c7ffa4a7f258a63e

- Add information about used tools and build instructions to README.
- Station information update.

#### Version 2.01b
##### Date:   Tue Nov 24 23:10:21 2015 +0300, commit e331b178c68415663d7fe5f7fafc779b1c5e0b5a

- Add information about stations.
- Code reorganized.
- Bug with show large ticket number as negative value fixed.
- New tickets layout implemented.
- Link to CHANGELOG from README corrected

#### Version 2.00b
##### Date:   Tue Nov 24 21:15:25 2015 +0300, commit 6e57140e7de0cab023c1c350559f836bf3fae7bc

- Version changed 1.01 -> 2.00
- README updated
- Build configuration changed.
  It allow to make 3 version of android package (release, betta and alpha) with differnt package name from one source tree
- Low level calls to NFC changed from MifareUltralight to NfcA. It allow to read JSC Micron made tickets.

##### Date:   Tue Nov 24 19:28:57 2015 +0300, commit 2e121ceec8cd278ec4497b920971678bada56378

- Project converted to modern Android Studio (v1.5)

