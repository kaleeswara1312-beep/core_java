import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;

class FileOperation {
    public static void main(String[] args) throws IOException {
        // String currentDirectory = System.getProperty("user.dir");
        // System.out.println(currentDirectory);

        // File fs = new File(currentDirectory, "Folder\\innerfolder\\innerfolder2");

        // if(fs.exists()){
        // System.out.println("Folder exists");
        // } else{
        // boolean created = fs.mkdirs();

        // System.out.println("Folder exists : " + created );

        // }

        String currentDir = System.getProperty("user.dir");

        File f = new File(currentDir, "sample1.txt");

        // if (f.exists()) {
        // System.out.println(currentDir);
        // // f.delete();

        // } else {
        // boolean isFileCreated = f.createNewFile();

        // File f1 = new File(currentDir, "sample1.txt");

        // f.renameTo(f1);
        // // f.delete();
        // System.out.println(isFileCreated);

        // }
        // System.out.println(f.exists());

        // File s[] = f.listFiles();
        // for(File sub : s){
        // if(sub.isFile()){
        // System.out.println(sub.getName());

        // System.out.println(sub.length());
        // }
        // }

        FileWriter fw = new FileWriter(f);
        fw.write("sdsdssadsaddsds");
        // fw.flush();
        fw.close();

        // FileReader fr = new FileReader(f);

        // int output = fr.read();
        // System.out.println(output);
        // while (output != -1) {
        //     System.out.println((char) output);
        //     output = fr.read();
        // }
        // fr.close();
        // InputStream is = new InputStream(currentDir);

        // FileOutputStream fos = new FileOutputStream("sample2.txt");
        
        // String s = "Kali is here";
        // byte b[] = s.getBytes();
        // fos.write(b);
        // // fos.flush();
        // fos.close();

    }
}