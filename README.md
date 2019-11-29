![ic_launcher_circle](https://user-images.githubusercontent.com/32466953/69854101-87b72380-1288-11ea-8de2-9964440e1179.png)

# ShuttleGo-Android

## Introduction
Most of the traffic between european airports and hotels is caused by private vehicles, it usually causes important traffic jams between these two. Providing a low cost and efficient access to airports has become a fundamental mobility issue for cities, so some minority transport methods are being promoted.

Shuttle bus is one of these new initiatives, which has become popular during the last years, making companies offer this new type of service as an alternative.

The goal of this bachelor thesis is showing a smartphone app that solves the user’s problem when booking these kind of services, giving portability, simplicity and quickness to the experience, properties that cannot be found in other similar apps. This application works for both ways, passengers and drivers. Although the project started as an idea only for airports, it’s scalable and can be used to solve another transport situations.

![](https://i.pinimg.com/originals/38/07/cd/3807cdf23c778c8789dbc7e723cae490.gif)

## Documentation
For the development of this project, we wanted to maximize the usability of the application trying, as far as possible, to comply with the most of its objectives such as improving efficiency, ease of learning or user satisfaction. The arrangement of the elements in views, drop-down menu or search bars on the map, they have a style similar to other popular applications, so the new users will not have too many problems to cope with the application.

At the level of internal design and coding, attempts have been made to adapt design patterns to the two platforms on which the project, with the aim of making the most scalable software, maintainable and robust possible. The code has a documentation complete and a coherent, clean and readable structure.

### Architecture
![Untitled Diagram](https://user-images.githubusercontent.com/32466953/69856620-55102980-128e-11ea-88b8-7b781d1173ba.png)

## Project Content
- Bachelor complete dodumentation: /doc-shuttle-go.pdf
- APK application installer: /shuttleGoInstaller.apk
- Source Andorid Studio client: /src/android-shuttle-go
- Source server: /src/firebase-server

## Setup
This section provides the necessary indications and requirementsfor the correct installation of the ShuttleGo mobile application.

  1. Requirements
    The client of this project is developed for the platform Android, at least you have to have version 6.0 (Marshmallow), although the     SDK used is 28 (Android 9.0, Pie).
  
  2. Most mobile devices have the option “install applications of unknown use ”deactivated. To activate said option should go to  Settings> Security. NOTE: the navigation through the settings may vary slightly in function of the Android version of the device and / or the layer of manufacturer customization.
  
  3. Once inside activate the option “Unknown origins”. Google will display a message warning of the risks
That this implies.

  4. Now you can proceed to the installation of the application, running the shuttleGoInstaller.apk file from the device.
  
  5. This file can be copied by connecting the terminal to a computer through a USB and dragging it to any device directory; or, it can be downloaded from the repository where it is hosted. In this manual, the following will be followed Steps of the second option.
  
  6. The file is downloaded and the device automatically starts the installation process. Must be press install to continue.

## Technologies
  - **Android Studio**: for the client application development and graphical interface. This module has been 
    developed with **Java**.
    
  - **Firebase API**: for server application development. This module has been developed with **JavaScript**.
  
  - **Mapbox API**: to render and manage the map management used by the application.

## Authors
The project has been carried out by [Carlos Castellanos](https://github.com/carlosCharlie) and [Victor Chamizo](https://github.com/vctorChamizo).

Happy coding! 💻
