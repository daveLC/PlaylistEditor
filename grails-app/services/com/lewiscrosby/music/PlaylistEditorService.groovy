package com.lewiscrosby.music

class PlaylistEditorService {

    def musicDirectory = 'C:\\Users\\davidlewis-crosby\\Music'

    def getFileList() {

        def fileList = new File(musicDirectory)

        fileList.listFiles()
    }
}


