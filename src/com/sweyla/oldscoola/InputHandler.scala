package com.sweyla.oldscoola

import java.awt.event.KeyListener
import java.awt.event.FocusListener
import java.awt.event.KeyEvent
import java.awt.event.FocusEvent

class InputHandler extends KeyListener with FocusListener {
  var keys = new Array[Boolean](1<<16)
  
  def keyTyped(arg0: KeyEvent): Unit = {}

  def keyPressed(arg0: KeyEvent): Unit = {
	val c = arg0.getKeyCode
	if (c >= 0 && c < keys.length) {
	  keys(c) = true
	}
  }

  def keyReleased(arg0: KeyEvent): Unit = {
    val c = arg0.getKeyCode
	if (c >= 0 && c < keys.length) {
	  keys(c) = false
	}
  }

  def focusGained(arg0: FocusEvent): Unit = {}
  def focusLost(event: FocusEvent): Unit = {}
}