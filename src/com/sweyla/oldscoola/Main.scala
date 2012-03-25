package com.sweyla.oldscoola

import scala.util.Random
import java.awt.event.KeyEvent

// Example of an engine
class Engine extends GameEngine {
  var alphabet = SpriteFont.load("/sprites/alphabet.png", 10, 
      "ABCDEFGHIJKLMNOPQRSTUVWXYZ.,!?0123456789-+(@#$%[ ")
  var x = 0
  
  override def tick(ctx:GameContext) = {
    if (ctx.keys(KeyEvent.VK_LEFT)) {
      x -= 1
    }
    if (ctx.keys(KeyEvent.VK_RIGHT)) {
      x += 1
    }
    /*
    if (keys(KeyEvent.VK_Q)) {
      alive = false
    }*/
  }
  
  override def render(canvas:Sprite, has_focus:Boolean) = {
    for (i <- 0.until(canvas.width)) {
      for (j <- 0.until(canvas.height)) {
        canvas.setPixel(i, j, Random.nextInt(100))
      }
    }
    
    canvas.fill(0xFF0000FF, 10+x, 10, 50, 50)
    //alphabet.renderTexOnto(canvas, 0, 0, 3)
    alphabet.renderTextOnto(canvas, 5, 5, "HELLO")
  }
}

object Main {
  def main(args:Array[String]) = {
    var engine = new Engine
    var component = new GameComponent(engine, 160, 120, 4)
    component.launch
  }
}