package com.wyq;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.concurrent.CountDownLatch;

import com.wyq.App.FileProcessorThread;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;

/**
 * Unit test for simple App.
 */
class AppTest {

    private String destDir;
    private String srcDir;

    @BeforeEach
    void dataPrepare() {
        System.out.println("prepare test data");
        String tempDir = System.getProperty("java.io.tmpdir");
        srcDir = tempDir + "/src_temp";
        destDir = tempDir + "/dest_temp";
        File src = new File(srcDir);
        src.mkdirs();
        File dest = new File(destDir);
        dest.mkdirs();
        BufferedWriter bw = null;
        try {
            FileWriter fw = new FileWriter(new File(srcDir + "/1.txt"));
            bw = new BufferedWriter(fw);
            bw.write("ThisIsATest");
        } catch (IOException ex) {
            ex.printStackTrace();
        } finally {
            if (bw != null) {
                try {
                    bw.close();
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
            }
        }  
    }

    /**
     * Rigorous Test.
     */
    @Test
    void testApp() {
        CountDownLatch latch = new CountDownLatch(1);
        FileProcessorThread thread = new App.FileProcessorThread(latch, new File(srcDir + "/1.txt"), destDir);
        thread.run();
        BufferedReader br = null;
        String data = "";
        try {
            br = new BufferedReader(new FileReader(new File(destDir + "/1.txt")));
            data = br.readLine();
        } catch (IOException ex) {
            ex.printStackTrace();
        } finally {
            if (br != null) {
                try {
                    br.close();
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
            }
        }


        assertEquals("hissest", data);
    }


    @AfterEach
    void cleanData() {
        System.out.println("clean test data");
        File srcFile = new File(srcDir + "/1.txt");
        File destFile = new File(destDir + "/1.txt");
        File src = new File(srcDir);
        File dest = new File(destDir);
        srcFile.delete();
        destFile.delete();
        src.delete();
        dest.delete();
    }
}
