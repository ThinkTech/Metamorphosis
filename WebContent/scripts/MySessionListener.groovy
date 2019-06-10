@WebListener
class MySessionListener extends SessionListener {

   def onCreate() {
       println "session created"                               
   }

   def onDestroy() {
       println "session destroyed"	                                      
   }
          
}
