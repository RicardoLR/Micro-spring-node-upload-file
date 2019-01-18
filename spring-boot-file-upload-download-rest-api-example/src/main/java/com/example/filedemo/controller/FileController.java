package com.example.filedemo.controller;

import com.example.filedemo.payload.Modelrest;
import com.example.filedemo.payload.ResponseNode;
import com.example.filedemo.payload.ResponsePrueba;
import com.example.filedemo.payload.UploadFileResponse;
import com.example.filedemo.service.FileStorageService;
import com.example.filedemo.util.UtilBase64Image;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import javax.servlet.http.HttpServletRequest;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.Base64;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
public class FileController {

	private static final Logger logger = LoggerFactory.getLogger(FileController.class);

	@Autowired
	private FileStorageService fileStorageService;

	@PostMapping("/uploadFile")
	public UploadFileResponse uploadFile(
			@RequestParam("file") MultipartFile file,
			@RequestParam(value = "route", required=false) String route
			) throws IOException {


		/**
		 * Guardado con File system
		 */
		String fileName = fileStorageService.storeFile(file);
		String fileDownloadUri = ServletUriComponentsBuilder.fromCurrentContextPath()
				.path("/downloadFile/")
				.path(fileName)
				.toUriString();
		String base64Guardar = "";

		String base64Image = "";

		if( route != null && route.equals("firebase") ) {

			/**
			 * Convertir a Base64 el PDF
			 * 
			 * Puede ser muy largo, no usarlo en rest template
			 */
			//			File file2 = convert(file);
			//			try (FileInputStream imageInFile = new FileInputStream( file2 ) ) {
			//				// Reading a Image file from file system
			//				byte imageData[] = new byte[(int) file2.length()];
			//				imageInFile.read(imageData);
			//				base64Image = Base64.getEncoder().encodeToString(imageData);
			//
			//				System.out.println("BASE 64");
			//				System.out.println(base64Image);
			//				System.out.println("-- BASE 64");
			//
			//			} catch (FileNotFoundException e) {
			//				System.out.println("Image not found" + e);
			//			} catch (IOException ioe) {
			//				System.out.println("Exception while reading the Image " + ioe);
			//			}


			/**
			 * REST template para enviar el Form data a node, el PDF
			 */
			LinkedMultiValueMap<String, Object> parts = new LinkedMultiValueMap<>();
			parts.add("file", new MultipartInputStreamFileResource(file.getInputStream(), file.getOriginalFilename()));
			parts.add("filename", file.getOriginalFilename());

			RestTemplate restTemplate = new RestTemplate();
			HttpHeaders headers = new HttpHeaders();
			headers.setContentType(MediaType.MULTIPART_FORM_DATA);

			HttpEntity<MultiValueMap<String, Object>> requestEntity = new HttpEntity<MultiValueMap<String, Object>>(parts, headers);

			// file upload path on destination server
			parts.add("destination", "./");
			ResponseEntity<ResponseNode> response = restTemplate.exchange("http://localhost:9090/node-receive-file", HttpMethod.POST, requestEntity, ResponseNode.class);
			ResponseNode modelo3 = response.getBody();
			System.out.println("modelo3.toString() a NODE");
			System.out.println(modelo3.toString());



			/**
			 * Ejemplo de envia y recibir respuesta, debemos modelas y tener contructores, vacios y sobre cargados
			 */
			//			ResponseEntity<ResponseNode> modelrest = new RestTemplate().postForEntity(
			//					"http://localhost:9090/node", 
			//					new Modelrest( "FDS", "base64Guardar" ),
			//					ResponseNode.class
			//					);		
			//			ResponseNode modelo = modelrest.getBody();
			//			System.out.print(modelo.toString());


		}else {
			base64Guardar = "null";
		}


		return new UploadFileResponse(fileName, fileDownloadUri, file.getContentType(), file.getSize(), "base64Image" );
	}


	// mover a utilerias
	public File convert(MultipartFile file) throws IOException
	{    
		File convFile = new File(file.getOriginalFilename());
		convFile.createNewFile(); 
		FileOutputStream fos = new FileOutputStream(convFile); 
		fos.write(file.getBytes());
		fos.close(); 
		return convFile;
	}


	@PostMapping("/uploadMultipleFiles")
	public List<UploadFileResponse> uploadMultipleFiles(@RequestParam("files") MultipartFile[] files) {
		return Arrays.asList(files)
				.stream()
				.map(file -> {
					try {
						return uploadFile(file, null);
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					return null;
				})
				.collect(Collectors.toList());
	}

	@GetMapping("/downloadFile/{fileName:.+}")
	public ResponseEntity<Resource> downloadFile(@PathVariable String fileName, HttpServletRequest request) {
		// Load file as Resource
		Resource resource = fileStorageService.loadFileAsResource(fileName);

		// Try to determine file's content type
		String contentType = null;
		try {
			contentType = request.getServletContext().getMimeType(resource.getFile().getAbsolutePath());
		} catch (IOException ex) {
			logger.info("Could not determine file type.");
		}

		// Fallback to the default content type if type could not be determined
		if(contentType == null) {
			contentType = "application/octet-stream";
		}

		return ResponseEntity.ok()
				.contentType(MediaType.parseMediaType(contentType))
				.header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + resource.getFilename() + "\"")
				.body(resource);
	}

}

class MultipartInputStreamFileResource extends InputStreamResource {

	private final String filename;

	MultipartInputStreamFileResource(InputStream inputStream, String filename) {
		super(inputStream);
		this.filename = filename;
	}

	@Override
	public String getFilename() {
		return this.filename;
	}

	@Override
	public long contentLength() throws IOException {
		return -1; // we do not want to generally read the whole stream into memory ...
	}
}
