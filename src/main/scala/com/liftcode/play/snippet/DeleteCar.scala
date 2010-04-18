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

class DeleteCar {

var id = S.param("id") openOr ""

    var car = try {
        Car.findByKey(id.toLong)
    } catch {
        case e:NumberFormatException => Empty
    }

    def delete (html: NodeSeq): NodeSeq ={
        car map ({ c =>
            def deleteCar() :Unit = {
                c.delete_!
                S.redirectTo("/car/list")
            }
            bind("car", html,
                 "make" -> c.make,
                 "model" -> c.model,
                 "manufacture_year" -> c.manufacture_year,
                 "submit" -> submit("Delete", deleteCar )
            )
        }) openOr Text("Invalid Car")
    }
}

