package com.lewiscrosby.music

import com.lewiscrosby.jna.GetWindowHandle
import com.sun.jna.Native
import com.sun.jna.Pointer
import com.sun.jna.platform.win32.User32
import com.sun.jna.platform.win32.WinDef
import com.sun.jna.platform.win32.WinUser

class PlaylistEditorService {

    def musicDirectory = 'C:\\Users\\davidlewis-crosby\\Music'

    static def WM_USER = 0x0400
    static def WM_WAMP = 0x400
    static def CURRENT_PLAYBACK_STATUS = 104
    static def CURRENT_VERSION = 0
    static def WRITE_PLAYLIST = 120
    static def PLAYLIST_FILE_INFO = 211

    def getFileList() {

        def fileList = new File(musicDirectory)

        fileList.listFiles()
    }

    def getCurrentPlaylist() {
        GetWindowHandle.enumWindows()
        def winAmpHandle = GetWindowHandle.findWindow('Winamp v1.x', null)

        //TCHAR szBuf[BUFSIZE];
        char[] filenameBuffer = new char[512]

        //int nPLCount = SendMessage(hwndWinamp, WM_WA_IPC, 0, IPC_GETLISTLENGTH);
        int playlistFileCount = GetWindowHandle.sendMessage(winAmpHandle, WM_WAMP, 0, filenameBuffer.length)

        //for (int i=0; i&lt;nPLCount; i++) {
        for (int i=0; i < playlistFileCount; i++) {
        //    LPVOID pBase = (LPVOID)::SendMessage(hwndWinamp, WM_USER, i, IPC_GETPLAYLISTFILE);
            def ret = GetWindowHandle.sendMessage(winAmpHandle, WM_USER, i, PLAYLIST_FILE_INFO)
            println ret
        //    ReadProcessMemory(hProcess, pBase, szBuf, sizeof(szBuf), NULL);
            // szBuf now contains the filename for entry i
        //}
        }


        // 0x0400 = WM_USER
        def ret = GetWindowHandle.sendMessage(winAmpHandle, WM_USER, 0, WRITE_PLAYLIST)
        println ret
    }


}


