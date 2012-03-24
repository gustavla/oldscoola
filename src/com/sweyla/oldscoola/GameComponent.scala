package com.sweyla.oldscoola

import java.awt.Canvas
import java.awt.BorderLayout
import java.awt.Canvas
import java.awt.Dimension
import java.awt.Graphics
import java.awt.image.BufferStrategy
import java.awt.image.BufferedImage
import java.awt.image.DataBufferInt
import java.awt.image.DataBuffer

import java.lang.Runnable

import javax.swing.JFrame
import javax.swing.JPanel

import scala.actors.Actor
import scala.actors.Actor._

trait GameEngine {
  def tick(keys:Array[Boolean], has_focus:Boolean) : Unit
  def render(canvas:Sprite, has_focus:Boolean) : Unit
}

class GameComponent(var engine:GameEngine) extends Canvas with Actor 
{
  val WIDTH = 160
  val HEIGHT = 120
  val PIXELSIZE = 3
  var d = new Dimension(WIDTH * PIXELSIZE, HEIGHT * PIXELSIZE)
  var running = false
  var buffer = new BufferedImage(WIDTH, HEIGHT, BufferedImage.TYPE_INT_RGB)
  //var thread:Thread
  
  setSize(d)
  setMaximumSize(d)
  setMinimumSize(d)
  setPreferredSize(d)
  
  var dbi:DataBufferInt = buffer.getRaster().getDataBuffer().asInstanceOf[DataBufferInt]
		
  var canvas = new Sprite(WIDTH, HEIGHT)
  var pixmap = dbi.getData()

  //canvas.fill(0xFF00FFFF)	
  
  def launch: Unit = {  
	var frame = new JFrame("Lo-Res Game!")
	var panel = new JPanel(new BorderLayout())
	panel.add(this, BorderLayout.CENTER)
	frame.setContentPane(panel)
	frame.pack
	frame.setLocationRelativeTo(null)
	frame.setResizable(false)
	frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE)
    frame.setVisible(true)
    running = true
    start
  }
	
  override def act() = {
    var lastTime = System.nanoTime
    val hz = 1.0/60.0
    val second = 1000000000L
	var ran = false
		
	var seconds = 0.0
	var frames = 0
	var lastWholeSecond = lastTime
		
	requestFocus()
    
    while (running) {
      var curTime = System.nanoTime
      var diff = curTime - lastTime
      lastTime = curTime
      seconds += diff/second.asInstanceOf[Double]
      ran = false
      
      while (seconds > hz) {
        tick
        ran = true
        seconds -= hz
      }
      
      ran match {
        case true =>
          render
          frames += 1
          if (curTime -lastWholeSecond > second) {
            println(frames + " fps")
            lastWholeSecond += second
            frames = 0
          }
      
        case false =>
          try {
        	Thread.sleep(1)
          } catch {
          case e:Exception => e.printStackTrace
          }
      }
    }
  }
  
  def tick = {
    //engine.tick(inputHandler.getKeys, hasFocus)
    engine.tick(Array[Boolean](), hasFocus)
  }
  
  def render() = {
    var bs = getBufferStrategy()
	if (bs == null) {
	  createBufferStrategy(3)
	} else {
	  // TODO: This should be a setting
	  canvas.clear()
			
	  engine.render(canvas, hasFocus)
			
	  for (i <- 0.until(WIDTH)) {
		for (j <- 0.until(HEIGHT)) {
		  pixmap(i + j*WIDTH) = 0xFFFFFF & canvas.getPixel(i, j)
		}
	  }
			
	  var g = bs.getDrawGraphics()
	  g.fillRect(0, 0, getWidth(), getHeight())
	  g.drawImage(buffer, 0, 0, WIDTH * PIXELSIZE, HEIGHT * PIXELSIZE, null)
	  g.dispose()
	  bs.show()
	}
  }
}