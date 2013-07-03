package com.lewiscrosby.music

import com.qotsa.exception.InvalidHandle
import com.qotsa.exception.InvalidParameter
import com.qotsa.jni.controller.WinampController

class WinampApiService {

    def fileInteractionService
    WinampController winampController

    static def musicDirectory = "${System.properties.'user.home'}\\music"

    def getMusicFileList() {

        // TODO: change these into music files
        def fileList = fileInteractionService.getFileList (musicDirectory)
        fileList.listFiles()
    }

    def getMusicTracks() {

        def tracks = []

        def fileList = fileInteractionService.getFileList (musicDirectory)

        fileList.listFiles().each { File file ->
            if (isValidMusicFile(file.path)) {
                tracks << new Track(file.path)
            }
        }

        tracks
    }

    def getCurrentPlaylist() {

        def playlistFiles = []

        try {
            def playlistLength = winampController.getPlayListLength()

            for (int i = 0; i< playlistLength; i++) {
                addPlayListFileToList (playlistFiles, i)
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

    def addFileToPlaylist (String filename) {

        // TODO: Clicking on a directory adds all files in that dir - could be useful
        try {
            winampController.appendToPlayList (filename)
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
    }

    def addPlayListFileToList (List playListFiles, int position) {

        def filename = winampController.getFileNameInList(position)

        if (isValidMusicFile(filename)) {
            def isCurrent = winampController.getListPos() == position
            playListFiles << new PlaylistTrack(filename, position, isCurrent)
        }
    }

    boolean isValidMusicFile (String filename) {

        ListEntry.allowedExtensions.contains(fileInteractionService.getFileExtension(filename))
    }
}
