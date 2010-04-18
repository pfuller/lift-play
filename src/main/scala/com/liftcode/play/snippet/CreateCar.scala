package com.liftcode.play.snippet

import com.liftcode.play._
import model._
import net.liftweb._
import http._

import SHtml._
import S._
import util._

import scala.xml._

class CreateCar {

    def create(html : NodeSeq): NodeSeq = {
      val car = new Car()
      car.toForm(Full("Create Car"), {_.save})
    }
    
}
