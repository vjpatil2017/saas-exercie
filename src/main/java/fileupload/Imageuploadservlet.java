package fileupload;
import java.io.File;
import java.io.IOException;

import java.util.List;
import static java.lang.System.*;
import java.nio.file.Files;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileItemFactory;
import org.apache.commons.fileupload.FileUploadException;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import com.google.cloud.vision.v1.AnnotateImageRequest;
import com.google.cloud.vision.v1.AnnotateImageResponse;
import com.google.cloud.vision.v1.BatchAnnotateImagesResponse;
import com.google.cloud.vision.v1.EntityAnnotation;
import com.google.cloud.vision.v1.Feature;
import com.google.cloud.vision.v1.Feature.Type;
import com.google.cloud.vision.v1.Image;
import com.google.cloud.vision.v1.ImageAnnotatorClient;
import com.google.protobuf.ByteString;
import java.io.PrintWriter; 



/**
 * Servlet implementation class Imageuploadservlet
 */
@WebServlet("/imageuploader")
public class Imageuploadservlet extends HttpServlet {
	private static final long serialVersionUID = 1L;

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		
		  response.setContentType("text/plain");
          response.setCharacterEncoding("UTF-8");
          response.getWriter().print("Testing google saas exercise!\r\n");
          
          
		if(!ServletFileUpload.isMultipartContent(request)){
			response.getWriter().print("No File has been uploaded, Please upload file and retry\r\n");
			return; 
		}
		FileItemFactory itemfactory = new DiskFileItemFactory(); 
		ServletFileUpload upload = new ServletFileUpload(itemfactory);
		try{
			
			List<FileItem> items = upload.parseRequest(request);
			
			
			for(FileItem item:items){
				
				String contentType = item.getContentType();
				    if(!contentType.equals("image/png") && !contentType.equals("image/jpeg") && !contentType.equals("image/gif")  && !contentType.equals("image/jpg"))    
					{
				    	response.setContentType("text/plain");
				        response.setCharacterEncoding("UTF-8");
				        response.getWriter().print("Only png, jpeg, jpg and gif format image files supported, Please upload correct image file\r\n");

					    return;
				    }
				
				
				byte[] data = item.get();
				ByteString imgBytes = ByteString.copyFrom(data);
			
			// Builds the image annotation request
	      
	          Image img = Image.newBuilder().setContent(imgBytes).build();
	          //Feature feat = Feature.newBuilder().setType();
	          Feature feature = Feature.newBuilder().setType(Type.LABEL_DETECTION).build();
	          AnnotateImageRequest imgreq =
	              AnnotateImageRequest.newBuilder().addFeatures(feature).setImage(img).build();
	          List<AnnotateImageRequest> requests = new ArrayList<>();
	        
	          requests.add(imgreq);
	          
	          ImageAnnotatorClient client = ImageAnnotatorClient.create();
	          BatchAnnotateImagesResponse batchResponse = client.batchAnnotateImages(requests);
	          List<AnnotateImageResponse> imageResponses = batchResponse.getResponsesList();
	          AnnotateImageResponse imageResponse = imageResponses.get(0);
	          
	          if (imageResponse.hasError()) 
	          {
	        	  response.setContentType("text/plain");
	              response.setCharacterEncoding("UTF-8");
	              response.getWriter().print("Error on Response from Vision API: \r\n "+ imageResponse.getError().getMessage());
	  
	          }

	      	response.setContentType("text/html");  
            PrintWriter out=response.getWriter();  
            out.println("<html><head>");  
            out.println("<title>Upload Image Label</title>");  
            out.println("</head>");  
            out.println("<body>"); 
            out.println("<h2>Different labels of the uploaded image and its percentage:</h2>");
            out.println("<div>");
            out.println("<table style=\"width:35%\">");
            out.println("<tr>" + 
            		"    <th>Type</th>" + 
            		"    <th>Percentage</th>" + 
            		"  </tr>");
         //   out.println(" <tr>");
	            // Print the labels extracted from the image
	            for (EntityAnnotation annotation : imageResponse.getLabelAnnotationsList()) {
	            	out.println(" <tr>" +
	                		"    <td>"+ annotation.getDescription() +"</td>" + 
	                		"    <td>"+ String.format("%.2f", annotation.getScore() * 100)+"%" +"</td>" 
	                		+"</tr>");
	           
	            }
            out.println("</table></div></body></html>");
            out.close();
           	
           	
          
            
		   }
		  }
		  catch(FileUploadException e){
 
	            response.setContentType("text/plain");
	            response.setCharacterEncoding("UTF-8");
	            response.getWriter().print("File Upload Exception:\r\n " +  e);
	
		  }
		  catch(Exception ex){

			    response.setContentType("text/plain");
	            response.setCharacterEncoding("UTF-8");
	            response.getWriter().print("General Exception:\r\n " + ex);

		}
		
	}

}
