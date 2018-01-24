package com.dea.prototipo.web;

import com.dea.prototipo.domain.*;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.StringUtils;
import org.opensourcedea.dea.*;

import java.io.File;

import java.util.*;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

@RequestMapping("/member/benchmarking")
@Controller
public class Benchmarking {

    @RequestMapping(value = "")
    public String benchmarking(Model uiModel) {
        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        uiModel.addAttribute("warehouses", Warehouse.findWarehousesByUser(user).getResultList());
        return "member/benchmarking/index";
    }

    @RequestMapping(value = "/solver", produces = "application/json;charset=utf-8")
    public @ResponseBody
    ResponseEntity<String> solver(
            @RequestParam(value = "warehouse") Long bodegaId,
            @RequestParam Integer period,
            @RequestParam(defaultValue = "default") String mode,
            @RequestParam Boolean operationType,
            @RequestParam Boolean productType,
            @RequestParam Boolean tiLevel,
            @RequestParam Boolean samePeriod) {
        System.out.println("bodegaId es " + bodegaId);

        operationType = operationType == null ? false : operationType;
        productType = productType == null ? false : productType;
        tiLevel = tiLevel == null ? false : tiLevel;
        samePeriod = samePeriod == null ? false : samePeriod;

        JSONArray errors = new JSONArray();
        JSONObject jo = new JSONObject();
        Warehouse warehouse = Warehouse.findWarehouse(bodegaId);
        List<WarehouseData> warehouseData = new ArrayList<>();

        System.out.println("warehouse.getId() es" + warehouse.getId());
        System.out.println("nombre warehouse con " + warehouse.getName());
        System.out.println("modo: " + mode);

        WarehouseData selectedWD = WarehouseData.findWarehouseDataByWarehouseAndPeriod(warehouse, period);

        if (selectedWD == null) {
            jo.put("message", "Datos de bodegas no encontrados");
            return new ResponseEntity<>(jo.toString(), HttpStatus.BAD_REQUEST);
        }

        switch (mode) {
            case "default":
            case "user":
                warehouseData = selectedWD.findBenchmarking(mode, operationType, productType, tiLevel, samePeriod);
                break;
            case "self":
                warehouseData = selectedWD.findBenchmarking(mode, false, false, false, false);
                break;
            default:
                return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }

        if (warehouseData.size() < 2) {
            jo.put("message", "No se han encontrado más bodegas con datos en el periodo");
            return new ResponseEntity<>(jo.toString(), HttpStatus.BAD_REQUEST);
        }

        warehouseData.add(selectedWD);
        int len = warehouseData.size();
        int varLen = 8;

        System.out.println("Elementos a comparar " + len);

        String[] DMUNames = new String[len];

        String[] varNames = new String[varLen];
        VariableType[] varTypes = new VariableType[varLen];
        VariableOrientation[] varOrientations = new VariableOrientation[varLen];

        varNames[0] = "Superficie";
        varTypes[0] = VariableType.STANDARD;
        varOrientations[0] = VariableOrientation.INPUT;

        varNames[1] = "Inversión";
        varTypes[1] = VariableType.STANDARD;
        varOrientations[1] = VariableOrientation.INPUT;

        varNames[2] = "Horas de Trabajo";
        varTypes[2] = VariableType.STANDARD;
        varOrientations[2] = VariableOrientation.INPUT;

        varNames[3] = "Broken Case Lines";
        varTypes[3] = VariableType.STANDARD;
        varOrientations[3] = VariableOrientation.OUTPUT;

        varNames[4] = "Full Case Lines";
        varTypes[4] = VariableType.STANDARD;
        varOrientations[4] = VariableOrientation.OUTPUT;

        varNames[5] = "Pallet Case Lines";
        varTypes[5] = VariableType.STANDARD;
        varOrientations[5] = VariableOrientation.OUTPUT;

        varNames[6] = "Acumulación";
        varTypes[6] = VariableType.STANDARD;
        varOrientations[6] = VariableOrientation.OUTPUT;

        varNames[7] = "Almacenamiento";
        varTypes[7] = VariableType.STANDARD;
        varOrientations[7] = VariableOrientation.OUTPUT;

        double[][] testDataMatrix = new double[len][varLen];

        //Set up the Data Matrix and  DMU Names
        int indexToTest = warehouseData.size() - 1;
        for (int i = 0; i < warehouseData.size(); i++) {
            WarehouseData current = warehouseData.get(i);
            Warehouse currentWarehouse = current.getWarehouse();

            if (current.getOutputStorage() == null) {
                errors.add("No se pudo comparar con" + current.getWarehouse().getName() + "-" + current.getPeriod() + ": brokenCaseLines + fullCaseLines + palletLines debe ser distinto de 0");
                break;
            }

            String warehouseName = currentWarehouse.getName();
            if (mode.equals("self")) {
                warehouseName = warehouse.getName() + " - " + current.getPeriod();
            }

            DMUNames[i] = warehouseName;

            testDataMatrix[i][0] = current.getSquareMeters();
            testDataMatrix[i][1] = current.getInputTotalInvestment();
            testDataMatrix[i][2] = current.getTotalWorkforce();
            testDataMatrix[i][3] = (double) current.getOutput().getBrokenCaseLines();
            testDataMatrix[i][4] = (double) current.getOutput().getFullCaseLines();
            testDataMatrix[i][5] = (double) current.getOutput().getPalletLines();
            testDataMatrix[i][6] = current.getOutputAccumulation();
            testDataMatrix[i][7] = current.getOutputStorage();
        }

        if (indexToTest == -1) {
            System.out.println("Warehouse Data not Found");
        }

        //Create a DEAProblem and specify number of DMUs (cantidad de bodegas en el sistema) and number of variables (8).
        DEAProblem tester = new DEAProblem(len, varLen);
        DEAProblem testerO = new DEAProblem(len, varLen);

        //Set the DEA Problem Model Type (BCC Input Oriented y Output Oriented).
        tester.setModelType(ModelType.BCC_I);
        testerO.setModelType(ModelType.BCC_O);

        //Set the DEA Problem DMU Names where testDMUName is a double[].
        tester.setDMUNames(DMUNames);
        testerO.setDMUNames(DMUNames);

        //Set the DEA Problem Variable Names where testVariableName is a String[].
        tester.setVariableNames(varNames);
        testerO.setVariableNames(varNames);

        //Set the DEA Problem Variable Orientation where testVariableOrientation is a VariableOrientation[].
        tester.setVariableOrientations(varOrientations);
        testerO.setVariableOrientations(varOrientations);

        //Set the DEA Problem Variable Types where testVariableTypes is a VariableType[].
        tester.setVariableTypes(varTypes);
        testerO.setVariableTypes(varTypes);

        tester.setDataMatrix(testDataMatrix);
        testerO.setDataMatrix(testDataMatrix);

        try {
            //Solve the DEA Problem
            tester.solve();
            testerO.solve();

            jo.put("input", evaluateInputs(tester, indexToTest, DMUNames[indexToTest]));
            jo.put("output", evualateOutputs(testerO, indexToTest, DMUNames[indexToTest]));
            jo.put("errors", errors);
            return new ResponseEntity<>(jo.toString(), HttpStatus.OK);//Variable String que mostrará por pantalla la información del DEA solver
        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }

    }

