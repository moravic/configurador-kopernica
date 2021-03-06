package com.neurologyca.kopernica.config.controller;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class AppController {
	@Value("${base.path}")
	private String basePath;
	
	@Value("${database.url}")
	private String databaseUrl;
    
	public static String estudio;
	public static String proyecto;
	public static String fullDatabaseUrl;
	
	@GetMapping("/app/properties/{project}/{study}")
	public void setProperties(@PathVariable String project, @PathVariable String study) {
		this.proyecto = project;
		this.estudio = study;
		fullDatabaseUrl = databaseUrl + project + "\\" + study + "\\csvs\\database";
		System.out.println(fullDatabaseUrl);
	}
		
	@SuppressWarnings("finally")
	@GetMapping("/app/")
	public List<String> getProyectos() throws Exception {
		//System.out.println("getProjects " + basePath);
		Path directory = Paths.get(basePath);
		List<String> folders = new ArrayList<String>();
		
		try {
			Files.walk(directory, 1).filter(entry -> !entry.equals(directory))
		    	.filter(Files::isDirectory).forEach(subdirectory ->{
		    		folders.add(subdirectory.getFileName().toString());
		    		 System.out.println(subdirectory.getFileName());
		    	});
		} 
		finally {
			if (folders.size()==0) 
				throw new Exception("No se encuentra la carpeta " + basePath + " o no se encuentran proyectos dentro de esa carpeta");

		return folders;
		}
	}
			
	@SuppressWarnings("finally")
	@GetMapping("/app/estudios/{proyecto}")
	public List<String> getEstudios(@PathVariable String proyecto) throws Exception {
		String subDirectory = basePath + "\\" + proyecto;
		//System.out.println("getEstudio " + subDirectory);
			
		Path directory = Paths.get(subDirectory);

		List<String> folders = new ArrayList<String>();
			
		try {
			Files.walk(directory, 1).filter(entry -> !entry.equals(directory))
			   	.filter(Files::isDirectory).forEach(subdirectory ->
			   	{
			    	folders.add(subdirectory.getFileName().toString());
			    	//System.out.println(subdirectory.getFileName());
			    });
		} 
		finally {
			if (folders.size()==0)
				throw new Exception("No se encuentra ninguna carpeta de estudios en " + directory);		
		return folders;
		}
	}
/*		
	@PostMapping("/app/{proyecto}")
	public String crearProyecto(@PathVariable String proyecto) throws Exception {
 
		String subDirectory = basePath + "\\" + proyecto;
		File directorio = new File(subDirectory);
    		
    	if (directorio.mkdirs()) {
    		   System.out.println("Se ha creado el directorio del proyecto.");
    	} else if (directorio.isDirectory()) {
    		    throw new Exception("Ya existe el proyecto " + proyecto);	
    	} else {
    		    throw new Exception("No se ha podido crear el directorio del proyecto " + proyecto);
    	}
			
		return proyecto;
		
				
	}
*/
}
