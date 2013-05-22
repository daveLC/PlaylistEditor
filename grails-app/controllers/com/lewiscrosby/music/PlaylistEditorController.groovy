package com.lewiscrosby.music

class PlaylistEditorController {

    def playlistEditorService

    def index() {

        render view: 'index'
    }

    def fileList() {

        def fileList = playlistEditorService.getFileList()

        playlistEditorService.getCurrentPlaylist()

        render (view: 'fileList', model: [fileList: fileList])
    }

    def playList() {

        def playlist = playlistEditorService.getPlaylist()

        render (view: 'fileList', model: [fileList: []])
    }
}
