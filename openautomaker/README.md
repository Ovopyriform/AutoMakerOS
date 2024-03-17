# OpenAutomaker
OpenAutomaker is an updated version of the AutoMaker software for use with Robox 3D printers.  Use at your own risk.  It's an early release based on the latest AutoMaker.  

Couple of things to note:
* Root hasn't been updated yet.  This will work with the latest AutoMaker Root
* GCode Viewer isn't included yet

Includes a few updates:
* Code base-lined to JDK 17 LTS
* Complete Maven/JDK based build and packaging
* Additional option to select Cura 5 as an experimental slicer (based on Henry's Cura 5 patch)

## Building OpenAutomaker
### Prerequisites
Maven is used to build OpenAutomaker and you can build using any IDE which supports Maven.  Eclipse project defs are included in the repo.
* JDK 17 LTS E.g. OpenJDK 17
* Apache Maven 3.9.0

### Building
At present this only works on Intel based Macs (all I've had time to do so far).

	git clone https://github.com/ovopyriform/OpenAutomaker.git
	cd OpenAutomaker/openautomaker-parent
	mvn clean install
	
This will create a dmg containing the application in the openautomaker-parent.  Install like any other mac app.