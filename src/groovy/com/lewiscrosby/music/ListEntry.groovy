package com.lewiscrosby.music

import org.codehaus.groovy.grails.web.context.ServletContextHolder as SCH
import org.codehaus.groovy.grails.web.servlet.GrailsApplicationAttributes as GA
import com.mpatric.mp3agic.Mp3File

class ListEntry {

    static def allowedExtensions = ['mp3']    // TODO: '.m4a'

    Mp3File mp3File
    String filename
    int position
    boolean isCurrent

    ListEntry (String filename, int position, boolean isCurrent) {
        this.filename = filename
        this.position = position
        this.isCurrent = isCurrent
        // TODO: Add m4a support (use http://www.jthink.net/jaudiotagger/examples_read.jsp)
        this.mp3File = new Mp3File(filename)
    }

    String getArtist() {
        tag.artist
    }

    String getTitle() {
        tag.title
    }

    String getDuration() {
        // TODO: is this available from winamp?
    }

    def getTag() {
        mp3File?.hasId3v2Tag() ? mp3File?.id3v2Tag : mp3File?.id3v1Tag
    }

}
