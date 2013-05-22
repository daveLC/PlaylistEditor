package com.lewiscrosby.jna;

//Version 0.9 Beta - Because there is one small thing left concerning the Processfind Method
//i want to try both dll files, but atm if one dll file doesnt contain the desired Function the trainer crashes
//this will be fixed when i have time for it - until then - try wich dll suits your system or make a Trainer for one
//and one Trainer for the other DLL - its only commenting out 3 lines of code 2 times ^^
//download jna.jar from the internet - and include to your project
//i recomm your using eclipse for coding



//Usual Imports
import java.util.Arrays;

//JNA imports - Lets us use Windows dll files
import com.sun.jna.Memory;
import com.sun.jna.Native;
import com.sun.jna.Pointer;
import com.sun.jna.ptr.IntByReference;
import com.sun.jna.win32.StdCallLibrary;

public class Example
{

    //Public Variables for AccessRights
    public static final int PROCESS_QUERY_INFORMATION = 0x0400;
    public static final int PROCESS_VM_READ = 0x0010;
    public static final int PROCESS_VM_WRITE = 0x0020;
    public static final int PROCESS_VM_OPERATION = 0x0008;

//Some functions, depending on Windows Version are located in Kernel32.dll, some in Psapi.dll

    //Access to external Kernel32.dll
    public interface Kernel32 extends StdCallLibrary
    {
        Kernel32 INSTANCE = (Kernel32) Native.loadLibrary("kernel32", Kernel32.class);
        boolean ReadProcessMemory(Pointer hProcess, int inBaseAddress, Pointer outputBuffer, int nSize, IntByReference outNumberOfBytesRead);

        public Pointer OpenProcess(int dwDesiredAccess, boolean bInheritHandle, int dwProcessId);


        boolean WriteProcessMemory(Pointer hProcess,int AdressToChange,Pointer ValuesToWrite,int nSize, IntByReference irgendwas);

        int GetLastError();

        //Needed for some Windows 7 Versions
        boolean EnumProcesses(int []ProcessIDsOut,int size , int[] BytesReturned);
        int GetProcessImageFileNameW(Pointer Process, char[] outputname, int lenght);
    }

    //Access to external Psapi.dll
    public interface Psapi extends StdCallLibrary
    {
        Psapi INSTANCE = (Psapi) Native.loadLibrary("Psapi", Psapi.class);
        //For some Windows 7 Versions and older down to XP
        boolean EnumProcesses(int []ProcessIDsOut,int size , int[] BytesReturned);
        int GetProcessImageFileNameW(Pointer Process, char[] outputname, int lenght);
    }

    //Processfinder - returns NULL if Process wasnt found
    public static Pointer FindMyProcess(String ProcessNameToFind)
    {
//Related to Version we have to use Kernel32.dll OR Psapi.dll to find the Process
        Psapi Psapidll = Psapi.INSTANCE;
        Kernel32 Kernel32dll = Kernel32.INSTANCE;

//we take an arraysize of 1024 - coz noone will have 1024 Processes running
        int[] processlist=new int[1024];
        int[] dummylist=new int[1024];

//Lets first try Psapi.dll
        try{Psapidll.EnumProcesses(processlist, 1024, dummylist);}
        catch(Exception e)
        {

        }

//Lets now try Kernel32.dll
//try{Kernel32dll.EnumProcesses(processlist, 1024, dummylist);}
//catch(Exception e)
// {

// }

//Ok - we now got our Array with all the ProcessID's from all running Processes in the array processlist
//Time to find out which Processid is our desired one!

//A pointer for our Processfinding mechanism
        Pointer tempProcess;
//Pointer for our desired Process
        Pointer Process=null;
//Char Array for the path of the processes containing also the filename.exe
        char []outputnames = new char[1024];
//A String for easier Comparison - see below
        String path="";

        for(int processid : processlist)
        {

            tempProcess=Kernel32dll.OpenProcess(PROCESS_QUERY_INFORMATION | PROCESS_VM_READ | PROCESS_VM_WRITE | PROCESS_VM_OPERATION, false, processid);

//Again we have to try both dll files in order to obtain our goal - one will work
            try{Psapidll.GetProcessImageFileNameW(tempProcess, outputnames, 1024);}
            catch(Exception e){}

//try{Kernel32dll.GetProcessImageFileNameW(tempProcess, outputnames, 1024);}
//catch(Exception e){}

//reset Path String
            path="";

            for(int k=0; k<1024;k++)
            {
//Convert our Char Array into a nice readable String
                if((int) outputnames[k]!=0)
                    path=path+outputnames[k];
            }

            if(path.contains(ProcessNameToFind))
            {
//If one of the processes found has the desired process exe name in its path its the one we want
                Process=tempProcess;
            }

//reset char
            outputnames = new char[1024];

        }

//Finally returning our Process - Null if we didnt find it.
        return Process;
    }

