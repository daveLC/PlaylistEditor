import com.lewiscrosby.music.WinampApiService
import com.qotsa.jni.controller.WinampController

// Place your Spring DSL code here
beans = {

    winampController (WinampController)

    winampApiService (WinampApiService) {
        fileInteractionService = ref("fileInteractionService")
        winampController = ref("winampController")
    }
}
