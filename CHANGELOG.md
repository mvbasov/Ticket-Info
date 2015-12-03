### Change log.

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