    //ReadProcessMemory ^^
    public static int[] ReadMyProcessMemory(Pointer ProcessToReadFrom, int AdressToReadFrom, int NumberOfBytesToRead )
    {
//Make the Desired Functions available
        Kernel32 Kernel32dll = Kernel32.INSTANCE;

        int offset=AdressToReadFrom;
        IntByReference baseAddress = new IntByReference();
        baseAddress.setValue(offset);
        Memory outputBuffer = new Memory(NumberOfBytesToRead);

        boolean reader = Kernel32dll.ReadProcessMemory(ProcessToReadFrom, offset, outputBuffer, NumberOfBytesToRead, null);



        if(reader)
        {
//Process the received Data
            byte[] bufferBytes = outputBuffer.getByteArray(0, NumberOfBytesToRead);

//Who wants signed byte? NOONE ! Lets convert it to a nice int !

            int[] realvalues=new int[bufferBytes.length];

            for(int i=0;i<bufferBytes.length;i++)
            { if(bufferBytes[i]<0)
            {
                realvalues[i]=256+bufferBytes[i];
            }
            else
            {
                realvalues[i]=bufferBytes[i];
            }
            }
//Conversion done ! lets Return the data (Remember its integer not hex)
            return realvalues;

        }
        else
        {
//Reading went wrong - SHIT
            return null;
        }
    }

    //WritePprocessMemory
    public static boolean WriteMyProcessMemory(Pointer ProcessToWriteTo,int AddressToWriteTo,int[] BytesToWrite)
    {
//Gain Access to Kernel32.dll
        Kernel32 Kernel32dll = Kernel32.INSTANCE;

        int offset=AddressToWriteTo;
        IntByReference baseAddress = new IntByReference();
        baseAddress.setValue(offset);
        Memory inputBuffer = new Memory(BytesToWrite.length);
        IntByReference dummy = new IntByReference();

//Convert the Interger Array to an usable Array of type Memory
        for(int i=0;i<BytesToWrite.length;i++)
        {
            inputBuffer.setByte(i, (byte)BytesToWrite[i]);
        }

        return Kernel32dll.WriteProcessMemory(ProcessToWriteTo, offset,inputBuffer , BytesToWrite.length, dummy);


    }

    public static void main(String[] args)
    {

//Small Example showing how to open and read an anddress in the Game baldursgate 2 - exename BGMain.exe - and then write to it
//Addresses are treated as HEX Values 0xhexvalue ie. 0x00a00

        Pointer MyProcess=FindMyProcess("BGMain");
        int [] value = ReadMyProcessMemory(MyProcess,0x00913F13,4);
        System.out.println(value[0]);
        System.out.println(value[1]);
        System.out.println(value[2]);
        System.out.println(value[3]);


//if you want to write the value 133 (Decimalsystem) just use newvalue[0]=133
//if you want to write Hex Values use newvalue[0]=0x90;where 0xHexValue

        int[] newvalue=new int[4];
        newvalue[0]=0x90;
        newvalue[1]=0x90;
        newvalue[2]=0x90;
        newvalue[3]=0x90;
        WriteMyProcessMemory(MyProcess,0x00913F13,newvalue);

    }

}
