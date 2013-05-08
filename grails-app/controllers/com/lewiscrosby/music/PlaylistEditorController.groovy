package com.lewiscrosby.music

class PlaylistEditorController {

    def playlistEditorService

    def index() {

        render view: 'index'
    }

    def fileList() {

        def fileList = playlistEditorService.getFileList()

        render (view: 'fileList', model: [fileList: fileList])
    }
}
