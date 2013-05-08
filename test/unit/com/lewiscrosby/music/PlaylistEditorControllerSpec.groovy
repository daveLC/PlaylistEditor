package com.lewiscrosby.music

import grails.plugin.spock.ControllerSpec
import grails.test.mixin.*
import grails.test.mixin.support.GrailsUnitTestMixin
import groovy.time.TimeDuration


@TestMixin(GrailsUnitTestMixin)
@TestFor(PlaylistEditorController)
class PlaylistEditorControllerSpec extends ControllerSpec {

    void "index action should return correct view"() {

        when:
        controller.index()

        then:
        response.status == 200
        view == '/playlistEditor/index'
    }

    void "fileList action should return list of files"() {

        given:
        def fileList = [ new ListEntry (artist: 'John', title: 'Music', filename: 'john-music.mp3', duration: new TimeDuration(0,4,21,0)) ]
        def playlistEditorService = Mock(PlaylistEditorService)
        controller.playlistEditorService = playlistEditorService
        1 * controller.playlistEditorService.getFileList() >> fileList

        when:
        controller.fileList()

        then:
        response.status == 200
        view == '/playlistEditor/fileList'
        model.fileList.size() == 1
    }
}
