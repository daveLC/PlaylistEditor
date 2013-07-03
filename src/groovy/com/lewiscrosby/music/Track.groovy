package com.lewiscrosby.music

import com.mpatric.mp3agic.Mp3File


class Track {

    static def allowedExtensions = ['mp3']    // TODO: '.m4a'

    Mp3File mp3File
    String filename

    Track (String filename) {
        this.filename = filename
        // TODO: Add m4a support (use http://www.jthink.net/jaudiotagger/examples_read.jsp)
        try {
            this.mp3File = new Mp3File(filename)
        }
        catch (InvalidDataException) {
            println "InvalidDataException importing file: $filename"
        }
    }

    String getArtist() {
        // TODO: fallback to filename if no tags exist
        tag?.artist
    }

    String getTitle() {
        // TODO: fallback to filename if no tags exist
        tag?.title
    }

    String getDuration() {
        // TODO: is this available from winamp?
    }

    def getTag() {
        mp3File?.hasId3v2Tag() ? mp3File?.id3v2Tag : mp3File?.hasId3v1Tag() ? mp3File?.id3v1Tag : null
    }

}
