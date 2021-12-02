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

  def routesRoute(cntPkgName: String, controllerName: String, method: String): String =
    s"""
       |POST  /$method  $cntPkgName.$controllerName.$method()
       |
       |""".stripMargin

}
