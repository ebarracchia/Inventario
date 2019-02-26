package com.barracchia.inventario.model;

import android.os.Environment;
import android.text.TextUtils;

import com.opencsv.CSVReader;

import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.List;

public class ItemInventario {
    private String group;
    private String code;
    private String brand;
    private String description;
    private String remark;

    private static List<ItemInventario> inventario;

    public ItemInventario(String group, String code, String brand, String description, String remark) {
        this.group = group;
        this.code = code;
        this.brand = brand;
        this.description = description;
        this.remark = remark;
    }

    public String getGroup() {
        return this.group;
    }

    public String getCode() {
        return this.code;
    }

    public String getBrand() {
        return this.brand;
    }

    public String getDescription() {
        return this.description;
    }

    public String getRemark() {
        return this.remark;
    }

    @Override
    public String toString() {
        return group + " " + code;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof ItemInventario) {
            return (((ItemInventario) obj).code).equals(this.code);
        }
        return false;
    }

    /**
     * Find item using code parameter
     * @param code 
     * @return
     */
    public static ItemInventario getByCodigo(String code) {
        if (!TextUtils.isEmpty(code))
            for (ItemInventario item : ItemInventario.getInventario()) {
                if (item.code.toUpperCase().equals(code.toUpperCase()))
                    return item;
            }
        return null;
    }

    /**
     * Get inventario list
     * @return
     */
    public static List<ItemInventario> getInventario() {
        if (inventario == null) {
            inventario = new ArrayList<>();
            loadCsvFile();
        }
        return inventario;
    }

    /**
     * Get inventario list fitered by param
     * @return
     */
    public static List<ItemInventario> getInventario(String filter) {
        List<ItemInventario> result = new ArrayList<>();
        if (!TextUtils.isEmpty(filter)) {
            for (ItemInventario item : ItemInventario.getInventario()) {
                if (item.code.toUpperCase().contains(filter.toUpperCase()) ||
                        item.group.toUpperCase().contains(filter.toUpperCase()) ||
                        item.brand.toUpperCase().contains(filter.toUpperCase()))
                    result.add(item);
            }
            return result;
        } else
            return ItemInventario.getInventario();
    }

    /**
     * Load items from CSV file
     */
    private static void loadCsvFile() {
        try {
            //Get the path of external storage directory. Here we used download directory to read CSV
            File downloadDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);
            //Read the specific PDF document from the download directory
            File filePath = new File(downloadDir + "/inventario.csv");

            CSVReader reader = new CSVReader(new FileReader(filePath));
            String[] nextLine;
            boolean firstLine = true;
            while ((nextLine = reader.readNext()) != null) {
                // nextLine[] is an array of values from the line
                if (!firstLine) {
                    System.out.println(nextLine[0] + nextLine[1] + "etc...");
                    ItemInventario itemInventario = new ItemInventario(
                            nextLine[0],
                            nextLine[1],
                            nextLine[2],
                            nextLine[3],
                            nextLine[4]);
                    inventario.add(itemInventario);
                } else
                    firstLine = false;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
