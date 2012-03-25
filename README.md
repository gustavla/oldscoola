# oldscoola

Lightweight skeleton code for making low-res old school games in Scala. Perfect for game contests such as Ludum Dare (although don't forget to announce your base code).

## Features

 * Low resolution canvas with full control of pixels
 * Steady game loop at 60 fps
 * Basic keyboard input
 * Image loading
 * Toggle fullscreen mode
 * Very primitive sprite atlas system
 * Very primitive font system

## To come

 * Better support for changing things such as fullscreen or pixel size from the Engine
 * Basic texture handling (but you can also do this manually, if you prefer)

## Getting started

Use can choose to use it either as a library or as skeleton code. These description are specific for Eclipse, but it shouldn't be much different on another setup.

### Skeleton code

Check out the repo into your Eclipse folder. Open the project and do Refactor -> Rename on the project and the package. Now, remove or modify Main.scala to build your project.

### Library

If you want to use it as a library, then check out the repo into your Eclipse folder. Now, create a new project. Add a file similar to Main.scala, except at the top, add

    import com.sweyla.oldscoola._

Now, menu click on your project and then Properties. In Java Build Path -> Projects, click the "Add..." button and select oldscoola. Click OK and try running Main.scala.
