package fi.hut.soberit.agilefant.db;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import fi.hut.soberit.agilefant.db.export.DbBackupper;

public class TestStreams {

    /**
     * This class if for debugging and testing! Idea is that test writes streams
     * to file. thus showing that streams actually work.
     * 
     * @throws IOException
     */
    public static void main(String[] args) throws IOException {

        DbBackupper takedbbackup = new DbBackupper();
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        File fantdirectory = new File(".");
        File generatetestfile = new File(fantdirectory.getCanonicalPath()
                + File.separator + "test-tmp" + File.separator);
        generatetestfile.mkdir();
        stream = takedbbackup.generateDBDumpStream();
        
        try {
            // location where test writes file. choose place where you can
            // write.
            FileOutputStream fos = new FileOutputStream(
                    fantdirectory.getCanonicalPath() + File.separator
                            + "test-tmp" + File.separator + "zippesqldump.zip");
            System.out.println("File generated to location: "
                    + fantdirectory.getCanonicalPath() + File.separator
                    + "test-tmp" + File.separator + "zippesqldump.zip");
            try {
                fos.write(stream.toByteArray());
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        } catch (FileNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }
}
