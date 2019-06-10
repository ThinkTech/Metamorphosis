@WebListener
class MyRequestListener extends RequestListener {
	
	def onCreate()  {
      println "request created "
    }
	
	def onDestroy()  {
      println "request destroyed "
    }
}
