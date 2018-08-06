# DataWedgeAutoImport
Sample app to show DataWedge auto import feature

**This application is provided without any guarantee or warranty**

Usage:
- The staging directory is given towards the top of the screen
- Copy all profiles to the staging directory (a test profile is provided in this repository under the testprofiles directory)
`adb push dwprofile_test.db /storage/emulated/0/datawedge_import/` (may differ on your device, please use the value given on the UI)
- Press 'Import'
- Success / error message will be shown on the screen
- DataWedge should be updated with your new profile(s)

![Application](https://raw.githubusercontent.com/darryncampbell/DataWedgeAutoImport/master/screens/001.png)

![DataWedge](https://raw.githubusercontent.com/darryncampbell/DataWedgeAutoImport/master/screens/002.png)
