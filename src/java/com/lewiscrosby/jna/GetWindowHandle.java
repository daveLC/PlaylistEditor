package com.lewiscrosby.jna;

import com.sun.jna.*;
import com.sun.jna.platform.win32.WinDef.HWND;
import com.sun.jna.platform.win32.WinUser;
import com.sun.jna.win32.*;

public class GetWindowHandle {

    public interface User32 extends StdCallLibrary {
        User32 INSTANCE = (User32) Native.loadLibrary("user32", User32.class, W32APIOptions.DEFAULT_OPTIONS);

        boolean EnumWindows(WinUser.WNDENUMPROC lpEnumFunc, Pointer arg);
        int GetWindowTextA(HWND hWnd, byte[] lpString, int nMaxCount);
        int GetClassName(HWND hwnd, char[] lpString, int nMaxCount);
        HWND FindWindow(String lpClassName, String lpWindowName);
        int SendMessage(HWND hWnd, int msg, int num1, int num2);
    }

    public static void enumWindows() {
        final User32 user32 = User32.INSTANCE;
        user32.EnumWindows(new WinUser.WNDENUMPROC() {
            int count = 0;
            @Override
            public boolean callback(HWND hWnd, Pointer arg1) {
                byte[] windowText = new byte[512];

                user32.GetWindowTextA(hWnd, windowText, 512);
                String wText = Native.toString(windowText);

                char[] classNameText = new char[512];
                user32.GetClassName(hWnd, classNameText, classNameText.length);
                String cText = Native.toString(classNameText);

                if (wText.isEmpty()) {
                    return true;
                }

                System.out.println("Found window with class " + cText + ", total " + ++count + " Text: " + wText);
                return true;
            }
        }, null);
    }

    public static HWND findWindow(String className, String windowName) throws WindowNotFoundException {
        HWND hwnd = User32.INSTANCE.FindWindow(className, windowName);
        if (hwnd == null) {
            throw new WindowNotFoundException(className, windowName);
        }
        return hwnd; //.hashCode();
    }

    public static int sendMessage(HWND handler, int msg, int num1, int num2) {
        return User32.INSTANCE.SendMessage(handler, msg, num1, num2);
    }

    public static class WindowNotFoundException extends Exception {
        public WindowNotFoundException(String className, String windowName) {
            super(String.format("Window null for className: %s; windowName: %s", className, windowName));
        }
    }

    /*
    HWND hwndWinamp = FindWindow("Winamp v1.x", NULL);
if (!hwndWinamp) return;
DWORD dwProcessId;
GetWindowThreadProcessId(hwndWinamp, &dwProcessId);

// Check if our process can read/write in winamp's process space
HANDLE hProcess = OpenProcess(PROCESS_VM_OPERATION | PROCESS_VM_READ | PROCESS_VM_WRITE, FALSE, dwProcessId);
if (!hProcess) return;

// Create the buffer in winamp's process space
LPTSTR pWinampBuf = (LPTSTR) VirtualAllocEx(hProcess, NULL, BUFSIZE, MEM_RESERVE | MEM_COMMIT, PAGE_READWRITE);

// Example: Get all playlist entries
TCHAR szBuf[BUFSIZE];
int nPLCount = SendMessage(hwndWinamp, WM_WA_IPC, 0, IPC_GETLISTLENGTH);
for (int i=0; i&lt;nPLCount; i++)
{
LPVOID pBase = (LPVOID)::SendMessage(hwndWinamp, WM_USER, i, IPC_GETPLAYLISTFILE);
ReadProcessMemory(hProcess, pBase, szBuf, sizeof(szBuf), NULL);
// szBuf now contains the filename for entry i
}

// Example: Set a skin
_tcscpy(szBuf, _T("Our_Friend_Joe_Final.zip"));
WriteProcessMemory(hProcess, pWinampBuf, szBuf, _tcslen(szBuf),NULL);
::SendMessage(hwndWinamp, WM_WA_IPC, (WPARAM)pWinampBuf, IPC_SETSKIN);

// Clean up
VirtualFreeEx(hProcess, pWinampBuf, 0, MEM_RELEASE);
     */

}