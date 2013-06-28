package com.lewiscrosby.music

import com.mpatric.mp3agic.Mp3File
import groovy.time.TimeDuration

class ListEntry {

    def allowedExtensions = ['.mp3', '.m4a']

    Mp3File mp3File
    String filename

    ListEntry (String filename) {
        this.filename = filename
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
        mp3File.hasId3v2Tag() ? mp3File.id3v2Tag : mp3File.id3v1Tag
    }
}
