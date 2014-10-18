package code.snippet

import scala.xml._
import scala.collection.mutable.{Map}

import net.liftweb._
import net.liftweb.util._
import Helpers._
import net.liftweb.json._
import net.liftweb.common._
import net.liftweb.http._
import SHtml._
import js._
import js.JsCmds._

import code.comet.ChatServer

class AngularSnippet {
  private implicit val formats = DefaultFormats

	def sayHi(in: JValue): JValue = {
		println("sayHi... " + in)
		for {
			text <- tryo(in.extract[String])
		} {
			println("text: " + text)
			ChatServer ! text
		}
		JBool(false)
	}

	def render(in: NodeSeq): NodeSeq = {
    for (sess <- S.session) {
  	  val script = JsCrVar("angularBackend", sess.buildRoundtrip(List[RoundTripInfo](
      "sayHi" -> sayHi _)))
	    S.appendGlobalJs(script)
	    S.appendGlobalJs(ajaxFailure)
  	}
  	in
  }

  private val ajaxFailure = Function(name = "ajaxFailed", params = Nil, body = JE.JsRaw("""
  	location.reload();
  """))
}