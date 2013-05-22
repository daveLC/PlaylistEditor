package com.lewiscrosby.jna;

import com.sun.jna.*;
import com.sun.jna.platform.win32.Kernel32;
import com.sun.jna.platform.win32.WinDef;
import com.sun.jna.platform.win32.WinDef.HWND;
import com.sun.jna.platform.win32.WinNT;
import com.sun.jna.platform.win32.WinUser;
import com.sun.jna.ptr.IntByReference;
import com.sun.jna.ptr.LongByReference;
import com.sun.jna.win32.*;
import org.apache.commons.codec.binary.Hex;

public class GetWindowHandle {

    static int BUFSIZE = 10;

    public interface User32 extends StdCallLibrary {
        User32 INSTANCE = (User32) Native.loadLibrary("user32", User32.class, W32APIOptions.DEFAULT_OPTIONS);

        boolean EnumWindows(WinUser.WNDENUMPROC lpEnumFunc, Pointer arg);
        int GetWindowTextA(HWND hWnd, char[] lpString, int nMaxCount);
        int GetClassName(HWND hwnd, char[] lpString, int nMaxCount);
        HWND FindWindow(String lpClassName, String lpWindowName);
        WinDef.LRESULT SendMessage(HWND hWnd, int msg, int num1, int num2);
        int GetWindowThreadProcessId(HWND hWnd, Pointer processId);
    }

    public interface Kernel32 extends Library {
        Kernel32 INSTANCE = (Kernel32) Native.loadLibrary("kernel32", Kernel32.class);

        // Optional: wraps every call to the native library in a
        // synchronized block, limiting native calls to one at a time
        Kernel32 SYNC_INSTANCE = (Kernel32) Native.synchronizedLibrary(INSTANCE);

        //Kernel32 INSTANCE = (Kernel32) Native.loadLibrary("kernel32", Kernel32.class);
        WinNT.HANDLE OpenProcess(int desiredAccess, boolean inheritHandle, int processId);

        //DWORD WINAPI GetLastError(void);
        int GetLastError();

        boolean CloseHandle(WinNT.HANDLE handle);
        boolean ReadProcessMemory(WinNT.HANDLE hProcess, Pointer lpBaseAddress, Pointer lpBuffer, int nSize, Pointer lpNumberOfBytesRead);
        boolean ReadProcessMemory(Pointer hProcess, int inBaseAddress, Pointer outputBuffer, int nSize, IntByReference outNumberOfBytesRead);
    }

    /*
HANDLE WINAPI OpenProcess(
_In_  DWORD dwDesiredAccess,
_In_  BOOL bInheritHandle,
_In_  DWORD dwProcessId
);
 */
    public static Pointer openProcess (int desiredAccess, boolean inheritHandle, int processId) {
        int PROCESS_VM_READ = 0x0010;
        int PROCESS_VM_WRITE = 0x0020;
        int PROCESS_VM_OPERATION = 0x0008;

        System.out.println("openProcess: " + desiredAccess + "," + inheritHandle + "," + processId);
        Kernel32 instance = Kernel32.INSTANCE;

        desiredAccess = PROCESS_VM_READ | PROCESS_VM_WRITE | PROCESS_VM_OPERATION;

        WinNT.HANDLE handle = instance.OpenProcess(desiredAccess, false, processId);
        if (handle == null) {
            //System.out.println("Error:"+ Native.getLastError());
            System.out.println("Error:"+Kernel32.INSTANCE.GetLastError());
            return null;
        }
        else {
            Pointer pointer = handle.getPointer();
            return pointer;
        }
    }
    public static boolean closeProcess(Pointer pointer) {
        WinNT.HANDLE handle = new WinNT.HANDLE(pointer);
        return Kernel32.INSTANCE.CloseHandle(handle);
    }

    public static void enumWindows() {
        final User32 user32 = User32.INSTANCE;
        user32.EnumWindows(new WinUser.WNDENUMPROC() {
            int count = 0;
            @Override
            public boolean callback(HWND hWnd, Pointer arg1) {
                char[] windowText = new char[512];

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

    /*
    GetWindowThreadProcessId(hwndWinamp, &dwProcessId);
DWORD WINAPI GetWindowThreadProcessId(
  _In_       HWND hWnd,
  _Out_opt_  LPDWORD lpdwProcessId
);

	void GetWindowThreadProcessId(TestLibrary.HWND hwnd, TestLibrary.LPDWORD proc_id);

	public static class HWND extends PointerType {
		public HWND(Pointer address) {
			super(address);
		}
		public HWND() {
			super();
		}
	};

     */
    public static int getThreadProcessId (Pointer handler) {
        Pointer processId = new IntByReference(0).getPointer();
        User32.INSTANCE.GetWindowThreadProcessId(new HWND(handler), processId);
        return processId.getInt(0);
    }

    // ReadProcessMemory(hProcess, pBase, szBuf, sizeof(szBuf), NULL);
    public static int[] readProcessMemory(Pointer hProcess, long lpBaseAddress) {
        /***** Buffer to put filename into *****/
        // TCHAR szBuf[BUFSIZE];
        char[] filenameBuffer = new char[BUFSIZE];
        WinNT.HANDLE handle = new WinNT.HANDLE(hProcess);

        Pointer lpBaseAddressPtr = new LongByReference(lpBaseAddress).getPointer();
        Pointer pointer = new IntByReference(0).getPointer();

        Memory filenameBufferPtr = new Memory(filenameBuffer.length);
        //filenameBufferPtr.write(0, filenameBuffer, 0, filenameBuffer.length);

        boolean success = Kernel32.INSTANCE.ReadProcessMemory(handle, lpBaseAddressPtr, filenameBufferPtr, filenameBuffer.length, pointer);

        System.out.println("Error:" + Native.getLastError());
        System.out.println ("success: " + success);
        //System.out.println("filenameBuffer: " + filenameBuffer);

        if(success) {

            char[] bufferBytes = filenameBufferPtr.getCharArray(0, filenameBuffer.length);

            int[] realValues = new int[bufferBytes.length];

            for (int i=0; i<bufferBytes.length; i++) {
                if(bufferBytes[i]<0) {
                    realValues[i]=256 + bufferBytes[i];
                }
                else {
                    realValues[i] = bufferBytes[i];
                }
            }
System.out.println ("bufferBytes: " +bufferBytes);
            return realValues;
        }
        else {
            return null;
        }
    }

    public static Pointer findWindow(String className, String windowName) throws WindowNotFoundException {
        HWND hwnd = User32.INSTANCE.FindWindow(className, windowName);
        if (hwnd == null) {
            throw new WindowNotFoundException(className, windowName);
        }
        return hwnd.getPointer();
    }

    public static WinDef.LRESULT sendMessage(Pointer handler, Integer msg, Integer num1, Integer num2) {
        WinDef.LRESULT result = User32.INSTANCE.SendMessage(new HWND(handler), msg, num1, num2);
        return result;
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