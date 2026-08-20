# Java File Handling & I/O Streams --- Exam Preparation Notes

## 1. `File` Class

### Definition

The Java `File` class (`java.io.File`) is used to **represent and
interact with file-system paths** such as files and directories.

> Important: `File` mainly works with **file/directory metadata and
> operations**. It does **not** itself read or write file contents.

### Get the Current Working Directory

``` java
String currentDir = System.getProperty("user.dir");
System.out.println(currentDir);
```

-   `user.dir` → current working directory of the Java application.

### Create a File/Directory Path

``` java
String currentDir = System.getProperty("user.dir");

File file = new File(currentDir, "test.txt");
```

The constructor combines the parent directory and child path.

### Common `File` Methods

  -----------------------------------------------------------------------
  Method                              Purpose
  ----------------------------------- -----------------------------------
  `exists()`                          Checks whether file/directory
                                      exists

  `isFile()`                          Checks whether the path represents
                                      a file

  `isDirectory()`                     Checks whether the path represents
                                      a directory

  `createNewFile()`                   Creates a new empty file

  `mkdir()`                           Creates one directory

  `mkdirs()`                          Creates directories including
                                      required parent directories

  `delete()`                          Deletes a file or empty directory

  `renameTo()`                        Renames/moves a file or directory

  `listFiles()`                       Returns files/directories inside a
                                      directory

  `getName()`                         Returns file/directory name

  `getAbsolutePath()`                 Returns absolute path

  `length()`                          Returns file size in bytes
  -----------------------------------------------------------------------

### Example

``` java
import java.io.File;
import java.io.IOException;

public class FileExample {
    public static void main(String[] args) throws IOException {

        String currentDir = System.getProperty("user.dir");
        File file = new File(currentDir, "test.txt");

        System.out.println("Path: " + file.getAbsolutePath());
        System.out.println("Exists: " + file.exists());

        if (!file.exists()) {
            file.createNewFile();
        }

        System.out.println("Is File: " + file.isFile());
        System.out.println("Size: " + file.length() + " bytes");
    }
}
```

### Pros

-   Easy way to work with file/directory paths.
-   Provides useful metadata operations.
-   Simple API for checking, creating, deleting and renaming files.
-   Can represent both files and directories.

### Cons

-   `File` does not provide the actual file-content reading/writing API.
-   Some methods such as `renameTo()` have platform-dependent behavior.
-   For modern file-system operations, Java NIO (`Path`, `Files`) is
    generally more powerful.

### Exam Point

**`File` = file-system information and file/directory operations.**

**`FileInputStream` / `FileReader` = reading content.**

**`FileOutputStream` / `FileWriter` = writing content.**

------------------------------------------------------------------------

# 2. Java I/O Streams

Java provides streams to perform **input and output (I/O)** operations.

## Two Major Types

``` text
                    Java I/O
                       |
          +------------+------------+
          |                         |
      Byte Stream              Character Stream
          |                         |
     InputStream /              Reader /
     OutputStream               Writer
```

### Byte Stream

Used for **raw binary data**.

Examples:

-   Images
-   Videos
-   Audio
-   PDF files
-   Other binary files

Main classes:

``` text
InputStream
OutputStream
```

Common implementations:

``` text
FileInputStream
FileOutputStream
BufferedInputStream
BufferedOutputStream
```

### Character Stream

Used mainly for **text data**.

Examples:

-   `.txt`
-   `.java`
-   `.html`
-   `.css`
-   `.xml`
-   `.json`

Main classes:

``` text
Reader
Writer
```

Common implementations:

``` text
FileReader
FileWriter
BufferedReader
BufferedWriter
```

------------------------------------------------------------------------

# 3. `FileInputStream`

## Definition

`FileInputStream` reads **raw bytes** from a file.

It is a **byte stream**.

### Example

``` java
import java.io.FileInputStream;
import java.io.IOException;

public class FileInputStreamExample {
    public static void main(String[] args) throws IOException {

        FileInputStream fis = new FileInputStream("test.txt");

        int data;

        while ((data = fis.read()) != -1) {
            System.out.print((char) data);
        }

        fis.close();
    }
}
```

### Important

`read()` returns:

-   A byte value (`0` to `255`) when data is available.
-   `-1` when the **end of the stream** is reached.

So:

``` java
while ((data = fis.read()) != -1)
```

is a common pattern.

### Advantages

-   Suitable for binary files.
-   Can read raw byte data.
-   Simple API.

### Disadvantages

-   Reading one byte at a time can be inefficient for large files.
-   Not designed specifically for text processing.
-   Character decoding must be handled separately when interpreting
    text.

