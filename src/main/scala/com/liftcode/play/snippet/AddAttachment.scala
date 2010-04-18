package com.liftcode.play.snippet

import com.liftcode.play._
import com.liftcode.play.model.Attachment

import scala.xml._
import net.liftweb._
import http._
import util._
import S._
import SHtml._
import scala.xml._
import Helpers._

import java.util.Date

/* date | desc | tags | value */
class AddAttachment extends StatefulSnippet {

  val attachment = new Attachment()
  var fileHolder : Box[FileParamHolder] = Empty

    /**
  * Dispatcher, matches on send to sent the message
  **/
  val dispatch: DispatchIt = {
    case "add" => add _
  }

  def add(in: NodeSeq): NodeSeq = {
    def doSubmit() {
      // Rework to not throw exceptions
      //val currentAccount = Account.find(account).open_!

      // Add the optional receipt if it's the correct type
      val contentOk = fileHolder match {
        case Full(FileParamHolder(_, null, _, _)) => true
        case Full(FileParamHolder(_, mime, _, data)) => {
            attachment.content(data)
            true
          }
        case _ => true
      }

      if (contentOk){
        attachment.save

        // ToDo: Add code to attach to message

        notice("Attachment added!")
        unregisterThisSnippet() // dpp: remove the statefullness of this snippet
      }
    }

    bind("att", in,
         //"account" -> select(user.editable.map(acct => (acsct.id.toString, acct.name)), Empty, id => account = id.toLong),
         "attachmentId" -> text("", attachment.attachmentId(_)),
         "contentType" -> text("", attachment.contentType(_)),
         "content" -> fileUpload(fph => fileHolder = Full(fph)),
         "submit" -> SHtml.submit("Send Message", () => doSubmit()))
  }
}