    private JSONObject evaluateInputs(DEAProblem tester, int warehouseIndex, String warehouseName) throws Exception {
        JSONObject result = new JSONObject();

        int[] ranks = tester.getRanks(false, RankingType.STANDARD, 5);
        int rank = ranks[warehouseIndex];

        int len = ranks.length;

        double[] projectionPercentages = tester.getProjectionPercentages()[warehouseIndex];

        double[] objectives = tester.getObjectives();
        double performance = Math.round(objectives[warehouseIndex] * 100) / 100;

        double slack0 = tester.getSlack(warehouseIndex, 0);
        double slack1 = tester.getSlack(warehouseIndex, 1);
        double slack2 = tester.getSlack(warehouseIndex, 2);

        result.put("performance", "Para la bodega [" + warehouseName + "] el valor  de su rendimiento equivale a " + performance + " o " + (performance * 100) + "%.");
        if (tester.getEfficiencyStatus(warehouseIndex)) {
            //EN EL CASO QUE LA BODEGA PRESENTE EFICIENCIA COMPLETA
            int efficient = 0;
            for (int i = 0; i < ranks.length; i++) {
                if (i != warehouseIndex && ranks[i] <= rank) {
                    efficient++;
                }
            }

            result.put("projection", "Por lo tanto se concluye que su bodega trabaja de manera completamente eficienciente.");
            result.put("ranking", "Esta bodega ocupa el ranking " + rank + " junto a " + efficient + " bodegas más que trabajan eficientemente");
        } else if (performance < 1 && slack0 == 0 && slack1 == 0 && slack2 == 0) {
            //INEFICIENCIA RADIAL PERO TODAS SUS SLACKS SON IGUAL A CERO, ESTO ES SOLO SE DEBE REDUCIR DE MANERA RADIAL SUS ENTRADAS PARA ALCANZAR EFICIENCIA
            System.out.println("Entro en INEFICIENCIA RADIAL PERO TODAS SUS SLACKS SON IGUAL A CERO, ESTO ES SOLO SE DEBE REDUCIR DE MANERA RADIAL SUS ENTRADAS PARA ALCANZAR EFICIENCIA");
            result.put("projection", "Esto indica que [" + warehouseName + "] podría reducir sus ENTRADAS en un " + (100 - performance * 100) + "% en relación a sus valores actuales manteniendo al menos el mismo nivel de SALIDAS. ");
            result.put("ranking", "De un total de " + len + " bodegas en el sistema y bajo un análisis de eficiencia técnica relativa, [" + warehouseName + "] ocupa la posición " + rank + ".");
        } else {
            double surface = Math.round(projectionPercentages[0] * 100) / 100;
            double inversion = Math.round(projectionPercentages[1] * 100) / 100;
            double workforce = Math.round(projectionPercentages[2] * 100) / 100;

            String projection;
            if (performance < 1 && (slack0 != 0 || slack1 != 0 || slack2 != 0)) {
                //INEFICIENCIA RADIAL PERO Al MENOS UNAS DE SUS SLACKS SON DISTINTAS A CERO.
                System.out.println("entro en INEFICIENCIA RADIAL PERO Al MENOS UNAS DE SUS SLACKS SON DISTINTAS A CERO.");
                projection = "Esto indica que [" + warehouseName + "] podría reducir la(s) siguiente(s) ENTRADAS en: ";
            } else {
                System.out.println("Ninguna de las anteriores");
                projection = "Si bien presenta eficiencia esta no es completa, puesto que al menos una(s) de su(s) ENTRADA(s) no está(n) siendo aprovechada(s) de modo que [" + warehouseName + "] podría reducir la(s) siguiente(s) ENTRADA(S) en: ";
            }

            if (surface != 0) {
                projection += "SUPERFICIE = " + String.valueOf(surface * -100) + "% ";
            }
            if (inversion != 0) {
                projection += " INVERSION = " + String.valueOf(inversion * -100) + "%";
            }
            if (workforce != 0) {
                projection += " HORAS-HOMBRE = " + String.valueOf(workforce * -100) + "%";
            }

            projection += ", en relación a sus valores actuales manteniendo al menos el mismo nivel de SALIDAS.";
            result.put("projection", projection);
            result.put("ranking", "De un total de " + len + " bodegas en el sistema y bajo un análisis de eficiencia técnica relativa, [" + warehouseName + "] ocupa la posición " + rank + ".");
        }

        return result;
    }