------------------------------------------------------------------------

# 4. `FileOutputStream`

## Definition

`FileOutputStream` writes **raw bytes** to a file.

It is a **byte stream**.

### Example

``` java
import java.io.FileOutputStream;
import java.io.IOException;

public class FileOutputStreamExample {
    public static void main(String[] args) throws IOException {

        String text = "Hello Java";

        FileOutputStream fos = new FileOutputStream("output.txt");

        fos.write(text.getBytes());

        fos.close();
    }
}
```

### Append Mode

``` java
FileOutputStream fos =
        new FileOutputStream("output.txt", true);
```

`true` means append instead of replacing the existing content.

### Advantages

-   Suitable for binary data.
-   Can write images, videos and other raw byte data.
-   Supports append mode.

### Disadvantages

-   Byte-by-byte operations can be slower for large files.
-   Not convenient for character/text processing.

------------------------------------------------------------------------

# 5. `FileReader`

## Definition

`FileReader` is a **character stream** used to read text from a file.

### Example

``` java
import java.io.FileReader;
import java.io.IOException;

public class FileReaderExample {
    public static void main(String[] args) throws IOException {

        FileReader reader = new FileReader("test.txt");

        int data;

        while ((data = reader.read()) != -1) {
            System.out.print((char) data);
        }

        reader.close();
    }
}
```

### Important

`FileReader.read()` returns:

-   Character data as an integer.
-   `-1` when the end of the stream is reached.

### Character-by-Character Reading

``` java
int data;

while ((data = reader.read()) != -1) {
    System.out.print((char) data);
}
```

### Advantages

-   Designed for text files.
-   Works with Java's character-stream APIs.
-   More appropriate than byte streams when processing characters.

### Disadvantages

-   Reading character-by-character may be inefficient for large files.
-   It does not provide convenient line-by-line reading by itself.

------------------------------------------------------------------------

# 6. `FileWriter`

## Definition

`FileWriter` is a **character stream** used to write text to a file.

### Example

``` java
import java.io.FileWriter;
import java.io.IOException;

public class FileWriterExample {
    public static void main(String[] args) throws IOException {

        FileWriter writer = new FileWriter("output.txt");

        writer.write("Hello Java");
        writer.write("\nFile handling is easy.");

        writer.close();
    }
}
```

### Append Mode

``` java
FileWriter writer = new FileWriter("output.txt", true);
```

### Advantages

-   Simple way to write text.
-   Suitable for character-based data.
-   Easy to use for small text files.

### Disadvantages

-   Not suitable for binary data.
-   Direct character-by-character operations may be inefficient for
    large files.
-   Buffered writers are generally preferred for frequent writes.

------------------------------------------------------------------------

# 7. `BufferedInputStream`

## Definition

`BufferedInputStream` is a **buffered byte stream** that wraps another
`InputStream`.

It improves I/O efficiency by reducing the number of direct underlying
stream operations.

### Example

``` java
import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.IOException;

public class BufferedInputStreamExample {
    public static void main(String[] args) throws IOException {

        BufferedInputStream bis =
                new BufferedInputStream(
                        new FileInputStream("test.txt"));

        int data;

        while ((data = bis.read()) != -1) {
            System.out.print((char) data);
        }

        bis.close();
    }
}
```

### Why use buffering?

Without buffering:

``` text
Application
    |
FileInputStream
    |
File System
```

With buffering:

``` text
Application
    |
BufferedInputStream
    |
FileInputStream
    |
File System
```

The buffer reduces frequent access to the underlying file/device.

### Advantages

-   Better performance for many small reads.
-   Reduces the number of underlying I/O operations.
-   Suitable for large files and binary data.
-   Works with `InputStream`.

### Disadvantages

-   Uses additional memory for the buffer.
-   Adds another layer to the I/O stack.
-   Still requires appropriate handling for very large files; buffering
    does not mean the entire file is loaded into memory.

------------------------------------------------------------------------

# 8. `BufferedOutputStream`

## Definition

`BufferedOutputStream` is a **buffered byte stream** used to improve
writing efficiency.

### Example

``` java
import java.io.BufferedOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;

public class BufferedOutputStreamExample {
    public static void main(String[] args) throws IOException {

        BufferedOutputStream bos =
                new BufferedOutputStream(
                        new FileOutputStream("output.txt"));

        String text = "Hello Java";

        bos.write(text.getBytes());

        bos.close();
    }
}
```

### Advantages

-   Improves performance for frequent writes.
-   Reduces direct underlying I/O operations.
-   Suitable for binary data.
-   Useful when writing large amounts of data.

