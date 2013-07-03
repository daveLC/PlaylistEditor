package com.lewiscrosby.music


class PlaylistTrack extends Track {

    int position
    boolean isCurrent

    PlaylistTrack (String filename, int position, boolean isCurrent) {
        super(filename)
        this.position = position
        this.isCurrent = isCurrent
    }
}
