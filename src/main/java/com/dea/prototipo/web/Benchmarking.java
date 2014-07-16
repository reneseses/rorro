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
		String mejora="";
		String sup="";
		String inv="";
		String hr="";
		String bc="";
		String fc="";
		String pl="";
		String acum="";
		String alm="";
		String bod="";
		String m="";
		String me="";
		String mej="";
		String mejo="";
		String mejor="";
		String mejoras="";
		String mejorass="";
		String proyeccion="";
		String vista="";

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

		//Set the DEA Problem Model Type (CCR Input Oriented).
		tester.setModelType(ModelType.CCR_I);

		//Set the DEA Problem DMU Names where testDMUName is a double[].
		tester.setDMUNames(testDMUNames);

		//Set the DEA Problem Variable Names where testVariableName is a String[].
		tester.setVariableNames(testVariableNames);

		//Set the DEA Problem Variable Orientation where testVariableOrientation is a VariableOrientation[].
		tester.setVariableOrientations(testVariableOrientations);

		//Set the DEA Problem Variable Types where testVariableTypes is a VariableType[].
		tester.setVariableTypes(testVariableTypes);

		tester.setDataMatrix(testDataMatrix);


		try {
			//Solve the DEA Problem
			tester.solve();

			//Get the solution Objectives
			double[] objectives = tester.getObjectives();        

			double [] [] pp=tester.getProjectionPercentages();

			ArrayList<NonZeroLambda>[] referenceSets = new ArrayList[largo];
			referenceSets = tester.getReferenceSet();

			double[] [] slacks = tester.getSlacks();

			double[] [] projections = tester.getProjections();

			double[] [] weights = tester.getWeight();

			int[] ranks = tester.getRanks(true, RankingType.STANDARD, 5);

			//SI LA BODEGA A CONSULTAR ES INEFICIENTE:
			//Rendimiento de bodega que se está consultando
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
			System.out.println(cont+"menores que ["+bod+"] y su ranking es "+ ranks[aux] );
			if(tester.getEfficiencyStatus(aux)){
				//EN EL CASO QUE LA BODEGA PRESENTE EFICIENCIA COMPLETA 
				BigDecimal eficiencia = new BigDecimal(objectives[aux]);
				BigDecimal ef=eficiencia.setScale(1, RoundingMode.HALF_UP);
				rendimiento="Para la bodega ["+ bod+"] el valor  de su rendimiento equivale a "+ String.valueOf(ef)+ " o "+ String.valueOf(ef.intValue()*100)+"%. ";
				proyeccion= "Por lo tanto se concluye que su bodega trabaja de manera completamente eficienciente.";
				ranking="Esta bodega ocupa el ranking "+ranks[aux]+" junto a "+cont+" bodegas más que trabajan eficientemente";
			}
			else{
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
					sup = "SUPERFICIE en "+String.valueOf(sur.doubleValue()*-100)+"%";
					m="SUPERFICIE en "+sup;
				}
				if(inr.doubleValue()!=0){
					inv=" INVERSION en "+String.valueOf(inr.doubleValue()*-100)+"%";
					me=m+" INVERSION en "+inv;	
				}
				if(hhr.doubleValue()!=0){
					hr=" HORAS-HOMBRE en "+String.valueOf(hhr.doubleValue()*-100)+"%";
					mej=me+" HORAS-HOMBRE en "+hr;
				}

				proyeccion="Esto indica que ["+bod+"] debería reducir las siguientes entradas:"+sup+inv+hr+" manteniendo el mismo nivel de sus salidas ";
				ranking="De un total de "+largo+" bodegas en el sistema y bajo un análisis de eficiencia técnica relativa, ["+bod+"] ocupa la posición "+ranks[aux]+".";
				System.out.println();
				System.out.println("DMUs Max-slack Solution");
				System.out.println();
				System.out.println();
				System.out.println("Max-slack Solution " +testDMUNames[aux] + ":");
				for (int j = 0; j < 8; j++) {
					System.out.println(testVariableOrientations[j] + " "+ testVariableNames[j] + ":   " + slacks[aux][j]);

				}

				for (int k = 0; k < largo; k++) {
					System.out.println(testDMUNames[k] + ": " );
					for(int j=0;j<8;j++){
						System.out.println(testDataMatrix[k][j]);
					}
				}

				System.out.println();
				System.out.println("DMUs Ratio Efficiency");
				System.out.println();
				for (int i = 0; i < largo; i++) {
					System.out.println("Ratio Efficiency " + testDMUNames[i]+ ":   " + objectives[i]);

				}	 
				System.out.println();
				System.out.println("DMUs projections porcentaje");
				System.out.println();
				for (int i = 0; i < largo; i++) {
					System.out.println();
					System.out.println("projections porcentaje solution " +testDMUNames[i] + ":");
					for (int j = 0; j < 3; j++) {
						System.out.println( + pp[i][j]);

					}
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
		
		HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Type", "application/json; charset=utf-8");
		
		return new ResponseEntity<String>(jo.toString(), headers, HttpStatus.OK);//Variable String que mostrará por pantalla la información del DEA solver

	}

	public String index() {
		return "benchmarking/index";
	}
}