### Disadvantages

-   Requires additional buffer memory.
-   Data may remain in the buffer until it is flushed or closed.
-   Adds a wrapper around the underlying stream.

------------------------------------------------------------------------

# 9. `BufferedReader` --- Important for Exams

For text files, `BufferedReader` is often more useful than using
`FileReader` directly.

It is a **character stream with buffering**.

### Read Line by Line

``` java
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

public class BufferedReaderExample {
    public static void main(String[] args) throws IOException {

        BufferedReader br =
                new BufferedReader(
                        new FileReader("test.txt"));

        String line;

        while ((line = br.readLine()) != null) {
            System.out.println(line);
        }

        br.close();
    }
}
```

### Important Difference

`read()`:

``` java
int data = reader.read();
```

Returns:

``` text
-1 → End of stream
```

`readLine()`:

``` java
String line = br.readLine();
```

Returns:

``` text
null → No more lines / End of stream
```

### Exam Memory Trick

``` text
read()      → -1
readLine()  → null
```

------------------------------------------------------------------------

# 10. Buffered vs Non-Buffered Streams

  -----------------------------------------------------------------------
  Feature                 FileInputStream         BufferedInputStream
  ----------------------- ----------------------- -----------------------
  Type                    Byte stream             Byte stream

  Buffering               No additional buffering Yes
                          layer                   

  Data                    Bytes                   Bytes

  Performance             Can be slower for many  Generally faster
                          small operations        

  Large files             Can be used             Often preferred

  Memory                  Lower extra buffer      Uses buffer memory
                          overhead                
  -----------------------------------------------------------------------

Similarly:

``` text
FileOutputStream
       ↓
BufferedOutputStream
```

and:

``` text
FileReader
       ↓
BufferedReader
```

------------------------------------------------------------------------

# 11. Byte Stream vs Character Stream

  Feature             Byte Stream                     Character Stream
  ------------------- ------------------------------- -----------------------------
  Base classes        `InputStream`, `OutputStream`   `Reader`, `Writer`
  Data                Bytes                           Characters
  Best for            Binary/raw data                 Text data
  Examples            Image, video, audio             TXT, Java, HTML, CSS
  Reading example     `FileInputStream`               `FileReader`
  Writing example     `FileOutputStream`              `FileWriter`
  EOF from `read()`   `-1`                            `-1`
  Line reading        Not directly convenient         `BufferedReader.readLine()`

### Easy Memory Trick

``` text
BYTE  → Binary → Image / Video / Audio
CHAR  → Character → Text / Java / HTML / CSS
```

------------------------------------------------------------------------

# 12. Complete Java I/O Hierarchy --- Simplified

``` text
                         Java I/O
                            |
             +--------------+--------------+
             |                             |
        Byte Streams                  Character Streams
             |                             |
       InputStream                    Reader
       OutputStream                   Writer
             |                             |
      +------+-------+             +-------+-------+
      |              |             |               |
FileInputStream  BufferedInput   FileReader    BufferedReader
FileOutputStream BufferedOutput  FileWriter
```

------------------------------------------------------------------------

# 13. Common Exam Questions

## Q1. What is the purpose of the `File` class?

**Answer:**

The `File` class represents file and directory paths and provides
methods to check, create, delete, rename and inspect files/directories.
It does not itself provide the main API for reading or writing file
contents.

------------------------------------------------------------------------

## Q2. How do you get the current working directory?

``` java
String dir = System.getProperty("user.dir");
```

**Memory Trick:**

``` text
user.dir → current working directory
```

------------------------------------------------------------------------

## Q3. Difference between `FileInputStream` and `FileReader`?

**Answer:**

`FileInputStream` is a byte stream and is suitable for binary/raw data,
while `FileReader` is a character stream designed for text data.

------------------------------------------------------------------------

## Q4. Which stream should be used for images and videos?

Use:

``` text
FileInputStream
FileOutputStream
```

because images and videos are binary data.

------------------------------------------------------------------------

## Q5. Which stream should be used for text files?

Use:

``` text
FileReader
FileWriter
```

For efficient line-oriented text processing:

``` text
BufferedReader
BufferedWriter
```

------------------------------------------------------------------------

## Q6. What is the difference between `read()` and `readLine()`?

``` text
read()
    ↓
Returns one character/byte as int
    ↓
-1 means end of stream

readLine()
    ↓
Returns a complete line as String
    ↓
null means end of stream
```

------------------------------------------------------------------------

## Q7. Does `BufferedInputStream` load the entire large file into memory?

**No.**

It uses a **buffer** to reduce frequent underlying I/O operations. It
does not mean the entire file is loaded into memory.

