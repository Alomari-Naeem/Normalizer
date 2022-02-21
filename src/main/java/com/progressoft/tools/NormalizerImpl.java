package com.progressoft.tools;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;


public class NormalizerImpl implements Normalizer {

    private int index = -1;
    private static final String ZSCORE_POSTFIX = "z";
    private static final String MIN_MAX_POSTFIX = "mm";

    @Override
    public ScoringSummary zscore(Path csvPath, Path destPath, String colToStandardize) throws IllegalArgumentException {

        List<Double> listOfNumbers = this.readerMethod(csvPath, colToStandardize);

        List<BigDecimal> normalizedListOfNumbers = new LinkedList<BigDecimal>();

        BigDecimal mean = this.mean(listOfNumbers);
        BigDecimal standardDeviation = this.standardDeviation(listOfNumbers);
        BigDecimal variance = this.variance(listOfNumbers);
        BigDecimal median = this.median(listOfNumbers);
        BigDecimal min = this.min(listOfNumbers);
        BigDecimal max = this.max(listOfNumbers);

        ScoringSummary scoringSummary = new ScoringSummaryImpl(mean, standardDeviation, variance, median, min, max);

        for (Double num : listOfNumbers) {
            BigDecimal zscore = new BigDecimal(num);
            zscore = zscore.subtract(mean);
            zscore = zscore.divide(standardDeviation, RoundingMode.HALF_EVEN);

            normalizedListOfNumbers.add(zscore);
        }

        this.writerMethod(csvPath, destPath, normalizedListOfNumbers, colToStandardize, ZSCORE_POSTFIX);


        return scoringSummary;
    }

    @Override
    public ScoringSummary minMaxScaling(Path csvPath, Path destPath, String colToNormalize) throws IllegalArgumentException {
        List<Double> listOfNumbers = this.readerMethod(csvPath, colToNormalize);

        List<BigDecimal> normalizedListOfNumbers = new LinkedList<BigDecimal>();

        BigDecimal mean = this.mean(listOfNumbers);
        BigDecimal standardDeviation = this.standardDeviation(listOfNumbers);
        BigDecimal variance = this.variance(listOfNumbers);
        BigDecimal median = this.median(listOfNumbers);
        BigDecimal min = this.min(listOfNumbers);
        BigDecimal max = this.max(listOfNumbers);

        ScoringSummary scoringSummary = new ScoringSummaryImpl(mean, standardDeviation, variance, median, min, max);

        for (Double num : listOfNumbers) {
            BigDecimal minMax = new BigDecimal(num);
            minMax = minMax.subtract(min);
            minMax = minMax.divide(max.subtract(min), RoundingMode.HALF_EVEN);

            normalizedListOfNumbers.add(minMax);
        }

        this.writerMethod(csvPath, destPath, normalizedListOfNumbers, colToNormalize, MIN_MAX_POSTFIX);

        return scoringSummary;
    }


    private BigDecimal mean(List<Double> listOfNumbers) {

        BigDecimal sum = new BigDecimal(0d);
        
        if (listOfNumbers == null || listOfNumbers.isEmpty()) return sum;
        for (Double num : listOfNumbers) {
            sum = sum.add(new BigDecimal(num));
        }
        
        BigDecimal mean = sum.divide(new BigDecimal(listOfNumbers.size()), RoundingMode.HALF_EVEN);
        
        mean = mean.setScale(2, RoundingMode.HALF_EVEN);
        return mean;
    }

    private BigDecimal standardDeviation(List<Double> listOfNumbers) {

        return this.variance(listOfNumbers).sqrt(MathContext.DECIMAL128).setScale(2, RoundingMode.HALF_EVEN);

    }

    private BigDecimal variance(List<Double> listOfNumbers) {


        BigDecimal variance = new BigDecimal(0d);

        BigDecimal mean = this.mean(listOfNumbers);

        double vr = 0;

        if (listOfNumbers == null || listOfNumbers.isEmpty()) return variance;
        for (Double num : listOfNumbers) {
            vr += Math.pow(num - mean.doubleValue(), 2);
        }

        vr /= listOfNumbers.size();

        variance = new BigDecimal(vr);
        variance = variance.setScale(0, RoundingMode.HALF_EVEN);
        variance = variance.setScale(2);
        return variance;

    }

