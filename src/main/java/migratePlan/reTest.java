package migratePlan;

import java.io.File;

public class reTest {

	public static void main(String[] args) {
		
		// Aquí la carpeta que queremos explorar
        String path = "D:\\Wokspace\\BambooSpec\\"; 

        String files;
        File folder = new File(path);
        File[] listOfFiles = folder.listFiles(); 

        for (int i = 0; i < listOfFiles.length; i++) 
        {

            if (listOfFiles[i].isFile()) 
            {
                files = listOfFiles[i].getName();
                if (files.endsWith(".java"))
                {
                    System.out.println(files);
                }
            }
        }
	}

}
