package bootstrap.liftweb

import net.liftweb._
import util._
import Helpers._

import common._
import http._
import js._
import sitemap._
import Loc._
import net.liftmodules.JQueryModule
import net.liftweb.http.js.jquery._

/**
 * A class that's instantiated early and run.  It allows the application
 * to modify lift's environment
 */
class Boot extends Loggable {
  def boot {
    // where to search snippet
    LiftRules.addToPackages("code")

    // Build SiteMap
    val entries = List(
      Menu.i("Home") / "index", // the simple way to declare a menu

      // more complex because this menu allows anything in the
      // /static path to be visible
      Menu(Loc("Static", Link(List("static"), true, "/static/index"),
	       "Static Content")))

    // set the sitemap.  Note if you don't want access control for
    // each page, just comment this line out.
    LiftRules.setSiteMap(SiteMap(entries:_*))

    //Show the spinny image when an Ajax call starts
    LiftRules.ajaxStart =
      Full(() => LiftRules.jsArtifacts.show("ajax-loader").cmd)

    // Make the spinny image go away when it ends
    LiftRules.ajaxEnd =
      Full(() => LiftRules.jsArtifacts.hide("ajax-loader").cmd)

    // Force the request to be UTF-8
    LiftRules.early.append(_.setCharacterEncoding("UTF-8"))

    // LiftRules.liftGCPollingInterval = 10.seconds
    LiftRules.unusedFunctionsLifeTime = 10.seconds

    LiftRules.handleUnmappedParameter.default.set((req: Req, parameterName: String) => {
      if (parameterName.startsWith("F")) {
        // S.redirectTo ResponseShortcutException(() => JsonResponse(JE.Call("location.reload")), Empty, false)
        // throw ResponseShortcutException(() => JsonResponse(JE.Call("location.reload")), Empty, false)
        // throw ResponseShortcutException.shortcutResponse(JavaScriptResponse(JE.JsRaw("location.reload()").cmd))
        // throw new ResponseShortcutException(() => JavaScriptResponse(JE.JsRaw("location.reload()").cmd), Empty, false)
        throw ResponseShortcutException.shortcutResponse(JavaScriptResponse(JsCmds.Reload))

        // S.redirectTo("#/hei")
        logger.warn("Unmapped Lift-like parameter seen in request [%s]: %s".format(req.uri, parameterName))
      }
    })

    //Init the jQuery module, see http://liftweb.net/jquery for more information.
    LiftRules.jsArtifacts = JQueryArtifacts
    JQueryModule.InitParam.JQuery=JQueryModule.JQuery191
    JQueryModule.init()

  }
}