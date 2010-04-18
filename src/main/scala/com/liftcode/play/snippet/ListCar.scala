package com.liftcode.play.snippet

import com.liftcode.play._
import model._
import net.liftweb._

import util._
import Helpers._
import http._

import scala.xml._

class ListCar {

  def list(html: NodeSeq) : NodeSeq = {
    toShow.flatMap(car =>
      bind("car", html,
           "make" -> car.make,
           "model" -> car.model,
           "manufacture_year" -> car.manufacture_year,
           FuncAttrBindParam("view_href", _ =>
          Text("view/"+ (car.primaryKeyField)),"href"),
           FuncAttrBindParam("edit_href", _ =>
          Text("edit/"+ (car.primaryKeyField)),"href"),
           FuncAttrBindParam("delete_href", _ =>
          Text("delete/"+ (car.primaryKeyField)),"href")
      )
    )
  }

  private def toShow =
  Car.findAll();

}
