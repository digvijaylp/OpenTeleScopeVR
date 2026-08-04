 ![alt text](https://github.com/digvijaylp/opentelescopevr/blob/main/art/icon.png)

# OpenTeleScopeVR
OpenTelescopeVR is a specialized VR-based Android camera application for people with low vision, designed to turn your smartphone into a digital telescope

By mounting your phone into a standard VR viewer and connecting an external keyboard or gamepad, you can use your phone to attend lectures, conferences and meetings. 
_________________________________

###  Acknowledgments and Credits

This project is a modified fork of **Open Camera**, an open-source camera app for Android. Massive thanks to **Mark Harman**, (the creator and maintainer of Open Camera) and other members of the community who contributed to the Open Camera project. 

* **Open Camera Website:** [https://opencamera.org.uk/](https://opencamera.org.uk/)
* **Open Camera Official Source Code:** [https://sourceforge.net/p/opencamera/code/](https://sourceforge.net/p/opencamera/code/)

_________________________________

### Key Features

* **Immersive VR Mode:** The camera feed is shown in a stereoscopic side-by-side view. The application defaults to a "Hide Everything" UI state, with no on-screen buttons and menus, to provide a clean, viewfinderfor VR headsets.
* **External Hardware Control:** Fully mapped to work with wired or writeless keyboards and gamepads, allowing you to control the **zoom-levels** and **color-filters** while it is strapped to your head.
* **Experimental: Unlocking Telephoto Access:** Supports manually adding camera Lens ID to bypass OEM restrictions hiding these lenses (like telephoto and periscope cameras) from third-party apps.

____________________________
### ⌨ Keyboard & Gamepad Controls

Once your phone is mounted in the VR headset, use a connected keyboard or controller to navigate the app:

* **Up / Down Arrow Keys:** Zoom In / Zoom Out
* **Left / Right Arrow Keys:** Toggle through available color filters
* **Enter / OK Key:** Toggle lens selection (Cycle through Main, Ultrawide, and Telephoto)

_________________________________

### 📸 Manual Camera ID Support (For Telephoto Lenses)

Many smartphone manufacturers (such as Samsung and Oppo/OnePlus) restrict third-party applications from accessing auxiliary cameras, particularly telephoto lenses. OpenTelescopeVR circumvents this by allowing you to manually inject the hardware camera ID.

**How to configure your telephoto lens:**
1. Find your specific device's telephoto Camera ID (often discussed in GCam porting forums or XDA Developers for your specific phone model).
2. Open the app settings.
3. Navigate to **Settings / Manual Camera IDs**.
4. Enter the ID (e.g., `52`, `54`, or `61`) to force the app to recognize and utilize that lens.

*Note 1: Because this forces hardware access, the app may occasionally crash on the first one ot two attempts after injecting a new ID. If this happens, simply restart the app and try toggling the lens again.*

*Note 2: Manufactures such as Samsung only allow certein whitelisted application IDs (starting with com.samsung.android.OpenTeleScopeVR for Samsung) to allow accesing its telephoto lens. For telephoto support, kindly switch to repository branch specific to your device brand.*
