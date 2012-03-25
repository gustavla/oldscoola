package com.sweyla.oldscoola

class SpriteFont extends SpriteAtlasSimple {
  var alphabet = ""
    
  def renderTextOnto(sprite:Sprite, x:Int, y:Int, text:String):Unit = {
    var cx = x
    text.foreach { c =>
      var index = alphabet.indexOf(c)
      if (index != -1) {
        renderCellOnto(sprite, cx, y, index)
      }
      cx += texSize
    }
  }
}

object SpriteFont {
  def load(filename:String, 
           size:Int, 
           alphabet:String, 
           maskcolor:Int = Sprite.DEFAULT_MASKCOLOR):SpriteFont = {
    var sprite = new SpriteFont
    sprite.texSize = size
    sprite.alphabet = alphabet
    sprite.load(filename, maskcolor)
    sprite
  }
}