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
    static def PLAYLIST_FILE_INFO_MESSAGE = 211
    static def PLAYLIST_LENGTH_MESSAGE = 124

    static def PROCESS_VM_READ = 0x0010
    static def PROCESS_VM_WRITE = 0x0020
    static def PROCESS_VM_OPERATION = 0x0008

    static def BUFFER_SIZE = 5000

    def getFileList() {

        def fileList = new File(musicDirectory)

        fileList.listFiles()
    }

    def getCurrentPlaylist() {
        //GetWindowHandle.enumWindows()

        /***** Get the Winamp window ******/
        // HWND hwndWinamp = FindWindow("Winamp v1.x", NULL);
        def winAmpHandle = GetWindowHandle.findWindow('Winamp v1.x', null)
        if (!winAmpHandle) {
            return
        }

        /***** Get the processId of Winamp ******/
        // DWORD dwProcessId;
        // GetWindowThreadProcessId(hwndWinamp, &dwProcessId);
        def processId
        GetWindowHandle.getThreadProcessId(winAmpHandle, processId);
        println "processId: $processId"

        /***** Get the Winamp process *****/
        // HANDLE hProcess = OpenProcess(PROCESS_VM_OPERATION | PROCESS_VM_READ | PROCESS_VM_WRITE, FALSE, dwProcessId);
        def process = GetWindowHandle.openProcess(PROCESS_VM_OPERATION | PROCESS_VM_READ | PROCESS_VM_WRITE, false, processId)
        if (!process) {
            return
        }

        /***** Create the buffer in winamp's process space *****/
        // LPTSTR pWinampBuf = (LPTSTR) VirtualAllocEx(hProcess, NULL, BUFSIZE, MEM_RESERVE | MEM_COMMIT, PAGE_READWRITE);
        // TODO:Pointer winampBuffer = GetWindowHandle.virtualAllocEx()

        /***** Buffer to put filename into *****/
        // TCHAR szBuf[BUFSIZE];
        def filenameBuffer = new char[BUFFER_SIZE]

        // int nPLCount = SendMessage(hwndWinamp, WM_WA_IPC, 0, IPC_GETLISTLENGTH);
        def playlistFileCount = GetWindowHandle.sendMessage(winAmpHandle, WM_WAMP, 0, PLAYLIST_LENGTH_MESSAGE)
        println "playlistFileCount: $playlistFileCount"

        // for (int i=0; i&lt;nPLCount; i++)
        for (int i=0; i < playlistFileCount; i++) {

            // LPVOID pBase = (LPVOID)::SendMessage(hwndWinamp, WM_USER, i, IPC_GETPLAYLISTFILE);
            def playlist = GetWindowHandle.sendMessage(winAmpHandle, WM_USER, i, PLAYLIST_FILE_INFO_MESSAGE)


            // ReadProcessMemory(hProcess, pBase, szBuf, sizeof(szBuf), NULL);
            GetWindowHandle.readProcessMemory(process, playlist.toPointer(), filenameBuffer, filenameBuffer.length, false.booleanValue())

            // filenameBuffer now contains the filename for entry i
            String b = new String(filenameBuffer);
            String fileName = Native.toString(filenameBuffer);
            println "b: $b"
        }


        // 0x0400 = WM_USER
        def ret = GetWindowHandle.sendMessage(winAmpHandle, WM_USER, 0, WRITE_PLAYLIST)
        GetWindowHandle.closeProcess(process)
        println ret
    }

    def addSongToPlaylist() {
/*        COPYDATASTRUCT cd;
        cd.dwData = IPC_PLAYFILE;
        cd.lpData = "d:\file.mp3";
        cd.cbData = sizeof(cd.lpData);
        SendMessage(plugin.hwndParent,WM_COPYDATA,(WPARAM)hWnd, (LPARAM)&cd);*/

    }


}


