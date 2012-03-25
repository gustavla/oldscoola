package com.sweyla.oldscoola

// This class if for sprite atlases that have an even square grid
class SpriteAtlasSimple extends Sprite {
  var texSize:Int = 0
  
  /**
   * Renders only one of the square texture elements onto
   * another sprite.
   * 
   * sprite			destination sprite
   * x, y			position in destination sprite
   * texi			texture index. Starts from top-left and 
   * 				reads like a western book.
   */
  def renderCellOnto(sprite:Sprite, x:Int, y:Int, texi:Int, rot:Int = 0):Unit = {

	val x0 = (texi%10) * texSize
	val x1 = x0 + texSize
	val y0 = (texi/10) * texSize
	val y1 = y0 + texSize
	
	renderOnto(sprite, x, y, x0, y0, x1, y1, rot)
  }
}

object SpriteAtlasSimple {
  def load(filename:String, texSize:Int, maskcolor:Int = Sprite.DEFAULT_MASKCOLOR) = { 
    var sprite = new SpriteAtlasSimple
    sprite.load(filename, maskcolor)
    sprite.texSize = texSize
    sprite
  }
}