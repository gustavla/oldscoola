package com.sweyla.oldscoola

import java.awt.Rectangle
import java.awt.image.BufferedImage
import java.io.IOException
import javax.imageio.ImageIO

class Sprite(val width:Int, val height:Int, var _pixmap:Array[Int]) {
  var cx = 0
  var cy = 0
  
  val LEFT = 0
  val DOWN = 1
  val RIGHT = 2
  val UP = 3
  val MIRROR_X = 4
  val MIRROR_Y = 5
  //var _pixmap:Array[Int]
  
  def this(w:Int, h:Int) = this(w, h, new Array[Int](w*h))
  
  def pixmap = _pixmap
  def pixmap_=(p:Array[Int]):Array[Int] = {
    assert(p.length == width * height)
    _pixmap = p
    _pixmap
  }
  
  def fill(color:Int, x:Int, y:Int, w:Int, h:Int):Unit = {
    for (i <- x.until(x+w)) {
      for (j <- y.until(y+h)) {
    	pixmap(i + j * width) = color
      }
    }
  }
  
  def clear() = fill(0)
  
  def fill(color:Int):Unit = fill(color, 0, 0, width, height)
  
  def pixelInside(x:Int, y:Int) = {
	  (x-cx) >= 0 && (x-cx) < width && (y-cy) >= 0 && (y-cy) < height
  }
  
  def blit(sprite:Sprite, x:Int, y:Int) : Unit = blit(sprite, x, y, 0)
  
  def blit(sprite:Sprite, x:Int, y:Int, rot:Int) = {
	var x0 = 0
	var y0 = 0
	var x1 = sprite.width
	var y1 = sprite.height
	
	var xx = 0
	var yy = 0
	for (i <- x0.until(x1)) {
	  for (j <- y0.until(y1)) {
		xx = i
		yy = j
		if (rot == 1 || rot == 3) {
			yy = y0+(i-x0)
			xx = x1-1-(j-y0)
		}
		if (rot >= 2 && rot <= 3) {
			xx = x1-1-(xx-x0)
			yy = y1-1-(yy-y0)
		}
		// Mirror
		if (rot == MIRROR_X) {
			xx = x1-1-(xx-x0)
		} else if (rot == MIRROR_Y) {
			yy = y1-1-(yy-y0)
		}
		
		var fx = x+i-x0
		var fy = y+j-y0
		
		var color = sprite.getPixel(xx, yy)

		overPixel(fx, fy, color);
	  }
	}
  }
  
  def overPixel(x:Int, y:Int, color:Int) = {
    var draw = false
	if (((color>>24)&0xFF) != 0 && pixelInside(x, y)) {
	  draw = true
	}
	if (draw) {	
	  var alpha = (color>>24)&0xFF;
			
	  if (alpha == 0xFF) {
		setPixel(x, y, color);
	  } else if (alpha == 0) {
		// do nothing
	  } else {
		
		var c = getPixel(x, y);
		var r0 = ((c>>16)&0xFF)/255.0;
		var g0 = ((c>>8 )&0xFF)/255.0;
		var b0 = ((c>>0 )&0xFF)/255.0;
		var a0 = ((c>>24)&0xFF)/255.0;
		
		var r1 = ((color>>16)&0xFF)/255.0;
		var g1 = ((color>>8 )&0xFF)/255.0;
		var b1 = ((color>>0 )&0xFF)/255.0;
		var a1 = (alpha)/255.0;

		// Linear interpolate it
		var r2 = r1 * a1 + r0 * a0 * (1 - a1)
		var g2 = g1 * a1 + g0 * a0 * (1 - a1)
		var b2 = b1 * a1 + b0 * a0 * (1 - a1)
		var a2 = a1 + a0 * (1 - a1);
		
		var result =
		    ((a2*0xFF).asInstanceOf[Int]<<24) | 
		    ((r2*0xFF).asInstanceOf[Int]<<16) | 
		    ((g2*0xFF).asInstanceOf[Int]<<8) |
		    ((b2*0xFF).asInstanceOf[Int])
		    
		setPixel(x, y, result);
	  }
	}
  }
	
  def getPixel(x:Int, y:Int):Int = getPixel(x, y, 0, 0)
  def getPixel(x:Int, y:Int, ox:Int, oy:Int) = {
	pixmap((ox+x-cx) + (oy+y-cy)*width)
  }
  
  def setPixel(x:Int, y:Int, color:Int):Unit = {
    if (pixelInside(x, y)) {
	  pixmap((x-cx) + (y-cy)*width) = color
	}
  }
}

// Some static functions
object Sprite {
  def load(filename:String):Sprite = load(filename, 0xffff40ff)
  def load(filename:String, maskcolor:Int):Sprite = {
	var img:BufferedImage = null
	try {
	  img = ImageIO.read(getClass.getResource(filename))
	} catch {
	  case e:IOException => e.printStackTrace(); return null
	}
	
	var sprite = new Sprite(img.getWidth, img.getHeight)
	img.getRGB(0, 0, img.getWidth(), img.getHeight(), sprite.pixmap, 0, img.getWidth());
	sprite.pixmap = sprite.pixmap.map((c) => if (c == maskcolor) 0 else c)
	sprite
  }
}