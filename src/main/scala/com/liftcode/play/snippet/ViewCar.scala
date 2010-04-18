package com.liftcode.play.snippet

import com.liftcode.play._
import model._
import net.liftweb._

import util._
import Helpers._
import http._

import scala.xml._

class ViewCar {

    var id = S.param("id") openOr ""

    var car = try {
        Car.findByKey(id.toLong)
    } catch {
        case e:NumberFormatException => Empty
    }

    def view(html : NodeSeq): NodeSeq = {
        car map ({ c =>
            bind("car", html,
                 "make" -> c.make,
                 "model" -> c.model,
                 "manufacture_year" -> c.manufacture_year
            )
        }) openOr Text("Invalid Car")

    }
}

