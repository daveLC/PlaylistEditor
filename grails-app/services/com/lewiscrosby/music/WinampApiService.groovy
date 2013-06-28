package com.lewiscrosby.music

import com.qotsa.exception.InvalidHandle
import com.qotsa.exception.InvalidParameter
import com.qotsa.jni.controller.WinampController

class WinampApiService {

    def fileInteractionService
    def mp3FileService

    static def musicDirectory = "${System.properties.'user.home'}\\music"

    def getMusicFileList() {

        def fileList = fileInteractionService.getFileList (musicDirectory)
        fileList.listFiles()
    }

    def getCurrentPlaylist() {

        def playlistFiles = []

        WinampController winampController = new WinampController()

        try {
            def playlistLength = winampController.getPlayListLength()

            for (int i = 0; i< playlistLength; i++) {
                def listEntry = new ListEntry(winampController.getFileNameInList(i))
                playlistFiles << listEntry
            }
        }
        catch (InvalidHandle invalidHandle) {
            log.info("Winamp not running, please start Winamp to use the Playlist Editor")
        }
        catch (InvalidParameter invalidParameter) {
            log.info("No entry at position: $invalidParameter")
        }
        catch (e) {
            e.printStackTrace()
            log.info("An error occurred, please try again")
        }

        playlistFiles
    }
}
