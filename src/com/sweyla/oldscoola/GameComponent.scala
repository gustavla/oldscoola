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
import java.awt.GraphicsEnvironment
import java.awt.event.WindowAdapter
import java.awt.event.WindowEvent
import java.awt.event.KeyEvent
import java.awt.DisplayMode

trait GameEngine {
  var alive = true
  def tick(keys:Array[Boolean], has_focus:Boolean) : Unit
  def render(canvas:Sprite, has_focus:Boolean) : Unit
}

class GameComponent(
    var engine:GameEngine, 
    val WIDTH:Int, 
    val HEIGHT:Int, 
    var _pixelsize:Int) 
  extends Canvas with Actor 
{
  var pixelsize:Int = 0
  setPixelSize(_pixelsize)
  var offsetX = 0
  var offsetY = 0
  var running = false
  var buffer = new BufferedImage(WIDTH, HEIGHT, BufferedImage.TYPE_INT_RGB)
  var dbi:DataBufferInt = buffer.getRaster().getDataBuffer().asInstanceOf[DataBufferInt]
  var canvas = new Sprite(WIDTH, HEIGHT)
  var pixmap = dbi.getData()
  val inputHandler = new InputHandler
  addKeyListener(inputHandler)
  var panel = new JPanel(new BorderLayout())
  var frame = new JFrame("Lo-Res Game!")
  val device = GraphicsEnvironment.getLocalGraphicsEnvironment.getDefaultScreenDevice
  var oldDispMode:DisplayMode = null 
  var fullscreen = false
  
  def launch: Unit = {  
	panel.add(this, BorderLayout.CENTER)
	frame.setContentPane(panel)
	frame.pack
	frame.setLocationRelativeTo(null)
	frame.setResizable(false)
	frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE)
    frame.setVisible(true)
    running = true
    //GraphicsEnvironment.getLocalGraphicsEnvironment.getDefaultScreenDevice.setFullScreenWindow(frame)    
    start
  }
  
  def toggleFullscreen = {
    setFullscreen(!fullscreen)
  }
  
  def setPixelSize(size:Int) {
    pixelsize = size
    var d = new Dimension(pixelsize * WIDTH, pixelsize * HEIGHT)
    setSize(d)
    setMaximumSize(d)
  	setMinimumSize(d)
  	setPreferredSize(d)
  	
  	if (fullscreen) {
  	  offsetX = oldDispMode.getWidth/2 - canvas.width*pixelsize/2
  	  offsetY = oldDispMode.getHeight/2 - canvas.height*pixelsize/2
  	} else {
  	  offsetX = 0
  	  offsetY = 0
  	}
  }
  
  def setFullscreen(fs:Boolean) = {
    println("Setting fullscreen to " + fs)
    (fullscreen, fs) match {
      case (false, true) => // Switching to fullscreen
        fullscreen = true
        oldDispMode = device.getDisplayMode
        frame.setVisible(false)
        frame.dispose
        frame.setUndecorated(true)
        //frame.setSize(oldDispMode.getWidth, oldDispMode.getHeight)
        device.setFullScreenWindow(frame)
        
        // Calculate the maximum possible pixel size
        var ps:Int = 
          (oldDispMode.getHeight / canvas.height) min
          (oldDispMode.getWidth / canvas.width)
        setPixelSize(ps)
        
        
        frame.setVisible(true)
        frame.requestFocus
        requestFocus
        
      case (true, false) => // Restoring
        fullscreen = false
        if (oldDispMode != null) {
          device.setDisplayMode(oldDispMode)
        }
        frame.setVisible(false)
        frame.dispose
        frame.setUndecorated(false)
        device.setFullScreenWindow(null)
        //setSize(d)
        setPixelSize(4)
        frame.setLocationRelativeTo(null);
        offsetX = 0
        offsetY = 0
        frame.setVisible(true)
        frame.requestFocus
        requestFocus
      
      case _ => // do nothing
    }
    
  }
	
  override def act:Unit = {
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
        if (!tick) {
          return
        }
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
  
  def tick:Boolean = {
    engine.tick(inputHandler.keys, hasFocus)
    
    // This is temporary, the Engine should be able to have a
    // signal back to the component, so these things can be done
    if (inputHandler.keys(KeyEvent.VK_F)) {
      inputHandler.keys(KeyEvent.VK_F) = false
      toggleFullscreen
    }
    
    // This doesn't work yet, the frame needs to be resized too
    /*
    if (inputHandler.keys(KeyEvent.VK_P)) {
      inputHandler.keys(KeyEvent.VK_P) = false
      setPixelSize(pixelsize + 1)
    }
    
    if (inputHandler.keys(KeyEvent.VK_O)) {
      inputHandler.keys(KeyEvent.VK_O) = false
      setPixelSize(pixelsize - 1)
    }
    */
    engine.alive
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
	  g.drawImage(buffer, offsetX, offsetY, WIDTH * pixelsize, HEIGHT * pixelsize, null)
	  g.dispose()
	  bs.show()
	}
  }
}