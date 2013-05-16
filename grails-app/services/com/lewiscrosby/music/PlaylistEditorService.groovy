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
    static def PLAYLIST_LENGTH = 124

    static def PROCESS_VM_READ = 0x0010

    def getFileList() {

        def fileList = new File(musicDirectory)

        fileList.listFiles()
    }

    def getCurrentPlaylist() {
        GetWindowHandle.enumWindows()
        def winAmpHandle = GetWindowHandle.findWindow('Winamp v1.x', null)

        //GetWindowThreadProcessId(hwndWinamp, &dwProcessId);
        int processId = GetWindowHandle.getThreadProcessId(winAmpHandle, null);
println "processId: $processId"
        //HANDLE hProcess = OpenProcess(PROCESS_VM_OPERATION | PROCESS_VM_READ | PROCESS_VM_WRITE, FALSE, dwProcessId);
        Pointer process = GetWindowHandle.openProcess(PROCESS_VM_READ, false, processId)
if (!process) {
    return
}
        //TCHAR szBuf[BUFSIZE];
        char[] filenameBuffer = new char[512]

        //int nPLCount = SendMessage(hwndWinamp, WM_WA_IPC, 0, IPC_GETLISTLENGTH);
        WinDef.LRESULT playlistFileCount = GetWindowHandle.sendMessage(winAmpHandle, WM_WAMP, 0, PLAYLIST_LENGTH)
println "playlistFileCount: $playlistFileCount"

        //for (int i=0; i&lt;nPLCount; i++) {
        for (int i=0; i < playlistFileCount; i++) {
        //    LPVOID pBase = (LPVOID)::SendMessage(hwndWinamp, WM_USER, i, IPC_GETPLAYLISTFILE);
            char[] playlist = GetWindowHandle.sendMessage(winAmpHandle, WM_USER, i, PLAYLIST_FILE_INFO) as char[]
            println playlist

        //    ReadProcessMemory(hProcess, pBase, szBuf, sizeof(szBuf), NULL);
      //public static boolean readProcessMemory(Pointer hProcess, byte[] lpBaseAddress, byte[] lpBuffer, int nSize, byte[] lpNumberOfBytesRead) {

            GetWindowHandle.readProcessMemory(process, playlist, filenameBuffer, filenameBuffer.length.intValue(), false.booleanValue())

            // filenameBuffer now contains the filename for entry i
            println "filenameBuffer: $filenameBuffer"
        //}
        }


        // 0x0400 = WM_USER
        def ret = GetWindowHandle.sendMessage(winAmpHandle, WM_USER, 0, WRITE_PLAYLIST)
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


