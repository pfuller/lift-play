package bootstrap.liftweb

import _root_.net.liftweb.util._
import _root_.net.liftweb.http._
import _root_.net.liftweb.sitemap._
import _root_.net.liftweb.sitemap.Loc._
import Helpers._
import _root_.net.liftweb.mapper.{DB, ConnectionManager, Schemifier, DefaultConnectionIdentifier, ConnectionIdentifier}
import _root_.java.sql.{Connection, DriverManager}
import _root_.com.liftcode.play.model._
import _root_.javax.servlet.http.{HttpServletRequest}

/**
  * A class that's instantiated early and run.  It allows the application
  * to modify lift's environment
  */
class Boot {
  def boot {
    if (!DB.jndiJdbcConnAvailable_?)
      DB.defineConnectionManager(DefaultConnectionIdentifier, DBVendor)

    // where to search snippet
    LiftRules.addToPackages("com.liftcode.play")
    Schemifier.schemify(true, Log.infoF _, User, Item, Car, Attachment)

    // Build SiteMap
//    val entries = Menu(Loc("Home", List("index"),"Home")) ::
//                  User.sitemap ::: Item.menus

    val entries = Menu(Loc("Home", List("index"),"Home")) ::
                  Menu(Loc("Item", List("item") -> true, "Item", Hidden)) ::
                  Menu(Loc("Car", List("car") -> true, "Car", Hidden)) ::
                  Menu(Loc("CarList", List("car","list"), "Cars")) ::
//        Menu(Loc("CarCreate", List("car","create"), "Create Car")) ::
//        Menu(Loc("CarEdit", List("car","edit"), "Edit Car")) ::
//        Menu(Loc("CarDelete", List("car","delete"), "Delete Car")) ::
                  User.sitemap ::: Item.menus ::: Attachment.menus

    LiftRules.setSiteMap(SiteMap(entries:_*))

    // Map request to the primary key for Item to use our view
    LiftRules.rewrite.append {
        case RewriteRequest(
          ParsePath(List("item", "view", id),_,_,_),_,_) =>
            RewriteResponse("item" :: "view" :: Nil, Map("id" -> id))

        case RewriteRequest(
          ParsePath(List("car", "view", id),_,_,_),_,_) =>
            RewriteResponse("car" :: "view" :: Nil, Map("id" -> id))

        case RewriteRequest(
          ParsePath(List("car", "edit", id),_,_,_),_,_) =>
          RewriteResponse("car" :: "edit" :: Nil, Map("id" -> id))

        case RewriteRequest(
          ParsePath(List("car", action, id),_,_,_),_,_) =>
          RewriteResponse("car" :: action :: Nil, Map("id" -> id))
    }

    /*
     * Show the spinny image when an Ajax call starts
     */
    LiftRules.ajaxStart =
      Full(() => LiftRules.jsArtifacts.show("ajax-loader").cmd)

    /*
     * Make the spinny image go away when it ends
     */
    LiftRules.ajaxEnd =
      Full(() => LiftRules.jsArtifacts.hide("ajax-loader").cmd)

    LiftRules.early.append(makeUtf8)

    S.addAround(DB.buildLoanWrapper)
  }

  /**
   * Force the request to be UTF-8
   */
  private def makeUtf8(req: HttpServletRequest) {
    req.setCharacterEncoding("UTF-8")
  }
}

/**
* Database connection calculation
*/
object DBVendor extends ConnectionManager {
  private var pool: List[Connection] = Nil
  private var poolSize = 0
  private val maxPoolSize = 4

  private def createOne: Box[Connection] = try {
    val driverName: String = Props.get("db.driver") openOr
    "org.apache.derby.jdbc.EmbeddedDriver"

    val dbUrl: String = Props.get("db.url") openOr
    "jdbc:derby:lift_example;create=true"

    Class.forName(driverName)

    val dm = (Props.get("db.user"), Props.get("db.password")) match {
      case (Full(user), Full(pwd)) =>
	DriverManager.getConnection(dbUrl, user, pwd)

      case _ => DriverManager.getConnection(dbUrl)
    }

    Full(dm)
  } catch {
    case e: Exception => e.printStackTrace; Empty
  }

  def newConnection(name: ConnectionIdentifier): Box[Connection] =
    synchronized {
      pool match {
	case Nil if poolSize < maxPoolSize =>
	  val ret = createOne
        poolSize = poolSize + 1
        ret.foreach(c => pool = c :: pool)
        ret

	case Nil => wait(1000L); newConnection(name)
	case x :: xs => try {
          x.setAutoCommit(false)
          Full(x)
        } catch {
          case e => try {
            pool = xs
            poolSize = poolSize - 1
            x.close
            newConnection(name)
          } catch {
            case e => newConnection(name)
          }
        }
      }
    }

  def releaseConnection(conn: Connection): Unit = synchronized {
    pool = conn :: pool
    notify
  }
}


