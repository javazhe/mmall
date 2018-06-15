<%@ page language="java" contentType="text/html; charset=UTF-8"
         pageEncoding="UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<body>
<h2>Hello World!</h2>
Spring MVC 文件上传
<form action="/manage/product/upload.do" enctype="multipart/form-data" name ="form" method="post">
    <input type="file" name = "upload_file">
    <input type = "submit" value = "上传文件">
</form>
<br>
<br>
Spring MVC 富文本上传
<form action="/manage/product/richtext_img_upload.do" enctype="multipart/form-data" name ="form" method="post">
    <input type="file" name = "upload_file">
    <input type = "submit" value = "富文本上传文件">
</form>
</body>
</html>
