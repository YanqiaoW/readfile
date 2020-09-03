package com.wyq;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Main
 */
public final class App {
    private App() {
    }

    public static void main(String[] args) {
        String srcDir = "";
        String destDir = "";
        List<File> srcFiles = new ArrayList<File>();

        File dest = new File(destDir);
        if (!dest.exists()) {
            dest.mkdirs();
        }

        // assumption: there is no nested folder
        File dir = new File(srcDir);
        if (!dir.exists()) {
            System.out.println("please input a valid src folder");
            return;
        }


        for (File f : dir.listFiles()) {
            srcFiles.add(f);
        }

        CountDownLatch latch = new CountDownLatch(srcFiles.size());
        ExecutorService pool = Executors.newFixedThreadPool(10);

        System.out.println("start processing files ...");
        for (int i = 0; i < srcFiles.size(); i++) {
            File f = srcFiles.get(i);
            pool.submit(new FileProcessorThread(latch, f, destDir));
        }

        try {
            latch.await();
        } catch (InterruptedException ex) {
            ex.printStackTrace();
        }

        System.out.println("finish file processing");
        pool.shutdownNow();
    }


    static class FileProcessorThread implements Runnable {

        private CountDownLatch latch;
        private File srcFile;
        private String destDir;

        public FileProcessorThread(CountDownLatch latch, File srcFile, String destDir) {
            this.latch = latch;
            this.srcFile = srcFile;
            this.destDir = destDir;
        }

        @Override
        public void run() {
            try {
                System.out.println(String.format("%s start processing file %s", Thread.currentThread().getName(), srcFile.getName()));
                BufferedReader br = null;
                BufferedWriter bw = null;
                try {
                    br = new BufferedReader(new FileReader(srcFile));
                    FileWriter fw = new FileWriter(new File(destDir + "/" + srcFile.getName()));
                    bw = new BufferedWriter(fw);
                    String line = null;
                    while ((line = br.readLine()) != null) {
                        bw.write(line.replaceAll("[A-Z]", ""));
                        bw.write("\n");
                    }
                } catch (Exception ex) {
                    ex.printStackTrace();
                } finally {
                    try {
                        if (br != null) {
                            br.close();
                        }
    
                        if (bw != null) {
                            bw.close();
                        }
                    } catch (IOException ex) {
                        ex.printStackTrace();
                    }

                }

                System.out.println(String.format("%s finish processing file %s", Thread.currentThread().getName(), srcFile.getName()));
            } catch (Exception ex) {
                ex.printStackTrace();
            } finally {
                latch.countDown();
            }
    
        }
    
    }
}


