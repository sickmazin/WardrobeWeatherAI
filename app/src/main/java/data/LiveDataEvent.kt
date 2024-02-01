package data

data class LiveDataEvent<out T>(private val content: T){
    private var hasBeenhandled=false
    fun getContentIfNotHandled(): T?{
        return if (hasBeenhandled) {
            null
        }else{
            hasBeenhandled=true
            content
        }
    }
}