    private JSONObject evualateOutputs(DEAProblem tester, int warehouseIndex, String warehouseName) throws Exception {
        JSONObject result = new JSONObject();
        int[] ranks = tester.getRanks(false, RankingType.STANDARD, 5);
        int rank = ranks[warehouseIndex];

        int len = ranks.length;

        double[] projectionPercentages = tester.getProjectionPercentages()[warehouseIndex];

        double[] objectives = tester.getObjectives();
        double performance = Math.round(objectives[warehouseIndex] * 100) / 100;

        double slack3 = tester.getSlack(warehouseIndex, 3);
        double slack4 = tester.getSlack(warehouseIndex, 4);
        double slack5 = tester.getSlack(warehouseIndex, 5);
        double slack6 = tester.getSlack(warehouseIndex, 6);
        double slack7 = tester.getSlack(warehouseIndex, 7);

        System.out.println(tester.getEfficiencyStatus(warehouseIndex));
        System.out.println("rendmiento de warehouse BCC-O" + warehouseIndex + " " + tester.getObjective(warehouseIndex));
        System.out.println("Max-slack output 4 " + tester.getSlack(warehouseIndex, 3));
        System.out.println("Max-slack output 5 " + tester.getSlack(warehouseIndex, 4));
        System.out.println("Max-slack output 6 " + tester.getSlack(warehouseIndex, 5));
        System.out.println("Max-slack output 7 " + tester.getSlack(warehouseIndex, 6));
        System.out.println("Max-slack output 8 " + tester.getSlack(warehouseIndex, 7));

        result.put("performance", "Para la bodega [" + warehouseName + "] el valor  de su rendimiento equivale a " + performance + " o " + (performance * 100) + "%.");
        if (tester.getEfficiencyStatus(warehouseIndex)) {
            //EN EL CASO QUE LA BODEGA PRESENTE EFICIENCIA COMPLETA
            int efficient = 0;
            for (int i = 0; i < ranks.length; i++) {
                if (i != warehouseIndex && ranks[i] <= rank) {
                    efficient++;
                }
            }

            result.put("projection", "Por lo tanto se concluye que su bodega trabaja de manera completamente eficienciente.");
            result.put("ranking", "Esta bodega ocupa el ranking " + rank + " junto a " + efficient + " bodegas más que trabajan eficientemente");
        } else if (performance < 1 && slack3 == 0 && slack4 == 0 && slack5 == 0 && slack6 == 0 && slack7 == 0) {
            //INEFICIENCIA RADIAL PERO TODAS SUS SLACKS SON IGUAL A CERO, ESTO ES SOLO SE DEBE AUMENTAR DE MANERA RADIAL SUS SALIDAS PARA ALCANZAR EFICIENCIA
            double efficiency = Math.round(100 / performance) / 100;
            result.put("projection", "Esto indica que [" + warehouseName + "] podría aumentar sus SALIDAS en un " + (efficiency * 100) + "% en relación a sus valores actuales mientras no se ocupen más recursos de ENTRADAS.");
            result.put("ranking", "De un total de " + len + " bodegas en el sistema y bajo un análisis de eficiencia técnica relativa, [" + warehouseName + "] ocupa la posición " + rank + ".");
        } else {
            double bcLines = 0;
            double fcLines = 0;
            double pLines = 0;
            double accumulation = 0;
            double storage = 0;

            if (!Double.isInfinite(projectionPercentages[3]) && !Double.isNaN(projectionPercentages[3])) {
                bcLines = Math.round(100 * projectionPercentages[3]) / 100;
            }

            if (!Double.isInfinite(projectionPercentages[4]) && !Double.isNaN(projectionPercentages[4])) {
                fcLines = Math.round(100 * projectionPercentages[4]) / 100;
            }

            if (!Double.isInfinite(projectionPercentages[5]) && !Double.isNaN(projectionPercentages[5])) {
                pLines = Math.round(100 * projectionPercentages[5]) / 100;
            }

            if (!Double.isInfinite(projectionPercentages[6]) && !Double.isNaN(projectionPercentages[6])) {
                accumulation = Math.round(100 * projectionPercentages[6]) / 100;
            }

            if (!Double.isInfinite(projectionPercentages[7]) && !Double.isNaN(projectionPercentages[7])) {
                storage = Math.round(100 * projectionPercentages[7]) / 100;
            }

            //System.out.println("valor redondeado de mejora para Broken Case"+bc);
            String bcLinesStr = bcLines == 0 ? "" : "BROKEN CASE LINES en " + (bcLines * 100) + "%";
            String fcLinesStr = fcLines == 0 ? "" : "FULL CASE LINES en " + (fcLines * 100) + "%";
            String pcLinesStr = pLines == 0 ? "" : "PALLET CASE LINES en " + (pLines * 100) + "%";
            String accumulationStr = accumulation == 0 ? "" : "ACUMULACIÓN en " + (accumulation * 100) + "%";
            String storageStr = storage == 0 ? "" : "ALMACENAMIENTO en " + (storage * 100) + "%";

            if (performance < 1 && (slack3 != 0 || slack4 != 0 || slack5 != 0 || slack6 != 0 || slack7 != 0)) {
                //INEFICIENCIA RADIAL PERO Al MENOS UNAS DE SUS SLACKS SON DISTINTAS A CERO.
                result.put("projection", "Esto indica que [" + warehouseName + "]  podría AUMENTAR la(s) siguiente(s) SALIDAS en: " + bcLinesStr + " " + fcLinesStr + " " + pcLinesStr + " " + accumulationStr + " " + storageStr + ", en relación a sus valores actuales manteniendo al menos el mismo nivel de ENTRADAS.");
                result.put("ranking", "De un total de " + len + " bodegas en el sistema y bajo un análisis de eficiencia técnica relativa, [" + warehouseName + "] ocupa la posición " + rank + ".");
            } else {
                result.put("projection", "Si bien presenta eficiencia esta no es completa, puesto que al menos una(s) de su(s) SALIDA(s) no está(n) siendo aprovechada(s) de modo que [" + warehouseName + "] podría AUMENTAR la(s) siguiente(s) SALIDAS en: " + bcLinesStr + " " + fcLinesStr + " " + pcLinesStr + " " + accumulationStr + " " + storageStr + ", en relación a sus valores actuales manteniendo al menos el mismo nivel de ENTRADAS.");
                result.put("ranking", "De un total de " + len + " bodegas en el sistema y bajo un análisis de eficiencia técnica relativa, [" + warehouseName + "] ocupa la posición " + rank + ".");
            }
        }

        return result;
    }

