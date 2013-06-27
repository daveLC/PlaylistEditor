package com.lewiscrosby.music

import com.qotsa.jni.controller.WinampController

class ApiController {

    def index() {

        WinampController winampController = new WinampController()

        winampController.run()
        def playlistLength = winampController.getPlayListLength()

        for (int i = 0; i< playlistLength; i++) {
            def file = winampController.getFileNameInList(i)
            println file
        }

        render "winamp starting..."
    }
}
