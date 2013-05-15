package com.lewiscrosby.music

import grails.converters.JSON
import grails.test.mixin.TestFor
import grails.test.mixin.TestMixin
import grails.test.mixin.support.GrailsUnitTestMixin
import groovy.time.TimeDuration
import org.springframework.core.io.ClassPathResource
import org.springframework.core.io.Resource
import spock.lang.Specification

@TestMixin(GrailsUnitTestMixin)
@TestFor(PlaylistEditorService)
class PlaylistEditorServiceSpec extends Specification {

    void "getFileList given directory with 2 files should return list of 2 ListEntries"() {

        given:
        service.musicDirectory = 'resources/test-data'

        when:
        def result = service.getFileList()

        then:
        result.size() == 2
    }

    def getTestDir() {
        Resource resource = new ClassPathResource('resources/test-data')
        resource.getFile()
    }

}
