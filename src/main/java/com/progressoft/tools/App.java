package com.progressoft.tools;

import java.io.FileNotFoundException;
import java.math.BigDecimal;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

public class App {
	
	
	private static final String MIN_MAX = "min-max";
	private static final String Z_SCORE = "z-score";
	
	/*
	 * args:
	 * 1. SOURCE_PATH
	 * 2. DEST_PATH
	 * 3.COLUMN_TO_NORMALIZE
	 * 4. NORMALIZATION_METHOD:
	 * 		a. min-max
	 * 		b. z-score
	 */
    public static void main(String[] args) throws FileNotFoundException {
        System.out.println("---------------------------------------------------");

        if (args.length < 4) {
        	 System.out.println("Insufficient Params");
        	 return;
        }

        System.out.println("Normalization Started");


        Path sourcePath = Paths.get(args[0]);
        Path destPath = Paths.get(args[1]);
        String columnToNormalize = args[2];
        String normalizationMethod = args[3];

        NormalizerImpl normalizer = new NormalizerImpl();

        if (normalizationMethod.equals(Z_SCORE)) {
        	normalizer.zscore(sourcePath, destPath, columnToNormalize);
        } else if (normalizationMethod.equals(MIN_MAX)) {
        	normalizer.minMaxScaling(sourcePath, destPath, columnToNormalize);
        } else {
        	 System.out.println("Invalid normalization method");
        	 System.out.println("Avaliable methods are: ");
        	 System.out.println("1. min-max");
        	 System.out.println("2. z-score");
        	 return;
        }

//
//        normalizer.zscore(Paths.get("C:\\Users\\Student\\401Java\\exam\\src\\test\\resources\\marks.csv"), Paths.get("C:\\Users\\Student\\401Java\\exam\\src\\test\\resources\\marks_scaled_z.csv"), "mark");
//        normalizer.minMaxScaling(Paths.get("C:\\Users\\Student\\401Java\\exam\\src\\test\\resources\\marks.csv"), Paths.get("C:\\Users\\Student\\401Java\\exam\\src\\test\\resources\\marks_scaled_mm.csv"), "mark");
//        normalizer.zscore(Paths.get("C:\\Users\\Student\\401Java\\exam\\src\\test\\resources\\employees.csv"), Paths.get("C:\\Users\\Student\\401Java\\exam\\src\\test\\resources\\employees_scaled_z.csv"), "salary");
//        normalizer.minMaxScaling(Paths.get("C:\\Users\\Student\\401Java\\exam\\src\\test\\resources\\employees.csv"), Paths.get("C:\\Users\\Student\\401Java\\exam\\src\\test\\resources\\employees_scaled_mm.csv"), "salary");
        System.out.println("---------------------------------------------------");
        System.out.println("Normalization Finished");
        System.out.println("---------------------------------------------------");
    }
}
