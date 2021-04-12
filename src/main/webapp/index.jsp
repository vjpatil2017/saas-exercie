<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>Image Detector</title>
<style>
.container {width =1140px;
	margin: 100px auto 20px 250px;
}
</style>
</head>
<body>
<body style="background-color: powderblue;">
	<div class="container">
		<form action="${ pageContext.request.contextPath}/imageuploader"  method="post"  encType="multipart/form-data">
			<label for="img">Select image:</label> 
			<input type="file" id="img" name="img"> <input type="submit"  value="Upload" >
		</form>
	</div>

</body>
</body>
</html>