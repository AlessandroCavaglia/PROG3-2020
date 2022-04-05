package Commons.Dao;

import Server.Model.Config;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;

public class FileManager {


    public static Boolean appendFile(String stringToWrite, String path) {
        boolean result =false;
        BufferedWriter writer = null;
        try {
            if (Files.exists(Paths.get(path))) {
                writer = new BufferedWriter(new FileWriter(path, true));
                writer.write(stringToWrite + "\n");
                result=true;
            }
        } catch (Exception e) {
            if(Config.DEBUG)
                e.printStackTrace();
        } finally {
            if (writer != null) {
                try {
                    writer.close();
                } catch (IOException ignored) {

                }
            }
        }
        return result;
    }

    public static Boolean writeFile(String stringToWrite, String path) {
        boolean result =false;
        BufferedWriter writer = null;
        try {
            if (Files.exists(Paths.get(path))) {
                writer = new BufferedWriter(new FileWriter(path));
                writer.write(stringToWrite);
                result=true;
            }
        } catch (Exception e) {
            if(Config.DEBUG)
                e.printStackTrace();
        } finally {
            if (writer != null) {
                try {
                    writer.close();
                } catch (IOException ignored) {

                }
            }
        }
        return result;
    }

    public static ArrayList<String> readFile(String path){
        ArrayList<String> result = new ArrayList<>();
        BufferedReader in=null;
        if (Files.exists(Paths.get(path))) {
            try {
                in = new BufferedReader(new FileReader(path));
                String line = in.readLine();
                while (line != null) {
                    result.add(line);
                    line = in.readLine();
                }
            } catch (Exception e) {
                if(Config.DEBUG)
                    e.printStackTrace();
            }finally {
                if(in!=null)
                    try{
                        in.close();
                    }catch (IOException ignored){ }
            }
        }
        return result;
    }


    public static boolean createFile(String path){
        File file= new File(path);
        try {
            file.createNewFile();
        }catch (IOException e) {
            return false;
        }
        return true;
    }

    public static boolean writeObject(Serializable serializable, String path){
        boolean result=false;
        FileOutputStream fout=null;
        ObjectOutputStream oos=null;
        if (Files.exists(Paths.get(path))) {
            try{
                 fout = new FileOutputStream(path);
                 oos= new ObjectOutputStream(fout);
                oos.writeObject(serializable);
                result=true;
            }  catch (IOException e) {
                e.printStackTrace();
            }finally {
                if(oos!=null)
                    try{
                        oos.close();
                    }catch (IOException ignored){ }
            }
        }
        return result;
    }

    public static Object readObject(String path){
        Object result=null;
        FileInputStream streamIn =null;
        ObjectInputStream objectinputstream=null;
        try{ streamIn = new FileInputStream(path);
            objectinputstream = new ObjectInputStream(streamIn);
            result =  objectinputstream.readObject();

        } catch (IOException  | ClassNotFoundException e) {
            if(Config.DEBUG)
                e.printStackTrace();
        }
        finally {
            if(streamIn!=null)
                try{
                    objectinputstream.close();
                    streamIn.close();
                }catch (IOException ignored){ }
        }
        return result;
    }

    public synchronized static int getID() {
        int id = 1;
        id += Integer.parseInt(readFile(Config.ID_FILE).get(0));
        writeFile(String.valueOf(id),Config.ID_FILE);
        return id - 1;
    }

    protected static class ReverseLineInputStream extends InputStream {

        RandomAccessFile in;

        long currentLineStart = -1;
        long currentLineEnd = -1;
        long currentPos = -1;
        long lastPosInFile = -1;
        int lastChar = -1;


        public ReverseLineInputStream(File file) throws FileNotFoundException {
            in = new RandomAccessFile(file, "r");
            currentLineStart = file.length();
            currentLineEnd = file.length();
            lastPosInFile = file.length() - 1;
            currentPos = currentLineEnd;

        }

        private void findPrevLine() throws IOException {
            if (lastChar == -1) {
                in.seek(lastPosInFile);
                lastChar = in.readByte();
            }

            currentLineEnd = currentLineStart;

            // There are no more lines, since we are at the beginning of the file and no lines.
            if (currentLineEnd == 0) {
                currentLineEnd = -1;
                currentLineStart = -1;
                currentPos = -1;
                return;
            }

            long filePointer = currentLineStart - 1;

            while (true) {
                filePointer--;

                // we are at start of file so this is the first line in the file.
                if (filePointer < 0) {
                    break;
                }

                in.seek(filePointer);
                int readByte = in.readByte();

                // We ignore last LF in file. search back to find the previous LF.
                if (readByte == 0xA && filePointer != lastPosInFile) {
                    break;
                }
            }
            // we want to start at pointer +1 so we are after the LF we found or at 0 the start of the file.
            currentLineStart = filePointer + 1;
            currentPos = currentLineStart;
        }

        public int read() throws IOException {

            if (currentPos < currentLineEnd) {
                in.seek(currentPos++);
                int readByte = in.readByte();
                return readByte;
            } else if (currentPos > lastPosInFile && currentLineStart < currentLineEnd) {
                // last line in file (first returned)
                findPrevLine();
                if (lastChar != '\n' && lastChar != '\r') {
                    // last line is not terminated
                    return '\n';
                } else {
                    return read();
                }
            } else if (currentPos < 0) {
                return -1;
            } else {
                findPrevLine();
                return read();
            }
        }

        @Override
        public void close() throws IOException {
            if (in != null) {
                in.close();
                in = null;
            }
        }
    }
}