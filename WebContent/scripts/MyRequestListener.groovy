@RequestListener
class MyRequestListener {
	
	def onCreate()  {
      println "request created "
    }
	
	def onDestroy()  {
      println "request destroyed "
    }
}
