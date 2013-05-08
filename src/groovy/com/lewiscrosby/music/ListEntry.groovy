package com.lewiscrosby.music

import groovy.time.TimeDuration

class ListEntry {

    def allowedExtensions = ['.mp3', '.m4a']

    String artist
    String title
    String filename
    TimeDuration duration
}
