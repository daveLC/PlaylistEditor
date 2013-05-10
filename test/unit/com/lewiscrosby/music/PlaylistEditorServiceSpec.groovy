package com.lewiscrosby.music

import grails.test.mixin.TestFor
import grails.test.mixin.TestMixin
import grails.test.mixin.support.GrailsUnitTestMixin
import groovy.time.TimeDuration
import spock.lang.Specification

@TestMixin(GrailsUnitTestMixin)
@TestFor(PlaylistEditorService)
class PlaylistEditorServiceSpec extends Specification {

    void "getFileList given directory with 2 files should return list of 2 ListEntries"() {

        given:


        when:
        def result = service.getFileList()

        then:
        result.size() == 2
    }

}
