package com.Hzz;

import java.io.*;

public class Pickle {
    public static void dump(String filename, Serializable data) throws FileNotFoundException, IOException {
        FileOutputStream file_stream = new FileOutputStream(filename);
        ObjectOutputStream object_stream = new ObjectOutputStream(file_stream);

        object_stream.writeObject(data);
        object_stream.close();
        file_stream.close();
    }

    public static Object load(String filename) throws IOException, ClassNotFoundException {
        FileInputStream file_stream = new FileInputStream(filename);
        ObjectInputStream object_stream = new ObjectInputStream(file_stream);

        Object ret = object_stream.readObject();
        object_stream.close();
        file_stream.close();

        return ret;
    }

}
