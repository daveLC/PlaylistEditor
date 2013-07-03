package com.lewiscrosby.music

class WinampApiController {

    def winampApiService

    def index() {

        def trackList = winampApiService.getMusicFileList()
        def playList = winampApiService.getCurrentPlaylist()

        [trackList: trackList, playList: playList]
    }

    def addToPlaylist (String filename) {

        println "adding file: $filename"

        winampApiService.addFileToPlaylist (filename)

        redirect (action: "index")
    }
}