------------------------------------------------------------------------

## Q8. Why use Buffered streams?

**Answer:**

Buffered streams improve I/O performance by reducing the number of
direct operations performed on the underlying stream or file system.

------------------------------------------------------------------------

# 14. Practical Example --- Copy a Binary File

For an image/video/binary file:

``` java
import java.io.*;

public class FileCopyExample {

    public static void main(String[] args) throws IOException {

        try (
            BufferedInputStream input =
                new BufferedInputStream(
                    new FileInputStream("input.jpg"));

            BufferedOutputStream output =
                new BufferedOutputStream(
                    new FileOutputStream("copy.jpg"))
        ) {

            byte[] buffer = new byte[8192];
            int bytesRead;

            while ((bytesRead = input.read(buffer)) != -1) {
                output.write(buffer, 0, bytesRead);
            }
        }
    }
}
```

### Flow

``` text
input.jpg
   |
FileInputStream
   |
BufferedInputStream
   |
byte[] buffer
   |
BufferedOutputStream
   |
FileOutputStream
   |
copy.jpg
```

### Why is this approach good?

-   Uses byte streams for binary data.
-   Uses buffering for better I/O performance.
-   Uses a fixed-size byte array instead of loading the entire file into
    memory.
-   `try-with-resources` automatically closes the streams.

------------------------------------------------------------------------

# 15. `try-with-resources` --- Important Interview Point

Instead of:

``` java
FileInputStream fis = new FileInputStream("test.txt");

try {
    // read
} finally {
    fis.close();
}
```

Prefer:

``` java
try (FileInputStream fis =
         new FileInputStream("test.txt")) {

    // read
}
```

Java automatically closes the resource after the `try` block.

This is especially useful with:

-   `FileInputStream`
-   `FileOutputStream`
-   `FileReader`
-   `FileWriter`
-   `BufferedInputStream`
-   `BufferedOutputStream`
-   `BufferedReader`
-   `BufferedWriter`

------------------------------------------------------------------------

# 16. Quick Revision Table

  ------------------------------------------------------------------------------
  Class                    Stream Type       Main Use          EOF
  ------------------------ ----------------- ----------------- -----------------
  `File`                   File-system API   File/directory    N/A
                                             operations        

  `FileInputStream`        Byte              Read binary/raw   `-1`
                                             data              

  `FileOutputStream`       Byte              Write binary/raw  N/A
                                             data              

  `BufferedInputStream`    Buffered Byte     Efficient byte    `-1`
                                             reading           

  `BufferedOutputStream`   Buffered Byte     Efficient byte    N/A
                                             writing           

  `FileReader`             Character         Read text         `-1`

  `FileWriter`             Character         Write text        N/A

  `BufferedReader`         Buffered          Efficient         `null` from
                           Character         text/line reading `readLine()`

  `BufferedWriter`         Buffered          Efficient text    N/A
                           Character         writing           
  ------------------------------------------------------------------------------

------------------------------------------------------------------------

# 17. One-Minute Exam Revision

``` text
File
 ↓
File/directory information + operations
 ↓
Does NOT primarily read/write content

Byte Stream
 ↓
InputStream / OutputStream
 ↓
FileInputStream / FileOutputStream
 ↓
Binary data
 ↓
Image / Video / Audio

Character Stream
 ↓
Reader / Writer
 ↓
FileReader / FileWriter
 ↓
Text data
 ↓
TXT / Java / HTML / CSS

Buffered Stream
 ↓
Adds buffering
 ↓
Improves I/O efficiency
 ↓
Does NOT load the entire file into memory

read()
 ↓
-1 = End of Stream

readLine()
 ↓
null = End of Stream
```

# 18. Memory Tricks

### File Class

**"File = File system, not File content."**

### Byte vs Character

**"Binary → Byte, Text → Character."**

### Input vs Output

**"Input = into the application, Output = out of the application."**

### EOF

**"`read()` → `-1`, `readLine()` → `null`."**

### Buffered

**"Buffer = fewer underlying I/O operations → better performance."**

### Most Important Exam Comparison

``` text
Binary file?
    ↓
FileInputStream / FileOutputStream
    ↓
Add BufferedInputStream / BufferedOutputStream for efficient I/O

Text file?
    ↓
FileReader / FileWriter
    ↓
Add BufferedReader / BufferedWriter for efficient text I/O
```

> **Modern Java note:** For new code, also learn Java NIO (`Path`,
> `Files`, `Files.newBufferedReader()`, `Files.newBufferedWriter()`). It
> provides a more modern and flexible file-system API than the older
> `File` API.