    private BigDecimal median(List<Double> listOfNumbers) {

        BigDecimal median = new BigDecimal(0d);

        List<Double> listToBrSorted = listOfNumbers.stream().collect(Collectors.toList());

        Collections.sort(listToBrSorted);

        if (listToBrSorted == null || listToBrSorted.isEmpty()) return median;

        if (listToBrSorted.size() % 2 != 0) {
            median = new BigDecimal(listToBrSorted.get(((listToBrSorted.size() - 1) / 2)));
        } else {
            Double firstNum = listToBrSorted.get((((listToBrSorted.size() - 1) / 2)));
            Double secondNum = listToBrSorted.get((((listToBrSorted.size() - 1) / 2) + 1));

            median = median.add(new BigDecimal(firstNum));
            median = median.add(new BigDecimal(secondNum));
            median = median.divide(new BigDecimal(2d), RoundingMode.HALF_EVEN);
        }
        median = median.setScale(2, RoundingMode.HALF_EVEN);

        return median;

    }

    private BigDecimal min(List<Double> listOfNumbers) {


        BigDecimal min = new BigDecimal(0);

        if (listOfNumbers == null || listOfNumbers.isEmpty()) {
            return min;
        }

        List<Double> listToBrSorted = listOfNumbers.stream().collect(Collectors.toList());

        Collections.sort(listToBrSorted);

        min = new BigDecimal(listToBrSorted.get(0));
        min = min.setScale(2, RoundingMode.HALF_EVEN);

        return min;

    }

    private BigDecimal max(List<Double> listOfNumbers) {

        BigDecimal max = new BigDecimal(0);

        if (listOfNumbers == null || listOfNumbers.isEmpty()) {
            return max;
        }

        List<Double> listToBrSorted = listOfNumbers.stream().collect(Collectors.toList());
        Collections.sort(listToBrSorted);

        max = new BigDecimal(listToBrSorted.get(listToBrSorted.size() - 1));
        max = max.setScale(2, RoundingMode.HALF_EVEN);

        return max;

    }

    public List<Double> readerMethod(Path csvPath, String columnName) {


        List<Double> listOfNumbers = new LinkedList<Double>();

        try (FileReader reader = new FileReader(String.valueOf(csvPath));
             BufferedReader br = new BufferedReader(reader)) {

            String line;
            int row = 0;


            while ((line = br.readLine()) != null) {

                if (row == 0) {
                    row++;
                    String[] columnsNames = line.split(",");
                    for (int columnIndex = 0; columnIndex < columnsNames.length; columnIndex++) {
                        if (columnsNames[columnIndex].equals(columnName)) {
                            index = columnIndex;
                        }
                    }

                    if (index == -1) {
                        throw new IllegalArgumentException("column " + columnName + " not found");
                    }

                    continue;
                }

                row++;

                listOfNumbers.add(Double.valueOf(line.split(",")[index]));
            }

        } catch (IOException e) {
            throw new IllegalArgumentException("source file not found");
        } catch (Exception e) {
            throw new IllegalArgumentException(e.getMessage());
        }
        index = -1;
        return listOfNumbers;
    }

    public void writerMethod(Path csvPath, Path destPath, List<BigDecimal> list, String colToNormalize, String postFix) {

        try (FileWriter writer = new FileWriter(String.valueOf(destPath));
             FileReader reader = new FileReader(String.valueOf(csvPath));
             BufferedWriter bw = new BufferedWriter(writer);
             BufferedReader br = new BufferedReader(reader)) {

            String line;
            List<String> finalLine = new LinkedList<String>();
            ;
            int row = 0;

            while ((line = br.readLine()) != null) {

                finalLine = new LinkedList<String>(Arrays.asList(line.split(",")));
                if (row == 0) {
                    String[] columnsNames = line.split(",");
                    for (int columnIndex = 0; columnIndex < columnsNames.length; columnIndex++) {
                        if (columnsNames[columnIndex].equals(colToNormalize)) {
                            index = columnIndex;
                        }
                    }

                    finalLine.add(index + 1, colToNormalize + "_" + postFix);
                } else {
                    finalLine.add(index + 1, String.valueOf(list.get(row - 1)));
                }

                row++;
                line = finalLine.stream().collect(Collectors.joining(","));
                bw.write(line);
                bw.newLine();


            }

        } catch (IOException e) {
            throw new IllegalArgumentException("source file not found");

        }
        index = -1;
    }

}
