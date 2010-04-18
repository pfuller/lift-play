package com.liftcode.play.model

import net.liftweb._
import mapper._
import util._

class Car extends LongKeyedMapper[Car] with IdPK {
  def getSingleton = Car

  object make extends MappedString(this, 64)
  
  object model extends MappedString(this, 64)

  object manufacture_year extends MappedInt(this)
}

/**
 * Car companion object
 **/
object Car extends Car with LongKeyedMetaMapper[Car] //with CRUDify[Long, Car]
{
  override def fieldOrder = List(make, model, manufacture_year)

  // override def showAllMenuLoc = Empty
  // override def createMenuLoc = Empty
  // override def viewMenuLoc = Empty
}
