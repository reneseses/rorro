package com.dea.prototipo.web;

import net.sf.json.JSONObject;

import org.opensourcedea.dea.*;

import java.math.BigDecimal;
import java.math.RoundingMode;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.dea.prototipo.domain.Bodega;
import com.dea.prototipo.domain.Datos;
import com.dea.prototipo.domain.Usuario;

@RequestMapping("/member/benchmarking")
@Controller
public class Benchmarking {

	@RequestMapping(value="")
	public String benchmarking(Model uiModel){
		Usuario usuario= (Usuario) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
		uiModel.addAttribute("bodegas", Bodega.findBodegasByUsuario(usuario).getResultList());
		return "benchmarking/index";
	}

	@RequestMapping(value="/solver", produces= "text/json")
	public @ResponseBody ResponseEntity<String> test(@RequestParam(value="bodega", required= true) Long bodegaId ){
		System.out.println("bodegaId es "+bodegaId);
		int aux = 0;
		int cont=0;
		String rendimiento = "";
		String ranking="";
		String sup="";
		String inv="";
		String hr="";
		String broken="";
		String full="";
		String pallet="";
		String acumu="";
		String almac="";
		String bod="";
		String proyeccion="";
		String rendimientoO="";
		String proyeccionO="";
		String rankingO="";
		
		
		Bodega bodega= Bodega.findBodega(bodegaId);
		int id=  bodega.getId().intValue();
		bod=bodega.getNombreBodega();
		String[] testDMUNames ;
		String[] testVariableNames = new String [8];
		VariableOrientation[] testVariableOrientations = new VariableOrientation[8];
		VariableType[] testVariableTypes = new VariableType[8];
		double[] [] testDataMatrix ;
		List<Datos> datos = new ArrayList <Datos>();
		datos=Datos.findAllDatoses();//SE DEBE BUSCAR LOS ÚLTIMOS DATOS REGISTRADOS PARA CADA BODEGA EN EL SISTEMA
		int largo=datos.size();
		testDMUNames= new String[largo];
		testDataMatrix = new double[largo][8];

		System.out.println("bodega.getId() es"+id);
		System.out.println("nombre bodega con bodega.getNombre()" + bod);

		System.out.println(largo);

		//Set up the DMU Names
		for(int i=0;i<datos.size();i++){
			testDMUNames[i] = datos.get(i).getBodega().getNombreBodega(); 

		}
		//Set up the Variable Names

		testVariableNames[0] = "Superficie";
		testVariableNames[1] = "Inversión";
		testVariableNames[2] = "Horas de Trabajo";
		testVariableNames[3] = "Broken Case Lines";
		testVariableNames[4] = "Full Case Lines";
		testVariableNames[5] = "Pallet Case Lines";
		testVariableNames[6] = "Acumulación";
		testVariableNames[7] = "Almacenamiento";
		//Set up the Data Matrix
		for(int i=0;i<datos.size();i++){
			testDataMatrix[i][0]=datos.get(i).getSuperficie();	
			testDataMatrix[i][1]=datos.get(i).getInversion();
			testDataMatrix[i][2]=datos.get(i).getHorasDeTrabajoTotal(); 		
			testDataMatrix[i][3]=(double)datos.get(i).getBrokenCaseLines();
			testDataMatrix[i][4]=(double)datos.get(i).getFullCaseLines();
			testDataMatrix[i][5]=(double)datos.get(i).getPalletLines();
			testDataMatrix[i][6]=datos.get(i).getAcumulacion();
			testDataMatrix[i][7]=datos.get(i).getAlmacenamiento();
		}
		//Set up the variable types

		testVariableOrientations [0] = VariableOrientation.INPUT;
		testVariableOrientations [1] = VariableOrientation.INPUT;
		testVariableOrientations [2] = VariableOrientation.INPUT;

		testVariableOrientations [3] = VariableOrientation.OUTPUT;
		testVariableOrientations [4] = VariableOrientation.OUTPUT;
		testVariableOrientations [5] = VariableOrientation.OUTPUT;
		testVariableOrientations [6] = VariableOrientation.OUTPUT;
		testVariableOrientations [7] = VariableOrientation.OUTPUT;

		for(int i=0;i<7;i++){
			testVariableTypes[i]=VariableType.STANDARD;
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

			double [] [] pp=tester.getProjectionPercentages();
			double [] [] ppO=testerO.getProjectionPercentages();

			ArrayList<NonZeroLambda>[] referenceSets = new ArrayList[largo];
			referenceSets = tester.getReferenceSet();

			double[] [] slacks = tester.getSlacks();
			double[] [] slacksO = testerO.getSlacks();

			double[] [] projections = tester.getProjections();

			double[] [] weights = tester.getWeight();

			int[] ranks = tester.getRanks(true, RankingType.STANDARD, 5);
			int[] ranksO = testerO.getRanks(true, RankingType.STANDARD, 5);


			//Busca la bodega en el arreglo que se está consultando por el usuario.
			for (int k = 0; k < largo; k++) {
				if(testDMUNames[k].equals(bodega.getNombreBodega())){
					aux=k;
					System.out.println("La bodega a consultar es "+testDMUNames[k]+ " y la posición a consultar será "+aux);
				}

			}
			for (int k = 0; k < largo; k++) {
				if(ranks[k]<=ranks[aux]){
					cont++;	
				}
				for (int i = 0; i < largo; i++) {
					if (ranks[i] == k) {
						System.out.println(testDMUNames[i] + ": " + ranks[i]);
					}
				}

			}
			
			System.out.println(cont+" menores que ["+bod+"] y su ranking es "+ ranks[aux] );
			System.out.println(tester.getEfficiencyStatus(aux));
			System.out.println("rendmiento de bodega "+aux+" "+tester.getObjective(aux));
          	System.out.println("Max-slack input 1 "+ tester.getSlack(aux, 0));
          	System.out.println("Max-slack input 2 "+ tester.getSlack(aux, 1));
          	System.out.println("Max-slack input 3 "+ tester.getSlack(aux, 2));
          	BigDecimal rend = new BigDecimal(objectives[aux]);
          	BigDecimal rendi=rend.setScale(1, RoundingMode.HALF_UP);
			if(tester.getEfficiencyStatus(aux)){
				//EN EL CASO QUE LA BODEGA PRESENTE EFICIENCIA COMPLETA 
				BigDecimal eficiencia = new BigDecimal(objectives[aux]);
				BigDecimal ef=eficiencia.setScale(1, RoundingMode.HALF_UP);
				rendimiento="Para la bodega ["+ bod+"] el valor  de su rendimiento equivale a "+ String.valueOf(ef)+ " o "+ String.valueOf(ef.intValue()*100)+"%. ";
				proyeccion= "Por lo tanto se concluye que su bodega trabaja de manera completamente eficienciente.";
				ranking="Esta bodega ocupa el ranking "+ranks[aux]+" junto a "+cont+" bodegas más que trabajan eficientemente";
			}
			
          	else if(rendi.doubleValue()<1&&tester.getSlack(aux, 0)==0&&tester.getSlack(aux, 1)==0&&tester.getSlack(aux, 2)==0){
				//INEFICIENCIA RADIAL PERO TODAS SUS SLACKS SON IGUAL A CERO, ESTO ES SOLO SE DEBE REDUCIR DE MANERA RADIAL SUS ENTRADAS PARA ALCANZAR EFICIENCIA

				BigDecimal eficiencia = new BigDecimal(objectives[aux]);
				BigDecimal ef=eficiencia.setScale(2, RoundingMode.HALF_UP);
				double efp=objectives[aux]*100;
				BigDecimal efiporc= new BigDecimal (efp);
				BigDecimal epr=efiporc.setScale(0, RoundingMode.HALF_UP);
				rendimiento="Para la bodega ["+ bod+"] el valor  de su rendimiento equivale a "+ String.valueOf(ef)+ " o "+ String.valueOf(epr)+"%. ";
								
				proyeccion="Esto indica que ["+bod+"] podría reducir sus ENTRADAS en un "+epr.doubleValue()+"% en relación a sus valores actuales manteniendo al menos el mismo nivel de SALIDAS. ";
				ranking="De un total de "+largo+" bodegas en el sistema y bajo un análisis de eficiencia técnica relativa, ["+bod+"] ocupa la posición "+ranks[aux]+".";
				System.out.println();
				System.out.println("DMUs Max-slack Solution");
				System.out.println();
				System.out.println();
				System.out.println("Max-slack Solution " +testDMUNames[aux] + ":");
				for (int j = 0; j < 8; j++) {
					System.out.println(testVariableOrientations[j] + " "+ testVariableNames[j] + ":   " + slacks[aux][j]);

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
					System.out.println("Ratio Efficiency " + testDMUNames[i]+ ":   " + objectives[i]);

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

			}
				
          	else if(rendi.doubleValue()<1&&(tester.getSlack(aux, 0)!=0||tester.getSlack(aux, 1)!=0||tester.getSlack(aux, 2)!=0)){
              	//INEFICIENCIA RADIAL PERO Al MENOS UNAS DE SUS SLACKS SON DISTINTAS A CERO.
					BigDecimal eficiencia = new BigDecimal(objectives[aux]);
	              	BigDecimal ef=eficiencia.setScale(2, RoundingMode.HALF_UP);
	              	double efp=objectives[aux]*100;
	              	BigDecimal efiporc= new BigDecimal (efp);
	              	BigDecimal epr=efiporc.setScale(0, RoundingMode.HALF_UP);
	              	rendimiento="Para la bodega ["+ bod+"] el valor  de su rendimiento equivale a "+ String.valueOf(ef)+ " o "+ String.valueOf(epr)+"%. ";
	              	            	    			              	
	    			BigDecimal su=new BigDecimal(pp[aux][0]);
	    			BigDecimal sur=su.setScale(2, RoundingMode.HALF_UP);
	    			BigDecimal in=new BigDecimal(pp[aux][1]);
	    			BigDecimal inr=in.setScale(2, RoundingMode.HALF_UP);
	    			BigDecimal hh=new BigDecimal(pp[aux][2]);
	    			BigDecimal hhr=hh.setScale(2, RoundingMode.HALF_UP);
	    			System.out.println("valor redondeado de mejora para superficie "+sur);
    								if(sur.doubleValue()!=0){
    									sup = "SUPERFICIE = "+String.valueOf((100-sur.doubleValue()*-100))+"%";
    								}
    								if
    								(inr.doubleValue()!=0){
    									inv=" INVERSION = "+String.valueOf((100-inr.doubleValue()*-100))+"%";
    								}
    								if
    								(hhr.doubleValue()!=0){
    									hr=" HORAS-HOMBRE = "+String.valueOf((100-hhr.doubleValue()*-100))+"%";
    								}
    								
    								
							proyeccion="Esto indica que ["+bod+"] podría reducir la(s) siguiente(s) ENTRADAS en: "+" "+sup+" "+" "+inv+" "+" "+hr+" "+", en relación a sus valores actuales manteniendo al menos el mismo nivel de SALIDAS.";
							ranking="De un total de "+largo+" bodegas en el sistema y bajo un análisis de eficiencia técnica relativa, ["+bod+"] ocupa la posición "+ranks[aux]+".";
								System.out.println();
				    			System.out.println("DMUs Max-slack Solution");
				    			System.out.println();
			    				System.out.println();
			    				System.out.println("Max-slack Solution " +testDMUNames[aux] + ":");
			    				for (int j = 0; j < 8; j++) {
			    					System.out.println(testVariableOrientations[j] + " "+ testVariableNames[j] + ":   " + slacks[aux][j]);
			    						
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
			    					System.out.println("Ratio Efficiency " + testDMUNames[i]+ ":   " + objectives[i]);
			    					
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

				}else {
					
					BigDecimal eficiencia = new BigDecimal(objectives[aux]);
	              	BigDecimal ef=eficiencia.setScale(2, RoundingMode.HALF_UP);
	              	double efp=objectives[aux]*100;
	              	BigDecimal efiporc= new BigDecimal (efp);
	              	BigDecimal epr=efiporc.setScale(0, RoundingMode.HALF_UP);
	              	rendimiento="Para la bodega ["+ bod+"] el valor  de su rendimiento equivale a "+ String.valueOf(ef)+ " o "+ String.valueOf(epr)+"%. ";
	              	            	    			              	
	    			BigDecimal su=new BigDecimal(pp[aux][0]);
	    			BigDecimal sur=su.setScale(2, RoundingMode.HALF_UP);
	    			BigDecimal in=new BigDecimal(pp[aux][1]);
	    			BigDecimal inr=in.setScale(2, RoundingMode.HALF_UP);
	    			BigDecimal hh=new BigDecimal(pp[aux][2]);
	    			BigDecimal hhr=hh.setScale(2, RoundingMode.HALF_UP);
	    			System.out.println("valor redondeado de mejora para superficie"+sur);
    								if(sur.doubleValue()!=0){
    									sup = "SUPERFICIE = "+String.valueOf((100-sur.doubleValue()*-100))+"%";
    								}
    								if
    								(inr.doubleValue()!=0){
    									inv=" INVERSION = "+String.valueOf((100-inr.doubleValue()*-100))+"%";
    								}
    								if
    								(hhr.doubleValue()!=0){
    									hr=" HORAS-HOMBRE = "+String.valueOf((100-hhr.doubleValue()*-100))+"%";
    								}
    								
    								
							proyeccion="Si bien presenta eficiencia esta no es completa, puesto que al menos una(s) de su(s) ENTRADA(s) no está(n) siendo aprovechada(s) de modo que ["+bod+"] podría reducir la(s) siguiente(s) en: "+" "+sup+" "+" "+inv+" "+" "+hr+" "+", en relación a sus valores actuales manteniendo al menos el mismo nivel de SALIDAS.";
							ranking="De un total de "+largo+" bodegas en el sistema y bajo un análisis de eficiencia técnica relativa, ["+bod+"] ocupa la posición "+ranks[aux]+".";
								System.out.println();
				    			System.out.println("DMUs Max-slack Solution");
				    			System.out.println();
			    				System.out.println();
			    				System.out.println("Max-slack Solution " +testDMUNames[aux] + ":");
			    				for (int j = 0; j < 8; j++) {
			    					System.out.println(testVariableOrientations[j] + " "+ testVariableNames[j] + ":   " + slacks[aux][j]);
			    						
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
			    					System.out.println("Ratio Efficiency " + testDMUNames[i]+ ":   " + objectives[i]);
			    					
			    				}
				}
			//SOLVER PARA ORIENTACION OUTPUT
			cont=0;
			for (int k = 0; k < largo; k++) {
				if(ranksO[k]<=ranksO[aux]){
					cont++;	
				}
				for (int i = 0; i < largo; i++) {
					if (ranksO[i] == k) {
						System.out.println(testDMUNames[i] + ": " + ranksO[i]);
					}
				}

			}
			
			System.out.println(cont+"menores que ["+bod+"] y su ranking es "+ ranksO[aux] );
			System.out.println(testerO.getEfficiencyStatus(aux));
			System.out.println("rendmiento de bodega BCC-O"+aux+" "+testerO.getObjective(aux));
          	System.out.println("Max-slack output 4 "+ testerO.getSlack(aux, 3));
          	System.out.println("Max-slack output 5 "+ testerO.getSlack(aux, 4));
          	System.out.println("Max-slack output 6 "+ testerO.getSlack(aux, 5));
          	System.out.println("Max-slack output 7 "+ testerO.getSlack(aux, 6));
          	System.out.println("Max-slack output 8 "+ testerO.getSlack(aux, 7));
          	
          	BigDecimal rendO = new BigDecimal(objectivesO[aux]);
          	BigDecimal rendiO=rendO.setScale(1, RoundingMode.HALF_UP);
			if(testerO.getEfficiencyStatus(aux)){
				//EN EL CASO QUE LA BODEGA PRESENTE EFICIENCIA COMPLETA 
				BigDecimal eficienciaO = new BigDecimal(objectivesO[aux]);
				BigDecimal efO=eficienciaO.setScale(1, RoundingMode.HALF_UP);
				rendimientoO="Para la bodega ["+ bod+"] el valor  de su rendimiento equivale a "+ String.valueOf(efO)+ " o "+ String.valueOf(efO.intValue()*100)+"%. ";
				proyeccionO= "Por lo tanto se concluye que su bodega trabaja de manera completamente eficienciente.";
				rankingO="Esta bodega ocupa el ranking "+ranksO[aux]+" junto a "+cont+" bodegas más que trabajan eficientemente";
			}
			
          	else if(rendiO.doubleValue()<1&&testerO.getSlack(aux, 3)==0&&testerO.getSlack(aux, 4)==0&&testerO.getSlack(aux, 5)==0&&testerO.getSlack(aux, 6)==0&&testerO.getSlack(aux, 7)==0){
				//INEFICIENCIA RADIAL PERO TODAS SUS SLACKS SON IGUAL A CERO, ESTO ES SOLO SE DEBE AUMENTAR DE MANERA RADIAL SUS SALIDAS PARA ALCANZAR EFICIENCIA

				BigDecimal eficienciaO = new BigDecimal(objectivesO[aux]);
				BigDecimal efO=eficienciaO.setScale(2, RoundingMode.HALF_UP);
				double efpO=objectivesO[aux]*100;
				BigDecimal efiporcO= new BigDecimal (efpO);
				BigDecimal eprO=efiporcO.setScale(0, RoundingMode.HALF_UP);
				rendimientoO="Para la bodega ["+ bod+"] el valor  de su rendimiento equivale a "+ String.valueOf(efO)+ " o "+ String.valueOf(eprO)+"%. ";
				proyeccionO="Esto indica que ["+bod+"] podría aumentar sus SALIDAS en un "+eprO.doubleValue()+"% en relación a sus valores actuales mientras no se ocupen más recursos de ENTRADAS.";
				rankingO="De un total de "+largo+" bodegas en el sistema y bajo un análisis de eficiencia técnica relativa, ["+bod+"] ocupa la posición "+ranksO[aux]+".";
				
				System.out.println();
				System.out.println("DMUs Max-slack Solution BCC-O");
				System.out.println();
				System.out.println();
				System.out.println("Max-slack Solution " +testDMUNames[aux] + ":");
				for (int j = 0; j < 8; j++) {
					System.out.println(testVariableOrientations[j] + " "+ testVariableNames[j] + ":   " + slacksO[aux][j]);

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
					System.out.println("Ratio Efficiency " + testDMUNames[i]+ ":   " + objectivesO[i]);

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

			}
				
          	else if(rendi.doubleValue()<1&&(testerO.getSlack(aux, 3)!=0||testerO.getSlack(aux, 4)!=0||testerO.getSlack(aux, 5)!=0||testerO.getSlack(aux, 6)!=0||testerO.getSlack(aux, 7)!=0)){
              	//INEFICIENCIA RADIAL PERO Al MENOS UNAS DE SUS SLACKS SON DISTINTAS A CERO.
					
	          		BigDecimal eficienciaO = new BigDecimal(objectivesO[aux]);
					BigDecimal efO=eficienciaO.setScale(2, RoundingMode.HALF_UP);
					double efpO=objectivesO[aux]*100;
					BigDecimal efiporcO= new BigDecimal (efpO);
					BigDecimal eprO=efiporcO.setScale(0, RoundingMode.HALF_UP);
	          		rendimientoO="Para la bodega ["+ bod+"] el valor  de su rendimiento equivale a "+ String.valueOf(efO)+ " o "+ String.valueOf(eprO)+"%. ";
	              	            	    			              	
	              	BigDecimal bc=new BigDecimal(ppO[aux][3]);
					BigDecimal bcr=bc.setScale(2, RoundingMode.HALF_UP);
					BigDecimal fc=new BigDecimal(ppO[aux][4]);
					BigDecimal fcr=fc.setScale(2, RoundingMode.HALF_UP);
					BigDecimal pl=new BigDecimal(ppO[aux][5]);
					BigDecimal plr=pl.setScale(2, RoundingMode.HALF_UP);
					BigDecimal ac=new BigDecimal(ppO[aux][6]);
					BigDecimal acr=ac.setScale(2, RoundingMode.HALF_UP);
					BigDecimal alm=new BigDecimal(ppO[aux][7]);
					BigDecimal almr=alm.setScale(2, RoundingMode.HALF_UP);
					System.out.println("valor redondeado de mejora para Broken Case"+bc);
					if(bcr.doubleValue()!=0){
						broken = "BROKEN CASE LINES en "+String.valueOf(bcr.doubleValue()*100)+"%";
					}
					if(fcr.doubleValue()!=0){
						full=" FULL CASE LINES en "+String.valueOf(fcr.doubleValue()*100)+"%";
					}
					if(plr.doubleValue()!=0){
						pallet=" PALLET CASE LINES en "+String.valueOf(plr.doubleValue()*100)+"%";
					}
					if(acr.doubleValue()!=0){
						acumu=" ACUMULACIÓN en "+String.valueOf(acr.doubleValue()*100)+"%";
					}
					if(almr.doubleValue()!=0){
						almac=" ALMACENAMIENTO en "+String.valueOf(almr.doubleValue()*100)+"%";
					}
    								
    								
					proyeccionO="Esto indica que ["+bod+"] podría AUMENTAR la(s) siguiente(s) SALIDAS en: "+" "+broken+" "+" "+full+" "+" "+pallet+" "+acumu+" "+almac+" , en relación a sus valores actuales manteniendo al menos el mismo nivel de ENTRADAS.";
					rankingO="De un total de "+largo+" bodegas en el sistema y bajo un análisis de eficiencia técnica relativa, ["+bod+"] ocupa la posición "+ranksO[aux]+".";
					System.out.println();
	    			System.out.println("DMUs Max-slack Solution");
	    			System.out.println();
    				System.out.println();
    				System.out.println("Max-slack Solution " +testDMUNames[aux] + ":");
    				for (int j = 0; j < 8; j++) {
    					System.out.println(testVariableOrientations[j] + " "+ testVariableNames[j] + ":   " + slacksO[aux][j]);
    						
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
    					System.out.println("Ratio Efficiency " + testDMUNames[i]+ ":   " + objectivesO[i]);
    					
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

				}else {
					
					BigDecimal eficienciaO = new BigDecimal(objectivesO[aux]);
	              	BigDecimal efO=eficienciaO.setScale(2, RoundingMode.HALF_UP);
	              	double efpO=objectivesO[aux]*100;
	              	BigDecimal efiporcO= new BigDecimal (efpO);
	              	BigDecimal eprO=efiporcO.setScale(0, RoundingMode.HALF_UP);
	              	rendimientoO="Para la bodega ["+ bod+"] el valor  de su rendimiento equivale a "+ String.valueOf(efO)+ " o "+ String.valueOf(eprO)+"%. ";         	
	              	BigDecimal bc=new BigDecimal(ppO[aux][3]);
					BigDecimal bcr=bc.setScale(2, RoundingMode.HALF_UP);
					BigDecimal fc=new BigDecimal(ppO[aux][4]);
					BigDecimal fcr=fc.setScale(2, RoundingMode.HALF_UP);
					BigDecimal pl=new BigDecimal(ppO[aux][5]);
					BigDecimal plr=pl.setScale(2, RoundingMode.HALF_UP);
					BigDecimal ac=new BigDecimal(ppO[aux][6]);
					BigDecimal acr=ac.setScale(2, RoundingMode.HALF_UP);
					BigDecimal alm=new BigDecimal(ppO[aux][7]);
					BigDecimal almr=alm.setScale(2, RoundingMode.HALF_UP);
					System.out.println("valor redondeado de mejora para Broken Case"+bc);
					if(bcr.doubleValue()!=0){
						broken = "BROKEN CASE LINES en "+String.valueOf(bcr.doubleValue()*100)+"%";
					}
					if(fcr.doubleValue()!=0){
						full=" FULL CASE LINES en "+String.valueOf(fcr.doubleValue()*100)+"%";
					}
					if(plr.doubleValue()!=0){
						pallet=" PALLET CASE LINES en "+String.valueOf(plr.doubleValue()*100)+"%";
					}
					if(acr.doubleValue()!=0){
						acumu=" ACUMULACIÓN en "+String.valueOf(acr.doubleValue()*100)+"%";
					}
					if(almr.doubleValue()!=0){
						almac=" ALMACENAMIENTO en "+String.valueOf(almr.doubleValue()*100)+"%";
					}
    								
    								
					proyeccionO="Si bien presenta eficiencia esta no es completa, puesto que al menos una(s) de su(s) SALIDA(s) no está(n) siendo aprovechada(s) de modo que ["+bod+"] podría AUMENTAR la(s) siguiente(s) SALIDAS en: "+" "+broken+" "+" "+full+" "+" "+pallet+" "+acumu+" "+almac+" , en relación a sus valores actuales manteniendo al menos el mismo nivel de ENTRADAS.";
					rankingO="De un total de "+largo+" bodegas en el sistema y bajo un análisis de eficiencia técnica relativa, ["+bod+"] ocupa la posición "+ranksO[aux]+".";
					System.out.println();
	    			System.out.println("DMUs Max-slack Solution");
	    			System.out.println();
    				System.out.println();
    				System.out.println("Max-slack Solution " +testDMUNames[aux] + ":");
    				for (int j = 0; j < 8; j++) {
    					System.out.println(testVariableOrientations[j] + " "+ testVariableNames[j] + ":   " + slacksO[aux][j]);
    						
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
    					System.out.println("Ratio Efficiency " + testDMUNames[i]+ ":   " + objectivesO[i]);
    					
    				}
				}
		}
		catch (Exception e) {
			e.printStackTrace();
		}
		
		JSONObject jo= new JSONObject();
		jo.put("rendimiento", rendimiento);
		jo.put("proyeccion", proyeccion);
		jo.put("ranking", ranking);
		jo.put("rendimientoO", rendimientoO);
		jo.put("proyeccionO", proyeccionO);
		jo.put("rankingO", rankingO);
		HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Type", "application/json; charset=utf-8");
		
		return new ResponseEntity<String>(jo.toString(), headers, HttpStatus.OK);//Variable String que mostrará por pantalla la información del DEA solver

	}
}
	
