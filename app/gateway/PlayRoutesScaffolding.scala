package gateway

trait PlayRoutesScaffolding {

  def routesHeader(): String =
    s"""
       |# Routes
       |# This file defines all application routes (Higher priority routes first)
       |
       |""".stripMargin


  def routesFooter(cntPkgName: String): String =
    s"""
       |
       |# Map static resources from the /public folder to the /assets URL path
       |GET     /assets/*file   $cntPkgName.Assets.versioned(path="/public", file: Asset)
       |
       |""".stripMargin

  def routesPostRoute(cntPkgName: String, controllerName: String, path: String, method: String): String =
    s"""
       |POST  $path  $cntPkgName.$controllerName.$method()
       |
       |""".stripMargin


  def routesGetRoute(cntPkgName: String, controllerName: String, path: String, method: String,
    pathParams: Map[String, String], queryParams: Map[String, String]): String =
    s"""
       |GET $path  $cntPkgName.$controllerName.$method(${pathParams.map { case (param, tp) => s"$param: $tp" }.mkString(", ")}, ${queryParams.map { case (param, tp) => s"$param: $tp" }.mkString(", ")})
       |
       |""".stripMargin

}
