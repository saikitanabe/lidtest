package code
package comet
import net.liftweb._
import http._
import util._
import Helpers._
import js._
import net.liftweb.common._
import net.liftweb.json._

/**
 * The screen real estate on the browser will be represented
 * by this component.  When the component changes on the server
 * the changes are automatically reflected in the browser.
 */
class Chat extends CometActor with CometListener with Loggable {
  private var msgs: Vector[String] = Vector() // private state
  /**
   * When the component is instantiated, register as
   * a listener with the ChatServer
   */
  def registerWith = ChatServer

  override def autoIncludeJsonCode = true

  /**
   * The CometActor is an Actor, so it processes messages.
   * In this case, we're listening for Vector[String],
   * and when we get one, update our private state
   * and reRender() the component.  reRender() will
   * cause changes to be sent to the browser.
   */
  override def lowPriority = {
    case v: Vector[String] => msgs = v; reRender()
  }
  /**
   * Put the messages in the li elements and clear
   * any elements that have the clearable class.
   */
  def render = {
    "@sendMessage" #> JsCmds.Script(scripts) & 
    "li *" #> msgs & ClearClearable
  }

  override def receiveJson = {
    case JObject(JField("command", JString(command)) :: 
                 JField("params", JArray(List(JString(msg)))) :: Nil) if command == "sendMessage" => {
      sendMessage(msg)
    }
    case x => {
      logger.error("Operation failed: " + x + "")
      if (logger.isDebugEnabled) {
        JsCmds.Alert("Sorry, " + x)
      }
    }
  }

  private def sendMessage(msg: String) = {
    ChatServer ! msg
  }

  private def scripts = JE.JsRaw("""
    function sendMessage(msg) {
    """ + jsonSend("sendMessage", JE.JsArray(JE.JsRaw("msg"))).toJsCmd + """
    }
    """).cmd
}