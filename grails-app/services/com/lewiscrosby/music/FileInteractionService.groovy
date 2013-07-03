package com.lewiscrosby.music

class FileInteractionService {

    def getFileList (String directoryPath) {

        new File(directoryPath)
    }

    def getFileExtension (String filename) {

        filename.lastIndexOf('.') ? filename.substring(filename.lastIndexOf('.')+1) : ''
    }
}
