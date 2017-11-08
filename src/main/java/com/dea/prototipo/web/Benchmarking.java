package com.dea.prototipo.web;

import com.dea.prototipo.domain.User;
import com.dea.prototipo.domain.WarehouseData;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import org.opensourcedea.dea.*;

import java.math.BigDecimal;
import java.math.RoundingMode;

import java.util.*;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.dea.prototipo.domain.Warehouse;

@RequestMapping("/member/benchmarking")
@Controller
public class Benchmarking {

    @RequestMapping(value = "")
    public String benchmarking(Model uiModel) {
        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        uiModel.addAttribute("warehouses", Warehouse.findWarehousesByUser(user).getResultList());
        return "member/benchmarking/index";
    }

    @RequestMapping(value = "/solver", produces = "text/json")
    public @ResponseBody
    ResponseEntity<String> solver(@RequestParam(value = "warehouse") Long bodegaId) {
        System.out.println("bodegaId es " + bodegaId);
        int aux = 0;
        int cont = 0;
        String rendimiento = "";
        String ranking = "";
        String sup = "";
        String inv = "";
        String hr = "";
        String broken = "";
        String full = "";
        String pallet = "";
        String acumu = "";
        String almac = "";
        String bod = "";
        String proyeccion = "";
        String rendimientoO = "";
        String proyeccionO = "";
        String rankingO = "";
        JSONArray errors = new JSONArray();

        Warehouse warehouse = Warehouse.findWarehouse(bodegaId);
        int id = warehouse.getId().intValue();
        bod = warehouse.getName();
        String[] testDMUNames;
        String[] testVariableNames = new String[8];
        VariableOrientation[] testVariableOrientations = new VariableOrientation[8];
        VariableType[] testVariableTypes = new VariableType[8];
        double[][] testDataMatrix;
        List<WarehouseData> warehouseData = WarehouseData.findAllWarehouseData(); //SE DEBE BUSCAR LOS ÚLTIMOS DATOS REGISTRADOS PARA CADA BODEGA EN EL SISTEMA
        int largo = warehouseData.size();
        testDMUNames = new String[largo];
        testDataMatrix = new double[largo][8];

        System.out.println("warehouse.getId() es" + id);
        System.out.println("nombre warehouse con warehouse.getNombre()" + bod);
        System.out.println(largo);

        //Set up the Variable Names
        testVariableNames[0] = "Superficie";
        testVariableNames[1] = "Inversión";
        testVariableNames[2] = "Horas de Trabajo";
        testVariableNames[3] = "Broken Case Lines";
        testVariableNames[4] = "Full Case Lines";
        testVariableNames[5] = "Pallet Case Lines";
        testVariableNames[6] = "Acumulación";
        testVariableNames[7] = "Almacenamiento";

        //Set up the Data Matrix and  DMU Names
        for (int i = 0; i < warehouseData.size(); i++) {
            WarehouseData current = warehouseData.get(i);

            testDMUNames[i] = current.getWarehouse().getName();

            if(current.getOutputStorage() == null) {
                errors.add("No se pudo comparar con" + current.getWarehouse().getName() + "-" + current.getPeriod() + ": brokenCaseLines + fullCaseLines + palletLines debe ser distinto de 0");
                break;
            }

            testDataMatrix[i][0] = current.getSquareMeters();
            testDataMatrix[i][1] = current.getInputTotalInvestment();
            testDataMatrix[i][2] = current.getTotalWorkforce();
            testDataMatrix[i][3] = (double) current.getOutput().getBrokenCaseLines();
            testDataMatrix[i][4] = (double) current.getOutput().getFullCaseLines();
            testDataMatrix[i][5] = (double) current.getOutput().getPalletLines();
            testDataMatrix[i][6] = current.getOutputAccumulation();
            testDataMatrix[i][7] = current.getOutputStorage(); // TODO: es output storage ???
        }
        //Set up the variable types

        testVariableOrientations[0] = VariableOrientation.INPUT;
        testVariableOrientations[1] = VariableOrientation.INPUT;
        testVariableOrientations[2] = VariableOrientation.INPUT;

        testVariableOrientations[3] = VariableOrientation.OUTPUT;
        testVariableOrientations[4] = VariableOrientation.OUTPUT;
        testVariableOrientations[5] = VariableOrientation.OUTPUT;
        testVariableOrientations[6] = VariableOrientation.OUTPUT;
        testVariableOrientations[7] = VariableOrientation.OUTPUT;

        for (int i = 0; i < 7; i++) {
            testVariableTypes[i] = VariableType.STANDARD;
        }

        //Create a DEAProblem and specify number of DMUs (cantidad de bodegas en el sistema) and number of variables (8).
        DEAProblem tester = new DEAProblem(largo, 8);
        DEAProblem testerO = new DEAProblem(largo, 8);

        //Set the DEA Problem Model Type (BCC Input Oriented y Output Oriented).
        tester.setModelType(ModelType.BCC_I);
        testerO.setModelType(ModelType.BCC_O);

        //Set the DEA Problem DMU Names where testDMUName is a double[].
        tester.setDMUNames(testDMUNames);
        testerO.setDMUNames(testDMUNames);

        //Set the DEA Problem Variable Names where testVariableName is a String[].
        tester.setVariableNames(testVariableNames);
        testerO.setVariableNames(testVariableNames);

        //Set the DEA Problem Variable Orientation where testVariableOrientation is a VariableOrientation[].
        tester.setVariableOrientations(testVariableOrientations);
        testerO.setVariableOrientations(testVariableOrientations);

        //Set the DEA Problem Variable Types where testVariableTypes is a VariableType[].
        tester.setVariableTypes(testVariableTypes);
        testerO.setVariableTypes(testVariableTypes);

        tester.setDataMatrix(testDataMatrix);
        testerO.setDataMatrix(testDataMatrix);


        try {
            //Solve the DEA Problem
            tester.solve();
            testerO.solve();
            //Get the solution Objectives
            double[] objectives = tester.getObjectives();
            double[] objectivesO = testerO.getObjectives();

            double[][] pp = tester.getProjectionPercentages();
            double[][] ppO = testerO.getProjectionPercentages();

            ArrayList<NonZeroLambda>[] referenceSets = new ArrayList[largo];
            referenceSets = tester.getReferenceSet();

            double[][] slacks = tester.getSlacks();
            double[][] slacksO = testerO.getSlacks();

            double[][] projections = tester.getProjections();

            double[][] weights = tester.getWeight();

            int[] ranks = tester.getRanks(false, RankingType.STANDARD, 5);
            int[] ranksO = testerO.getRanks(false, RankingType.STANDARD, 5);

            //Busca la warehouse en el arreglo que se está consultando por el usuario.
            for (int k = 0; k < largo; k++) {
                if (testDMUNames[k].equals(warehouse.getName())) {
                    aux = k;
                    System.out.println("La warehouse a consultar es " + testDMUNames[k] + " y la posición a consultar será " + aux);
                }

            }
            for (int k = 0; k < largo; k++) {
                if (ranks[k] <= ranks[aux]) {
                    cont++;
                }
                for (int i = 0; i < largo; i++) {
                    if (ranks[i] == k) {
                        System.out.println(testDMUNames[i] + ": " + ranks[i]);
                    }
                }

            }

            System.out.println(cont + " menores que [" + bod + "] y su ranking es " + ranks[aux]);
            System.out.println(tester.getEfficiencyStatus(aux));
            System.out.println("rendmiento de warehouse " + aux + " " + tester.getObjective(aux));
            System.out.println("Max-slack input 1 " + tester.getSlack(aux, 0));
            System.out.println("Max-slack input 2 " + tester.getSlack(aux, 1));
            System.out.println("Max-slack input 3 " + tester.getSlack(aux, 2));
            BigDecimal rend = new BigDecimal(objectives[aux]);
            BigDecimal rendi = rend.setScale(1, RoundingMode.HALF_UP);
            if (tester.getEfficiencyStatus(aux)) {
                //EN EL CASO QUE LA BODEGA PRESENTE EFICIENCIA COMPLETA
                BigDecimal eficiencia = new BigDecimal(objectives[aux]);
                BigDecimal ef = eficiencia.setScale(1, RoundingMode.HALF_UP);
                rendimiento = "Para la warehouse [" + bod + "] el valor  de su rendimiento equivale a " + String.valueOf(ef) + " o " + String.valueOf(ef.intValue() * 100) + "%. ";
                proyeccion = "Por lo tanto se concluye que su bodega trabaja de manera completamente eficienciente.";
                ranking = "Esta warehouse ocupa el ranking " + ranks[aux] + " junto a " + cont + " bodegas más que trabajan eficientemente";
            } else if (rendi.doubleValue() < 1 && tester.getSlack(aux, 0) == 0 && tester.getSlack(aux, 1) == 0 && tester.getSlack(aux, 2) == 0) {
                //INEFICIENCIA RADIAL PERO TODAS SUS SLACKS SON IGUAL A CERO, ESTO ES SOLO SE DEBE REDUCIR DE MANERA RADIAL SUS ENTRADAS PARA ALCANZAR EFICIENCIA
                System.out.println("Entro en INEFICIENCIA RADIAL PERO TODAS SUS SLACKS SON IGUAL A CERO, ESTO ES SOLO SE DEBE REDUCIR DE MANERA RADIAL SUS ENTRADAS PARA ALCANZAR EFICIENCIA"
                );
                BigDecimal eficiencia = new BigDecimal(objectives[aux]);
                BigDecimal ef = eficiencia.setScale(2, RoundingMode.HALF_UP);
                double efp = objectives[aux] * 100;
                BigDecimal efiporc = new BigDecimal(efp);
                BigDecimal epr = efiporc.setScale(0, RoundingMode.HALF_UP);
                rendimiento = "Para la warehouse [" + bod + "] el valor  de su rendimiento equivale a " + String.valueOf(ef) + " o " + String.valueOf(epr) + "%. ";

                proyeccion = "Esto indica que [" + bod + "] podría reducir sus ENTRADAS en un " + (100 - epr.doubleValue()) + "% en relación a sus valores actuales manteniendo al menos el mismo nivel de SALIDAS. ";
                ranking = "De un total de " + largo + " bodegas en el sistema y bajo un análisis de eficiencia técnica relativa, [" + bod + "] ocupa la posición " + ranks[aux] + ".";
                System.out.println();
                System.out.println("DMUs Max-slack Solution");
                System.out.println();
                System.out.println();
                System.out.println("Max-slack Solution " + testDMUNames[aux] + ":");
                for (int j = 0; j < 8; j++) {
                    System.out.println(testVariableOrientations[j] + " " + testVariableNames[j] + ":   " + slacks[aux][j]);

                }

				/*for (int k = 0; k < largo; k++) {
                    System.out.println(testDMUNames[k] + ": " );
					for(int j=0;j<8;j++){
						System.out.println(testDataMatrix[k][j]);
					}
				}*/

                System.out.println();
                System.out.println("DMUs Ratio Efficiency BCC INPUT");
                System.out.println();
                for (int i = 0; i < largo; i++) {
                    System.out.println("Ratio Efficiency " + testDMUNames[i] + ":   " + objectives[i]);

                }
                /*System.out.println();
                System.out.println("DMUs projections porcentaje");
				System.out.println();
				for (int i = 0; i < largo; i++) {
					System.out.println();
					System.out.println("projections porcentaje solution " +testDMUNames[i] + ":");
					for (int j = 0; j < 3; j++) {
						System.out.println( + pp[i][j]);

					}
				}*/

            } else if (tester.getObjective(aux) < 1 && tester.getSlack(aux, 0) != 0 || tester.getSlack(aux, 1) != 0 || tester.getSlack(aux, 2) != 0) {
                //INEFICIENCIA RADIAL PERO Al MENOS UNAS DE SUS SLACKS SON DISTINTAS A CERO.
                System.out.println("entro en INEFICIENCIA RADIAL PERO Al MENOS UNAS DE SUS SLACKS SON DISTINTAS A CERO.");
                BigDecimal eficiencia = new BigDecimal(objectives[aux]);
                BigDecimal ef = eficiencia.setScale(2, RoundingMode.HALF_UP);
                double efp = objectives[aux] * 100;
                BigDecimal efiporc = new BigDecimal(efp);
                BigDecimal epr = efiporc.setScale(0, RoundingMode.HALF_UP);
                rendimiento = "Para la warehouse [" + bod + "] el valor  de su rendimiento equivale a " + String.valueOf(ef) + " o " + String.valueOf(epr) + "%. ";

                BigDecimal su = new BigDecimal(pp[aux][0]);
                BigDecimal sur = su.setScale(2, RoundingMode.HALF_UP);
                BigDecimal in = new BigDecimal(pp[aux][1]);
                BigDecimal inr = in.setScale(2, RoundingMode.HALF_UP);
                BigDecimal hh = new BigDecimal(pp[aux][2]);
                BigDecimal hhr = hh.setScale(2, RoundingMode.HALF_UP);
                System.out.println("Valor eficiencia " + rendi.doubleValue());
                System.out.println("valor redondeado de mejora para superficie " + sur);
                if (sur.doubleValue() != 0) {
                    sup = "SUPERFICIE = " + String.valueOf(sur.doubleValue() * -100) + "%";
                }
                if
                        (inr.doubleValue() != 0) {
                    inv = " INVERSION = " + String.valueOf(inr.doubleValue() * -100) + "%";
                }
                if
                        (hhr.doubleValue() != 0) {
                    hr = " HORAS-HOMBRE = " + String.valueOf(hhr.doubleValue() * -100) + "%";
                }


                proyeccion = "Esto indica que [" + bod + "] podría reducir la(s) siguiente(s) ENTRADAS en: " + " " + sup + " " + " " + inv + " " + " " + hr + " " + ", en relación a sus valores actuales manteniendo al menos el mismo nivel de SALIDAS.";
                ranking = "De un total de " + largo + " bodegas en el sistema y bajo un análisis de eficiencia técnica relativa, [" + bod + "] ocupa la posición " + ranks[aux] + ".";
                System.out.println();
                System.out.println("DMUs Max-slack Solution");
                System.out.println();
                System.out.println();
                System.out.println("Max-slack Solution " + testDMUNames[aux] + ":");
                for (int j = 0; j < 8; j++) {
                    System.out.println(testVariableOrientations[j] + " " + testVariableNames[j] + ":   " + slacks[aux][j]);

                }
				    				
			    				/*for (int k = 0; k < largo; k++) {
				    		    System.out.println(testDMUNames[k] + ": " );
		    						for(int j=0;j<8;j++){
		    						System.out.println(testDataMatrix[k][j]);
		    						}
		    					}*/

                System.out.println();
                System.out.println("DMUs Ratio Efficiency BCC INPUT");
                System.out.println();
                for (int i = 0; i < largo; i++) {
                    System.out.println("Ratio Efficiency " + testDMUNames[i] + ":   " + objectives[i]);

                }
				    				/*System.out.println();
				    				System.out.println("DMUs projections porcentaje");
				    				System.out.println();
				    				for (int i = 0; i < largo; i++) {
			    					System.out.println();
			    					System.out.println("projections porcentaje solution " +testDMUNames[i] + ":");
				    					for (int j = 0; j < 3; j++) {
				    						System.out.println( + pp[i][j]);

				    					}
				    				}*/

            } else {
                System.out.println("Ninguna de las anteriores");
                BigDecimal eficiencia = new BigDecimal(objectives[aux]);
                BigDecimal ef = eficiencia.setScale(2, RoundingMode.HALF_UP);
                double efp = objectives[aux] * 100;
                BigDecimal efiporc = new BigDecimal(efp);
                BigDecimal epr = efiporc.setScale(0, RoundingMode.HALF_UP);
                rendimiento = "Para la warehouse [" + bod + "] el valor  de su rendimiento equivale a " + String.valueOf(ef) + " o " + String.valueOf(epr) + "%. ";

                BigDecimal su = new BigDecimal(pp[aux][0]);
                BigDecimal sur = su.setScale(2, RoundingMode.HALF_UP);
                BigDecimal in = new BigDecimal(pp[aux][1]);
                BigDecimal inr = in.setScale(2, RoundingMode.HALF_UP);
                BigDecimal hh = new BigDecimal(pp[aux][2]);
                BigDecimal hhr = hh.setScale(2, RoundingMode.HALF_UP);
                System.out.println("valor redondeado de mejora para superficie" + sur);
                if (sur.doubleValue() != 0) {
                    sup = "SUPERFICIE = " + String.valueOf(sur.doubleValue() * -100) + "%";
                }
                if
                        (inr.doubleValue() != 0) {
                    inv = " INVERSION = " + String.valueOf(inr.doubleValue() * -100) + "%";
                }
                if
                        (hhr.doubleValue() != 0) {
                    hr = " HORAS-HOMBRE = " + String.valueOf(hhr.doubleValue() * -100) + "%";
                }


                proyeccion = "Si bien presenta eficiencia esta no es completa, puesto que al menos una(s) de su(s) ENTRADA(s) no está(n) siendo aprovechada(s) de modo que [" + bod + "] podría reducir la(s) siguiente(s) ENTRADA(S) en: " + " " + sup + " " + " " + inv + " " + " " + hr + " " + ", en relación a sus valores actuales manteniendo al menos el mismo nivel de SALIDAS.";
                ranking = "De un total de " + largo + " bodegas en el sistema y bajo un análisis de eficiencia técnica relativa, [" + bod + "] ocupa la posición " + ranks[aux] + ".";
                System.out.println();
                System.out.println("DMUs Max-slack Solution");
                System.out.println();
                System.out.println();
                System.out.println("Max-slack Solution " + testDMUNames[aux] + ":");
                for (int j = 0; j < 8; j++) {
                    System.out.println(testVariableOrientations[j] + " " + testVariableNames[j] + ":   " + slacks[aux][j]);

                }
				    				
			    				/*for (int k = 0; k < largo; k++) {
				    		    System.out.println(testDMUNames[k] + ": " );
		    						for(int j=0;j<8;j++){
		    						System.out.println(testDataMatrix[k][j]);
		    						}
		    					}*/

                System.out.println();
                System.out.println("DMUs Ratio Efficiency BCC INPUT");
                System.out.println();
                for (int i = 0; i < largo; i++) {
                    System.out.println("Ratio Efficiency " + testDMUNames[i] + ":   " + objectives[i]);

                }
            }
            //SOLVER PARA ORIENTACION OUTPUT
            cont = 0;
            for (int k = 0; k < largo; k++) {
                if (ranksO[k] <= ranksO[aux]) {
                    cont++;
                }
                for (int i = 0; i < largo; i++) {
                    if (ranksO[i] == k) {
                        System.out.println(testDMUNames[i] + ": " + ranksO[i]);
                    }
                }

            }

            System.out.println(cont + "menores que [" + bod + "] y su ranking es " + ranksO[aux]);
            System.out.println(testerO.getEfficiencyStatus(aux));
            System.out.println("rendmiento de warehouse BCC-O" + aux + " " + testerO.getObjective(aux));
            System.out.println("Max-slack output 4 " + testerO.getSlack(aux, 3));
            System.out.println("Max-slack output 5 " + testerO.getSlack(aux, 4));
            System.out.println("Max-slack output 6 " + testerO.getSlack(aux, 5));
            System.out.println("Max-slack output 7 " + testerO.getSlack(aux, 6));
            System.out.println("Max-slack output 8 " + testerO.getSlack(aux, 7));

            BigDecimal rendO = new BigDecimal(objectivesO[aux]);
            BigDecimal rendiO = rendO.setScale(1, RoundingMode.HALF_UP);
            if (testerO.getEfficiencyStatus(aux)) {
                //EN EL CASO QUE LA BODEGA PRESENTE EFICIENCIA COMPLETA
                BigDecimal eficienciaO = new BigDecimal(objectivesO[aux]);
                BigDecimal efO = eficienciaO.setScale(1, RoundingMode.HALF_UP);
                rendimientoO = "Para la warehouse [" + bod + "] el valor  de su rendimiento equivale a " + String.valueOf(efO) + " o " + String.valueOf(efO.intValue() * 100) + "%. ";
                proyeccionO = "Por lo tanto se concluye que su bodega trabaja de manera completamente eficienciente.";
                rankingO = "Esta warehouse ocupa el ranking " + ranksO[aux] + " junto a " + cont + " bodegas más que trabajan eficientemente";

                System.out.println(Arrays.toString(ranksO));
                System.out.println(aux);
            } else if (rendiO.doubleValue() < 1 && testerO.getSlack(aux, 3) == 0 && testerO.getSlack(aux, 4) == 0 && testerO.getSlack(aux, 5) == 0 && testerO.getSlack(aux, 6) == 0 && testerO.getSlack(aux, 7) == 0) {
                //INEFICIENCIA RADIAL PERO TODAS SUS SLACKS SON IGUAL A CERO, ESTO ES SOLO SE DEBE AUMENTAR DE MANERA RADIAL SUS SALIDAS PARA ALCANZAR EFICIENCIA
                double efo = 1 / objectivesO[aux];
                BigDecimal eficienciaO = new BigDecimal(efo);
                BigDecimal efO = eficienciaO.setScale(2, RoundingMode.HALF_UP);
                double efpO = (1 / objectivesO[aux]) * 100;
                BigDecimal efiporcO = new BigDecimal(efpO);
                BigDecimal eprO = efiporcO.setScale(0, RoundingMode.HALF_UP);
                rendimientoO = "Para la warehouse [" + bod + "] el valor  de su rendimiento equivale a " + String.valueOf(efO) + " o " + String.valueOf(eprO) + "%. ";
                proyeccionO = "Esto indica que [" + bod + "] podría aumentar sus SALIDAS en un " + eprO.doubleValue() + "% en relación a sus valores actuales mientras no se ocupen más recursos de ENTRADAS.";
                rankingO = "De un total de " + largo + " bodegas en el sistema y bajo un análisis de eficiencia técnica relativa, [" + bod + "] ocupa la posición " + ranksO[aux] + ".";

                System.out.println();
                System.out.println("DMUs Max-slack Solution BCC-O");
                System.out.println();
                System.out.println();
                System.out.println("Max-slack Solution " + testDMUNames[aux] + ":");
                for (int j = 0; j < 8; j++) {
                    System.out.println(testVariableOrientations[j] + " " + testVariableNames[j] + ":   " + slacksO[aux][j]);

                }

				/*for (int k = 0; k < largo; k++) {
					System.out.println(testDMUNames[k] + ": " );
					for(int j=0;j<8;j++){
						System.out.println(testDataMatrix[k][j]);
					}
				}*/

                System.out.println();
                System.out.println("DMUs Ratio Efficiency BCC OUTPUT");
                System.out.println();
                for (int i = 0; i < largo; i++) {
                    System.out.println("Ratio Efficiency " + testDMUNames[i] + ":   " + objectivesO[i]);

                }
				/*System.out.println();
				System.out.println("DMUs projections porcentaje");
				System.out.println();
				for (int i = 0; i < largo; i++) {
					System.out.println();
					System.out.println("projections porcentaje solution " +testDMUNames[i] + ":");
					for (int j = 0; j < 3; j++) {
						System.out.println( + ppO[i][j]);

					}
				}*/

            } else if (tester.getObjective(aux) < 1 && (testerO.getSlack(aux, 3) != 0 || testerO.getSlack(aux, 4) != 0 || testerO.getSlack(aux, 5) != 0 || testerO.getSlack(aux, 6) != 0 || testerO.getSlack(aux, 7) != 0)) {
                //INEFICIENCIA RADIAL PERO Al MENOS UNAS DE SUS SLACKS SON DISTINTAS A CERO.
                double b = 0;
                double f = 0;
                double p = 0;
                double a = 0;
                double s = 0;
                double efo = 1 / objectivesO[aux];
                BigDecimal eficienciaO = new BigDecimal(efo);
                BigDecimal efO = eficienciaO.setScale(2, RoundingMode.HALF_UP);
                double efpO = (1 / objectivesO[aux]) * 100;
                BigDecimal efiporcO = new BigDecimal(efpO);
                BigDecimal eprO = efiporcO.setScale(0, RoundingMode.HALF_UP);
                rendimientoO = "Para la warehouse [" + bod + "] el valor  de su rendimiento equivale a " + String.valueOf(efO) + " o " + String.valueOf(eprO) + "%. ";
                System.out.println("Porcentaje de proyección para " + aux + " es " + ppO[aux][3]);

                if (Double.isInfinite(ppO[aux][3]) || Double.isNaN(ppO[aux][3])) {
                    System.out.println("Entro y Broken Case y su % de mejora es " + ppO[aux][3]);
                    b = 0;
                } else {
                    BigDecimal bc = new BigDecimal(ppO[aux][3]);
                    BigDecimal bcr = bc.setScale(2, RoundingMode.HALF_UP);
                    b = bcr.doubleValue();
                }
                if (Double.isInfinite(ppO[aux][4]) || Double.isNaN(ppO[aux][4])) {
                    BigDecimal fc = new BigDecimal(0);
                    BigDecimal fcr = fc.setScale(0, RoundingMode.HALF_UP);
                    f = fcr.doubleValue();

                } else {
                    BigDecimal fc = new BigDecimal(ppO[aux][4]);
                    BigDecimal fcr = fc.setScale(2, RoundingMode.HALF_UP);
                    f = fcr.doubleValue();

                }
                if (Double.isInfinite(ppO[aux][5]) || Double.isNaN(ppO[aux][5])) {
                    BigDecimal pl = new BigDecimal(0);
                    BigDecimal plr = pl.setScale(0, RoundingMode.HALF_UP);
                    p = plr.doubleValue();

                } else {
                    BigDecimal pl = new BigDecimal(ppO[aux][5]);
                    BigDecimal plr = pl.setScale(2, RoundingMode.HALF_UP);
                    p = plr.doubleValue();

                }
                if (Double.isInfinite(ppO[aux][6]) || Double.isNaN(ppO[aux][6])) {
                    BigDecimal ac = new BigDecimal(0);
                    BigDecimal acr = ac.setScale(0, RoundingMode.HALF_UP);
                    a = acr.doubleValue();

                } else {
                    BigDecimal ac = new BigDecimal(ppO[aux][6]);
                    BigDecimal acr = ac.setScale(2, RoundingMode.HALF_UP);
                    a = acr.doubleValue();
                }
                if (Double.isInfinite(ppO[aux][6]) || Double.isNaN(ppO[aux][6])) {
                    BigDecimal alm = new BigDecimal(0);
                    BigDecimal almr = alm.setScale(0, RoundingMode.HALF_UP);
                    s = almr.doubleValue();

                } else {
                    BigDecimal alm = new BigDecimal(ppO[aux][7]);
                    BigDecimal almr = alm.setScale(2, RoundingMode.HALF_UP);
                    s = almr.doubleValue();
                }

                //System.out.println("valor redondeado de mejora para Broken Case"+bc);
                if (b != 0) {
                    broken = "BROKEN CASE LINES en " + String.valueOf(b * 100) + "%";
                    System.out.println("entro al si tiene mejora");
                } else {
                    broken = "BROKEN CASE LINES en 0%";
                    System.out.println("Entro al no tiene mejora");
                }
                if (f != 0) {
                    full = " FULL CASE LINES en " + String.valueOf(f * 100) + "%";
                } else {
                    full = " FULL CASE LINES EN 0%";
                }
                if (p != 0) {
                    pallet = " PALLET CASE LINES en " + String.valueOf(p * 100) + "%";
                } else {
                    pallet = " PALLET CASE LINES en 0%";
                }
                if (a != 0) {
                    acumu = " ACUMULACIÓN en " + String.valueOf(a * 100) + "%";
                } else {
                    acumu = " ACUMULAIÓN en 0%";
                }
                if (s != 0) {
                    almac = " ALMACENAMIENTO en " + String.valueOf(s * 100) + "%";
                } else {
                    almac = " ALMACENAMIENTO en 0%";
                }


                proyeccionO = "Esto indica que [" + bod + "] podría AUMENTAR la(s) siguiente(s) SALIDAS en: " + " " + broken + " " + " " + full + " " + " " + pallet + " " + acumu + " " + almac + " , en relación a sus valores actuales manteniendo al menos el mismo nivel de ENTRADAS.";
                rankingO = "De un total de " + largo + " bodegas en el sistema y bajo un análisis de eficiencia técnica relativa, [" + bod + "] ocupa la posición " + ranksO[aux] + ".";
                System.out.println();
                System.out.println("DMUs Max-slack Solution");
                System.out.println();
                System.out.println();
                System.out.println("Max-slack Solution " + testDMUNames[aux] + ":");
                for (int j = 0; j < 8; j++) {
                    System.out.println(testVariableOrientations[j] + " " + testVariableNames[j] + ":   " + slacksO[aux][j]);

                }
	    				
    				/*for (int k = 0; k < largo; k++) {
	    		    System.out.println(testDMUNames[k] + ": " );
						for(int j=0;j<8;j++){
						System.out.println(testDataMatrix[k][j]);
						}
					}*/

                System.out.println();
                System.out.println("DMUs Ratio Efficiency");
                System.out.println();
                for (int i = 0; i < largo; i++) {
                    System.out.println("Ratio Efficiency " + testDMUNames[i] + ":   " + objectivesO[i]);

                }
	    				/*System.out.println();
	    				System.out.println("DMUs projections porcentaje");
	    				System.out.println();
	    				for (int i = 0; i < largo; i++) {
    					System.out.println();
    					System.out.println("projections porcentaje solution " +testDMUNames[i] + ":");
	    					for (int j = 0; j < 3; j++) {
	    						System.out.println( + pp[i][j]);

	    					}
	    				}*/

            } else {

                double efo = 1 / objectivesO[aux];
                BigDecimal eficienciaO = new BigDecimal(efo);
                BigDecimal efO = eficienciaO.setScale(2, RoundingMode.HALF_UP);
                double efpO = (1 / objectivesO[aux]) * 100;
                BigDecimal efiporcO = new BigDecimal(efpO);
                BigDecimal eprO = efiporcO.setScale(0, RoundingMode.HALF_UP);
                rendimientoO = "Para la warehouse [" + bod + "] el valor  de su rendimiento equivale a " + String.valueOf(efO) + " o " + String.valueOf(eprO) + "%. ";
                BigDecimal bc = new BigDecimal(ppO[aux][3]);
                BigDecimal bcr = bc.setScale(2, RoundingMode.HALF_UP);
                BigDecimal fc = new BigDecimal(ppO[aux][4]);
                BigDecimal fcr = fc.setScale(2, RoundingMode.HALF_UP);
                BigDecimal pl = new BigDecimal(ppO[aux][5]);
                BigDecimal plr = pl.setScale(2, RoundingMode.HALF_UP);
                BigDecimal ac = new BigDecimal(ppO[aux][6]);
                BigDecimal acr = ac.setScale(2, RoundingMode.HALF_UP);
                BigDecimal alm = new BigDecimal(ppO[aux][7]);
                BigDecimal almr = alm.setScale(2, RoundingMode.HALF_UP);
                System.out.println("valor redondeado de mejora para Broken Case" + bc);
                if (bcr.doubleValue() != 0) {
                    broken = "BROKEN CASE LINES en " + String.valueOf(bcr.doubleValue() * 100) + "%";
                }
                if (fcr.doubleValue() != 0) {
                    full = " FULL CASE LINES en " + String.valueOf(fcr.doubleValue() * 100) + "%";
                }
                if (plr.doubleValue() != 0) {
                    pallet = " PALLET CASE LINES en " + String.valueOf(plr.doubleValue() * 100) + "%";
                }
                if (acr.doubleValue() != 0) {
                    acumu = " ACUMULACIÓN en " + String.valueOf(acr.doubleValue() * 100) + "%";
                }
                if (almr.doubleValue() != 0) {
                    almac = " ALMACENAMIENTO en " + String.valueOf(almr.doubleValue() * 100) + "%";
                }


                proyeccionO = "Si bien presenta eficiencia esta no es completa, puesto que al menos una(s) de su(s) SALIDA(s) no está(n) siendo aprovechada(s) de modo que [" + bod + "] podría AUMENTAR la(s) siguiente(s) SALIDAS en: " + " " + broken + " " + " " + full + " " + " " + pallet + " " + acumu + " " + almac + " , en relación a sus valores actuales manteniendo al menos el mismo nivel de ENTRADAS.";
                rankingO = "De un total de " + largo + " bodegas en el sistema y bajo un análisis de eficiencia técnica relativa, [" + bod + "] ocupa la posición " + ranksO[aux] + ".";
                System.out.println();
                System.out.println("DMUs Max-slack Solution");
                System.out.println();
                System.out.println();
                System.out.println("Max-slack Solution " + testDMUNames[aux] + ":");
                for (int j = 0; j < 8; j++) {
                    System.out.println(testVariableOrientations[j] + " " + testVariableNames[j] + ":   " + slacksO[aux][j]);

                }
	    				
    				/*for (int k = 0; k < largo; k++) {
	    		    System.out.println(testDMUNames[k] + ": " );
						for(int j=0;j<8;j++){
						System.out.println(testDataMatrix[k][j]);
						}
					}*/

                System.out.println();
                System.out.println("DMUs Ratio Efficiency");
                System.out.println();
                for (int i = 0; i < largo; i++) {
                    System.out.println("Ratio Efficiency " + testDMUNames[i] + ":   " + objectivesO[i]);

                }
            }

            JSONObject jo = new JSONObject();
            jo.put("rendimiento", rendimiento);
            jo.put("proyeccion", proyeccion);
            jo.put("ranking", ranking);
            jo.put("rendimientoO", rendimientoO);
            jo.put("proyeccionO", proyeccionO);
            jo.put("rankingO", rankingO);
            jo.put("errors", errors);
            HttpHeaders headers = new HttpHeaders();
            headers.add("Content-Type", "application/json; charset=utf-8");

            return new ResponseEntity<String>(jo.toString(), headers, HttpStatus.OK);//Variable String que mostrará por pantalla la información del DEA solver
        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseEntity<String>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }

    }
}
	
