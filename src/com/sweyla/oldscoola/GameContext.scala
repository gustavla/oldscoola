package com.sweyla.oldscoola

/**
 * A Context is a means of communication between the GameComponenet and 
 * the GameEngine. Instead of the GameEngine having access to all the
 * gory details of the GameComponent, this context is sent to GameEngine.tick(),
 * with information such as key presses and whether the game has focus.
 * 
 * The engine can also call functions that are used to communication information
 * back to the GameComponent, so that it can control switching fullscreen, 
 * pixel size, etc.
 */
class GameContext {
  var keys:Array[Boolean] = null
  var inputText:String = ""
  var fullscreen = false
  var hasFocus = false 
  
  def toggleFullscreen = {
    fullscreen = !fullscreen
  }
}