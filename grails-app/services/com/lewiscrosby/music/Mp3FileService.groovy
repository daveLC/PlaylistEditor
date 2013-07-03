package com.lewiscrosby.music

import com.mpatric.mp3agic.Mp3File

class Mp3FileService {

    def getMp3FileFromPath (String filename) {
        Mp3File song = new Mp3File(filename)

    }
}
