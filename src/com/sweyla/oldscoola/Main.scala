package com.sweyla.oldscoola

import scala.util.Random

// Example of an engine
class Engine extends GameEngine {
  var x = 0
  
  override def tick(keys:Array[Boolean], has_focus:Boolean) = {
    x = (x + 1)%50
  }
  
  override def render(canvas:Sprite, has_focus:Boolean) = {
    for (i <- 0.until(canvas.width)) {
      for (j <- 0.until(canvas.height)) {
        canvas.setPixel(i, j, Random.nextInt())
      }
    }
    
    canvas.fill(0xFF0000FF, 10+x, 10, 50, 50)
  }
}

object Main {
  def main(args:Array[String]) = {
    var engine = new Engine
    var component = new GameComponent(engine)
    component.launch
  }
}