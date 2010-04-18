package com.liftcode.play.model

import net.liftweb._
import mapper._
import util._

class Item extends LongKeyedMapper[Item] with IdPK with CRUDify [Long, Item]{
  def getSingleton = Item
  object name extends MappedPoliteString(this, 128)
  object amount extends MappedInt(this)
}

object Item extends Item with LongKeyedMetaMapper[Item]with CRUDify [Long, Item]{
  override def viewMenuLoc = Empty
}
