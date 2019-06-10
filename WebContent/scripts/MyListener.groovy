@WebListener
class MyListener extends RequestListener {
	
	def init()  {
      println "request initialized "+request
    }
	

}
