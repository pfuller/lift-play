package com.liftcode.play.snippet

import com.liftcode.play._
import model._
import net.liftweb._
import http._

import SHtml._
import S._
import mapper._
import util._
import Helpers._

import scala.xml.{NodeSeq, Text}

class EditCar {

  var id = S.param("id") openOr ""

  var car = try {
    Car.findByKey(id.toLong)
  } catch {
    case e:NumberFormatException => Empty
  }

   def edit (html: NodeSeq): NodeSeq ={
   car map ({ c =>
   c.toForm(Full("save"), "/car/list")
   }) openOr Text("Invalid Car")
   }
   
}