    @RequestMapping(value = "/test", produces = "text/json")
    public @ResponseBody
    ResponseEntity<String> test() {
        JSONObject jsonObject = new JSONObject();

        String[] varNames = new String[8];
        VariableOrientation[] varOrientations = new VariableOrientation[8];
        VariableType[] testVariableTypes = new VariableType[8];
        varOrientations[0] = VariableOrientation.INPUT;
        varOrientations[1] = VariableOrientation.INPUT;
        varOrientations[2] = VariableOrientation.INPUT;

        varOrientations[3] = VariableOrientation.OUTPUT;
        varOrientations[4] = VariableOrientation.OUTPUT;
        varOrientations[5] = VariableOrientation.OUTPUT;
        varOrientations[6] = VariableOrientation.OUTPUT;
        varOrientations[7] = VariableOrientation.OUTPUT;

        varNames[0] = "Superficie";
        varNames[1] = "Inversión";
        varNames[2] = "Horas de Trabajo";
        varNames[3] = "Broken Case Lines";
        varNames[4] = "Full Case Lines";
        varNames[5] = "Pallet Case Lines";
        varNames[6] = "Acumulación";
        varNames[7] = "Almacenamiento";

        for (int j = 0; j < 20; j++) {
            Warehouse warehouse = new Warehouse();
            warehouse.setName("Warehouse " + j);
            warehouse.setProductType(ProductType.Drinks);

            //warehouse.merge();

            WarehouseData wData = new WarehouseData();

            wData.setPeriod(2017);
            wData.setWarehouse(warehouse);

            WarehouseDataConveyor conveyor = new WarehouseDataConveyor();
            conveyor.setPowerBelt(2);
            //conveyor.merge();

            wData.setConveyor(conveyor);

            //wData.merge();
        }


        for (int i = 0; i < 8; i++) {
            testVariableTypes[i] = VariableType.STANDARD;
        }

        try {
            File f = new File("D:\\Documentos\\git\\rorro\\data.txt");
            List<String> lines = FileUtils.readLines(f, "UTF-8");
            int len = lines.size();
            String[] DMUNames = new String[len];

            //Create a DEAProblem and specify number of DMUs (cantidad de bodegas en el sistema) and number of variables (8).
            DEAProblem tester = new DEAProblem(len, 8);
            DEAProblem testerO = new DEAProblem(len, 8);

            //Set the DEA Problem Model Type (BCC Input Oriented y Output Oriented).
            tester.setModelType(ModelType.BCC_I);
            testerO.setModelType(ModelType.BCC_O);

            //Set the DEA Problem DMU Names where testDMUName is a double[].
            tester.setDMUNames(DMUNames);
            testerO.setDMUNames(DMUNames);

            //Set the DEA Problem Variable Names where testVariableName is a String[].
            tester.setVariableNames(varNames);
            testerO.setVariableNames(varNames);

            //Set the DEA Problem Variable Orientation where testVariableOrientation is a VariableOrientation[].
            tester.setVariableOrientations(varOrientations);
            testerO.setVariableOrientations(varOrientations);

            //Set the DEA Problem Variable Types where testVariableTypes is a VariableType[].
            tester.setVariableTypes(testVariableTypes);
            testerO.setVariableTypes(testVariableTypes);

            double[][] testDataMatrix = new double[len][8];

            int i = 0;
            for (String line : lines) {
                line = line.replaceAll(" ", "");
                DMUNames[i] = "Bodega " + i;
                String[] split = line.split("\t");

                System.out.println(StringUtils.join(split, ","));


                testDataMatrix[i][0] = Double.parseDouble(split[7]) * 0.3048;
                testDataMatrix[i][1] = Double.parseDouble(split[1]);
                testDataMatrix[i][2] = Double.parseDouble(split[0]);
                testDataMatrix[i][3] = Double.parseDouble(split[2]);
                testDataMatrix[i][4] = Double.parseDouble(split[3]);
                testDataMatrix[i][5] = Double.parseDouble(split[4]);
                testDataMatrix[i][6] = Double.parseDouble(split[6]);
                testDataMatrix[i][7] = Double.parseDouble(split[5]);

                i++;
                if (i >= len) {
                    break;
                }

                System.out.println(StringUtils.join(split, ","));
            }

            tester.setDataMatrix(testDataMatrix);
            testerO.setDataMatrix(testDataMatrix);
            tester.solve();
            testerO.solve();
            double[] results = tester.getObjectives();
            for (i = 0; i < results.length; i++) {
                jsonObject.put("Bodega " + i, results[i]);
            }

        } catch (Exception e) {
            e.printStackTrace();
            jsonObject.put("error", e.getMessage());
        }

        return new ResponseEntity<>(jsonObject.toString(), HttpStatus.OK);
    }
}

