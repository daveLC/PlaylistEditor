package com.lewiscrosby.music

class WinampApiController {

    def winampApiService

    def index() {

        def fileList = winampApiService.getMusicFileList()
        def playList = winampApiService.getCurrentPlaylist()

        [fileList: fileList, playList: playList]
    }
}
